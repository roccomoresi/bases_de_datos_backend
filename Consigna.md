Ingeniería de Datos II - Trabajo Practico 
(Persistencia Poliglota)

Objetivo: Construir una aplicación para gestionar la registración de temperaturas de sensores ubicados alrededor del mundo y su explotación comercial para distintos usuarios registrados y los procesos autorizados a realizar con los mismos.
	Los datos de los sensores y sus mediciones.
	El control del funcionamiento de los sensores y la emisión de alertas tanto de funcionamiento de estos como de situaciones climáticas.
	La generación de los procesos, servicios y reportes solicitados por los usuarios.
	El control y registración de los procesos, servicios y reportes emitidos y por emitirse.
	La facturación y pago de los procesos, servicios y reportes utilizados y la imputación a las cuentas corrientes de los usuarios que la posean.
	Los datos de los usuarios que pueden utilizar los servicios de acuerdo con sus roles.
	Los mensajes intercambiados entre usuarios y personal de mantenimiento y ejecución de procesos.
Se pide:
	Definir los modelos de BD más adecuados para el desarrollo de la aplicación, justificando sus elecciones en base a las características de los productos y sus fortalezas y debilidades.
	Modelar y construir las BD necesarios para el desarrollo de la solución (debe entregar de cada BD utilizada el modelo físico de la estructura con la justificación).
	Construir una aplicación que cumpla con los requisitos detallados y cualquier otro que sea necesario para convertirla en funcional
Principales Entidades: A continuación, se detalla una serie de entidades y sus atributos, agregue todos los atributos que considere necesario para cumplir los objetivos.
Sensor
	ID del sensor
	Nombre o código
	Tipo Sensor (temperatura – humedad)
	Latitud
	Longitud
	Ciudad
	País
	Estado Sensor (activo/inactivo/falla)
	Fecha de inicio emisión
Medición
	ID de medición
	Sensor
	Fecha y hora
	Temperatura
	Humedad
Usuario
	ID de usuario
	Nombre completo
	Email
	Contraseña (encriptada)
	Estado (activo/inactivo)
	Fecha de registro
Sesión
	ID de sesión
	Usuario
	Rol
	Fecha y hora de inicio
	Fecha y hora de cierre
	Estado Actual (activa/inactiva)
Rol
	ID de rol
	Descripción (usuario – técnico – administrador)
Mensaje
	ID de mensaje
	Remitente (Usuario)
	Destinatario (Usuario o Grupo)
	Fecha y hora
	Contenido
	Tipo (privado/grupal)
Grupo
	ID de grupo
	Nombre del grupo
	Usuarios miembros
Proceso
	ID de proceso
	Nombre 
	Descripción
	Tipo de proceso
	Costo
Solicitud de Proceso
	ID de solicitud
	Usuario
	Proceso
	Fecha de solicitud
	Estado (pendiente/completado)
 
Historial de Ejecución
	ID de ejecución
	Solicitud
	Fecha de ejecución
	Resultado
	Estado
Factura
	ID de factura
	Usuario
	Fecha de emisión
	Procesos facturados
	Estado (pendiente/pagada/vencida)
Pago
	ID de pago
	Factura
	Fecha de pago
	Monto pagado
	Método de pago
Cuenta Corriente
	ID de cuenta
	Usuario
	Saldo actual
	Historial de movimientos
Alerta
	ID de alerta
	Tipo (sensor/climática)
	ID de sensor (si aplica)
	Fecha y hora
	Descripción
	Estado (activa/resuelta)
Control de Funcionamiento
	ID de control
	Sensor
	Fecha de revisión
	Estado del sensor
	Observaciones

 
La aplicación deberá:
	Registro de sensores y almacenamiento de mediciones.
	Gestión de usuarios con roles y permisos diferenciados. 
	Inicio de sesión y control de sesiones activas y los roles en cada una. 
	Mensajería entre usuarios (privada y/o grupal). 
	Gestión de nuevos procesos.
	Procesos que pueden ejecutarse sobre los datos, su control y registración.
	Confección de los informes al usuario.
	Lista de procesos solicitados por usuario indicando si los mismos están pendientes o completados. 
	Historial de ejecución de procesos. 

Tipos de Procesos:
	Informe de humedad y temperaturas máximas y mínimas por ciudades, zonas, países en un rango de fechas anualizadas, mensualizadas, etc.
	Informe de humedad y temperaturas promedio por ciudades, zonas, países en un rango de fechas anualizadas, mensualizadas, etc.
	Alertas de temperaturas y humedad en un rango determinado en una ciudad, zona, país en un rango de fechas.
	Servicios de consultas en línea de la información de los sensores por ciudad, zona, país en un rango de fechas
	Procesos periódicos de consultas sobre humedad y temperaturas por ciudades, zonas, países anualizados, mensualizadas, etc.
	Facturación a usuarios, control de pagos y acreditación en cuanta corriente.

Termino: 
	La aplicación se desarrollará en grupo, de no más de 4 integrantes.
	Para el desarrollo de esta puede utilizar cualquier lenguaje de programación que desee, implementando los patrones de diseño correspondiente y las buenas prácticas de desarrollo y testeo.
	El diseño de la aplicación debe corresponder a un profesional de sistemas.
	La misma se deberá encontrar funcionando para la clase asignada en el cronograma.
	Ese día cada grupo expondrá las bases elegidas, su justificación y mostrará el funcionamiento de la aplicación. 
	Se interrogará a cualquiera de los integrantes del grupo sobre cualquier aspecto de la aplicación desarrollada. 
	La ausencia a la fecha de presentación corresponde a un TP desaprobado. Si no puede asistir, previamente avise para coordinar otra fecha.
