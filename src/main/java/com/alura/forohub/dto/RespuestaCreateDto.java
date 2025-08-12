package com.alura.forohub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (record) para crear una respuesta.
 * Inmutable y conciso — validaciones incluidas para la entrada.
 */
public record RespuestaCreateDto(
        @NotBlank(message = "El mensaje de la respuesta es obligatorio")
        String mensaje,

        @NotNull(message = "El id del autor es obligatorio")
        Long autorId,

        @NotNull(message = "El id del tópico es obligatorio")
        Long topicoId
) { }
