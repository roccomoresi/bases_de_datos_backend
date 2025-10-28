package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.dto.InformeCiudadRequest;
import com.example.persistencia.poliglota.dto.InformeCiudadResponse;
import com.example.persistencia.poliglota.dto.InformePaisResponse;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorPais;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorCiudadRepository;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorPaisRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.persistencia.poliglota.dto.InformeSeriePunto;

@Service
public class InformeService {

    private final MedicionPorCiudadRepository medicionPorCiudadRepository;
    private final MedicionPorPaisRepository medicionPorPaisRepository;

    public InformeService(MedicionPorCiudadRepository medicionPorCiudadRepository,
                          MedicionPorPaisRepository medicionPorPaisRepository) {
        this.medicionPorCiudadRepository = medicionPorCiudadRepository;
        this.medicionPorPaisRepository = medicionPorPaisRepository;
    }

    public InformeCiudadResponse calcularPorCiudad(InformeCiudadRequest req) {
        Objects.requireNonNull(req.getCiudad(), "ciudad es requerida");
        Objects.requireNonNull(req.getPais(), "pais es requerido");

        List<MedicionPorCiudad> mediciones = medicionPorCiudadRepository
                .findByCiudadAndPais(req.getCiudad(), req.getPais());

        Date desde = req.getDesde() != null ? Date.from(Instant.parse(req.getDesde())) : null;
        Date hasta = req.getHasta() != null ? Date.from(Instant.parse(req.getHasta())) : null;

        var filtradas = mediciones.stream()
                .filter(m -> desde == null || !m.getFechaMedicion().before(desde))
                .filter(m -> hasta == null || !m.getFechaMedicion().after(hasta))
                .toList();

        DoubleSummaryStatistics statsTemp = filtradas.stream()
                .map(MedicionPorCiudad::getTemperatura)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics statsHum = filtradas.stream()
                .map(MedicionPorCiudad::getHumedad)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        InformeCiudadResponse r = new InformeCiudadResponse();
        r.setCiudad(req.getCiudad());
        r.setPais(req.getPais());
        r.setCantidadMediciones((long) filtradas.size());
        r.setTemperaturaMax(statsTemp.getCount() > 0 ? statsTemp.getMax() : null);
        r.setTemperaturaMin(statsTemp.getCount() > 0 ? statsTemp.getMin() : null);
        r.setTemperaturaPromedio(statsTemp.getCount() > 0 ? statsTemp.getAverage() : null);
        r.setHumedadMax(statsHum.getCount() > 0 ? statsHum.getMax() : null);
        r.setHumedadMin(statsHum.getCount() > 0 ? statsHum.getMin() : null);
        r.setHumedadPromedio(statsHum.getCount() > 0 ? statsHum.getAverage() : null);
        return r;
    }

    public InformePaisResponse calcularPorPais(String pais, String desdeIso, String hastaIso) {
        Objects.requireNonNull(pais, "pais es requerido");

        var mediciones = medicionPorPaisRepository.findByPais(pais);

        Date desde = desdeIso != null ? Date.from(Instant.parse(desdeIso)) : null;
        Date hasta = hastaIso != null ? Date.from(Instant.parse(hastaIso)) : null;

        var filtradas = mediciones.stream()
                .filter(m -> desde == null || !m.getFechaMedicion().before(desde))
                .filter(m -> hasta == null || !m.getFechaMedicion().after(hasta))
                .toList();

        DoubleSummaryStatistics statsTemp = filtradas.stream()
                .map(MedicionPorPais::getTemperatura)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics statsHum = filtradas.stream()
                .map(MedicionPorPais::getHumedad)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        InformePaisResponse r = new InformePaisResponse();
        r.setPais(pais);
        r.setCantidadMediciones((long) filtradas.size());
        r.setTemperaturaMax(statsTemp.getCount() > 0 ? statsTemp.getMax() : null);
        r.setTemperaturaMin(statsTemp.getCount() > 0 ? statsTemp.getMin() : null);
        r.setTemperaturaPromedio(statsTemp.getCount() > 0 ? statsTemp.getAverage() : null);
        r.setHumedadMax(statsHum.getCount() > 0 ? statsHum.getMax() : null);
        r.setHumedadMin(statsHum.getCount() > 0 ? statsHum.getMin() : null);
        r.setHumedadPromedio(statsHum.getCount() > 0 ? statsHum.getAverage() : null);
        return r;
    }

    public List<InformeSeriePunto> serieCiudadMensual(String ciudad, String pais, String desdeIso, String hastaIso) {
        List<MedicionPorCiudad> base = medicionPorCiudadRepository.findByCiudadAndPais(ciudad, pais);
        return agruparEnSerie(base, desdeIso, hastaIso, true);
    }

    public List<InformeSeriePunto> serieCiudadAnual(String ciudad, String pais, String desdeIso, String hastaIso) {
        List<MedicionPorCiudad> base = medicionPorCiudadRepository.findByCiudadAndPais(ciudad, pais);
        return agruparEnSerie(base, desdeIso, hastaIso, false);
    }

