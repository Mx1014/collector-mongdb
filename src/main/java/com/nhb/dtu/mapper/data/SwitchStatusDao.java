package com.nhb.dtu.mapper.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.SwitchStatus;

@Repository
public interface SwitchStatusDao extends MongoRepository<SwitchStatus, String> {

}
