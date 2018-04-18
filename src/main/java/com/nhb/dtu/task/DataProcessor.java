package com.nhb.dtu.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nhb.dtu.attribute.DtuContext;
import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.base.Device;
import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.init.InitModbusGenericProMap;
import com.nhb.dtu.protocol.AC_JLMK;
import com.nhb.dtu.protocol.AC_JLMK_STATUS;
import com.nhb.dtu.protocol.AirCtrlCommunication;
import com.nhb.dtu.protocol.CJ188_GAS;
import com.nhb.dtu.protocol.CJ188_WATER;
import com.nhb.dtu.protocol.CalibrationModbusV02;
import com.nhb.dtu.protocol.DC_JLMK;
import com.nhb.dtu.protocol.ElecOperateStatus;
import com.nhb.dtu.protocol.Meter4_02a_1;
import com.nhb.dtu.protocol.Meter4_02e;
import com.nhb.dtu.protocol.Meter_07;
import com.nhb.dtu.protocol.Meter_DTSY_Modbus2;
import com.nhb.dtu.protocol.Meter_MobilePhaseEnergy;
import com.nhb.dtu.protocol.NewElecOperaStatus;
import com.nhb.dtu.protocol.Temperature;
import com.nhb.dtu.service.collector.ReceiptCollectorService;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @ClassName: DataProcessor
 * @Function: DTU定时采集任务
 * @date: May 27, 2017 10:31:05 AM
 * 
 * @author sunlei
 * @version
 * @since JDK 1.8
 */
public class DataProcessor implements Runnable {
	private Logger logger = LoggerFactory.getLogger(DataProcessor.class);

	private ChannelHandlerContext ctx;

	private AttributeKey<DtuContext> attrDtuContext;

	private Map<String, Class<?>> modbusGenericProMap = InitModbusGenericProMap.getModbusGenericProMap();

	// 采集周期
	private Integer READPERIOD = ConfigBean.getReadPeriod();

	public DataProcessor(ChannelHandlerContext ctx, AttributeKey<DtuContext> attrDtuContext) {
		this.ctx = ctx;
		this.attrDtuContext = attrDtuContext;
	}

