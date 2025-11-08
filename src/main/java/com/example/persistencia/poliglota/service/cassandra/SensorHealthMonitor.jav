//FALTA IMPLEMENTARLO CON SCHEDULED

@Service
public class SensorHealthMonitor {

    private final SensorRepository sensorRepository;
    private final MedicionPorSensorRepository medicionPorSensorRepository;
    private final AlertaMongoClient alertaMongoClient;

    @Scheduled(fixedRate = 600000) // cada 10 minutos
    public void verificarSensoresInactivos() {
        List<Sensor> sensores = sensorRepository.findAll();
        Date ahora = new Date();

        for (Sensor sensor : sensores) {
            List<MedicionPorSensor> mediciones = medicionPorSensorRepository
                    .findBySensorId(sensor.getId());

            if (!mediciones.isEmpty()) {
                Date ultima = mediciones.get(mediciones.size() - 1).getFechaMedicion();
                long minutos = (ahora.getTime() - ultima.getTime()) / 60000;

                if (minutos > 30 && !"inactivo".equals(sensor.getEstado())) {
                    sensorRepository.updateEstado(sensor.getId(), "inactivo");
                    alertaMongoClient.enviarAlerta(
                        sensor.getId(),
                        "sensor",
                        "📡 El sensor " + sensor.getNombre() + " no transmite hace " + minutos + " minutos.",
                        sensor.getCiudad(),
                        sensor.getPais(),
                        null, null, "media"
                    );
                }
            }
        }
    }
}
