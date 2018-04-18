/** 
 * Project Name:dtu-server 
 * File Name:CollectorStatusService.java 
 * Package Name:com.nhb.dtu.service.collector 
 * Date:May 26, 20172:53:50 PM 
 * Copyright (c) 2017, lorisun@live.com All Rights Reserved. 
 * 
*/

package com.nhb.dtu.service.collector;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nhb.dtu.entity.CollectorStatus;
import com.nhb.dtu.mapper.collector.CollectorStatusDao;

/**
 * 
 * @ClassName: CollectorStatusService
 * @Description:采集器状态Service
 * @author XS guo
 * @date 2017年6月30日 上午11:27:21
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
public class CollectorStatusService {

	@Autowired
	private CollectorStatusDao collectorStatusDao;

	public CollectorStatus get(String id) {
		if (collectorStatusDao.findById(id).equals(Optional.empty())) {
			return null;
		}
		return collectorStatusDao.findById(id).get();
	}

	public CollectorStatus save(CollectorStatus collectorStatus) {
		return collectorStatusDao.save(collectorStatus);
	}
	
	public void delete(String collectorId) {
		collectorStatusDao.deleteById(collectorId);
	}

}
