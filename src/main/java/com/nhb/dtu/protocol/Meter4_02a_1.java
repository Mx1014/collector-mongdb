package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.entity.DataElecOil;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.ElectricityType;
import com.nhb.dtu.service.data.DataElecOilService;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: Meter4_02a_1
 * @Description: 三相四路表全功能协议
 * @author XS guo
 * @date 2017年8月30日 上午9:18:16
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Meter4_02a_1 extends Meter4_02 {

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}

	private DataElecOilService getDataElecOilService() {
		return SpringContextHolder.getBean("dataElecOilService");
	}

	private boolean rateFromDataBaseEnabled = ConfigBean.getRateFromDataBaseEnabled();

	public Meter4_02a_1(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		super(receiptCollector, receiptMeter);
	}

	@Override
	public void handleResult() {
		Date now = new Date();
		List<ReceiptDevice> receiptDeviceList = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
		for (int i = 0; i < 4 && i < receiptDeviceList.size(); i++) {
			if (rateFromDataBaseEnabled) {
				if (receiptDeviceList.get(i) == null) {
					return;
				}
				if (receiptDeviceList.get(i).getVoltageRatio() != null) {
					Ubb = receiptDeviceList.get(i).getVoltageRatio();
				}
				if (receiptDeviceList.get(i).getCurrentRatio() != null) {
					Ibb = receiptDeviceList.get(i).getCurrentRatio();
				}
			}
			resultMutiplyRateThreePhase(i);
			DataElectricity dataElectricity = new DataElectricity();
			dataElectricity.setId(UUID.randomUUID());
			dataElectricity.setDeviceId(receiptCollector.getCollectorNo() + receiptMeter.getMeterNo()
					+ receiptDeviceList.get(i).getCircuitNo());
			dataElectricity.setReadTime(now);
			dataElectricity.setElectricityType(ElectricityType.AC_THREE.name());
			dataElectricity.setFrequency(frequency);
			dataElectricity.setVoltageA(voltageA);
			dataElectricity.setVoltageB(voltageB);
			dataElectricity.setVoltageC(voltageC);
			dataElectricity.setVoltageAB(voltageAB);
			dataElectricity.setVoltageBC(voltageBC);
			dataElectricity.setVoltageCA(voltageAC);
			dataElectricity.setCurrentA(currentA[i]);
			dataElectricity.setCurrentB(currentB[i]);
			dataElectricity.setCurrentC(currentC[i]);
			dataElectricity.setKva(kva[i]);
			dataElectricity.setKvaA(kvaA[i]);
			dataElectricity.setKvaB(kvaB[i]);
			dataElectricity.setKvaC(kvaC[i]);
			dataElectricity.setKw(kw[i]);
			dataElectricity.setKwA(kwA[i]);
			dataElectricity.setKwB(kwB[i]);
			dataElectricity.setKwC(kwC[i]);
			dataElectricity.setKvar(kvar[i]);
			dataElectricity.setKvarA(kvarA[i]);
			dataElectricity.setKvarB(kvarB[i]);
			dataElectricity.setKvarC(kvarC[i]);
			dataElectricity.setKwh(kwh[i]);
			dataElectricity.setKwhForward(kwhForward[i]);
			dataElectricity.setKwhReverse(kwhReverse[i]);
			dataElectricity.setKvarh1(kvarh1[i]);
			dataElectricity.setKvarh2(kvarh2[i]);
			dataElectricity.setPowerFactor(powerFactor[i]);
			dataElectricity.setPowerFactorA(powerFactorA[i]);
			dataElectricity.setPowerFactorB(powerFactorB[i]);
			dataElectricity.setPowerFactorC(powerFactorC[i]);
			getDataElectricityService().save(dataElectricity);
			DataElecOil deo = new DataElecOil();
			deo.setDataId(dataElectricity.getId());
			deo.setKvarh1(oilCommonkvarh1);
			deo.setKvarh2(oilCommonkvarh2);
			deo.setKwhFor(oilCommonKwhForward);
			deo.setKwhRev(oilCommonKwhReverse);
			deo.setKwhTotal(oilCommonKwh);
			getDataElecOilService().save(deo);
		}
	}

	private void resultMutiplyRateThreePhase(int index) {
		voltageA *= Ubb;
		voltageB *= Ubb;
		voltageC *= Ubb;
		voltageAB *= Ubb;
		voltageAC *= Ubb;
		voltageBC *= Ubb;
		oilCommonKwh *= Ubb * Ibb;
		oilCommonKwhForward *= Ubb * Ibb;
		oilCommonKwhReverse *= Ubb * Ibb;
		oilCommonkvarh1 *= Ubb * Ibb;
		oilCommonkvarh1 *= Ubb * Ibb;
		currentA[index] *= Ibb;
		currentB[index] *= Ibb;
		currentC[index] *= Ibb;
		kva[index] *= Ubb * Ibb;
		kvaA[index] *= Ubb * Ibb;
		kvaB[index] *= Ubb * Ibb;
		kvaC[index] *= Ubb * Ibb;
		kvar[index] *= Ubb * Ibb;
		kvarA[index] *= Ubb * Ibb;
		kvarB[index] *= Ubb * Ibb;
		kvarC[index] *= Ubb * Ibb;
		kw[index] *= Ubb * Ibb;
		kwA[index] *= Ubb * Ibb;
		kwB[index] *= Ubb * Ibb;
		kwC[index] *= Ubb * Ibb;
		kwh[index] *= Ubb * Ibb;
		kwhForward[index] *= Ubb * Ibb;
		kwhReverse[index] *= Ubb * Ibb;
		kvarh1[index] *= Ubb * Ibb;
		kvarh2[index] *= Ubb * Ibb;

	}
}
