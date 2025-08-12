package com.alura.forohub.repository;

import com.alura.forohub.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Usuario.
 * Extiende JpaRepository para disponer de operaciones CRUD básicas.
 *
 * Métodos útiles añadidos:
 *  - findByUsername / findByEmail  -> para buscar datos de autenticación
 *  - existsByUsername / existsByEmail -> para validaciones antes de crear usuarios
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

}
