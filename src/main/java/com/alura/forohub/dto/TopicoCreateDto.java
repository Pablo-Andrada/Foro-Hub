package com.alura.forohub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para la creación de tópicos — definido como record para ser inmutable y conciso.
 */
public record TopicoCreateDto(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede tener más de 255 caracteres")
        String titulo,

        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,

        @NotNull(message = "El id del autor es obligatorio")
        Long autorId,

        @NotBlank(message = "El curso es obligatorio")
        @Size(max = 150, message = "El nombre del curso no puede tener más de 150 caracteres")
        String curso
) { }
