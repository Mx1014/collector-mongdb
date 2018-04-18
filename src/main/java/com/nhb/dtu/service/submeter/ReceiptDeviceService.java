package com.nhb.dtu.service.submeter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.ReceiptDevice;
import com.nhb.dtu.mapper.submeter.ReceiptDeviceDao;

@Service
public class ReceiptDeviceService {

	@Autowired
	private ReceiptDeviceDao receiptDeviceDao;

	public ReceiptDevice findByMeterIdAndCircuitNo(String meterId, String circuitNo) {
		return receiptDeviceDao.findByMeterIdAndCircuitNo(meterId, circuitNo);
	}

	public List<ReceiptDevice> findByMeterId(String meterId) {
		return receiptDeviceDao.findByMeterId(meterId);
	}

	public ReceiptDevice findById(String deviceId) {
		if (receiptDeviceDao.findById(deviceId).equals(Optional.empty())) {
			return null;
		}
		return receiptDeviceDao.findById(deviceId).get();
	}

	public void delete(String id) {
		receiptDeviceDao.deleteById(id);
	}

	public List<ReceiptDevice> findAll() {
		return receiptDeviceDao.findAll();
	}

	public ReceiptDevice save(ReceiptDevice circuit) {
		return receiptDeviceDao.save(circuit);
	}

}
