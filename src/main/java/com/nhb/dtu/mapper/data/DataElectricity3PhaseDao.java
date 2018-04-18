package com.nhb.dtu.mapper.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.nhb.dtu.entity.DataElectricity3Phase;

@Repository
public interface DataElectricity3PhaseDao extends MongoRepository<DataElectricity3Phase, Long> {

}
