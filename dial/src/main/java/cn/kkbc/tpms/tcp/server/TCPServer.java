package cn.kkbc.tpms.tcp.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kkbc.tpms.tcp.processor.HeartProcessor;
import cn.kkbc.tpms.tcp.processor.LineProcessor;
import cn.kkbc.tpms.tcp.processor.MsgQueueProcessor;
import cn.kkbc.tpms.tcp.processor.Processor;
import cn.kkbc.tpms.tcp.service.TCPServerHandler;

public class TCPServer {

	private Logger log = LoggerFactory.getLogger(getClass());
	private volatile boolean isRunning = false;

	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;
	private int port;
	private List<Processor> processors;

	public TCPServer() {
		this.initProcessors();
	}

	public TCPServer(int port) {
		this();
		this.port = port;
	}

	private void bind() throws Exception {
		this.bossGroup = new NioEventLoopGroup();
		this.workerGroup = new NioEventLoopGroup();
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)//
				.channel(NioServerSocketChannel.class) //
				.childHandler(new ChannelInitializer<SocketChannel>() { //
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
//						ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(Consts.tcp_client_idle_minutes, 0, 0, TimeUnit.MINUTES));
						
						ch.pipeline().addLast(new TCPServerHandler());
					}
				}).option(ChannelOption.SO_BACKLOG, 128) //
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		this.log.info("TCP服务启动完毕,port={}", this.port);
		ChannelFuture channelFuture = serverBootstrap.bind(port).sync();

		channelFuture.channel().closeFuture().sync();
	}

	public synchronized void startServer() {
		if (this.isRunning) {
			throw new IllegalStateException(this.getName() + " is already started .");
		}
		this.isRunning = true;

		this.processors.stream().filter(p -> p != null).forEach(p -> p.startProcess());
		new Thread(() -> {
			try {
				this.bind();
			} catch (Exception e) {
				this.log.info("TCP服务启动出错:{}", e.getMessage());
				e.printStackTrace();
			}
		}, this.getName()).start();
	}

	public synchronized void stopServer() {
		if (!this.isRunning) {
			throw new IllegalStateException(this.getName() + " is not yet started .");
		}
		this.isRunning = false;

		this.processors.stream().filter(p -> p != null).forEach(p -> p.stopProcess());

		try {
			Future<?> future = this.workerGroup.shutdownGracefully().await();
			if (!future.isSuccess()) {
				log.error("workerGroup 无法正常停止:{}", future.cause());
			}

			future = this.bossGroup.shutdownGracefully().await();
			if (!future.isSuccess()) {
				log.error("bossGroup 无法正常停止:{}", future.cause());
			}
			// this.channelFuture.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.log.info("TCP服务已经停止...");
	}

	private String getName() {
		return "TCP-Server";
	}

	public static void main(String[] args) throws Exception {
		TCPServer server = new TCPServer(20049);
		server.startServer();

		// Thread.sleep(3000);
		// server.stopServer();
	}

	private void initProcessors() {
		this.processors = new ArrayList<>();
		this.processors.add(new MsgQueueProcessor());
		this.processors.add(new HeartProcessor());
		this.processors.add(new LineProcessor());
	}
}