package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataDtsy;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.DataSwitch;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.enums.SwitchStatusEnum;
import com.nhb.dtu.service.data.DataDtsyService;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.data.DataSwitchService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: Meter_DTSY_Modbus
 * @Description: 外购-三相预付费电能表Modbus协议
 * @author XS guo
 * @date 2017年7月11日 上午9:42:04
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Meter_DTSY_Modbus2 extends AbstractDevice {

	// 一级报警电量
	protected Integer alarmPowerLevel1;
	// 二级报警电量
	protected Integer alarmPowerLevel2;
	// 过负荷门限
	protected double loadThreshold;
	// 售电次数
	protected Integer sellNum;
	// 累计电量
	protected double cumulativePower;
	// 剩余电量
	protected double surplusPower;
	// 总累计电量
	protected double totalCumulativePower;
	// 透支电量
	protected Integer overdrawPower;
	// 囤积电量
	protected Integer cornerPower;
	// 最近一次购电量
	protected double lastPurchasePower;
	// 过零电量
	protected double zeroPower;
	// 非法卡使用次数
	protected Integer illegalTimes;
	// 过负荷次数
	protected Integer loadThresholdTimes;
	// 恶性负载门限
	protected Integer malignantLoadThreshold;
	// 恶性负载次数
	protected Integer malignantLoadTimes;
	// 當前縂有功電能
	protected double dataElectricity;

	protected String status;

	/**
	 * 
	 * Title:Meter_DTSY_Modbus Description:构造方法
	 * 
	 * @param receiptCollector
	 * @param receiptMeter
	 */
	public Meter_DTSY_Modbus2(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		makeFrame1();
		makeFrame2();
		makeFrameElec();
		makeFrameStatus();
	}

	private void makeFrameStatus() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x11;
		data[3] = 0x14;
		data[4] = 0x00;
		data[5] = 0x01;

		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrame2() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x10;
		data[3] = 0x11;
		data[4] = 0x00;
		data[5] = 0x05;

		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrameElec() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x00;
		data[3] = 0x63;
		data[4] = 0x00;
		data[5] = 0x02;
		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrame1() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x10;
		data[3] = 0x00;
		data[4] = 0x00;
		data[5] = 0x10;

		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		if (readingFrames.size() != 4) {
			return false;
		}

		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}
		if (!CRC.isValid(data)) {
			return false;
		}
		if (data[2] == 0x02) {
			if (data[4] == 0x00) {
				status = SwitchStatusEnum.ON.getKey();
			} else if (data[4] == 0x01) {
				status = SwitchStatusEnum.OFF.getKey();
			}
		}
		if (data[2] == 0x04) {
			dataElectricity = (data[3] * 256 * 256 * 256 + data[4] * 256 * 256 + data[5] * 256 + data[6]) / 100.0;
		} else if (data[2] == 0x0a) {
			illegalTimes = Integer.parseInt(Integer.toHexString(data[3])) * 100
					+ Integer.parseInt(Integer.toHexString(data[4]));
			loadThresholdTimes = Integer.parseInt(Integer.toHexString(data[5])) * 100
					+ Integer.parseInt(Integer.toHexString(data[6]));
			malignantLoadThreshold = data[7] * 256 * 256 * 256 + data[8] * 256 * 256 + data[9] * 256 + data[10];
			malignantLoadTimes = data[11] * 256 + data[12];
		} else if (data[2] == 0x20) {
			alarmPowerLevel1 = Integer.parseInt(Integer.toHexString(data[3])) * 100
					+ Integer.parseInt(Integer.toHexString(data[4]));
			alarmPowerLevel2 = Integer.parseInt(Integer.toHexString(data[5])) * 100
					+ Integer.parseInt(Integer.toHexString(data[6]));
			loadThreshold = (Integer.parseInt(Integer.toHexString(data[7])) * 100
					+ Integer.parseInt(Integer.toHexString(data[8]))) / 100.0;
			sellNum = Integer.parseInt(Integer.toHexString(data[9])) * 100
					+ Integer.parseInt(Integer.toHexString(data[10]));
			cumulativePower = (data[11] * 100 * 100 * 100 + data[12] * 100 * 100 + data[13] * 100 + data[14]) / 100.0;
			surplusPower = (Integer.parseInt(Integer.toHexString(data[15])) * 100 * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[16])) * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[17])) * 100
					+ Integer.parseInt(Integer.toHexString(data[18]))) / 100.0;
			totalCumulativePower = (Integer.parseInt(Integer.toHexString(data[19])) * 100 * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[20])) * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[21])) * 100
					+ Integer.parseInt(Integer.toHexString(data[22]))) / 100.0;
			overdrawPower = Integer.parseInt(Integer.toHexString(data[23])) * 100
					+ Integer.parseInt(Integer.toHexString(data[24]));
			cornerPower = Integer.parseInt(Integer.toHexString(data[25])) * 100
					+ Integer.parseInt(Integer.toHexString(data[26]));
			lastPurchasePower = (Integer.parseInt(Integer.toHexString(data[27])) * 100 * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[28])) * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[29])) * 100
					+ Integer.parseInt(Integer.toHexString(data[30]))) / 100.0;
			zeroPower = (Integer.parseInt(Integer.toHexString(data[31])) * 100 * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[32])) * 100 * 100
					+ Integer.parseInt(Integer.toHexString(data[33])) * 100
					+ Integer.parseInt(Integer.toHexString(data[34]))) / 100.0;
		}

		return true;
	}

	@Override
	public void handleResult() {
		ReceiptDevice receiptDevice = getReceiptDeviceService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		Date nowTime = new Date();
		DataElectricity electricity = new DataElectricity();
		electricity.setId(UUID.randomUUID());
		electricity.setDeviceId(receiptDevice.getId());
		electricity.setKwh(dataElectricity);
		electricity.setReadTime(nowTime);
		getDataElectricityService().save(electricity);
		DataDtsy dtsy = new DataDtsy();
		dtsy.setData_id(electricity.getId());
		dtsy.setAlarmPowerLevel1(alarmPowerLevel1);
		dtsy.setAlarmPowerLevel2(alarmPowerLevel2);
		dtsy.setCornerPower(cornerPower);
		dtsy.setCumulativePower(cumulativePower);
		dtsy.setIllegalTimes(illegalTimes);
		dtsy.setLastPurchasePower(lastPurchasePower);
		dtsy.setLoadThreshold(loadThreshold);
		dtsy.setLoadThresholdTimes(loadThresholdTimes);
		dtsy.setMalignantLoadThreshold(malignantLoadThreshold);
		dtsy.setMalignantLoadTimes(malignantLoadTimes);
		dtsy.setOverdrawPower(overdrawPower);
		dtsy.setSellNum(sellNum);
		dtsy.setSurplusPower(surplusPower);
		dtsy.setTotalCumulativePower(totalCumulativePower);
		dtsy.setZeroPower(zeroPower);
		dtsy.setReadTime(nowTime);
		getDataDtsyService().save(dtsy);

		DataSwitch dataSwitch = new DataSwitch();
		dataSwitch.setId(UUID.randomUUID());
		dataSwitch.setStatus(status);
		dataSwitch.setReadTime(nowTime);
		dataSwitch.setDeviceId(receiptDevice.getId());
		getDataSwitchService().save(dataSwitch);

		SwitchStatus switchStatus = getSwitchStatusService().findById(receiptDevice.getId());

		if (null == switchStatus) {
			switchStatus = new SwitchStatus();
			switchStatus.setDeviceId(receiptDevice.getId());
		}
		switchStatus.setReadTime(nowTime);
		switchStatus.setStatus(status);
		getSwitchStatusService().save(switchStatus);

	}

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}

	private DataDtsyService getDataDtsyService() {
		return SpringContextHolder.getBean("dataDtsyService");
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private DataSwitchService getDataSwitchService() {
		return SpringContextHolder.getBean("dataSwitchService");
	}

	private SwitchStatusService getSwitchStatusService() {
		return SpringContextHolder.getBean("switchStatusService");
	}
}
