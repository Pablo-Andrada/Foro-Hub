package com.alura.forohub.dto;

import java.time.LocalDateTime;

/**
 * DTO que devolvemos al cliente con los datos de un tópico.
 */
public record TopicoResponseDto(
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fechaCreacion,
        String status,
        Long autorId,
        String autorNombre,
        String curso
) { }
