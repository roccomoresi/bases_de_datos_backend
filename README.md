Excelente 🔥
te voy a dejar un **README completísimo y listo para tu grupo**, pensado para que cualquiera lo levante **sin tener que entender todo el backend**.

---

# 🧠 Proyecto: Persistencia Políglota

**Integración de MySQL + Neo4j + Cassandra**
📚 Trabajo Práctico de Bases de Datos NoSQL

---

## 🚀 Objetivo del TP

Este proyecto demuestra una **arquitectura de persistencia poliglota**, integrando tres bases de datos con un backend unificado en **Spring Boot**.
Cada motor se utiliza según su **fortaleza específica**:

| Base de Datos    | Tipo       | Uso en el sistema                                        | Justificación                                                       |
| ---------------- | ---------- | -------------------------------------------------------- | ------------------------------------------------------------------- |
| 🟦 **MySQL**     | Relacional | Manejo de **usuarios y transacciones**                   | Garantiza **consistencia** y soporte para **transacciones ACID**    |
| 🟩 **Neo4j**     | Grafos     | Gestión de **relaciones entre usuarios (colaboradores)** | Ideal para representar **redes, conexiones y relaciones complejas** |
| 🟪 **Cassandra** | Columnar   | Registro de **sensores o lecturas de IoT**               | Optimizada para **alta disponibilidad y escalabilidad horizontal**  |

---

## 🧱 Estructura general

```
persistencia_poliglota/
│
├── src/main/java/com/example/persistencia/poliglota/
│   ├── controller/
│   │   ├── sql/TransaccionController.java
│   │   ├── neo4j/UsuarioController.java
│   │   └── cassandra/SensorController.java
│   ├── service/
│   │   ├── sql/TransaccionService.java
│   │   ├── neo4j/UsuarioService.java
│   │   └── cassandra/SensorService.java
│   ├── model/
│   │   ├── sql/UsuarioSQL.java
│   │   ├── neo4j/UsuarioNeo.java
│   │   └── cassandra/Sensor.java
│   └── repository/
│       ├── sql/UsuarioRepository.java
│       ├── neo4j/UsuarioRepository.java
│       └── cassandra/SensorRepository.java
│
├── docker-compose.yml
└── README.md  👈 (este archivo)
```

---

## 🐳 1️⃣ Levantar las bases de datos con Docker

Abrí una terminal en la raíz del proyecto y ejecutá:

```bash
docker-compose up -d
```

📦 Esto inicia los contenedores:

| Contenedor            | Imagen        | Puerto      |
| --------------------- | ------------- | ----------- |
| `mysql_poliglota`     | mysql:8.0     | 3306        |
| `neo4j_poliglota`     | neo4j:5.22    | 7474 / 7687 |
| `cassandra_poliglota` | cassandra:4.1 | 9042        |

Podés verificar que estén corriendo con:

```bash
docker ps
```

---

## 🧩 2️⃣ Configurar conexiones en `application.properties`

```properties
# 🟦 MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/poliglota
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 🟩 Neo4j
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=1234

# 🟪 Cassandra
spring.cassandra.contact-points=localhost
spring.cassandra.keyspace-name=sensores
spring.cassandra.port=9042
spring.cassandra.local-datacenter=datacenter1
spring.cassandra.schema-action=create_if_not_exists
```

---

## 💻 3️⃣ Ejecutar el backend

En la carpeta raíz del proyecto:

```bash
./mvnw spring-boot:run
```

El servidor se inicia en:
👉 `http://localhost:8080`

---

## 📬 4️⃣ Endpoints principales

### 🟦 MySQL – Transacciones y Usuarios

**Crear usuario**

```
POST /api/sql/usuarios
```

Body:

```json
{
  "nombre": "Rocco",
  "email": "rocco@mail.com"
}
```

**Ver usuarios**

```
GET /api/sql/usuarios
```

**Ejemplo de respuesta de error (email duplicado):**

```json
{
  "error": "Petición inválida",
  "detalle": "El email 'rocco@mail.com' ya está registrado."
}
```

---

### 🟩 Neo4j – Relaciones entre usuarios

**Crear usuario con relaciones**

```
POST /api/neo4j/usuarios
```

Body:

```json
{
  "nombre": "Rocco",
  "colaboraCon": ["Lucio", "Camila"]
}
```

**Ver todos los usuarios (y sus relaciones)**

```
GET /api/neo4j/usuarios
```

**Buscar colaboraciones de un usuario**

```
GET /api/neo4j/usuarios/{nombre}
```

---

### 🟪 Cassandra – Lecturas de sensores

**Insertar lectura**

```
POST /api/cassandra/sensores
```

Body:

```json
{
  "id_sensor": "temp-01",
  "valor": 22.5,
  "fecha": "2025-10-08T19:00:00"
}
```

**Ver todas las lecturas**

```
GET /api/cassandra/sensores
```

---

## ⚖️ 5️⃣ Justificación técnica (para el informe)

| Componente | Rol                        | Tipo de consistencia                         |
| ---------- | -------------------------- | -------------------------------------------- |
| MySQL      | Transaccional y relacional | **Consistencia fuerte (ACID)**               |
| Neo4j      | Modelo de grafos           | **Lectura flexible (eventual)**              |
| Cassandra  | Columnar distribuido       | **Alta disponibilidad (AP del teorema CAP)** |

🧠 Cada motor se usa donde **mejor se desempeña**:

* MySQL asegura integridad y unicidad (usuarios, facturas).
* Neo4j optimiza la consulta de relaciones (colaboradores).
* Cassandra garantiza escalabilidad en series de tiempo (sensores).

---

## 💡 6️⃣ Ejemplo de flujo completo

1. Se crea un usuario en MySQL.
2. Se relaciona con otros en Neo4j (colaboraciones).
3. Sus sensores asociados envían lecturas a Cassandra.

Todo el backend expone endpoints REST unificados.

---

## 🧪 7️⃣ Despliegue en múltiples clusters

La app está preparada para escalar en **5 clusters distintos**, uno por base de datos:

* 1 para MySQL (transaccional)
* 1 para Neo4j (grafo)
* 1 para Cassandra (columnar)
* 1 para balanceo de carga
* 1 para API Gateway o servicio de integración

Esto demuestra la **tolerancia a fallos y escalabilidad** del enfoque poliglota.

---

## 🧰 8️⃣ Requisitos

* Docker + Docker Compose
* Java 17
* Maven 3.9+
* Postman (para probar endpoints)

---


---

# Actualización leve
