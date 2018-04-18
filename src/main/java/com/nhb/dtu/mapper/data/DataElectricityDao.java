package com.nhb.dtu.mapper.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.DataElectricity;

/**
 * @ClassName:DataElectricityDao
 * @Function: 历史数据表
 * @Date: June 30, 2017 10:27:02 AM
 * @author xuyahui
 * @version
 * @since JDK 1.8
 * @see
 */
@Repository
public interface DataElectricityDao extends MongoRepository<DataElectricity, Long> {

}
