package com.example.persistencia.poliglota.model.sql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // ✅ Constructor vacío (necesario para JPA)
@AllArgsConstructor // ✅ Constructor completo (si querés)
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private String email;

    private String password;

        // ✅ Relación correcta: muchos usuarios pueden tener un rol
    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    // ✅ Constructor personalizado (para tu TransaccionService)
    public Usuario(String nombre, String email) {
        this.nombre = nombre;
        this.email = email;
    }
}
