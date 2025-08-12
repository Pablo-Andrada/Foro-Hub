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
 * Métodos pensados para:
 *  - listar respuestas de un tópico
 *  - ofrecer paginación cuando hay muchas respuestas
 */
@Repository
public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {

    /**
     * Todas las respuestas asociadas a un tópico (por id del tópico).
     * Útil para listar en el detalle de un tópico.
     */
    List<Respuesta> findByTopicoId(Long topicoId);

    /**
     * Versión con paginación, si querés mostrar páginas de respuestas.
     */
    Page<Respuesta> findByTopicoId(Long topicoId, Pageable pageable);
}
