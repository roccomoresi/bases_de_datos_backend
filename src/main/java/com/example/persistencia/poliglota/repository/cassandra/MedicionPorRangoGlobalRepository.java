package com.example.persistencia.poliglota.repository.cassandra;

import com.example.persistencia.poliglota.model.cassandra.MedicionPorRangoGlobal;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MedicionPorRangoGlobalRepository extends CassandraRepository<MedicionPorRangoGlobal, String> {
    List<MedicionPorRangoGlobal> findByYearMonthBucketInAndFechaMedicionBetween(List<String> buckets, Date desde, Date hasta);
}

