package com.nhb.dtu.mapper.data;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.DataFuelGas;

/**
 * 
 * @ClassName: DataFuelGasDao
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年8月7日 下午2:49:15
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Repository
public interface DataFuelGasDao extends MongoRepository<DataFuelGas, UUID> {

}
