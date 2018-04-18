package com.nhb.dtu.protocol;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.util.CollectionUtils;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.DataElectricity3Phase;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.ElectricityType;
import com.nhb.dtu.service.data.DataElectricity3PhaseService;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: Meter_MobilePhaseEnergy
 * @Description: 三相单路表全功能协议
 * @author XS guo
 * @date 2017年6月30日 下午3:12:52
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Meter_MobilePhaseEnergy extends AbstractDevice {

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}

	private DataElectricity3PhaseService getDataElectricity3PhaseService() {
		return SpringContextHolder.getBean("dataElectricity3PhaseService");
	}

	private boolean rateFromDataBaseEnabled = ConfigBean.getRateFromDataBaseEnabled();

	private String time;
	private double frequency;
	private double voltageA;
	private double voltageB;
	private double voltageC;
	private double currentA;
	private double currentB;
	private double currentC;
	private double kw;
	private double kwA;
	private double kwB;
	private double kwC;
	private double kvar;
	private double kvarA;
	private double kvarB;
	private double kvarC;
	private double kva;
	private double kvaA;
	private double kvaB;
	private double kvaC;
	private double powerFactor;
	private double powerFactorA;
	private double powerFactorB;
	private double powerFactorC;
	private double kwh;
	private double kwhForward;
	private double kwhReverse;
	private double kvarh1;
	private double kvarh2;
	private double voltAB;
	private double voltBC;
	private double voltAC;

	private double kwhA;
	private double kwhForwardA;
	private double kwhReverseA;
	private double kwhB;
	private double kwhForwardB;
	private double kwhReverseB;
	private double kwhC;
	private double kwhForwardC;
	private double kwhReverseC;
	private double kvarh1A;
	private double kvarh2A;
	private double kvarh1B;
	private double kvarh2B;
	private double kvarh1C;
	private double kvarh2C;

	private double Ubb = 1;
	private double Ibb = 1;

	public Meter_MobilePhaseEnergy(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		this.receiptCollector = receiptCollector;
		this.receiptMeter = receiptMeter;
		buildWritingFrames();
	}

	@Override
	public void buildWritingFrames() {
		makeFrame();
		makeFrameRate();
		makeFramePhaseEnergy();
	}

	private void makeFrame() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x10;
		data[3] = 0x00;
		data[4] = 0x00;
		data[5] = 0x29;

		int[] crc = CRC.calculateCRC(data, 6);
		data[6] = crc[0];
		data[7] = crc[1];

		byte[] frame = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			frame[i] = (byte) data[i];
		}
		writingFrames.add(frame);
	}

	private void makeFrameRate() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x00;
		data[3] = 0x06;
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

	private void makeFramePhaseEnergy() {
		int[] data = new int[8];
		data[0] = Integer.parseInt(receiptMeter.getMeterNo());
		data[1] = 0x03;
		data[2] = 0x90;
		data[3] = 0x00;
		data[4] = 0x00;
		data[5] = 0x1E;

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

		if (readingFrames.size() != 3)
			return false;
		int[] data = new int[frame.length];
		for (int i = 0; i < data.length; i++) {
			data[i] = frame[i] & 0xFF;
		}

		if (!CRC.isValid(data))
			return false;
		int meterNo = Integer.parseInt(receiptMeter.getMeterNo());
		if (meterNo != data[0]) {
			return false;
		}
		if (data[2] == 0x52) {
			time = "20";
			for (int i = 5; i < 10; i++) {
				if (data[i] < 0x10)
					time += "0" + Integer.toHexString(data[i]);
				else
					time += Integer.toHexString(data[i]);
			}
			frequency = (data[11] * 256 + data[12]) / 100.0;
			voltageA = (data[13] * 256 + data[14]) / 10.0;
			voltageB = (data[15] * 256 + data[16]) / 10.0;
			voltageC = (data[17] * 256 + data[18]) / 10.0;
			currentA = (data[19] * 256 + data[20]) / 100.0;
			currentB = (data[21] * 256 + data[22]) / 100.0;
			currentC = (data[23] * 256 + data[24]) / 100.0;
			kw = (data[25] * 256 + data[26]) / 100.0;
			kwA = (data[27] * 256 + data[28]) / 100.0;
			kwB = (data[29] * 256 + data[30]) / 100.0;
			kwC = (data[31] * 256 + data[32]) / 100.0;
			kvar = (data[33] * 256 + data[34]) / 100.0;
			kvarA = (data[35] * 256 + data[36]) / 100.0;
			kvarB = (data[37] * 256 + data[38]) / 100.0;
			kvarC = (data[39] * 256 + data[40]) / 100.0;
			kva = (data[41] * 256 + data[42]) / 100.0;
			kvaA = (data[43] * 256 + data[44]) / 100.0;
			kvaB = (data[45] * 256 + data[46]) / 100.0;
			kvaC = (data[47] * 256 + data[48]) / 100.0;
			powerFactor = (data[49] * 256 + data[50]) / 100.0;
			powerFactorA = (data[51] * 256 + data[52]) / 100.0;
			powerFactorB = (data[53] * 256 + data[54]) / 100.0;
			powerFactorC = (data[55] * 256 + data[56]) / 100.0;
			kwh = (data[59] * 256 * 256 * 256 + data[60] * 256 * 256 + data[61] * 256 + data[62]) / 100.0;
			kwhForward = (data[63] * 256 * 256 * 256 + data[64] * 256 * 256 + data[65] * 256 + data[66]) / 100.0;
			kwhReverse = (data[67] * 256 * 256 * 256 + data[68] * 256 * 256 + data[69] * 256 + data[70]) / 100.0;
			kvarh1 = (data[71] * 256 * 256 * 256 + data[72] * 256 * 256 + data[73] * 256 + data[74]) / 100.0;
			kvarh2 = (data[75] * 256 * 256 * 256 + data[76] * 256 * 256 + data[77] * 256 + data[78]) / 100.0;
			voltAB = (data[79] * 256 + data[80]) / 10.0;
			voltBC = (data[81] * 256 + data[82]) / 10.0;
			voltAC = (data[83] * 256 + data[84]) / 10.0;
		} else if (data[2] == 0x04) {
			Ubb = data[3] * 256 + data[4];
			Ibb = data[5] * 256 + data[6];
		} else if (data[2] == 0x3C) {
			kwhA = (data[3] * 256 * 256 * 256 + data[4] * 256 * 256 + data[5] * 256 + data[6]) / 100.0;
			kwhForwardA = (data[7] * 256 * 256 * 256 + data[8] * 256 * 256 + data[9] * 256 + data[10]) / 100.0;
			kwhReverseA = (data[11] * 256 * 256 * 256 + data[12] * 256 * 256 + data[13] * 256 + data[14]) / 100.0;
			kvarh1A = (data[15] * 256 * 256 * 256 + data[16] * 256 * 256 + data[17] * 256 + data[18]) / 100.0;
			kvarh2A = (data[19] * 256 * 256 * 256 + data[20] * 256 * 256 + data[21] * 256 + data[22]) / 100.0;
			kwhB = (data[23] * 256 * 256 * 256 + data[24] * 256 * 256 + data[25] * 256 + data[26]) / 100.0;
			kwhForwardB = (data[27] * 256 * 256 * 256 + data[28] * 256 * 256 + data[29] * 256 + data[30]) / 100.0;
			kwhReverseB = (data[31] * 256 * 256 * 256 + data[32] * 256 * 256 + data[33] * 256 + data[34]) / 100.0;
			kvarh1B = (data[35] * 256 * 256 * 256 + data[36] * 256 * 256 + data[37] * 256 + data[38]) / 100.0;
			kvarh2B = (data[39] * 256 * 256 * 256 + data[40] * 256 * 256 + data[41] * 256 + data[42]) / 100.0;
			kwhC = (data[43] * 256 * 256 * 256 + data[44] * 256 * 256 + data[45] * 256 + data[46]) / 100.0;
			kwhForwardC = (data[47] * 256 * 256 * 256 + data[48] * 256 * 256 + data[49] * 256 + data[50]) / 100.0;
			kwhReverseC = (data[51] * 256 * 256 * 256 + data[52] * 256 * 256 + data[53] * 256 + data[54]) / 100.0;
			kvarh1C = (data[55] * 256 * 256 * 256 + data[56] * 256 * 256 + data[57] * 256 + data[58]) / 100.0;
			kvarh2C = (data[59] * 256 * 256 * 256 + data[60] * 256 * 256 + data[61] * 256 + data[62]) / 100.0;
		}
		return true;

	}

	@Override
	public void handleResult() {
		try {
			List<ReceiptDevice> receiptDevices = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
			if (CollectionUtils.isEmpty(receiptDevices)) {
				return;
			}

			for (ReceiptDevice receiptDevice : receiptDevices) {

				if (rateFromDataBaseEnabled) {
					if (receiptDevice.getVoltageRatio() != null) {
						Ubb = receiptDevice.getVoltageRatio();
					}
					if (receiptDevice.getCurrentRatio() != null) {
						Ibb = receiptDevice.getCurrentRatio();
					}
				}

				resultMutiplyRate();
				DataElectricity dataElectricity = new DataElectricity();
				dataElectricity.setId(UUID.randomUUID());
				dataElectricity.setDeviceId(receiptDevice.getId());
				dataElectricity.setCurrentA(currentA);
				dataElectricity.setCurrentB(currentB);
				dataElectricity.setCurrentC(currentC);
				dataElectricity.setVoltageA(voltageA);
				dataElectricity.setVoltageB(voltageB);
				dataElectricity.setVoltageC(voltageC);
				dataElectricity.setElectricityType(ElectricityType.AC_THREE.name());
				dataElectricity.setFrequency(frequency);
				dataElectricity.setKva(kva);
				dataElectricity.setKvaA(kvaA);
				dataElectricity.setKvaB(kvaB);
				dataElectricity.setKvaC(kvaC);
				dataElectricity.setKvar(kvar);
				dataElectricity.setKvarA(kvarA);
				dataElectricity.setKvarB(kvarB);
				dataElectricity.setKvarC(kvarC);
				dataElectricity.setKw(kw);
				dataElectricity.setKwA(kwA);
				dataElectricity.setKwB(kwB);
				dataElectricity.setKwC(kwC);
				dataElectricity.setPowerFactor(powerFactor);
				dataElectricity.setPowerFactorA(powerFactorA);
				dataElectricity.setPowerFactorB(powerFactorB);
				dataElectricity.setPowerFactorC(powerFactorC);
				dataElectricity.setKwh(kwh);
				dataElectricity.setKwhForward(kwhForward);
				dataElectricity.setKwhReverse(kwhReverse);
				dataElectricity.setKvarh1(kvarh1);
				dataElectricity.setKvarh2(kvarh2);
				dataElectricity.setVoltageAB(voltAB);
				dataElectricity.setVoltageBC(voltBC);
				dataElectricity.setVoltageCA(voltAC);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
				dataElectricity.setReadTime(sdf.parse(time));
				DataElectricity electricity = getDataElectricityService().save(dataElectricity);

				DataElectricity3Phase dataElectricity3Phase = new DataElectricity3Phase();
				dataElectricity3Phase.setDataId(electricity.getId());
				dataElectricity3Phase.setKvarh1(kvarh1);
				dataElectricity3Phase.setKvarh1A(kvarh1A);
				dataElectricity3Phase.setKvarh1B(kvarh1B);
				dataElectricity3Phase.setKvarh1C(kvarh1C);
				dataElectricity3Phase.setKvarh2(kvarh2);
				dataElectricity3Phase.setKvarh2A(kvarh2A);
				dataElectricity3Phase.setKvarh2B(kvarh2B);
				dataElectricity3Phase.setKvarh2C(kvarh2C);
				dataElectricity3Phase.setKwh(kwh);
				dataElectricity3Phase.setKwhA(kwhA);
				dataElectricity3Phase.setKwhB(kwhB);
				dataElectricity3Phase.setKwhC(kwhC);
				dataElectricity3Phase.setKwhForward(kwhForward);
				dataElectricity3Phase.setKwhForwardA(kwhForwardA);
				dataElectricity3Phase.setKwhForwardB(kwhForwardB);
				dataElectricity3Phase.setKwhForwardC(kwhForwardC);
				dataElectricity3Phase.setKwhReverse(kwhReverse);
				dataElectricity3Phase.setKwhReverseA(kwhReverseA);
				dataElectricity3Phase.setKwhReverseB(kwhReverseB);
				dataElectricity3Phase.setKwhReverseC(kwhReverseC);
				getDataElectricity3PhaseService().save(dataElectricity3Phase);
			}
		} catch (Exception ex) {
			logger.error("save Device of dtuNo:" + receiptCollector.getCollectorNo() + " meterNo:"
					+ receiptMeter.getMeterNo() + " error!", ex);
		}
	}

	private void resultMutiplyRate() {
		voltageA *= Ubb;
		voltageB *= Ubb;
		voltageC *= Ubb;
		currentA *= Ibb;
		currentB *= Ibb;
		currentC *= Ibb;
		kw = kw * Ubb * Ibb;
		kwA = kwA * Ubb * Ibb;
		kwB = kwB * Ubb * Ibb;
		kwC = kwC * Ubb * Ibb;
		kva = kva * Ubb * Ibb;
		kvaA = kvaA * Ubb * Ibb;
		kvaB = kvaB * Ubb * Ibb;
		kvaC = kvaC * Ubb * Ibb;
		kvar = kvar * Ubb * Ibb;
		kvarA = kvarA * Ubb * Ibb;
		kvarB = kvarB * Ubb * Ibb;
		kvarC = kvarC * Ubb * Ibb;
		kwh = kwh * Ubb * Ibb;
		kwhForward = kwhForward * Ubb * Ibb;
		kwhReverse = kwhReverse * Ubb * Ibb;
		kvarh1 = kvarh1 * Ubb * Ibb;
		kvarh2 = kvarh2 * Ubb * Ibb;
		kwhA = kwhA * Ubb * Ibb;
		kwhForwardA = kwhForwardA * Ubb * Ibb;
		kwhReverseA = kwhReverseA * Ubb * Ibb;
		kvarh1A = kvarh1A * Ubb * Ibb;
		kvarh2A = kvarh2A * Ubb * Ibb;
		kwhB = kwhB * Ubb * Ibb;
		kwhForwardB = kwhForwardB * Ubb * Ibb;
		kwhReverseB = kwhReverseB * Ubb * Ibb;
		kvarh1B = kvarh1B * Ubb * Ibb;
		kvarh2B = kvarh2B * Ubb * Ibb;
		kwhC = kwhC * Ubb * Ibb;
		kwhForwardC = kwhForwardC * Ubb * Ibb;
		kwhReverseC = kwhReverseC * Ubb * Ibb;
		kvarh1C = kvarh1C * Ubb * Ibb;
		kvarh2C = kvarh2C * Ubb * Ibb;

		DecimalFormat df = new DecimalFormat("######0.00");
		voltageA = Double.parseDouble(df.format(voltageA));
		voltageB = Double.parseDouble(df.format(voltageB));
		voltageC = Double.parseDouble(df.format(voltageC));
		currentA = Double.parseDouble(df.format(currentA));
		currentB = Double.parseDouble(df.format(currentB));
		currentC = Double.parseDouble(df.format(currentC));
		kw = Double.parseDouble(df.format(kw));
		kwA = Double.parseDouble(df.format(kwA));
		kwB = Double.parseDouble(df.format(kwB));
		kwC = Double.parseDouble(df.format(kwC));
		kva = Double.parseDouble(df.format(kva));
		kvaA = Double.parseDouble(df.format(kvaA));
		kvaB = Double.parseDouble(df.format(kvaB));
		kvaC = Double.parseDouble(df.format(kvaC));
		kvar = Double.parseDouble(df.format(kvar));
		kvarA = Double.parseDouble(df.format(kvarA));
		kvarB = Double.parseDouble(df.format(kvarB));
		kvarC = Double.parseDouble(df.format(kvarC));
		kwh = Double.parseDouble(df.format(kwh));
		kwhForward = Double.parseDouble(df.format(kwhForward));
		kwhReverse = Double.parseDouble(df.format(kwhReverse));
		kvarh1 = Double.parseDouble(df.format(kvarh1));
		kvarh2 = Double.parseDouble(df.format(kvarh2));
		kwhA = Double.parseDouble(df.format(kwhA));
		kwhForwardA = Double.parseDouble(df.format(kwhForwardA));
		kwhReverseA = Double.parseDouble(df.format(kwhReverseA));
		kvarh1A = Double.parseDouble(df.format(kvarh1A));
		kvarh2A = Double.parseDouble(df.format(kvarh2A));
		kwhB = Double.parseDouble(df.format(kwhB));
		kwhForwardB = Double.parseDouble(df.format(kwhForwardB));
		kwhReverseB = Double.parseDouble(df.format(kwhReverseB));
		kvarh1B = Double.parseDouble(df.format(kvarh1B));
		kvarh2B = Double.parseDouble(df.format(kvarh2B));
		kwhC = Double.parseDouble(df.format(kwhC));
		kwhForwardC = Double.parseDouble(df.format(kwhForwardC));
		kwhReverseC = Double.parseDouble(df.format(kwhReverseC));
		kvarh1C = Double.parseDouble(df.format(kvarh1C));
		kvarh2C = Double.parseDouble(df.format(kvarh2C));
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

}
