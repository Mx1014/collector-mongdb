package com.nhb.dtu.controller;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.attribute.DtuContext;
import com.nhb.dtu.base.Device;
import com.nhb.dtu.command.CommandRequest;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.AirConditionCtrlEnum;
import com.nhb.dtu.enums.ProtrocolTypeEnum;
import com.nhb.dtu.enums.SwitchStatusEnum;
import com.nhb.dtu.init.DtuContextMap;
import com.nhb.dtu.protocol.AC_JLMK_OFF;
import com.nhb.dtu.protocol.AC_JLMK_ON;
import com.nhb.dtu.protocol.AirCtrlAlarmParam;
import com.nhb.dtu.protocol.AirCtrlSwitchOff;
import com.nhb.dtu.protocol.AirCtrlSwitchOn;
import com.nhb.dtu.protocol.CJ188_GAS_SwitchOff;
import com.nhb.dtu.protocol.CJ188_GAS_SwitchOn;
import com.nhb.dtu.protocol.CJ188_WATER_SwitchOff;
import com.nhb.dtu.protocol.CJ188_WATER_SwitchOn;
import com.nhb.dtu.protocol.ElecOperateSwitchOff;
import com.nhb.dtu.protocol.ElecOperateSwitchOn;
import com.nhb.dtu.protocol.Meter_DTSY_SwitchOff;
import com.nhb.dtu.protocol.Meter_DTSY_SwitchOn;
import com.nhb.dtu.protocol.NewElecOperaSwitchOff;
import com.nhb.dtu.protocol.NewElecOperaSwitchOn;
import com.nhb.dtu.protocol.RecloseCount;
import com.nhb.dtu.service.collector.ReceiptCollectorService;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.common.StringUtil;

/**
 * 
 * @ClassName: RemoteCtrl
 * @Description: 远程控制入口-添加命令到队列中
 * @author XS guo
 * @date 2017年8月8日 下午7:08:46
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

@Service("remoteCtrl")
public class RemoteCtrl {

	@Autowired
	private ReceiptDeviceService receiptDeviceService;

	@Autowired
	private ReceiptMeterService receiptMeterService;

	@Autowired
	private ReceiptCollectorService receiptCollectorService;

	/**
	 * 
	 * @Title: addCmdCommand @Description: 添加远程控制命令到对应的DTUContext下 @return
	 *         void @throws
	 */
	public void addCmdCommand(CommandRequest request) {
		if (null == request || null == request.getCircuitId() || StringUtil.isNullOrEmpty(request.getType())) {
			return;
		}
		ReceiptDevice circuit = receiptDeviceService.findById(request.getCircuitId());
		if (null == circuit) {
			return;
		}
		ReceiptMeter meter = receiptMeterService.findById(circuit.getMeterId());
		if (null == meter) {
			return;
		}
		ReceiptCollector collector = receiptCollectorService.findById(meter.getCollectorId());

		// 获取当前 DtuContext
		DtuContext dtuContext = DtuContextMap.getInstance().get(collector.getCollectorNo());

		if (dtuContext == null || dtuContext.getDtuCHC() == null || !dtuContext.getDtuCHC().channel().isActive()) {
			return;
		}
		if (dtuContext.getCmdCHC() != null) {
			return;
		}
		// 获取当前 dtu中的远程控制命令
		ConcurrentLinkedQueue<Device> cmdQueue = dtuContext.getCmdQueue();

		// 空调控制器
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.AIR_CTRL.getKey())) {
			// 开机
			if (request.getType().equals(AirConditionCtrlEnum.SWITCHON.getKey())
					|| request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new AirCtrlSwitchOn(collector, meter, circuit));
			}
			// 关机
			if (request.getType().equals(AirConditionCtrlEnum.SWITCHOFF.getKey())
					|| request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new AirCtrlSwitchOff(collector, meter, circuit));
			}
			// 更新参数
			if (request.getType().equals(AirConditionCtrlEnum.AIRPARAM.getKey())) {
				cmdQueue.add(new AirCtrlAlarmParam(collector, meter, circuit));
			}
		}
		// 188 协议燃气表
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.CJ188_GAS.getKey())) {
			// 开阀
			if (request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new CJ188_GAS_SwitchOn(collector, meter, circuit));
			}
			// 关阀
			if (request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new CJ188_GAS_SwitchOff(collector, meter, circuit));
			}
		}
		// 188 协议水表
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.CJ188_WATER.getKey())) {
			if (request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new CJ188_WATER_SwitchOn(collector, meter, circuit));
			}
			if (request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new CJ188_WATER_SwitchOff(collector, meter, circuit));
			}
		}
		// 预付费电表
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.MODBUS_DTSY.getKey())) {
			if (request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new Meter_DTSY_SwitchOn(collector, meter, circuit));
			}
			if (request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new Meter_DTSY_SwitchOff(collector, meter, circuit));
			}
		}
		// 智能电操
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.RECLOSER.getKey())) {
			// 合闸
			if (request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new ElecOperateSwitchOn(collector, meter, circuit));
			}
			// 分闸
			if (request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new ElecOperateSwitchOff(collector, meter, circuit));
			}
		}
		// 智能电操（新）
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.RECLOSER_NEW.getKey())) {
			if (request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new NewElecOperaSwitchOn(collector, meter, circuit));
			}
			if (request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new NewElecOperaSwitchOff(collector, meter, circuit));
			}
			if (request.getType().equals("RECLOSE")) {
				cmdQueue.add(new RecloseCount(collector, meter, circuit));
			}
		}
		// 铁塔卡扣表
		if (meter.getProtocolType().equals(ProtrocolTypeEnum.AC_JLMK.getKey())) {
			if (request.getType().equals(SwitchStatusEnum.ON.getKey())) {
				cmdQueue.add(new AC_JLMK_ON(collector, meter, circuit));
			}
			if (request.getType().equals(SwitchStatusEnum.OFF.getKey())) {
				cmdQueue.add(new AC_JLMK_OFF(collector, meter, circuit));
			}
		}
	}
}
