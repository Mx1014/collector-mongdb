package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataSwitch;
import com.nhb.dtu.mapper.data.DataSwitchDao;

@Service
public class DataSwitchService {

	@Autowired
	private DataSwitchDao dataSwitchDao;

	public DataSwitch save(DataSwitch dataSwitch) {
		return dataSwitchDao.save(dataSwitch);
	}

}
