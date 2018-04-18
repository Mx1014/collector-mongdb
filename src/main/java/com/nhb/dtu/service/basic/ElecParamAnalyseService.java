package com.nhb.dtu.service.basic;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.ElecParamAnalyse;
import com.nhb.dtu.mapper.basic.ElecParamAnalyseDao;

/**
 * 
 * @ClassName: ElecParamAnalyseService
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年6月30日 下午4:08:39
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class ElecParamAnalyseService {

	@Autowired
	private ElecParamAnalyseDao elecParamAnalyseDao;

	/**
	 * 
	 * @Title: findAll @Description: TODO(这里用一句话描述这个方法的作用) @return
	 *         List<ElecParamAnalyse> @throws
	 */
	public List<ElecParamAnalyse> findAll() {
		return (List<ElecParamAnalyse>) elecParamAnalyseDao.findAll();
	}

}
