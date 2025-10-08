🧠 Proyecto: Persistencia Políglota

Este proyecto es un backend hecho en Spring Boot 3.5.6 con conexión a tres bases de datos diferentes:

🐬 MySQL → Base de datos relacional

☸️ Cassandra → Base de datos NoSQL tipo columna

🕸 Neo4j → Base de datos de grafos

🚀 Objetivo del proyecto

Demostrar cómo una misma aplicación puede interactuar con múltiples motores de base de datos a la vez (poliglotismo de persistencia).

🧩 Tecnologías utilizadas
Tipo	Tecnología	Versión
Lenguaje	Java	17
Framework	Spring Boot	3.5.6
ORM Relacional	Hibernate (JPA)	Integrado
BD Relacional	MySQL	8.0
BD NoSQL	Cassandra	4.1
BD Grafos	Neo4j	5.22
Contenedores	Docker + Docker Compose	Última estable
⚙️ Requisitos previos

Antes de empezar, asegurate de tener instalado:

 Docker Desktop
📥 Descargar aquí

 Java 17 o superior
📥 Descargar aquí

 Git
📥 Descargar aquí

 VS Code o IntelliJ IDEA (opcional)

💡 En Windows, abrí PowerShell (no CMD) para ejecutar los comandos.

🧱 Paso 1 — Clonar el repositorio
git clone https://github.com/roccomoresi/bases_de_datos_backend.git
cd bases_de_datos_backend

🐳 Paso 2 — Levantar las bases de datos con Docker

Ejecutá el siguiente comando para levantar MySQL, Cassandra y Neo4j:

docker compose up -d


Esto:

Creará los contenedores con sus volúmenes persistentes.

Expondrá los puertos:

MySQL → 3306

Cassandra → 9042

Neo4j → 7474 (interfaz web) y 7687 (Bolt)

Podés verificar que están corriendo con:

docker ps


Deberías ver algo así:

CONTAINER ID   IMAGE           PORTS
xxxxxx          mysql:8.0       0.0.0.0:3306->3306/tcp
xxxxxx          cassandra:4.1   0.0.0.0:9042->9042/tcp
xxxxxx          neo4j:5.22      7474/tcp, 7687/tcp

🧰 Paso 3 — Crear las bases y keyspaces necesarios
🐬 En MySQL

Entrá al contenedor de MySQL:

docker exec -it mysql_poliglota mysql -u root -p


(la contraseña es root123)

Dentro del cliente MySQL, ejecutá:

CREATE DATABASE poliglota_db;
EXIT;

☸️ En Cassandra

Entrá al contenedor de Cassandra:

docker exec -it cassandra_poliglota cqlsh


Dentro del shell de Cassandra:

CREATE KEYSPACE sensores
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
EXIT;

🕸 En Neo4j

Abrí el panel en el navegador:
👉 http://localhost:7474

Usuario: neo4j
Contraseña: neo4jpoliglota (la primera vez te pedirá cambiarla)

⚙️ Paso 4 — Ejecutar el backend

Para correr el servidor de Spring Boot:

./mvnw spring-boot:run


(En Windows, si te pide permiso, poné “Ejecutar de todos modos”).

Si ves algo como esto, ¡todo salió bien! 🎉

Tomcat started on port 8080 (http)
Started PersistenciaPoliglotaApplication in X.XXX seconds

🧪 Paso 5 — Probar que todo funcione

Abrí en tu navegador:
👉 http://localhost:8080/status

Deberías ver:

{
  "status": "✅ Aplicación corriendo correctamente",
  "mysql": "OK",
  "cassandra": "OK",
  "neo4j": "OK"
}


Si ves eso → ¡todo está funcionando! 🚀

Tips extra

Si el backend no levanta, revisá que los 3 contenedores estén "Up".

Si Cassandra o MySQL tiran error de conexión, borrá los volúmenes y recreá todo:

docker compose down -v
docker compose up -d


Si mvnw no se ejecuta en PowerShell, usá:

mvnw.cmd spring-boot:run