	public DataProcessor(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	DtuContext dtuContext;

	@Override
	public void run() {

		// 获取当前dtu的信息
		dtuContext = ctx.channel().attr(attrDtuContext).get();
		// 当前dtu context信息
		ChannelHandlerContext dtuCHC = dtuContext.getDtuCHC();
		if (dtuCHC == null || !dtuCHC.channel().isActive()) {
			return;
		}
		// 全部DTU分批处理
		if (dtuContext.getDelay() > 0) {
			dtuContext.setDelay(dtuContext.getDelay() - 1);
			return;
		}
		// 添加定时读取队列信息
		handlePrdQueue(dtuContext);
		// 远程控制队列
		handleCmdQueue(dtuContext);
		// 发送指令 读取数据
		writeDevice(dtuContext);
	}

	private void handlePrdQueue(DtuContext dtuContext) {
		// 获取当前设备,如果设备 不为空并且没有完成，返回 继续等待数据返回
		Device device = dtuContext.getCurrentDevice();
		if (device != null && !device.isComplete()) {
			return;
		}
		ConcurrentLinkedQueue<Device> prdQueue = dtuContext.getPrdQueue();
		// collectPeriod记录抄表时间间隔，如果配置文件中要求统一的采集间隔，则从配置文件中读取采集时间
		// 如果采集时间不统一，则分别从采集器的采集周期字段中读取采集周期
		long collectPeriod = 0L;
		ReceiptCollector collector = getReceiptCollectorService().findCollectorByNo(dtuContext.getDtuNo());
		if (null == collector) {
			logger.warn("{} not found", dtuContext.getDtuNo());
			return;
		}
		// 先从数据库配置 读取采集周期时间，如果数据库中没有配置，再从配置文件中同意获取
		if (null != collector.getCollectionFrequency()) {
			collectPeriod = collector.getCollectionFrequency();
		} else {
			collectPeriod = READPERIOD;
		}
		// 把时间设为 毫秒数
		collectPeriod *= 60000L;
		if (prdQueue.isEmpty() && System.currentTimeMillis() - dtuContext.getLastTime() > collectPeriod) {
			// 查询dtu下的电表
			List<ReceiptMeter> meters = getReceiptMeterService().findMetersByCollectorId(collector.getId());
			if (meters.isEmpty()) {
				logger.warn("{} is empty", dtuContext.getDtuNo());
				return;
			}
			// 校时用
			addCalibrations(prdQueue, collector, meters);
			// 组装帧
			addDevices(prdQueue, collector, meters);
			// 更新 采集时间，用于判断 是否到达采集周期
			dtuContext.setLastTime(System.currentTimeMillis());
		}
	}

	/**
	 * 
	 * @Title: handleCmdQueue @Description: 要处理的 远程控制命令 @return void @throws
	 */
	private void handleCmdQueue(DtuContext dtuContext) {
		Device device = dtuContext.getCurrentDevice();
		if (device != null && !device.isComplete()) {
			return;
		}
	}

	// 每天凌晨校时
	private void addCalibrations(ConcurrentLinkedQueue<Device> prdQueue, ReceiptCollector collector,
			List<ReceiptMeter> meters) {
		Date now = new Date();
		Date date = DateUtils.truncate(now, Calendar.DATE);
		if (now.getTime() - date.getTime() > READPERIOD * 60000) {
			return;
		}
		boolean calibrateModbusV02 = false;
		for (ReceiptMeter meter : meters) {
			if (meter.getProtocolType().equals("NHB_M4V02_0") || meter.getProtocolType().equals("NHB_M4V02_1")
					|| meter.getProtocolType().equals("NHB_M4V02_2") || meter.getProtocolType().equals("NHB_M4V02_3")
					|| meter.getProtocolType().equals("NHB_M4V02_4")) {
				calibrateModbusV02 = true;
			}
		}
		if (calibrateModbusV02) {
			CalibrationModbusV02 calibrationModbusV02 = new CalibrationModbusV02();
			calibrationModbusV02.setIgnoreResponse(true);
			prdQueue.add(calibrationModbusV02);
		}
	}

	private void addDevices(ConcurrentLinkedQueue<Device> prdQueue, ReceiptCollector collector,
			List<ReceiptMeter> meters) {
		// 如果当前设备 没有处理完成 不进行队列添加
		Device device = dtuContext.getCurrentDevice();
		if (device != null && !device.isComplete()) {
			return;
		}
		for (ReceiptMeter meter : meters) {
			switch (meter.getProtocolType()) {
			case "AC_JLMK":// 交流智能电表
				prdQueue.add(new AC_JLMK(collector, meter));
				prdQueue.add(new AC_JLMK_STATUS(collector, meter));
				break;
			case "DC_JLMK": // 直流计量模块
				prdQueue.add(new DC_JLMK(collector, meter));
				break;
			case "TEMPERATURE": // 温度传感器
				prdQueue.add(new Temperature(collector, meter));
				break;
			case "RECLOSER": // 智能电操（旧）
				prdQueue.add(new ElecOperateStatus(collector, meter));
				break;
			case "RECLOSER_NEW": // 智能电操（新）
				prdQueue.add(new NewElecOperaStatus(collector, meter));
				break;
			case "CJ188_WATER": // 188协议水表
				prdQueue.add(new CJ188_WATER(collector, meter));
				break;
			case "CJ188_GAS": // 188协议燃气表
				prdQueue.add(new CJ188_GAS(collector, meter));
				break;
			case "AIR_CTRL": // 空调控制器
				prdQueue.add(new AirCtrlCommunication(collector, meter));
				break;
			case "MODBUS_DTSY": // 三相预付费导轨表（Modbus协议）
				prdQueue.add(new Meter_DTSY_Modbus2(collector, meter));
				break;
			case "DLT645_2007": // 07协议表
				prdQueue.add(new Meter_07(collector, meter));
				break;
			case "NHB_M1V02_3": // 三相单路表
				prdQueue.add(new Meter_MobilePhaseEnergy(collector, meter));
				break;
			case "NHB_M4V02_4_1": // 02协议四路表 增加线电压的四路三相,
				prdQueue.add(new Meter4_02a_1(collector, meter));
				break;
			case "NHB_M4V02_0": // 三相四路表 做 12路单相表 使用协议
				prdQueue.add(new Meter4_02e(collector, meter));
				break;
			case "MODBUS_GENERIC": // Modbus通用协议
				try {
					if (modbusGenericProMap.containsKey(meter.getGenericProIdentifier())) {
						Class<?> clazz = modbusGenericProMap.get(meter.getGenericProIdentifier());
						Object obj = clazz.newInstance();
						AbstractDevice modbusDevice = (AbstractDevice) obj;
						modbusDevice.setReceiptCollector(collector);
						modbusDevice.setReceiptMeter(meter);
						modbusDevice.buildWritingFrames();
						prdQueue.add((Device) modbusDevice);
					}
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		}
	}

	/**
	 * write one frame every time
	 * 
	 * @param dtuContext
	 */
	private void writeDevice(DtuContext dtuContext) {
		Device device = nextDevice(dtuContext);
		if (device == null || device.isComplete()) {
			return;
		}
		byte[] frame = device.nextWritingFrame();
		if (frame == null) {
			return;
		}
		ctx.writeAndFlush(frame);
		if (device.isIgnoreResponse()) {
			device.processReadingFrame(new byte[] {});
		}
	}

	private Device nextDevice(DtuContext dtuContext) {
		Device device = dtuContext.getCurrentDevice();
		if (device != null && !device.isComplete()) {
			return device;
		}
		if (!dtuContext.getCmdQueue().isEmpty()) {
			device = dtuContext.getCmdQueue().poll();
		} else if (!dtuContext.getPrdQueue().isEmpty()) {
			device = dtuContext.getPrdQueue().poll();
		} else {
			device = null;
		}
		dtuContext.setCurrentDevice(device);
		return device;
	}

	private ReceiptCollectorService getReceiptCollectorService() {
		return SpringContextHolder.getBean("receiptCollectorService");
	}

	private ReceiptMeterService getReceiptMeterService() {
		return SpringContextHolder.getBean("receiptMeterService");
	}
}
