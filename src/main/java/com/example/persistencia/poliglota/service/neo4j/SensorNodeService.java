package com.example.persistencia.poliglota.service.neo4j;

import com.example.persistencia.poliglota.model.neo4j.SensorNode;
import com.example.persistencia.poliglota.repository.neo4j.SensorNodeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SensorNodeService {

    private final SensorNodeRepository sensorNodeRepository;

    public SensorNodeService(SensorNodeRepository sensorNodeRepository) {
        this.sensorNodeRepository = sensorNodeRepository;
    }

    public List<SensorNode> getAll() {
        return sensorNodeRepository.findAll();
    }

    public SensorNode save(SensorNode sensor) {
        return sensorNodeRepository.save(sensor);
    }
}
