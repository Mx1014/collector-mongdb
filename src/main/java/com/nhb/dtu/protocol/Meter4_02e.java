package com.nhb.dtu.protocol;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.nhb.dtu.config.ConfigBean;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.enums.ElectricityType;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

/**
 * 
 * @ClassName: Meter4_02e
 * @Description: 三相四路表 做 12路单相表使用
 * @author XS guo
 * @date 2017年8月30日 上午9:45:58
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class Meter4_02e extends Meter4_02 {

	private boolean rateFromDataBaseEnabled = ConfigBean.getRateFromDataBaseEnabled();

	public Meter4_02e(ReceiptCollector receiptCollector, ReceiptMeter receiptMeter) {
		super(receiptCollector, receiptMeter);
	}

	@Override
	public void handleResult() {
		Date now = new Date();
		List<ReceiptDevice> receiptDeviceList = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
		for (int i = 0; i < 12 && i < receiptDeviceList.size(); i++) {
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
			resultMutiplyRate(i);
			DataElectricity dataElectricity = new DataElectricity();
			dataElectricity.setId(UUID.randomUUID());
			dataElectricity.setDeviceId(receiptDeviceList.get(i).getId());
			dataElectricity.setReadTime(now);
			dataElectricity.setElectricityType(ElectricityType.AC_SINGLE.toString());
			dataElectricity.setFrequency(frequency);
			dataElectricity.setVoltage(voltageSP[i]);
			dataElectricity.setCurrent(currentSP[i]);
			dataElectricity.setKva(kvaSP[i]);
			dataElectricity.setKw(kwSP[i]);
			dataElectricity.setKvar(kvarSP[i]);
			dataElectricity.setPowerFactor(powerFactorSP[i]);
			dataElectricity.setKwh(kwhSP[i]);
			dataElectricity.setKwhForward(kwhForwardSP[i]);
			dataElectricity.setKwhReverse(kwhReverseSP[i]);
			dataElectricity.setKvarh1(kvarh1SP[i]);
			dataElectricity.setKvarh2(kvarh2SP[i]);
			getDataElectricityService().save(dataElectricity);
		}
	}

	private void resultMutiplyRate(int index) {
		voltageSP[index] *= Ubb;
		currentSP[index] *= Ibb;
		kvaSP[index] *= Ubb * Ibb;
		kwSP[index] *= Ubb * Ibb;
		kvarSP[index] *= Ubb * Ibb;
		kwhSP[index] *= Ubb * Ibb;
		kwhForwardSP[index] *= Ubb * Ibb;
		kwhReverseSP[index] *= Ubb * Ibb;
		kvarh1SP[index] *= Ubb * Ibb;
		kvarh2SP[index] *= Ubb * Ibb;
	}

	private DataElectricityService getDataElectricityService() {
		return SpringContextHolder.getBean("dataElectricityService");
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return SpringContextHolder.getBean("receiptDeviceService");
	}
}
