package com.nhb.dtu.handler;

import java.util.Calendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.nhb.dtu.attribute.DtuContext;
import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.controller.DtuController;
import com.nhb.dtu.task.DataProcessor;
import com.nhb.dtu.utils.FrameUtils;
import com.nhb.utils.nhb_utils.common.StringUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

/**
 * @ClassName: DtuServerHandler
 * @Function: Handler处理类
 * @date: May 23, 2017 3:36:12 PM
 * @author sunlei
 * @version
 * @since JDK 1.8
 */
public class DtuServerHandler extends SimpleChannelInboundHandler<byte[]> {
	private static final Logger logger = LoggerFactory.getLogger(DtuServerHandler.class);
	/**
	 * 通道上下文信息
	 */
	private AttributeKey<DtuContext> attrDtuContext;

	/**
	 * DTU业务控制
	 */
	@Autowired
	private DtuController dtuController;

	/**
	 * 心跳周期
	 */
	private int heartbeat = ConfigBean.getHeartbeat();

	/**
	 * 任务开始前延时时间
	 */
	private long scheduleDelay = ConfigBean.getDelay();

	DataProcessor dataProcessor;

	long delay;

	/**
	 * 客户端连接后触发
	 * 
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelActive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.warn("channelActive被触发，已经有设备连接上采集软件");
		// 生成 唯一dtu 上下文 key
		attrDtuContext = AttributeKey.valueOf(String.valueOf(UUID.randomUUID()));
		// dtu 业务处理
		dtuController = new DtuController(attrDtuContext);
		dataProcessor = new DataProcessor(ctx, attrDtuContext);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.HOUR, 1);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		delay = (c.getTime().getTime() - System.currentTimeMillis()) / 1000;

		/**
		 * DTU定时采集任务
		 */
		ctx.executor().scheduleAtFixedRate(dataProcessor, scheduleDelay, heartbeat, TimeUnit.SECONDS);
	};

	/**
	 * channelInactive: 客户端断开连接后触发
	 * 
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		if (StringUtil.isNullOrEmpty(ctx.channel().attr(attrDtuContext).get().getDtuNo())) {
			logger.warn("设备注册失败！");
		} else {
			logger.warn(ctx.channel().attr(attrDtuContext).get().getDtuNo() + " : 编号设备主动断开连接!");
		}
		// ctx.executor().shutdownGracefully();
		// ctx.close();
	}

	/**
	 * channelRead0: Decoder发送数据后触发
	 * 
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext,
	 *      java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		logger.warn("channelRead0服务器接收到数据：" + FrameUtils.toString(msg));
		if ((msg[0] == (byte) 0x68) && (msg[1] == (byte) 0x01) && (msg[msg.length - 1] == (byte) 0x16)) {
			dtuController.saveRegisterInfo(ctx, msg);
		} else if ((msg[0] == (byte) 0x68) && (msg[1] == (byte) 0x02) && (msg[msg.length - 1] == (byte) 0x16)) {
			dtuController.heartbeat(ctx, msg);
		} else if ((msg[0] == (byte) 0x68) && (msg[1] == (byte) 0x03) && (msg[msg.length - 1] == (byte) 0x16)
				&& (msg[8] != (byte) 0x6a)) {
			dtuController.readDevice(ctx, msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if (StringUtil.isNullOrEmpty(ctx.channel().attr(attrDtuContext).get().getDtuNo())) {
			logger.info("数据异常！", cause.getMessage());
		}
		logger.info(ctx.channel().attr(attrDtuContext).get().getDtuNo() + "：未知异常！", cause.getMessage());
	}
}
