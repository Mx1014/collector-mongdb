package com.nhb.dtu.service.data;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.AirCtrlComm;
import com.nhb.dtu.mapper.data.AirCtrlCummDao;

@Service
public class AirCtrlCommService {

	@Autowired
	private AirCtrlCummDao airCtrlCummDao;

	public AirCtrlComm findById(String circuitId) {
		if (airCtrlCummDao.findById(circuitId).equals(Optional.empty())) {
			return null;
		}
		return airCtrlCummDao.findById(circuitId).get();
	}

	public AirCtrlComm save(AirCtrlComm comm) {
		return airCtrlCummDao.save(comm);
	}

	public List<AirCtrlComm> findAll() {
		return (List<AirCtrlComm>) airCtrlCummDao.findAll();
	}

}
