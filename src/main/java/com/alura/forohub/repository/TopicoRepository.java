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
 * Repositorio para la entidad Topico.
 *
 * He conservado tus métodos originales y añadí métodos "AndActivoTrue"
 * que el servicio usa para implementar borrado lógico sin romper nada.
 *
 * - NO elimines los métodos existentes si los necesites en otras partes.
 * - Usa los métodos que terminan en "AndActivoTrue" cuando quieras
 *   trabajar solo con tópicos activos (GET, PUT, DELETE lógico).
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
     * Ejemplo: paginación general (heredado de JpaRepository).
     * Nota: este findAll(Pageable) trae TODO (activos e inactivos).
     * Para listados normales usar findByActivoTrue(pageable).
     */
    Page<Topico> findAll(Pageable pageable);

    // ------------------------------------------------------------
    // MÉTODOS PARA BORRADO LÓGICO / FILTRADO (añadidos)
    // ------------------------------------------------------------

    /**
     * Busca un tópico por id solo si está activo.
     * Útil para GET detalle y UPDATE donde no queremos operar sobre inactivos.
     */
    Optional<Topico> findByIdAndActivoTrue(Long id);

    /**
     * Listado paginado solo con tópicos activos (excluye borrados lógicamente).
     * Usar esto para el endpoint de listado principal.
     */
    Page<Topico> findByActivoTrue(Pageable pageable);

    /**
     * Listado primeros 10 activos ordenados por fecha.
     */
    List<Topico> findTop10ByActivoTrueOrderByFechaCreacionAsc();

    /**
     * Búsqueda por curso solo activos.
     */
    List<Topico> findByCursoAndActivoTrue(String curso);

    /**
     * Búsqueda por curso y rango de fechas solo activos.
     */
    List<Topico> findByCursoAndFechaCreacionBetweenAndActivoTrue(String curso, LocalDateTime start, LocalDateTime end);
}
