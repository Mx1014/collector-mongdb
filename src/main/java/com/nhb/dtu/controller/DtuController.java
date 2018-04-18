/** 
 * Project Name:MeterSocketservApp 
 * File Name:DtuController.java 
 * Package Name:com.xhb.sockserv.collector 
 * Date:Mar 9, 20179:12:26 AM 
 * Copyright (c) 2017, lorisun@live.com All Rights Reserved. 
 * 
*/
package com.nhb.dtu.controller;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.dtu.attribute.DtuContext;
import com.nhb.dtu.base.Device;
import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.entity.CollectorStatus;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.init.DtuContextMap;
import com.nhb.dtu.protocol.MeterXinhongboDsm;
import com.nhb.dtu.service.collector.CollectorStatusService;
import com.nhb.dtu.service.collector.ReceiptCollectorService;
import com.nhb.dtu.utils.FrameUtils;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @ClassName:DtuController
 * @Function: DTU信息处理
 * @Date: Mar 9, 2017 9:12:26 AM
 * @author sunlei
 * @version
 * @since JDK 1.8
 * @see
 */
public class DtuController {

	private static final Logger logger = LoggerFactory.getLogger(DtuController.class);

	/**
	 * 通道上下文属性key
	 */
	private AttributeKey<DtuContext> attrDtuContext;

	/**
	 * 通道上下文属性
	 */
	private DtuContext dtuContext;

	/**
	 * 采集器状态实体
	 */
	private CollectorStatus collectorStatus;

	/**
	 * 服务器端口
	 */
	private int serverPort = ConfigBean.serverPort;

	public DtuController(AttributeKey<DtuContext> attrDtuContext) {
		this.attrDtuContext = attrDtuContext;
	}

	/**
	 * heartbeat: 处理dtu的心跳报文
	 * 
	 * @author sunlei
	 * @param ctx
	 * @param msg
	 * @since JDK 1.8
	 */
	public void heartbeat(ChannelHandlerContext ctx, byte[] msg) {
		String dtuNo = FrameUtils.getDtuNo(msg);
		if (StringUtils.isEmpty(dtuNo)) {
			return;
		}
		ctx.writeAndFlush(new byte[] { (byte) 0x02 });
		updateDtuStatus(dtuNo, ctx);
	}

	/**
	 * readDevice: 处理上传的数据报文
	 * 
	 * @author sunlei
	 * @param ctx
	 * @param msg
	 * @since JDK 1.8
	 */
	public void readDevice(ChannelHandlerContext ctx, byte[] msg) {
		String dtuNo = FrameUtils.getDtuNo(msg);
		// 更新DTU状态
		updateDtuStatus(dtuNo, ctx);

		DtuContext dtuContext = ctx.channel().attr(attrDtuContext).get();
		if (dtuContext == null) {
			return;
		}
		if (msg.length >= 10 && (msg[8] == (byte) 0x88) && (msg[9] == (byte) 0x05)) {
			MeterXinhongboDsm meterXinhongboDsm = new MeterXinhongboDsm();
			meterXinhongboDsm.processReadingFrame(msg);
			byte[] bytes = new byte[15];
			bytes[0] = msg[8];
			bytes[1] = msg[9];
			bytes[2] = (byte) 0x00;
			bytes[3] = (byte) 0x01;
			System.arraycopy(msg, 14, bytes, 4, 7);
			if (meterXinhongboDsm.isComplete()) {
				bytes[11] = (byte) 0x01;
			} else {
				bytes[11] = (byte) 0x00;
			}
			int sum = 0;
			for (int i = 1; i < bytes.length - 3; i++) {
				sum = sum + (bytes[i] & 0xFF);
			}
			bytes[12] = (byte) (sum % 256);
			bytes[13] = (byte) (sum / 256);
			bytes[14] = (byte) 0x16;
			ctx.writeAndFlush(bytes);
			return;
		}

		Device device = dtuContext.getCurrentDevice();
		ctx.channel().attr(attrDtuContext).set(dtuContext);
		if (device == null || device.isComplete()) {
			return;
		}
		device.processReadingFrame(msg);
	}

	/**
	 * saveRegisterInfo: 处理采集器（dtu）注册信息
	 * 
	 * @author sunlei
	 * @param ctx
	 * @param msg
	 * @since JDK 1.8
	 */
	public void saveRegisterInfo(ChannelHandlerContext ctx, byte[] msg) {
		String dtuNo = FrameUtils.getDtuNo(msg);
		if (StringUtils.isEmpty(dtuNo)) {
			return;
		}
		dtuContext = new DtuContext();
		dtuContext.setDtuNo(dtuNo);
		dtuContext.setDtuCHC(ctx);
		dtuContext.setDelay(new Random().nextInt(10) + 1);
		// 封装管道上下文属性
		ctx.channel().attr(attrDtuContext).set(dtuContext);
		logger.warn(ctx.channel().attr(attrDtuContext).get().getDtuNo() + " : 编号设备注册成功!");
		ctx.writeAndFlush(new byte[] { (byte) 0x01 });
		// 将 dtu信息 保存到缓存中
		DtuContextMap.getInstance().put(dtuNo, dtuContext);

		updateDtuStatus(dtuNo, ctx);
	}

	/**
	 * updateDtuStatus: 更新采集器在线状态
	 * 
	 * @author sunlei
	 * @param dtuNo
	 * @param ctx
	 * @since JDK 1.8
	 */
	private void updateDtuStatus(String dtuNo, ChannelHandlerContext ctx) {
		ReceiptCollector collector = getReceiptCollectorService().findCollectorByNo(dtuNo);
		if (Objects.isNull(collector)) {
			logger.error(dtuNo + "：编号DTU未配置到回单表");
			return;
		}
		collectorStatus = getCollectorStatusService().get(collector.getId());
		if (null == collectorStatus) {
			collectorStatus = new CollectorStatus();
			collectorStatus.setCollectorId(collector.getId());
		}
		collectorStatus.setCollectorNo(collector.getCollectorNo());
		collectorStatus.setCollectorName(collector.getName());
		// 获取客户端IP信息
		InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
		collectorStatus.setCollectorIp(address.getAddress().getHostAddress());
		collectorStatus.setCollectorPort(address.getPort());
		// 获取本地IP信息
		try {
			collectorStatus.setServerIp(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		collectorStatus.setServerPort(serverPort);
		collectorStatus.setActiveTime(new Date());
		getCollectorStatusService().save(collectorStatus);
	}

	private ReceiptCollectorService getReceiptCollectorService() {
		return SpringContextHolder.getBean("receiptCollectorService");
	}

	private CollectorStatusService getCollectorStatusService() {
		return SpringContextHolder.getBean("collectorStatusService");
	}

}
