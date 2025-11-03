package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.dto.*;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorCiudad;
import com.example.persistencia.poliglota.model.cassandra.MedicionPorPais;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorCiudadRepository;
import com.example.persistencia.poliglota.repository.cassandra.MedicionPorPaisRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InformeService {

    private final MedicionPorCiudadRepository medicionPorCiudadRepository;
    private final MedicionPorPaisRepository medicionPorPaisRepository;

    public InformeService(MedicionPorCiudadRepository medicionPorCiudadRepository,
                          MedicionPorPaisRepository medicionPorPaisRepository) {
        this.medicionPorCiudadRepository = medicionPorCiudadRepository;
        this.medicionPorPaisRepository = medicionPorPaisRepository;
    }

    // 📊 Informe de ciudad (usando rango de fechas)
    public InformeCiudadResponse calcularPorCiudad(InformeCiudadRequest req) {
        Objects.requireNonNull(req.getCiudad(), "ciudad es requerida");
        Objects.requireNonNull(req.getPais(), "pais es requerido");

        Date desde = parseFecha(req.getDesde(), true);
        Date hasta = parseFecha(req.getHasta(), false);

        List<MedicionPorCiudad> mediciones = medicionPorCiudadRepository
                .findByCiudadAndFechaMedicionBetween(req.getCiudad(), desde, hasta);

        return calcularResumenCiudad(req.getCiudad(), req.getPais(), mediciones);
    }

    // 📊 Informe de país (rango de fechas)
    public InformePaisResponse calcularPorPais(String pais, String desdeIso, String hastaIso) {
        Objects.requireNonNull(pais, "pais es requerido");

        Date desde = parseFecha(desdeIso, true);
        Date hasta = parseFecha(hastaIso, false);

        List<MedicionPorPais> mediciones = medicionPorPaisRepository
                .findByPaisAndFechaMedicionBetween(pais, desde, hasta);

        return calcularResumenPais(pais, mediciones);
    }

    // 📈 Serie mensual y anual por ciudad
    public List<InformeSeriePunto> serieCiudadMensual(String ciudad, String pais, String desdeIso, String hastaIso) {
        Date desde = parseFecha(desdeIso, true);
        Date hasta = parseFecha(hastaIso, false);
        List<MedicionPorCiudad> base = medicionPorCiudadRepository
                .findByCiudadAndFechaMedicionBetween(ciudad, desde, hasta);
        return agruparEnSerieCiudad(base, true);
    }

    public List<InformeSeriePunto> serieCiudadAnual(String ciudad, String pais, String desdeIso, String hastaIso) {
        Date desde = parseFecha(desdeIso, true);
        Date hasta = parseFecha(hastaIso, false);
        List<MedicionPorCiudad> base = medicionPorCiudadRepository
                .findByCiudadAndFechaMedicionBetween(ciudad, desde, hasta);
        return agruparEnSerieCiudad(base, false);
    }

    // 📈 Serie mensual y anual por país
    public List<InformeSeriePunto> seriePaisMensual(String pais, String desdeIso, String hastaIso) {
        Date desde = parseFecha(desdeIso, true);
        Date hasta = parseFecha(hastaIso, false);
        List<MedicionPorPais> base = medicionPorPaisRepository
                .findByPaisAndFechaMedicionBetween(pais, desde, hasta);
        return agruparEnSeriePais(base, true);
    }

    public List<InformeSeriePunto> seriePaisAnual(String pais, String desdeIso, String hastaIso) {
        Date desde = parseFecha(desdeIso, true);
        Date hasta = parseFecha(hastaIso, false);
        List<MedicionPorPais> base = medicionPorPaisRepository
                .findByPaisAndFechaMedicionBetween(pais, desde, hasta);
        return agruparEnSeriePais(base, false);
    }

    // 🧮 --- Métodos auxiliares ---
    private Date parseFecha(String iso, boolean inicioDia) {
        if (iso == null) return null;
        try {
            String fixed = iso.contains("T") ? iso : iso + (inicioDia ? "T00:00:00" : "T23:59:59");
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(fixed);
        } catch (Exception e) {
            throw new RuntimeException("Formato de fecha inválido: " + iso);
        }
    }

    private InformeCiudadResponse calcularResumenCiudad(String ciudad, String pais, List<MedicionPorCiudad> datos) {
        DoubleSummaryStatistics tempStats = datos.stream()
                .map(MedicionPorCiudad::getTemperatura)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics humStats = datos.stream()
                .map(MedicionPorCiudad::getHumedad)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        InformeCiudadResponse r = new InformeCiudadResponse();
        r.setCiudad(ciudad);
        r.setPais(pais);
        r.setCantidadMediciones((long) datos.size());
        r.setTemperaturaMax(tempStats.getCount() > 0 ? tempStats.getMax() : null);
        r.setTemperaturaMin(tempStats.getCount() > 0 ? tempStats.getMin() : null);
        r.setTemperaturaPromedio(tempStats.getCount() > 0 ? tempStats.getAverage() : null);
        r.setHumedadMax(humStats.getCount() > 0 ? humStats.getMax() : null);
        r.setHumedadMin(humStats.getCount() > 0 ? humStats.getMin() : null);
        r.setHumedadPromedio(humStats.getCount() > 0 ? humStats.getAverage() : null);
        return r;
    }

    private InformePaisResponse calcularResumenPais(String pais, List<MedicionPorPais> datos) {
        DoubleSummaryStatistics tempStats = datos.stream()
                .map(MedicionPorPais::getTemperatura)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics humStats = datos.stream()
                .map(MedicionPorPais::getHumedad)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        InformePaisResponse r = new InformePaisResponse();
        r.setPais(pais);
        r.setCantidadMediciones((long) datos.size());
        r.setTemperaturaMax(tempStats.getCount() > 0 ? tempStats.getMax() : null);
        r.setTemperaturaMin(tempStats.getCount() > 0 ? tempStats.getMin() : null);
        r.setTemperaturaPromedio(tempStats.getCount() > 0 ? tempStats.getAverage() : null);
        r.setHumedadMax(humStats.getCount() > 0 ? humStats.getMax() : null);
        r.setHumedadMin(humStats.getCount() > 0 ? humStats.getMin() : null);
        r.setHumedadPromedio(humStats.getCount() > 0 ? humStats.getAverage() : null);
        return r;
    }

    private List<InformeSeriePunto> agruparEnSerieCiudad(List<MedicionPorCiudad> mediciones, boolean mensual) {
        DateTimeFormatter fmt = mensual ? DateTimeFormatter.ofPattern("yyyy-MM") : DateTimeFormatter.ofPattern("yyyy");

        Map<String, List<MedicionPorCiudad>> grupos = mediciones.stream()
                .collect(Collectors.groupingBy(m -> m.getFechaMedicion().toInstant().atZone(ZoneOffset.UTC).format(fmt)));

        return grupos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> buildSeriePunto(e.getKey(), e.getValue()))
                .toList();
    }

    private List<InformeSeriePunto> agruparEnSeriePais(List<MedicionPorPais> mediciones, boolean mensual) {
        DateTimeFormatter fmt = mensual ? DateTimeFormatter.ofPattern("yyyy-MM") : DateTimeFormatter.ofPattern("yyyy");

        Map<String, List<MedicionPorPais>> grupos = mediciones.stream()
                .collect(Collectors.groupingBy(m -> m.getFechaMedicion().toInstant().atZone(ZoneOffset.UTC).format(fmt)));

        return grupos.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> buildSeriePunto(e.getKey(), e.getValue()))
                .toList();
    }

    private InformeSeriePunto buildSeriePunto(String periodo, List<?> lista) {
        DoubleSummaryStatistics statsTemp = lista.stream()
                .map(o -> o instanceof MedicionPorCiudad m ? m.getTemperatura() : ((MedicionPorPais) o).getTemperatura())
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        DoubleSummaryStatistics statsHum = lista.stream()
                .map(o -> o instanceof MedicionPorCiudad m ? m.getHumedad() : ((MedicionPorPais) o).getHumedad())
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .summaryStatistics();

        InformeSeriePunto p = new InformeSeriePunto();
        p.setPeriodo(periodo);
        p.setCantidadMediciones((long) lista.size());
        p.setTemperaturaMax(statsTemp.getCount() > 0 ? statsTemp.getMax() : null);
        p.setTemperaturaMin(statsTemp.getCount() > 0 ? statsTemp.getMin() : null);
        p.setTemperaturaPromedio(statsTemp.getCount() > 0 ? statsTemp.getAverage() : null);
        p.setHumedadMax(statsHum.getCount() > 0 ? statsHum.getMax() : null);
        p.setHumedadMin(statsHum.getCount() > 0 ? statsHum.getMin() : null);
        p.setHumedadPromedio(statsHum.getCount() > 0 ? statsHum.getAverage() : null);
        return p;
    }
}
