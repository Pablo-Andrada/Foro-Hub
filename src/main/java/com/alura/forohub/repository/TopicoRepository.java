package com.alura.forohub.repository;

import com.alura.forohub.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Topico.
 *
 * Incluye métodos de conveniencia para:
 *  - chequear duplicados por titulo+mensaje
 *  - obtener primeros 10 ordenados por fecha asc (opcional del challenge)
 *  - búsqueda por curso y búsqueda por intervalo de fechas (p. ej. por año)
 *  - soporte para paginación (findAll(Pageable))
 */
@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    /**
     * Comprueba si ya existe un tópico con el mismo título y mensaje (regla de negocio).
     */
    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    /**
     * Devuelve los primeros 10 tópicos ordenados por fecha de creación ascendente.
     * (útil para la propuesta "mostrar primeros 10 ordenados por fecha ASC")
     */
    List<Topico> findTop10ByOrderByFechaCreacionAsc();

    /**
     * Búsqueda por curso.
     */
    List<Topico> findByCurso(String curso);

    /**
     * Búsqueda por curso y rango de fechas (por ejemplo, para filtrar por año).
     * Rango cerrado: fechaCreacion entre start (inclusive) y end (inclusive).
     */
    List<Topico> findByCursoAndFechaCreacionBetween(String curso, LocalDateTime start, LocalDateTime end);

    /**
     * Ejemplo: paginación general para listados.
     * El controlador puede aceptar Pageable y delegar aquí.
     */
    Page<Topico> findAll(Pageable pageable);
}
