# 🧠 Proyecto: Persistencia Políglota

**Integración de MySQL + MongoDB + Cassandra**
📚 Trabajo Práctico de Bases de Datos NoSQL — *Ingeniería de Datos II*

---

## 🚀 Objetivo del TP

Este proyecto demuestra una **arquitectura de persistencia poliglota**, integrando tres bases de datos bajo un mismo backend **Spring Boot**.
Cada motor se usa según su **fortaleza específica**:

| Base de Datos    | Tipo       | Uso principal en el sistema                                | Justificación técnica                                             |
| ---------------- | ---------- | ---------------------------------------------------------- | ----------------------------------------------------------------- |
| 🟦 **MySQL**     | Relacional | Usuarios, roles, sesiones, procesos, facturas y pagos.     | Garantiza **consistencia ACID** y soporte de transacciones.       |
| 🟩 **MongoDB**   | Documental | Mensajería, solicitudes, alertas, y datos dinámicos.       | Flexible y escalable, ideal para **datos semi-estructurados**.    |
| 🟪 **Cassandra** | Columnar   | Lecturas de sensores, mediciones históricas y control IoT. | Optimizada para **escrituras masivas** y **alta disponibilidad**. |

---

## 🧱 Estructura del proyecto

```
persistencia_poliglota/
│
├── src/main/java/com/example/persistencia/poliglota/
│   ├── controller/
│   │   ├── sql/UsuarioController.java
│   │   ├── mongo/AlertaController.java
│   │   └── cassandra/SensorController.java
│   ├── service/
│   │   ├── sql/UsuarioService.java
│   │   ├── mongo/AlertaService.java
│   │   └── cassandra/SensorService.java
│   ├── model/
│   │   ├── sql/Usuario.java
│   │   ├── mongo/Alerta.java
│   │   └── cassandra/Sensor.java
│   └── repository/
│       ├── sql/UsuarioRepository.java
│       ├── mongo/AlertaRepository.java
│       └── cassandra/SensorRepository.java
│
├── docker-compose.yml
└── README.md
```

---

## 🐳 1️⃣ Levantar las bases con Docker

Ejecutá en la raíz del proyecto:

```bash
docker-compose up -d
```

Esto inicia los contenedores:

| Contenedor            | Imagen        | Puerto |
| --------------------- | ------------- | ------ |
| `mysql_poliglota`     | mysql:8.0     | 3306   |
| `mongo_poliglota`     | mongo:7.0     | 27017  |
| `cassandra_poliglota` | cassandra:4.1 | 9042   |

Verificá con:

```bash


docker ps



## 💻 3️⃣ Ejecutar el backend

En la raíz del proyecto:

```bash
./mvnw spring-boot:run
```

El servidor corre en:
👉 `http://localhost:8080`

---

## ⚖️ 5️⃣ Justificación técnica

| Componente | Rol                       | Tipo de consistencia    | Fortalezas clave                                         |
| ---------- | ------------------------- | ----------------------- | -------------------------------------------------------- |
| MySQL      | Transaccional relacional  | **Fuerte (ACID)**       | Ideal para usuarios, roles, facturación.                 |
| MongoDB    | Documental distribuido    | **Eventual**            | Flexible, sin esquema, permite escalabilidad horizontal. |
| Cassandra  | Columnar distribuido (AP) | **Alta disponibilidad** | Excelente rendimiento en lecturas/escrituras IoT.        |

---

## 🧪 6️⃣ Flujo completo de ejemplo

1. El **usuario** se registra (MySQL).
2. El **sensor** reporta datos de temperatura (Cassandra).
3. Se genera una **alerta** si el valor supera un umbral (MongoDB).
4. El administrador puede ver todos los registros y emitir facturas.

---

## 🧰 7️⃣ Requisitos técnicos

* Java 17
* Maven 3.9+
* Docker y Docker Compose
* Postman (para probar endpoints)

---

## 🔑 Usuarios preconfigurados

| Rol     | Email                                     | Contraseña |
| ------- | ----------------------------------------- | ---------- |
| ADMIN   | [admin@test.com](mailto:admin@test.com)   | 1234       |
| TECNICO | [carlos@test.com](mailto:carlos@test.com) | 1234       |
| USUARIO | [rocio@test.com](mailto:rocio@test.com)   | 1234       |

---

## 📊 8️⃣ Escalabilidad y despliegue

Diseñado para ejecutarse con **tres clusters independientes**:

* MySQL (transacciones)
* MongoDB (documentos y alertas)
* Cassandra (IoT y series de tiempo)

Con posible extensión a:

* API Gateway
* Cluster de balanceo y monitoreo

Garantizando **resiliencia**, **tolerancia a fallos** y **desacoplamiento total** de las fuentes de datos.

---


