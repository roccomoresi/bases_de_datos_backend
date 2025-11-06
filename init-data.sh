#!/bin/bash

# ===========================================================
# Script de carga inicial para TP Persistencia Políglota
# Autor: Cande + ChatGPT
# BD: MySQL + MongoDB + Cassandra (según endpoints)
# ===========================================================

# 👉 Ajustá esto si tu backend corre en otro puerto
BASE_URL="http://localhost:8080"

echo "⏳ Iniciando carga de datos..."

# -----------------------------------------------------------
# 1. CREACIÓN DE ROLES (MySQL)
# -----------------------------------------------------------
echo "➡️ Creando roles en MySQL..."
curl -X POST "$BASE_URL/api/sql/roles" -H "Content-Type: application/json" -d '{"descripcion": "ADMIN"}'
curl -X POST "$BASE_URL/api/sql/roles" -H "Content-Type: application/json" -d '{"descripcion": "TECNICO"}'
curl -X POST "$BASE_URL/api/sql/roles" -H "Content-Type: application/json" -d '{"descripcion": "USUARIO"}'

sleep 1

# -----------------------------------------------------------
# 2. CREACIÓN DE USUARIOS (MySQL)
# -----------------------------------------------------------
echo "➡️ Creando usuarios..."

# Cande (ADMIN → idRol = 1)
curl -X POST "$BASE_URL/api/sql/usuarios" -H "Content-Type: application/json" -d '{
  "nombreCompleto": "Candela Fernandez",
  "email": "cande@example.com",
  "contrasena": "1234",
  "estado": "ACTIVO",
  "fechaRegistro": "2025-11-05T22:03:05.083Z",
  "rol": { "idRol": 1 }
}'

# Rocco (TECNICO → idRol = 2)
curl -X POST "$BASE_URL/api/sql/usuarios" -H "Content-Type: application/json" -d '{
  "nombreCompleto": "Rocco Moresi",
  "email": "rocco@example.com",
  "contrasena": "1234",
  "estado": "ACTIVO",
  "fechaRegistro": "2025-11-05T22:03:05.083Z",
  "rol": { "idRol": 2 }
}'

# Solcha (USUARIO → idRol = 3)
curl -X POST "$BASE_URL/api/sql/usuarios" -H "Content-Type: application/json" -d '{
  "nombreCompleto": "Solcha Vazquez",
  "email": "solcha@example.com",
  "contrasena": "1234",
  "estado": "ACTIVO",
  "fechaRegistro": "2025-11-05T22:03:05.083Z",
  "rol": { "idRol": 3 }
}'

sleep 1

# -----------------------------------------------------------
# 3. INICIO DE SESIÓN (MySQL)
# -----------------------------------------------------------
echo "➡️ Iniciando sesión para Cande..."
curl -X POST "$BASE_URL/api/sql/sesiones/iniciar/1"

sleep 1

# -----------------------------------------------------------
# 4. CREACIÓN DE PROCESOS (Mongo)
# -----------------------------------------------------------
echo "➡️ Creando procesos (Mongo)..."
curl -X POST "$BASE_URL/api/mongo/procesos" -H "Content-Type: application/json" -d '{
  "id": "PROC-TEMP-MENSUAL",
  "nombre": "Informe de temperaturas mensuales",
  "descripcion": "Analiza temperaturas máximas y mínimas por ciudad",
  "tipo": "INFORME",
  "costo": 2500,
  "activo": true
}'

curl -X POST "$BASE_URL/api/mongo/procesos" -H "Content-Type: application/json" -d '{
  "id": "PROC-ALERTAS",
  "nombre": "Generación automática de alertas",
  "descripcion": "Detecta condiciones de riesgo climático",
  "tipo": "ALERTA",
  "costo": 4000,
  "activo": true
}'

sleep 1

# -----------------------------------------------------------
# 5. SOLICITUD DE PROCESO (Mongo)
# -----------------------------------------------------------
echo "➡️ Creando solicitud..."
curl -X POST "$BASE_URL/api/mongo/solicitudes/nueva" -H "Content-Type: application/json" -d '{
  "usuarioId": 3,
  "procesoId": "PROC-TEMP-MENSUAL",
  "descripcion": "Informe de humedad/temperatura",
  "ciudad": "Madrid",
  "pais": "España",
  "rangoFechas": "Octubre 2025"
}'

sleep 1

# -----------------------------------------------------------
# 6. CHAT PRIVADO (Mongo)
# -----------------------------------------------------------
echo "➡️ Creando chat privado..."
curl -X POST "$BASE_URL/api/mongo/chats/privado" -H "Content-Type: application/json" -d '{
  "participantes": ["cande@example.com", "rocco@example.com"],
  "nombreGrupo": "Chat Privado",
  "tipo": "PRIVADO",
  "mensajes": []
}'

sleep 1

# -----------------------------------------------------------
# 7. CHAT GRUPAL (Mongo)
# -----------------------------------------------------------
echo "➡️ Creando chat grupal..."
curl -X POST "$BASE_URL/api/mongo/chats/grupo" -H "Content-Type: application/json" -d '{
  "participantes": ["cande@example.com", "rocco@example.com", "solcha@example.com"],
  "nombreGrupo": "Soporte Técnico",
  "tipo": "GRUPAL",
  "mensajes": []
}'

sleep 1

# -----------------------------------------------------------
# 8. ALERTA (Mongo)
# -----------------------------------------------------------
echo "➡️ Creando alerta..."
curl -X POST "$BASE_URL/api/mongo/alertas" -H "Content-Type: application/json" -d '{
  "tipo": "CLIMATICA",
  "sensorId": "TOKYO-001",
  "ciudad": "Tokyo",
  "pais": "Japón",
  "fecha": "2025-11-05T22:11:50.376Z",
  "descripcion": "Temperatura fuera de rango",
  "estado": "ACTIVA",
  "severidad": "ALTA",
  "color": "rojo",
  "icono": "warning",
  "fuente": "sensor",
  "detalles": {
    "umbralMaximo": 40,
    "valorActual": 42
  }
}'

echo "✅ Datos cargados con éxito."
