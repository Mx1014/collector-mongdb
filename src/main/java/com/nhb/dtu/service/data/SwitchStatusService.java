package com.nhb.dtu.service.data;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.SwitchStatus;
import com.nhb.dtu.mapper.data.SwitchStatusDao;

@Service
public class SwitchStatusService {
	@Autowired
	private SwitchStatusDao switchStatusDao;

	public SwitchStatus save(SwitchStatus switchStatus) {
		return switchStatusDao.save(switchStatus);
	}

	public SwitchStatus findById(String deviceId) {
		if (switchStatusDao.findById(deviceId).equals(Optional.empty())) {
			return null;
		}
		return switchStatusDao.findById(deviceId).get();
	}

	public void delete(String deviceId) {
		switchStatusDao.deleteById(deviceId);
	}

}
