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
 * Incluye:
 *  - Métodos originales (sin filtro de activo) para compatibilidad.
 *  - Métodos filtrados por activo=true para manejo de borrado lógico.
 *
 * Nota: `JpaRepository` ya trae métodos como findAll(), findById(), save(), delete(), etc.
 */
@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    // ---------------------------
    // MÉTODOS ORIGINALES (compatibilidad con código existente)
    // ---------------------------

    /**
     * Lista todas las respuestas asociadas a un tópico (sin paginar).
     * Incluye también las respuestas inactivas (activo=false).
     */
    List<Respuesta> findByTopicoId(Long topicoId);

    /**
     * Lista todas las respuestas asociadas a un tópico (paginadas).
     * Incluye también las respuestas inactivas (activo=false).
     */
    Page<Respuesta> findByTopicoId(Long topicoId, Pageable pageable);

    // ---------------------------
    // MÉTODOS RECOMENDADOS (filtran solo las activas)
    // ---------------------------

    /**
     * Lista paginada de respuestas activas (activo=true) para un tópico.
     * Usado en RespuestaServiceImpl.listarPorTopico().
     */
    Page<Respuesta> findByTopicoIdAndActivoTrue(Long topicoId, Pageable pageable);

    /**
     * Lista simple (sin paginar) de respuestas activas para un tópico.
     */
    List<Respuesta> findByTopicoIdAndActivoTrue(Long topicoId);

    /**
     * Lista de respuestas activas por id de autor (sin paginar).
     */
    List<Respuesta> findByAutorIdAndActivoTrue(Long autorId);

    /**
     * Búsqueda paginada de todas las respuestas.
     *
     * ⚠️ Este método ya existe implícitamente en JpaRepository como `findAll(Pageable)`.
     *     Se deja aquí solo si querés documentarlo o sobrescribirlo.
     */
    @Override
    Page<Respuesta> findAll(Pageable pageable);
}
