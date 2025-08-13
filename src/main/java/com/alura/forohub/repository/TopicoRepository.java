package com.alura.forohub.repository;

import com.alura.forohub.model.Topico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Topico.
 *
 * Incluye métodos para:
 *  - Comprobación de duplicados (titulo + mensaje)
 *  - Comprobación de duplicado excluyendo un id (útil en updates)
 *  - Búsquedas y paginación considerando el flag 'activo' para borrado lógico
 *
 * Rutas de uso:
 *  - findByActivoTrue(Pageable) -> listado de tópicos activos (para GET)
 *  - findByIdAndActivoTrue(id) -> obtener detalle solo si está activo
 */
@Repository
public interface TopicoRepository extends JpaRepository<Topico, Long> {

    /**
     * Comprueba si ya existe un tópico con el mismo título y mensaje.
     * Usado en creación para evitar duplicados exactos.
     */
    boolean existsByTituloAndMensaje(String titulo, String mensaje);

    /**
     * Comprueba si existe un tópico con el mismo título y mensaje
     * pero con un id distinto al provisto. útil para UPDATE donde no
     * queremos considerar el propio registro como duplicado.
     *
     * Ejemplo de uso en el servicio:
     *   if (topicoRepository.existsByTituloAndMensajeAndIdNot(titulo, mensaje, id)) -> lanzar duplicado
     */
    boolean existsByTituloAndMensajeAndIdNot(String titulo, String mensaje, Long id);

    // --- Métodos auxiliares / consultas comunes ---

    List<Topico> findTop10ByOrderByFechaCreacionAsc();

    List<Topico> findByCurso(String curso);

    List<Topico> findByCursoAndFechaCreacionBetween(String curso, LocalDateTime start, LocalDateTime end);

    @Override
    Page<Topico> findAll(Pageable pageable);

    /**
     * Busca un tópico por id solo si está activo (filtra borrados lógicos).
     */
    Optional<Topico> findByIdAndActivoTrue(Long id);

    /**
     * Listado paginado solo con tópicos activos (excluye borrados lógicamente).
     */
    Page<Topico> findByActivoTrue(Pageable pageable);

    List<Topico> findTop10ByActivoTrueOrderByFechaCreacionAsc();

    List<Topico> findByCursoAndActivoTrue(String curso);

    List<Topico> findByCursoAndFechaCreacionBetweenAndActivoTrue(String curso, LocalDateTime start, LocalDateTime end);
}
