package com.alura.forohub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")  // Mapeo con la tabla usuarios en la base
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-incremental en MySQL
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username; // Nombre de usuario único

    @Column(nullable = false, length = 150)
    private String nombre;   // Nombre real o completo

    @Column(nullable = false, unique = true, length = 150)
    private String email;    // Email único para contacto / login

    @Column(nullable = false, length = 255)
    private String password; // Contraseña hasheada (nunca guardes en texto plano)

    @Column(nullable = false)
    private Boolean activo = true;  // Indica si el usuario está activo o no

    @Column(nullable = false, length = 50)
    private String rol = "ROLE_USER"; // Roles tipo ROLE_USER, ROLE_ADMIN, etc.

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Fecha de creación

    // Para posibles futuras relaciones se puede agregar aquí,
    // como lista de tópicos o respuestas creadas, pero no es obligatorio aún.

}
