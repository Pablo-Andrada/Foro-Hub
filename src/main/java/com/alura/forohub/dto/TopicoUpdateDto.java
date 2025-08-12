package com.alura.forohub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para actualización de tópico.
 */
public record TopicoUpdateDto(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede tener más de 255 caracteres")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotBlank(message = "El estado es obligatorio")
        @Size(max = 50, message = "El estado no puede tener más de 50 caracteres")
        String status,

        @NotBlank(message = "El curso es obligatorio")
        @Size(max = 150, message = "El nombre del curso no puede tener más de 150 caracteres")
        String curso
) { }
