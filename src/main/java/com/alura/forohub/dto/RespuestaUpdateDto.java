package com.alura.forohub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar una respuesta. Por ahora solo se permite cambiar el mensaje.
 */
public record RespuestaUpdateDto(
        @NotBlank(message = "El mensaje de la respuesta no puede estar vacío")
        String mensaje
) { }
