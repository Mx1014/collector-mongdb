package com.nhb.dtu.mapper.data;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.DataElecOil;

/**
 * 
 * @ClassName: DataElecOilDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年7月10日 上午9:28:25
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Repository("dataElecOilDao")
public interface DataElecOilDao extends MongoRepository<DataElecOil, UUID> {

}
