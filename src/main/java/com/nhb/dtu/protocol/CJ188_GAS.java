package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataFuelGas;
import com.nhb.dtu.entity.DataSwitch;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.enums.SwitchStatusEnum;
import com.nhb.dtu.service.data.DataFuelGasService;
import com.nhb.dtu.service.data.DataSwitchService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: CJ188_GAS
 * @Description: 188协议燃气表协议
 * @author XS guo
 * @date 2017年8月7日 上午11:02:04
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class CJ188_GAS extends AbstractDevice {

	private int[] address = new int[7]; // 0为最高位

	DataFuelGas dataFuelGas;

	Double flow;

	String status;

	public CJ188_GAS(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		for (int i = 0; i < address.length; i++) {
			address[i] = Integer.parseInt(receiptMeter.getMeterNo().substring(2 * i, 2 * i + 2), 16);
		}
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {

		int[] data = new int[16];
		// 起始帧
		data[0] = 0x68;
		// 气表 为30
		data[1] = 0x30;
		// 表号 14位
		data[2] = address[6];
		data[3] = address[5];
		data[4] = address[4];
		data[5] = address[3];
		data[6] = address[2];
		data[7] = address[1];
		data[8] = address[0];
		// 控制码
		data[9] = 0x01;
		// 数据长度
		data[10] = 0x03;
		// 数据标识
		data[11] = 0x90;
		data[12] = 0x1F;
		// SER序列号
		data[13] = 0x00;
		// CS 校验和
		data[14] = Verify.csSum(data[0] + data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12] + data[13]);
		// 结束帧
		data[15] = 0x16;

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);

	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		// 去除 00开头和 3个 FE 和 包头包尾
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}

		// TODO 检验和
		if (data[9] != 0x81) {
			return false;
		}

		// 处理数据
		flow = (double) (Integer.parseInt(Integer.toHexString(data[17])) * 100 * 100 * 100
				+ Integer.parseInt(Integer.toHexString(data[16])) * 100 * 100
				+ Integer.parseInt(Integer.toHexString(data[15])) * 100
				+ Integer.parseInt(Integer.toHexString(data[14]))) / 100.0;

		if (data[31] == 0x00) {
			status = SwitchStatusEnum.ON.getKey();
		} else {
			status = SwitchStatusEnum.OFF.getKey();
		}

		return true;
	}

	@Override
	public void handleResult() {
		ReceiptDevice circuit = getReceiptCircuitService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		dataFuelGas = new DataFuelGas();
		Date nowTime = new Date();
		dataFuelGas.setReadTime(nowTime);
		dataFuelGas.setId(UUID.randomUUID());
		dataFuelGas.setDeviceId(circuit.getId());
		dataFuelGas.setConsumption(flow);
		dataFuelGas.setStatus(status);
		getDataFuelGasService().save(dataFuelGas);

		DataSwitch dataSwitch = new DataSwitch();
		dataSwitch.setId(UUID.randomUUID());
		dataSwitch.setStatus(status);
		dataSwitch.setReadTime(nowTime);
		dataSwitch.setDeviceId(circuit.getId());
		getDataSwitchService().save(dataSwitch);

		SwitchStatus switchStatus = getSwitchStatusService().findById(circuit.getId());
		if (null == switchStatus) {
			switchStatus = new SwitchStatus();
			switchStatus.setDeviceId(circuit.getId());
		}
		switchStatus.setReadTime(nowTime);
		switchStatus.setStatus(status);
		getSwitchStatusService().save(switchStatus);

	}

	private DataFuelGasService getDataFuelGasService() {
		return SpringContextHolder.getBean("dataFuelGasService");
	}

	private ReceiptDeviceService getReceiptCircuitService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private SwitchStatusService getSwitchStatusService() {
		return SpringContextHolder.getBean("switchStatusService");
	}

	private DataSwitchService getDataSwitchService() {
		return SpringContextHolder.getBean("dataSwitchService");
	}

}
