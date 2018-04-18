package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataElectricity;
import com.nhb.dtu.mapper.data.DataElectricityDao;

/**
 * @ClassName:DataElectricityService
 * @Reason: TODO ADD REASON.
 * @Date: June 26, 2017 2:53:50 PM
 * @author xuyahui
 * @version
 * @since JDK 1.8
 * @see
 */
@Service
public class DataElectricityService {
	@Autowired
	private DataElectricityDao dataElectricityDao;

	public DataElectricity save(DataElectricity dataElectricity) {
		return dataElectricityDao.save(dataElectricity);
	}

}
