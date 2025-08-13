package com.alura.forohub.controller;

import com.alura.forohub.dto.RespuestaCreateDto;
import com.alura.forohub.dto.RespuestaResponseDto;
import com.alura.forohub.dto.RespuestaUpdateDto;
import com.alura.forohub.service.RespuestaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador REST para respuestas.
 * Rutas bajo /api/respuestas
 */
@RestController
@RequestMapping("/api/respuestas")
public class RespuestaController {

    private final RespuestaService respuestaService;

    public RespuestaController(RespuestaService respuestaService) {
        this.respuestaService = respuestaService;
    }

    /**
     * POST /api/respuestas
     * Crea una nueva respuesta.
     * Devuelve 201 Created con ubicación del recurso.
     */
    @PostMapping
    public ResponseEntity<RespuestaResponseDto> crearRespuesta(@Valid @RequestBody RespuestaCreateDto dto) {
        RespuestaResponseDto creado = respuestaService.crearRespuesta(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();

        return ResponseEntity.created(location).body(creado);
    }

    /**
     * GET /api/respuestas?topicoId={id}
     * Listado paginado de respuestas de un tópico (activo=true).
     */
    @GetMapping
    public ResponseEntity<Page<RespuestaResponseDto>> listarPorTopico(
            @RequestParam(name = "topicoId") Long topicoId,
            Pageable pageable) {
        Page<RespuestaResponseDto> page = respuestaService.listarPorTopico(topicoId, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/respuestas/{id}
     * Devuelve detalle de una respuesta activa.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RespuestaResponseDto> detalle(@PathVariable Long id) {
        RespuestaResponseDto dto = respuestaService.obtenerDetalle(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /api/respuestas/{id}
     * Actualiza el mensaje de una respuesta.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaResponseDto> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody RespuestaUpdateDto dto) {
        RespuestaResponseDto actualizado = respuestaService.actualizarRespuesta(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/respuestas/{id}
     * Borrado lógico de una respuesta.
     * Devuelve 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        respuestaService.eliminarRespuesta(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/respuestas/{id}/reactivar
     * Reactiva una respuesta previamente eliminada (activo = true).
     * Devuelve 200 OK + DTO reactivado.
     */
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<RespuestaResponseDto> reactivar(@PathVariable Long id) {
        RespuestaResponseDto reactivada = respuestaService.reactivarRespuesta(id);
        return ResponseEntity.ok(reactivada);
    }
}
