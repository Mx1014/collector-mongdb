package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataElectricity3Phase;
import com.nhb.dtu.mapper.data.DataElectricity3PhaseDao;

@Service
public class DataElectricity3PhaseService {
	@Autowired
	private DataElectricity3PhaseDao dataElectricity3PhaseDao;

	public void save(DataElectricity3Phase dataElectricity3Phase) {
		dataElectricity3PhaseDao.save(dataElectricity3Phase);
	}

}
