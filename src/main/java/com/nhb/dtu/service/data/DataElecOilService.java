package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataElecOil;
import com.nhb.dtu.mapper.data.DataElecOilDao;

/**
 * 
 * @ClassName: DataElecOilService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年7月10日 上午9:28:31
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class DataElecOilService {
	@Autowired
	private DataElecOilDao dataElecOilDao;

	public DataElecOil save(DataElecOil dataElecOil) {
		return dataElecOilDao.save(dataElecOil);
	}

}
