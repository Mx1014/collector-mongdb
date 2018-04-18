package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataFuelGas;
import com.nhb.dtu.mapper.data.DataFuelGasDao;

/**
 * 
 * @ClassName: DataFuelGasService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年8月7日 下午2:49:41
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class DataFuelGasService {
	@Autowired
	private DataFuelGasDao dataFuelGasDao;

	public DataFuelGas save(DataFuelGas dataFuelGas) {
		return dataFuelGasDao.save(dataFuelGas);
	}
}
