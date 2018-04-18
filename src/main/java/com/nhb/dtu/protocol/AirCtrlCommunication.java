package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.UUID;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.AirCtrlComm;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.enums.AirCtrlAlarmStatusEnum;
import com.nhb.dtu.service.data.AirCtrlCommService;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.data.SwitchStatusService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: AirCtrlCommunication
 * @Description: 空调控制器设备通信协议
 * @author XS guo
 * @date 2017年7月21日 下午1:42:28
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class AirCtrlCommunication extends AbstractDevice {

	// 空调开关状态
	private String AirStatus;
	// 制冷状态异常警告
	private String refrigerationEx;
	// 制热状态异常告警
	private String heatingEx;
	// 电源告警
	private String powerEx;
	// 电流传感器故障
	private String currsensorEx;
	// 回风温度传感器故障
	private String returnairEx;
	// 出风温度传感器故障
	private String deliveryairEx;
	// A相电压
	private Integer powerA;
	// A相电流
	private Double currentA;
	// A相电压
	private Integer powerB;
	// A相电流
	private Double currentB;
	// A相电压
	private Integer powerC;
	// A相电流
	private Double currentC;
	// 送风温度
	private Integer supplyTemp;
	// 回风温度
	private Integer returnTemp;
	// 当前总电能
	private Double currElec;
	// 昨日消耗总电能
	@SuppressWarnings("unused")
	private Double lastElec;

	public AirCtrlCommunication(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		buildAirCtrlSwitchStatusFrame();
		buildAirCtrlAlarmFrame();
		buildAirCtrlAnalogDataFrame();
		buildAirCtrlElecFrame();
	}

	/**
	 * 
	 * @Title: buildAirCtrlElecFrame @Description: 获取历史数据-电能 @return
	 *         void @throws
	 */
	private void buildAirCtrlElecFrame() {
		int[] data = new int[18];
		// 起始位置SOI 1位
		data[0] = 0x7E;
		// 通讯版本号 2位
		data[1] = 0x31;
		data[2] = 0x30;
		// 表地址 2位
		String meterNo = Integer.toHexString(Integer.parseInt(receiptMeter.getMeterNo())).toUpperCase();
		if (meterNo.length() < 2) {
			meterNo = "0" + meterNo;
		}
		char[] meterChar = meterNo.toCharArray();
		data[3] = (byte) meterChar[0] & 0xFF;
		data[4] = (byte) meterChar[1] & 0xFF;
		// cid1
		data[5] = 0x36;
		data[6] = 0x36;
		// cid2
		data[7] = 0x38;
		data[8] = 0x31;
		// length 校验
		Integer lenId = Integer.parseInt("00", 16);
		int[] lchlSum = Verify.lchlSum(lenId);
		data[9] = lchlSum[0] & 0xFF;
		data[10] = lchlSum[1] & 0xFF;
		data[11] = lchlSum[2] & 0xFF;
		data[12] = lchlSum[3] & 0xFF;
		// CHKSUM 校验和码
		int[] chkSum = Verify.chkSum(data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12]);
		data[13] = chkSum[0];
		data[14] = chkSum[1];
		data[15] = chkSum[2];
		data[16] = chkSum[3];
		// 结束码 1位
		data[17] = 0x0D;

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);

	}

	/**
	 * 
	 * @Title: buildAirCtrlAnalogDataFrame @Description: 模拟量量化数据 @return
	 *         void @throws
	 */
	private void buildAirCtrlAnalogDataFrame() {
		int[] data = new int[18];
		// 起始位置SOI 1位
		data[0] = 0x7E;
		// 通讯版本号 2位
		data[1] = 0x31;
		data[2] = 0x30;
		// 表地址 2位
		String meterNo = Integer.toHexString(Integer.parseInt(receiptMeter.getMeterNo())).toUpperCase();
		if (meterNo.length() < 2) {
			meterNo = "0" + meterNo;
		}
		char[] meterChar = meterNo.toCharArray();
		data[3] = (byte) meterChar[0] & 0xFF;
		data[4] = (byte) meterChar[1] & 0xFF;
		// cid1
		data[5] = 0x36;
		data[6] = 0x36;
		// cid2
		data[7] = 0x34;
		data[8] = 0x32;
		// length 校验
		Integer lenId = Integer.parseInt("00", 16);
		int[] lchlSum = Verify.lchlSum(lenId);
		data[9] = lchlSum[0] & 0xFF;
		data[10] = lchlSum[1] & 0xFF;
		data[11] = lchlSum[2] & 0xFF;
		data[12] = lchlSum[3] & 0xFF;
		// CHKSUM 校验和码
		int[] chkSum = Verify.chkSum(data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12]);
		data[13] = chkSum[0];
		data[14] = chkSum[1];
		data[15] = chkSum[2];
		data[16] = chkSum[3];

		// 结束码 1位
		data[17] = 0x0D;

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	/**
	 * 
	 * @Title: buildAirCtrlSwitchStatusFrame @Description: 空调控制器-开关状态 @return
	 *         void @throws
	 */
	private void buildAirCtrlSwitchStatusFrame() {
		int[] data = new int[18];
		// 起始位置SOI 1位
		data[0] = 0x7E;
		// 通讯版本号 2位
		data[1] = 0x31;
		data[2] = 0x30;
		// 表地址 2位
		String meterNo = Integer.toHexString(Integer.parseInt(receiptMeter.getMeterNo())).toUpperCase();
		if (meterNo.length() < 2) {
			meterNo = "0" + meterNo;
		}
		char[] meterChar = meterNo.toCharArray();
		data[3] = (byte) meterChar[0] & 0xFF;
		data[4] = (byte) meterChar[1] & 0xFF;
		// cid1
		data[5] = 0x36;
		data[6] = 0x36;
		// cid2
		data[7] = 0x34;
		data[8] = 0x33;
		// length 校验
		Integer lenId = Integer.parseInt("00", 16);
		int[] lchlSum = Verify.lchlSum(lenId);
		data[9] = lchlSum[0] & 0xFF;
		data[10] = lchlSum[1] & 0xFF;
		data[11] = lchlSum[2] & 0xFF;
		data[12] = lchlSum[3] & 0xFF;
		// CHKSUM 校验和码
		int[] chkSum = Verify.chkSum(data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12]);
		data[13] = chkSum[0];
		data[14] = chkSum[1];
		data[15] = chkSum[2];
		data[16] = chkSum[3];
		// 结束码 1位
		data[17] = 0x0D;

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	/**
	 * 
	 * @Title: buildAirCtrlAlarmFrame @Description: 空调控制器-告警信息 @return
	 *         void @throws
	 */
	private void buildAirCtrlAlarmFrame() {
		int[] data = new int[18];
		// 起始位置SOI 1位
		data[0] = 0x7E;
		// 通讯版本号 2位
		data[1] = 0x31;
		data[2] = 0x30;
		// 表地址 2位
		String meterNo = Integer.toHexString(Integer.parseInt(receiptMeter.getMeterNo())).toUpperCase();
		if (meterNo.length() < 2) {
			meterNo = "0" + meterNo;
		}
		char[] meterChar = meterNo.toCharArray();
		data[3] = (byte) meterChar[0] & 0xFF;
		data[4] = (byte) meterChar[1] & 0xFF;
		// cid1
		data[5] = 0x36;
		data[6] = 0x36;
		// cid2
		data[7] = 0x34;
		data[8] = 0x34;
		// length 校验
		Integer lenId = Integer.parseInt("00", 16);
		int[] lchlSum = Verify.lchlSum(lenId);
		data[9] = lchlSum[0] & 0xFF;
		data[10] = lchlSum[1] & 0xFF;
		data[11] = lchlSum[2] & 0xFF;
		data[12] = lchlSum[3] & 0xFF;
		// CHKSUM 校验和码
		int[] chkSum = Verify.chkSum(data[1] + data[2] + data[3] + data[4] + data[5] + data[6] + data[7] + data[8]
				+ data[9] + data[10] + data[11] + data[12]);
		data[13] = chkSum[0];
		data[14] = chkSum[1];
		data[15] = chkSum[2];
		data[16] = chkSum[3];
		// 结束码 1位
		data[17] = 0x0D;

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
		// 空调开关状态
		if (data.length == 24) {
			// TODO length、lchsum检验
			// 解析 RTN 为 00 ，返回数据成功
			String rtn = (char) data[7] + "" + (char) data[8];
			if (!rtn.equals("00")) {
				return false;
			}
			String status = (char) data[15] + "" + (char) data[16];
			if (status.equals("01")) {
				AirStatus = "ON";
			} else if (status.equals("00")) {
				AirStatus = "OFF";
			}
		} else if (data.length == 32) {
			// TODO length、lchsum检验
			// 解析 RTN 为 00 ，返回数据成功
			String rtn = (char) data[7] + "" + (char) data[8];
			if (!rtn.equals("00")) {
				return false;
			}
			// 制冷状态 告警解析
			refrigerationEx = analyzeData((char) data[15] + "" + (char) data[16]);
			// 制热状态解析
			heatingEx = analyzeData((char) data[17] + "" + (char) data[18]);
			// 电源状态解析
			powerEx = analyzeData((char) data[19] + "" + (char) data[20]);
			// 电流传感器状态解析
			currsensorEx = analyzeData((char) data[21] + "" + (char) data[22]);
			// 回风温度传感器
			returnairEx = analyzeData((char) data[23] + "" + (char) data[24]);
			// 出风温度传感器
			deliveryairEx = analyzeData((char) data[25] + "" + (char) data[26]);
		} else if (data.length == 50) {
			// TODO length、lchsum检验
			// 解析 RTN 为 00 ，返回数据成功
			String rtn = (char) data[7] + "" + (char) data[8];
			if (!rtn.equals("00")) {
				return false;
			}
			String judgePowerA = (char) data[13] + "" + (char) data[14] + "" + (char) data[15] + "" + (char) data[16];
			if (judgePowerA.equals("2020")) {
				powerA = null;
				currentA = null;
			} else {
				powerA = Integer.parseInt(
						(char) data[13] + "" + (char) data[14] + "" + (char) data[15] + "" + (char) data[16], 16);
				currentA = ((double) (Integer.parseInt(
						(char) data[25] + "" + (char) data[26] + "" + (char) data[27] + "" + (char) data[28], 16)))
						/ 100;
			}
			String judgePowerB = (char) data[17] + "" + (char) data[18] + "" + (char) data[19] + "" + (char) data[20];
			if (judgePowerB.equals("2020")) {
				powerB = null;
				currentB = null;
			} else {
				powerB = Integer.parseInt(
						(char) data[17] + "" + (char) data[18] + "" + (char) data[19] + "" + (char) data[20], 16);
				currentB = ((double) (Integer.parseInt(
						(char) data[29] + "" + (char) data[30] + "" + (char) data[31] + "" + (char) data[32], 16)))
						/ 100;
			}
			String judgePowerC = (char) data[21] + "" + (char) data[22] + "" + (char) data[23] + "" + (char) data[24];
			if (judgePowerC.equals("2020")) {
				powerC = null;
				currentC = null;
			} else {
				powerC = Integer.parseInt(
						(char) data[21] + "" + (char) data[22] + "" + (char) data[23] + "" + (char) data[24], 16);
				currentC = ((double) (Integer.parseInt(
						(char) data[33] + "" + (char) data[34] + "" + (char) data[35] + "" + (char) data[36], 16)))
						/ 100;
			}
			supplyTemp = Integer
					.parseInt((char) data[37] + "" + (char) data[38] + "" + (char) data[39] + "" + (char) data[40], 16);
			returnTemp = Integer
					.parseInt((char) data[41] + "" + (char) data[42] + "" + (char) data[43] + "" + (char) data[44], 16);
		} else if (data.length == 34) {
			// TODO length、lchsum检验
			// 解析 RTN 为 00 ，返回数据成功
			String rtn = (char) data[7] + "" + (char) data[8];
			if (!rtn.equals("00")) {
				return false;
			}
			lastElec = ((double) (Integer.parseInt(
					(char) data[13] + "" + (char) data[14] + "" + (char) data[15] + "" + (char) data[16] + ""
							+ (char) data[17] + "" + (char) data[18] + "" + (char) data[19] + "" + (char) data[20],
					16))) / 100;
			currElec = ((double) (Integer.parseInt(
					(char) data[21] + "" + (char) data[22] + "" + (char) data[23] + "" + (char) data[24] + ""
							+ (char) data[25] + "" + (char) data[26] + "" + (char) data[27] + "" + (char) data[28],
					16))) / 100;

		}
		return true;
	}

	/**
	 * 
	 * @Title: analyzeData @Description: 提取方法 @return String @throws
	 */
	private String analyzeData(String data) {
		String returnValue = null;
		switch (data) {
		case "00":
			returnValue = AirCtrlAlarmStatusEnum.NORMAL.getKey();
			break;
		case "01":
			returnValue = AirCtrlAlarmStatusEnum.BELOWLOWER.getKey();
			break;
		case "02":
			returnValue = AirCtrlAlarmStatusEnum.HIGHLOWER.getKey();
			break;
		case "20":
			returnValue = AirCtrlAlarmStatusEnum.UNDETECTION.getKey();
			break;
		case "F0":
			returnValue = AirCtrlAlarmStatusEnum.BREAKDOWN.getKey();
			break;
		}
		return returnValue;
	}

	@Override
	public void handleResult() {
		ReceiptDevice device = getReceiptDeviceService().findByMeterIdAndCircuitNo(receiptMeter.getId(), "1");
		AirCtrlComm entity = new AirCtrlComm();
		Date nowTime = new Date();
		entity.setId(UUID.randomUUID());
		entity.setCircuitId(device.getId());
		entity.setReadTime(nowTime);
		entity.setStatus(AirStatus);
		entity.setCurrsensorEx(currsensorEx);
		entity.setDeliveryairEx(deliveryairEx);
		entity.setHeatingEx(heatingEx);
		entity.setPowerEx(powerEx);
		entity.setRefrigerationEx(refrigerationEx);
		entity.setReturnairEx(returnairEx);
		entity.setPowerA(powerA);
		entity.setCurrentA(currentA);
		entity.setPowerB(powerB);
		entity.setCurrentB(currentB);
		entity.setPowerC(powerC);
		entity.setCurrentC(currentC);
		entity.setSupplyTemp(supplyTemp);
		entity.setReturnTemp(returnTemp);
		getAirCtrlCummService().save(entity);
		DataElectricity electricity = new DataElectricity();
		electricity.setId(UUID.randomUUID());
		electricity.setDeviceId(device.getId());
		electricity.setReadTime(nowTime);
		electricity.setKwh(currElec);
		if (null != currentA) {
			electricity.setCurrentA(currentA);
			electricity.setVoltageA((double) powerA);
		}
		if (null != currentB) {
			electricity.setCurrentB(currentB);
			electricity.setVoltageB((double) powerB);
		}
		if (null != currentC) {
			electricity.setCurrentC(currentC);
			electricity.setVoltageC((double) powerC);
		}
		getElectricityService().save(electricity);

		SwitchStatus switchStatus = getSwitchStatusService().findById(device.getId());
		if (null == switchStatus) {
			switchStatus = new SwitchStatus();
			switchStatus.setDeviceId(device.getId());
		}
		switchStatus.setReadTime(nowTime);
		switchStatus.setStatus(AirStatus);
		getSwitchStatusService().save(switchStatus);

	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private AirCtrlCommService getAirCtrlCummService() {
		return SpringContextHolder.getBean("airCtrlCommService");
	}

	private DataElectricityService getElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}

	private SwitchStatusService getSwitchStatusService() {
		return SpringContextHolder.getBean("switchStatusService");
	}

}
