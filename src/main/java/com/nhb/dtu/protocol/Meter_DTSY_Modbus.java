package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataDtsy;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.service.data.DataDtsyService;
import com.nhb.dtu.service.data.DataElectricityService;
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
public class Meter_DTSY_Modbus extends AbstractDevice {

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
	// 电表状态字2
	protected String statusWord2;
	// 當前縂有功電能
	protected double dataElectricity;

	/**
	 * 
	 * Title:Meter_DTSY_Modbus Description:构造方法
	 * 
	 * @param receiptCollector
	 * @param receiptMeter
	 */
	public Meter_DTSY_Modbus(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		makeFrame();
		makeFrameElec();
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

	private void makeFrame() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x11;
		data[3] = 0x00;
		data[4] = 0x00;
		data[5] = 0x15;

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
		if (readingFrames.size() != 2) {
			return false;
		}

		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}
		if (!CRC.isValid(data)) {
			return false;
		}
		if (data[2] == 0x04) {
			dataElectricity = (data[3] * 256 * 256 * 256 + data[4] * 256 * 256 + data[5] * 256 + data[6]) / 100.0;
		} else if (data[2] == 0x2a) {
			alarmPowerLevel1 = (data[3] << 8) + data[4];
			alarmPowerLevel2 = data[5] * 256 + data[6];
			loadThreshold = (data[7] * 256 + data[8]) / 100.0;
			sellNum = data[9] * 256 + data[10];
			cumulativePower = (data[11] * 256 * 256 * 256 + data[12] * 256 * 256 + data[13] * 256 + data[14]) / 100.0;
			surplusPower = (data[15] * 256 * 256 * 256 + data[16] * 256 * 256 + data[17] * 256 + data[18]) / 100.0;
			totalCumulativePower = (data[19] * 256 * 256 * 256 + data[20] * 256 * 256 + data[21] * 256 + data[22])
					/ 100.0;
			overdrawPower = data[23] * 256 + data[24];
			cornerPower = data[25] * 256 + data[26];
			lastPurchasePower = (data[27] * 256 * 256 * 256 + data[28] * 256 * 256 + data[29] * 256 + data[30]) / 100.0;
			zeroPower = (data[31] * 256 * 256 * 256 + data[32] * 256 * 256 + data[33] * 256 + data[34]) / 100.0;
			illegalTimes = data[35] * 256 + data[36];
			loadThresholdTimes = data[37] * 256 + data[38];
			malignantLoadThreshold = data[39] * 256 + data[40];
			malignantLoadTimes = data[41] * 256 + data[42];
			statusWord2 = String.valueOf(data[43] * 256 + data[44]);
		}

		return true;
	}

	@Override
	public void handleResult() {
		ReceiptDevice receiptDevice = getReceiptDeviceService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		Date nowTime = new Date();
		DataElectricity electricity = new DataElectricity();
		electricity.setId(UUID.randomUUID());
		electricity.setDeviceId(
				receiptCollector.getCollectorNo() + receiptMeter.getMeterNo() + receiptDevice.getCircuitNo());
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
		dtsy.setStatusWord2(statusWord2);
		dtsy.setSurplusPower(surplusPower);
		dtsy.setTotalCumulativePower(totalCumulativePower);
		dtsy.setZeroPower(zeroPower);
		dtsy.setReadTime(nowTime);
		getDataDtsyService().save(dtsy);
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

}
