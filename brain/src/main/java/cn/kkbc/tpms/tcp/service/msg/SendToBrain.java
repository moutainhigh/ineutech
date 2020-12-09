package cn.kkbc.tpms.tcp.service.msg;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kkbc.util.ToolsForByte;

public class SendToBrain {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 给脑波发送心跳包
	 */
	public void sendHeartData(Channel channel) throws InterruptedException{
		byte[] data=new byte[]{ (byte) 0xA5, 0x5A, 0x02, 0x01, 0x00, (byte) 0xff};
		log.info("channel状态----isActive:"+channel.isActive()+";isOpen:"+channel.isOpen()+";isRegistered:"+channel.isRegistered()+";isWritable:"+channel.isWritable());
		if (channel.isWritable()) {
			ChannelFuture future = channel.writeAndFlush(Unpooled.copiedBuffer(data)).sync();
			if (!future.isSuccess()) {
				log.error("发送心跳数据包出错:{}", future.cause());
			}
		}
	}
	public static void main(String[] args) throws Exception {
		ToolsForByte toolsForByte=new ToolsForByte();
		System.out.println(toolsForByte.byteToInteger(new byte[]{(byte) 0xA5,0x5A}));
	}
	
}
