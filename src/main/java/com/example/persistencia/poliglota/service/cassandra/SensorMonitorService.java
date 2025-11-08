package com.example.persistencia.poliglota.service.cassandra;

import com.example.persistencia.poliglota.model.cassandra.Medicion;
import com.example.persistencia.poliglota.model.cassandra.Sensor;
import com.example.persistencia.poliglota.model.mongo.Alerta;
import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
import com.example.persistencia.poliglota.repository.mongo.AlertaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class SensorMonitorService {

    private final SensorRepository sensorRepository;
    private final MedicionRepository medicionRepository;
    private final AlertaRepository alertaRepository;

    public SensorMonitorService(SensorRepository sensorRepository,
                                MedicionRepository medicionRepository,
                                AlertaRepository alertaRepository) {
        this.sensorRepository = sensorRepository;
        this.medicionRepository = medicionRepository;
        this.alertaRepository = alertaRepository;
    }

    // 🔁 Se ejecuta cada 1 minuto
    @Scheduled(fixedRate = 60000)
    public void verificarSensoresInactivos() {
        var sensores = sensorRepository.findAll();

        for (Sensor sensor : sensores) {
            Medicion ultimaMedicion = medicionRepository.findUltimaBySensor(sensor.getId());

            boolean sinMediciones = (ultimaMedicion == null);
            boolean inactivo = false;

            if (!sinMediciones) {
                Date fecha = ultimaMedicion.getFechaMedicion();
                Instant fechaInstant = fecha.toInstant();

            System.out.println("🛰️ Sensor: " + sensor.getNombre());
            System.out.println("Última medición: " + fechaInstant);
            System.out.println("Ahora: " + Instant.now());
            System.out.println("Minutos de diferencia: " +
            Duration.between(fechaInstant, Instant.now()).toMinutes());
            System.out.println("---------------------------------------------------");


                inactivo = Duration.between(fechaInstant, Instant.now()).toMinutes() >= 15;

            }

            if (sinMediciones || inactivo) {
                boolean yaExiste = alertaRepository
                        .existsBySensorIdAndTipoAndEstado(sensor.getId(), "no_funcionamiento", "activa");

                if (!yaExiste) {
                    Alerta alerta = new Alerta(
                            UUID.randomUUID(),
                            "no_funcionamiento",
                            sensor.getId(),
                            sensor.getCiudad(),
                            sensor.getPais(),
                            Instant.now(),
                            "El sensor no emite mediciones desde hace un período prolongado"
,
                            "activa",
                            "crítica",
                            "#ff0000",
                            "⚠️",
                            "monitor_automático",
                            Map.of(
                                    "ultima_medicion", ultimaMedicion != null
                                            ? ultimaMedicion.getFechaMedicion()
                                            : null
                            )
                    );

                    alertaRepository.save(alerta);
                    System.out.println("🚨 [ALERTA] Sensor inactivo: " +
                            sensor.getNombre() + " (" + sensor.getId() + ")");
                }
            }
        }
    }
}
