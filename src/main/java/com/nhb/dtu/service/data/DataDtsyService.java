package com.nhb.dtu.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.DataDtsy;
import com.nhb.dtu.mapper.data.DataDtsyDao;

/**
 * 
 * @ClassName: DataDtsyService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年7月12日 上午9:08:56
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class DataDtsyService {

	@Autowired
	private DataDtsyDao dataDtsyDao;

	public DataDtsy save(DataDtsy dataDtsy) {
		return dataDtsyDao.save(dataDtsy);
	}
}
