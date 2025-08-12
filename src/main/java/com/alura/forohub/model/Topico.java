//package com.alura.forohub.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "topicos",
//        uniqueConstraints = @UniqueConstraint(
//                columnNames = {"titulo", "mensaje"})) // Para evitar duplicados según tu esquema
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
//public class Topico {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, length = 255)
//    private String titulo;
//
//    @Column(nullable = false, columnDefinition = "TEXT")
//    private String mensaje;
//
//    @Column(name = "fecha_creacion", nullable = false)
//    private LocalDateTime fechaCreacion = LocalDateTime.now();
//
//    @Column(nullable = false, length = 50)
//    private String status = "ABIERTO";  // Ej: ABIERTO, CERRADO, RESPONDIDO
//
//    @ManyToOne(fetch = FetchType.LAZY)  // Muchos tópicos pueden ser de un mismo autor
//    @JoinColumn(name = "autor_id", nullable = false)
//    private Usuario autor;
//
//    @Column(nullable = false, length = 150)
//    private String curso;
//
//    @Column(nullable = false)
//    private Boolean activo = true;  // Para borrado lógico
//
//}
package com.alura.forohub.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "topicos",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"titulo", "mensaje"})) // Evita duplicados por título y mensaje
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Topico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = false, length = 50)
    private String status = "ABIERTO";  // Ej: ABIERTO, CERRADO, RESPONDIDO

    @ManyToOne(fetch = FetchType.LAZY)  // Relación con autor (muchos tópicos a un autor)
    @JoinColumn(name = "autor_id", nullable = false)
    private Usuario autor;

    @Column(nullable = false, length = 150)
    private String curso;

    @Column(nullable = false)
    private Boolean activo = true;  // Flag para borrado lógico (true = activo)
}
