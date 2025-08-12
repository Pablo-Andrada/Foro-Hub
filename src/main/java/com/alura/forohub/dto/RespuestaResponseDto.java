package com.alura.forohub.dto;

import java.time.LocalDateTime;

/**
 * DTO que devolvemos al cliente con los datos de una respuesta.
 */
public record RespuestaResponseDto(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        Long autorId,
        String autorNombre,
        Long topicoId
) { }
