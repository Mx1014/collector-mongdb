package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataTemperature;
import com.nhb.dtu.mapper.data.DataTemperatureDao;

/**
 * 
 * @ClassName: DataTemperatureDService
 * @Description: 温度
 * @author XS guo
 * @date 2017年10月16日 下午3:19:54
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class DataTemperatureDService {

	@Autowired
	private DataTemperatureDao dataTemperatureDao;

	public DataTemperature save(DataTemperature temperature) {
		return dataTemperatureDao.save(temperature);
	}

}
