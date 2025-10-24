// package com.example.persistencia.poliglota.service.cassandra;

// import com.example.persistencia.poliglota.model.cassandra.Medicion;
// import com.example.persistencia.poliglota.model.cassandra.Sensor;
// import com.example.persistencia.poliglota.repository.cassandra.MedicionRepository;
// import com.example.persistencia.poliglota.repository.cassandra.SensorRepository;
// import com.example.persistencia.poliglota.service.mongo.AlertaService;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;

// import java.time.Instant;
// import java.util.Date;
// import java.util.List;
// import java.util.Random;

// @Service
// public class MedicionSchedulerService {

//     private final MedicionRepository medicionRepository;
//     private final SensorRepository sensorRepository;
//     private final AlertaService alertaService;
//     private final Random random = new Random();

//     public MedicionSchedulerService(
//             MedicionRepository medicionRepository,
//             SensorRepository sensorRepository,
//             AlertaService alertaService
//     ) {
//         this.medicionRepository = medicionRepository;
//         this.sensorRepository = sensorRepository;
//         this.alertaService = alertaService;
//     }

//     // 🔁 genera una nueva medición por sensor cada 10 s (hasta 5 por sensor)
//     @Scheduled(fixedRate = 10000)
//     public void generarMedicionesAutomaticas() {
//         List<Sensor> sensores = sensorRepository.findAll();
//         if (sensores.isEmpty()) return;

//         for (Sensor sensor : sensores) {
//             long count = medicionRepository.countBySensorId(sensor.getId());
//             if (count >= 5) {
//                 continue; // ya alcanzó el límite
//             }

//             Medicion medicion = new Medicion();
//             medicion.setSensorId(sensor.getId());
//             medicion.setCiudad(sensor.getCiudad());
//             medicion.setPais(sensor.getPais());
//             medicion.setFechaMedicion(Date.from(Instant.now()));


//             medicion.setTemperatura(20 + random.nextDouble() * 20); // 20–40°C
//             medicion.setHumedad(15 + random.nextDouble() * 70);     // 15–85%

//             medicionRepository.save(medicion);
//             System.out.println("🌡️ Nueva medición automática para " + sensor.getNombre() +
//                     " (" + (count + 1) + "/5)");

//             if (medicion.getTemperatura() > 35) {
//                 alertaService.crear(
//                         sensor.getId(),
//                         "climatica",
//                         "🔥 Temperatura alta en " + sensor.getCiudad() + ": "
//                                 + medicion.getTemperatura() + "°C",
//                         sensor.getCiudad(),
//                         sensor.getPais()
//                 );
//             }

//             if (medicion.getHumedad() < 25) {
//                 alertaService.crear(
//                         sensor.getId(),
//                         "climatica",
//                         "💧 Humedad baja en " + sensor.getCiudad() + ": "
//                                 + medicion.getHumedad() + "%",
//                         sensor.getCiudad(),
//                         sensor.getPais()
//                 );
//             }
//         }
//     }
// }