    public List<InformeSeriePunto> seriePaisMensual(String pais, String desdeIso, String hastaIso) {
        var base = medicionPorPaisRepository.findByPais(pais);
        return agruparEnSeriePais(base, desdeIso, hastaIso, true);
    }

    public List<InformeSeriePunto> seriePaisAnual(String pais, String desdeIso, String hastaIso) {
        var base = medicionPorPaisRepository.findByPais(pais);
        return agruparEnSeriePais(base, desdeIso, hastaIso, false);
    }

    private List<InformeSeriePunto> agruparEnSerie(List<MedicionPorCiudad> mediciones, String desdeIso, String hastaIso, boolean mensual) {
        Date desde = desdeIso != null ? Date.from(Instant.parse(desdeIso)) : null;
        Date hasta = hastaIso != null ? Date.from(Instant.parse(hastaIso)) : null;

        var filtradas = mediciones.stream()
                .filter(m -> desde == null || !m.getFechaMedicion().before(desde))
                .filter(m -> hasta == null || !m.getFechaMedicion().after(hasta))
                .toList();

        DateTimeFormatter fmt = mensual ? DateTimeFormatter.ofPattern("yyyy-MM") : DateTimeFormatter.ofPattern("yyyy");

        Map<String, List<MedicionPorCiudad>> grupos = filtradas.stream()
                .collect(Collectors.groupingBy(m -> m.getFechaMedicion().toInstant().atZone(ZoneOffset.UTC).format(fmt)));

        return grupos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    var lista = e.getValue();
                    DoubleSummaryStatistics statsTemp = lista.stream().map(MedicionPorCiudad::getTemperatura).filter(Objects::nonNull).mapToDouble(Double::doubleValue).summaryStatistics();
                    DoubleSummaryStatistics statsHum = lista.stream().map(MedicionPorCiudad::getHumedad).filter(Objects::nonNull).mapToDouble(Double::doubleValue).summaryStatistics();
                    InformeSeriePunto p = new InformeSeriePunto();
                    p.setPeriodo(e.getKey());
                    p.setCantidadMediciones((long) lista.size());
                    p.setTemperaturaMax(statsTemp.getCount() > 0 ? statsTemp.getMax() : null);
                    p.setTemperaturaMin(statsTemp.getCount() > 0 ? statsTemp.getMin() : null);
                    p.setTemperaturaPromedio(statsTemp.getCount() > 0 ? statsTemp.getAverage() : null);
                    p.setHumedadMax(statsHum.getCount() > 0 ? statsHum.getMax() : null);
                    p.setHumedadMin(statsHum.getCount() > 0 ? statsHum.getMin() : null);
                    p.setHumedadPromedio(statsHum.getCount() > 0 ? statsHum.getAverage() : null);
                    return p;
                })
                .toList();
    }

    private List<InformeSeriePunto> agruparEnSeriePais(List<MedicionPorPais> mediciones, String desdeIso, String hastaIso, boolean mensual) {
        Date desde = desdeIso != null ? Date.from(Instant.parse(desdeIso)) : null;
        Date hasta = hastaIso != null ? Date.from(Instant.parse(hastaIso)) : null;

        var filtradas = mediciones.stream()
                .filter(m -> desde == null || !m.getFechaMedicion().before(desde))
                .filter(m -> hasta == null || !m.getFechaMedicion().after(hasta))
                .toList();

        DateTimeFormatter fmt = mensual ? DateTimeFormatter.ofPattern("yyyy-MM") : DateTimeFormatter.ofPattern("yyyy");

        Map<String, List<MedicionPorPais>> grupos = filtradas.stream()
                .collect(Collectors.groupingBy(m -> m.getFechaMedicion().toInstant().atZone(ZoneOffset.UTC).format(fmt)));

        return grupos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    var lista = e.getValue();
                    DoubleSummaryStatistics statsTemp = lista.stream().map(MedicionPorPais::getTemperatura).filter(Objects::nonNull).mapToDouble(Double::doubleValue).summaryStatistics();
                    DoubleSummaryStatistics statsHum = lista.stream().map(MedicionPorPais::getHumedad).filter(Objects::nonNull).mapToDouble(Double::doubleValue).summaryStatistics();
                    InformeSeriePunto p = new InformeSeriePunto();
                    p.setPeriodo(e.getKey());
                    p.setCantidadMediciones((long) lista.size());
                    p.setTemperaturaMax(statsTemp.getCount() > 0 ? statsTemp.getMax() : null);
                    p.setTemperaturaMin(statsTemp.getCount() > 0 ? statsTemp.getMin() : null);
                    p.setTemperaturaPromedio(statsTemp.getCount() > 0 ? statsTemp.getAverage() : null);
                    p.setHumedadMax(statsHum.getCount() > 0 ? statsHum.getMax() : null);
                    p.setHumedadMin(statsHum.getCount() > 0 ? statsHum.getMin() : null);
                    p.setHumedadPromedio(statsHum.getCount() > 0 ? statsHum.getAverage() : null);
                    return p;
                })
                .toList();
    }
}


