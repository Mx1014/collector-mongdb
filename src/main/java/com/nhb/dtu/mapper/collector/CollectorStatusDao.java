/** 
 * Project Name:dtu-server 
 * File Name:CollectorStatusDao.java 
 * Package Name:com.nhb.dtu.dao.receipt 
 * Date:May 26, 201710:27:02 AM 
 * Copyright (c) 2017, lorisun@live.com All Rights Reserved. 
 * 
*/

package com.nhb.dtu.mapper.collector;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.CollectorStatus;

/**
 * 
 * @ClassName: CollectorStatusDao
 * @Description: 采集器状态dao层
 * @author XS guo
 * @date 2017年6月30日 上午11:23:32
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Repository
public interface CollectorStatusDao extends MongoRepository<CollectorStatus, String> {
}
