// package com.example.persistencia.poliglota.controller;

// import com.example.persistencia.poliglota.config.AlertaAutoGenerator;
// import com.example.persistencia.poliglota.service.mongo.AlertaService;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/api/mongo/alertas/admin")
// public class AlertaAdminController {

//     private final AlertaService alertaService;
//     private final AlertaAutoGenerator alertaAutoGenerator;

//     public AlertaAdminController(AlertaService alertaService, AlertaAutoGenerator alertaAutoGenerator) {
//         this.alertaService = alertaService;
//         this.alertaAutoGenerator = alertaAutoGenerator;
//     }

//     /**
//      * 🔄 Endpoint administrativo: borra todas las alertas y las regenera automáticamente
//      * desde los sensores activos de Cassandra.
//      */
//     @DeleteMapping("/recrear")
//     public ResponseEntity<String> recrearAlertas() {
//         try {
//             int eliminadas = alertaService.eliminarTodas();
//             alertaAutoGenerator.run();
//             return ResponseEntity.ok("✅ Se eliminaron " + eliminadas + " alertas y se regeneraron automáticamente.");
//         } catch (Exception e) {
//             return ResponseEntity.internalServerError().body("❌ Error al recrear alertas: " + e.getMessage());
//         }
//     }
// }
