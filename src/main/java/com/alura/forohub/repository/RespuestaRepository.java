package com.alura.forohub.repository;

import com.alura.forohub.model.Respuesta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad Respuesta.
 *
 * He incluido tanto las consultas simples que ya tenías como versiones
 * que filtran por `activo = true` (borrado lógico). De esta forma mantenemos
 * compatibilidad con código existente y añadimos métodos útiles para el servicio.
 *
 * Conservá los métodos antiguos si hay código que los llama; usá los métodos
 * con "AndActivoTrue" desde el servicio/controller para ignorar respuestas borradas.
 */
@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // ---------------------------
    // MÉTODOS ORIGINALES (compatibilidad)
    // ---------------------------

    /**
     * Todas las respuestas asociadas a un tópico (por id del tópico).
     * Útil para listar en el detalle de un tópico (sin paginar).
     * <p>
     * Nota: devuelve también las respuestas marcadas como activo = false.
     * Para evitar eso, usá findByTopicoIdAndActivoTrue(...)
     */
    List<Respuesta> findByTopicoId(Long topicoId);

    /**
     * Versión con paginación (no filtra por activo).
     */
    Page<Respuesta> findByTopicoId(Long topicoId, Pageable pageable);

    // ---------------------------
    // MÉTODOS RECOMENDADOS (filtrando por activo = true)
    // ---------------------------

    /**
     * Lista paginada de respuestas activas para un tópico.
     * Usar cuando querés mostrar solo respuestas no borradas.
     */
    Page<Respuesta> findByTopicoIdAndActivoTrue(Long topicoId, Pageable pageable);

    /**
     * Lista simple (sin paginar) de respuestas activas de un tópico.
     */
    List<Respuesta> findByTopicoIdAndActivoTrue(Long topicoId);

    /**
     * Buscar respuestas activas por autor.
     */
    List<Respuesta> findByAutorIdAndActivoTrue(Long autorId);

    /**
     * Búsqueda paginada general (por si necesitás listar todas las respuestas activas).
     * La implementación de Spring Data permite pasar filtros adicionales si lo necesitás.
     */
    Page<Respuesta> findAll(Pageable pageable);
}
