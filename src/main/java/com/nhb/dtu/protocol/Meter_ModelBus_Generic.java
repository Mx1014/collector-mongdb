package com.nhb.dtu.protocol;

import java.util.List;

import com.nhb.dtu.base.AbstractDevice;
import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.entity.DataElectricity3Phase;
import com.nhb.dtu.entity.ReceiptCollector;
import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.entity.ReceiptMeter;
import com.nhb.dtu.service.collector.ReceiptCollectorService;
import com.nhb.dtu.service.data.DataElectricity3PhaseService;
import com.nhb.dtu.service.data.DataElectricityService;
import com.nhb.dtu.service.meter.ReceiptMeterService;
import com.nhb.dtu.service.submeter.ReceiptDeviceService;
import com.nhb.utils.nhb_utils.context.SpringContextHolder;

public class Meter_ModelBus_Generic extends AbstractDevice {

	@Override
	public void buildWritingFrames() {

	}

	@Override
	public boolean analyzeFrame(byte[] frame) {
		return false;
	}

	@Override
	public void handleResult() {

	}

	@SuppressWarnings("rawtypes")
	public List doGetReceiptCicuirt() {
		List<ReceiptDevice> receiptDevices = getReceiptDeviceService().findByMeterId(receiptMeter.getId());
		return receiptDevices;
	}

	public ReceiptMeter getReceiptMeter(String meterId) {
		return getReceiptMeterService().findById(meterId);
	}

	public ReceiptCollector getReceiptCollector(String collectorId) {
		return getReceiptCollectorService().findById(collectorId);
	}

	public void saveDataToDataBase(DataElectricity dataElectricity, DataElectricity3Phase dataElectricity3Phase) {
		getDataElectricityService().save(dataElectricity);
		if (dataElectricity3Phase != null) {
			dataElectricity3Phase.setDataId(dataElectricity.getId());
			geDataElectricity3PhaseService().save(dataElectricity3Phase);
		}
	}

	private ReceiptDeviceService getReceiptDeviceService() {
		return (ReceiptDeviceService) SpringContextHolder.getBean("receiptDeviceService");
	}

	private ReceiptMeterService getReceiptMeterService() {
		return SpringContextHolder.getBean("receiptMeterService");
	}

	private ReceiptCollectorService getReceiptCollectorService() {
		return SpringContextHolder.getBean("receiptCollectorService");
	}

	private DataElectricityService getDataElectricityService() {
		return (DataElectricityService) SpringContextHolder.getBean("dataElectricityService");
	}

	private DataElectricity3PhaseService geDataElectricity3PhaseService() {
		return (DataElectricity3PhaseService) SpringContextHolder.getBean("dataElectricity3PhaseService");
	}

}
