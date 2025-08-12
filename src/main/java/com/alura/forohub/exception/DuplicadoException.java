package com.alura.forohub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción para indicar intento de crear un recurso duplicado.
 * Se traduce a HTTP 400 Bad Request.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicadoException extends RuntimeException {

    public DuplicadoException(String mensaje) {
        super(mensaje);
    }
}
