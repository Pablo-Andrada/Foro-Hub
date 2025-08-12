package com.alura.forohub.service;

import com.alura.forohub.dto.RespuestaCreateDto;
import com.alura.forohub.dto.RespuestaResponseDto;
import com.alura.forohub.dto.RespuestaUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contrato del servicio de respuestas.
 * Define claramente las operaciones que expone el controlador.
 */
public interface RespuestaService {

    /**
     * Crea una nueva respuesta y devuelve el DTO de respuesta.
     */
    RespuestaResponseDto crearRespuesta(RespuestaCreateDto dto);

    /**
     * Lista las respuestas de un tópico especificado (paginado).
     * Se espera que implemente filtrado por activo = true (borrado lógico).
     */
    Page<RespuestaResponseDto> listarPorTopico(Long topicoId, Pageable pageable);

    /**
     * Devuelve el detalle de una respuesta por su id.
     */
    RespuestaResponseDto obtenerDetalle(Long id);

    /**
     * Actualiza el mensaje de una respuesta existente.
     */
    RespuestaResponseDto actualizarRespuesta(Long id, RespuestaUpdateDto dto);

    /**
     * "Elimina" una respuesta mediante borrado lógico (activo = false).
     */
    void eliminarRespuesta(Long id);

    /**
     * Reactiva una respuesta previamente eliminada (opcional).
     */
    RespuestaResponseDto reactivarRespuesta(Long id);
}
