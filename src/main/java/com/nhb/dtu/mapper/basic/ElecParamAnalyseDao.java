package com.nhb.dtu.mapper.basic;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.ElecParamAnalyse;

/**
 * 
 * @ClassName: ElecParamAnalyseMapper
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author XS guo
 * @date 2017年6月30日 下午3:57:12
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Repository
public interface ElecParamAnalyseDao extends MongoRepository<ElecParamAnalyse, Long> {

}
