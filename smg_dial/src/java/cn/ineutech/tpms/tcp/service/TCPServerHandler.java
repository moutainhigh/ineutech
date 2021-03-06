package cn.ineutech.tpms.tcp.service;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.ineutech.tpms.tcp.TPMSConsts;
import cn.ineutech.tpms.tcp.server.SessionManager;
import cn.ineutech.tpms.tcp.service.msg.BaseMsgProcessService;
import cn.ineutech.tpms.tcp.service.queue.MemoryMsgQueueServiceImpl;
import cn.ineutech.tpms.tcp.service.queue.MsgQueueService;
import cn.ineutech.tpms.tcp.vo.QueueElement;
import cn.ineutech.tpms.tcp.vo.Session;

import com.ineutech.entity.TestScoreVO;
import com.ineutech.entity.TestUser;
import com.ineutech.service.DeviceService;
import com.ineutech.service.impl.DeviceServiceImpl;
import com.ineutech.util.SpringContextUtils;

/**
 * 
 * @name: TCPServerHandler 
 * @description: 处理tcp服务接收到的拨盘数据
 * @date 2018年2月1日 下午2:15:00
 * @author liululu
 */
public class TCPServerHandler extends ChannelInboundHandlerAdapter {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final SessionManager sessionManager;
	private MsgQueueService msgQueueService;
	private DeviceService service;
	private BaseMsgProcessService sendToDevice;

	public TCPServerHandler() {
		this.sessionManager = SessionManager.getInstance();
		this.msgQueueService = new MemoryMsgQueueServiceImpl();
		this.sendToDevice = new BaseMsgProcessService();
		this.service = (DeviceServiceImpl) SpringContextUtils.getContext()
				.getBean("deviceService");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws InterruptedException {
		try {

			String data = (String) msg;
			logger.info("received data:{}", data);

			QueueElement element = new QueueElement(ctx.channel(), data);
			this.msgQueueService.push(element);

		} finally {
			release(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		logger.error("发生异常:{}", cause.getMessage());

		if (!ctx.channel().isActive()) {
			System.out.println("SimpleClient:" + ctx.channel().remoteAddress()
					+ "异常");
		}

		cause.printStackTrace();
		ctx.close();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Session session = Session.buildSession(ctx.channel());
		sessionManager.putPad(session.getId(), session);
		logger.info("拨盘终端连接:{}", session);
		sessionManager.getChannelGroup().add(ctx.channel());
		logger.info("当前登陆总数:" + sessionManager.getChannelGroup().size());

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		String sessionId = ctx.channel().id().asLongText();
		Session session = sessionManager.findPadBySessionId(sessionId);
		this.sessionManager.removePadBySessionId(sessionId);
		logger.info("拨盘终端断开连接:{}", session.getSeatNo());
		sessionManager.getChannelGroup().remove(ctx.channel());
		logger.info("当前登陆总数:" + sessionManager.getChannelGroup().size());

		if (session.getTestUser() != null) {
			// 掉线状态发送给监控端
			sendToDevice.sendToMonitor(sendToDevice.loginResult(
					TPMSConsts.LOGIN_FAIL, session.getSeatNo() + ""));
			sessionManager.clearLoginUserInfo(session.getTestUser()
					.getHumanId());

			TestScoreVO vo = new TestScoreVO();
			vo.setHumanId(session.getTestUser().getHumanId());
			vo.setDeviceStatus(TestUser.DEVICE_STATUS_OFFLINE);
			vo.setScore(null);
			service.updScoreAndStatus(vo);
		}
		// 表情端断掉后发送给监控端
		if (TPMSConsts.CLIENT_FACE.equals(session.getClientType())) {
			sendToDevice.sendToMonitor(sendToDevice.sendDataToMonitor(
					TPMSConsts.DOWN_COMMAND_FACE_STATUS,
					"1",TPMSConsts.FACE_STATUS_OFFLINE));
			sendToDevice.sendToMonitor(sendToDevice.sendDataToMonitor(
					TPMSConsts.DOWN_COMMAND_FACE_STATUS,
					"2",TPMSConsts.FACE_STATUS_OFFLINE));
			sendToDevice.sendToMonitor(sendToDevice.sendDataToMonitor(
					TPMSConsts.DOWN_COMMAND_FACE_STATUS,
					"3",TPMSConsts.FACE_STATUS_OFFLINE));
			sendToDevice.sendToMonitor(sendToDevice.sendDataToMonitor(
					TPMSConsts.DOWN_COMMAND_FACE_STATUS,
					"4",TPMSConsts.FACE_STATUS_OFFLINE));
			sendToDevice.sendToMonitor(sendToDevice.sendDataToMonitor(
					TPMSConsts.DOWN_COMMAND_FACE_STATUS,
					"5",TPMSConsts.FACE_STATUS_OFFLINE));
			sendToDevice.sendToMonitor(sendToDevice.sendDataToMonitor(
					TPMSConsts.DOWN_COMMAND_FACE_STATUS,
					"6",TPMSConsts.FACE_STATUS_OFFLINE));
			
			sessionManager.clearFaceInfo();
		}

		ctx.channel().close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				// Session session =
				// this.sessionManager.removeBySessionId(Session.buildId(ctx.channel()));
				Session session = this.sessionManager.findPadBySessionId(ctx
						.channel().id().asLongText());
				logger.error("服务器主动断开连接:{}", session);
				ctx.close();
			}
		}
	}

	private void release(Object msg) {
		try {
			ReferenceCountUtil.release(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}