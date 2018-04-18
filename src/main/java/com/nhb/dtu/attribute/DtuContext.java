package com.nhb.dtu.attribute;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.nhb.dtu.base.Device;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @ClassName: DtuContext
 * @Function: DTU属性上下文
 * @date: May 22, 2017 11:29:15 AM
 * 
 * @author sunlei
 * @version
 * @since JDK 1.8
 */
public class DtuContext {
	/**
	 * DTU编号
	 */
	private String dtuNo;

	private ChannelHandlerContext dtuCHC;

	private ChannelHandlerContext cmdCHC;

	private ConcurrentLinkedQueue<Device> prdQueue = new ConcurrentLinkedQueue<Device>();

	private ConcurrentLinkedQueue<Device> cmdQueue = new ConcurrentLinkedQueue<Device>();

	private Device currentDevice;

	private long lastTime;

	private int delay;

	public String getDtuNo() {
		return dtuNo;
	}

	public void setDtuNo(String dtuNo) {
		this.dtuNo = dtuNo;
	}

	public ChannelHandlerContext getDtuCHC() {
		return dtuCHC;
	}

	public void setDtuCHC(ChannelHandlerContext dtuCHC) {
		this.dtuCHC = dtuCHC;
	}

	public ChannelHandlerContext getCmdCHC() {
		return cmdCHC;
	}

	public void setCmdCHC(ChannelHandlerContext cmdCHC) {
		this.cmdCHC = cmdCHC;
	}

	public ConcurrentLinkedQueue<Device> getPrdQueue() {
		return prdQueue;
	}

	public void setPrdQueue(ConcurrentLinkedQueue<Device> prdQueue) {
		this.prdQueue = prdQueue;
	}

	public ConcurrentLinkedQueue<Device> getCmdQueue() {
		return cmdQueue;
	}

	public void setCmdQueue(ConcurrentLinkedQueue<Device> cmdQueue) {
		this.cmdQueue = cmdQueue;
	}

	public Device getCurrentDevice() {
		return currentDevice;
	}

	public void setCurrentDevice(Device currentDevice) {
		this.currentDevice = currentDevice;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

}
