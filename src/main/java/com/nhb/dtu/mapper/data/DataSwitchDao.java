package com.nhb.dtu.mapper.data;

import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.DataSwitch;

@Repository
public interface DataSwitchDao extends MongoRepository<DataSwitch, UUID> {

}
