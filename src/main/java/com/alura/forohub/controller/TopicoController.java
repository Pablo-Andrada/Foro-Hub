package com.alura.forohub.controller;

import com.alura.forohub.dto.TopicoCreateDto;
import com.alura.forohub.dto.TopicoResponseDto;
import com.alura.forohub.dto.TopicoUpdateDto;
import com.alura.forohub.service.TopicoService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controlador REST para tópicos.
 * Rutas bajo /api/topicos
 */
@RestController
@RequestMapping("/api/topicos")
public class TopicoController {

    private final TopicoService topicoService;

    public TopicoController(TopicoService topicoService) {
        this.topicoService = topicoService;
    }

    /**
     * POST /api/topicos
     * Crea un nuevo tópico. Devuelve 201 Created con el recurso creado.
     * (Permitir a usuarios autenticados estándar)
     */
    @PostMapping
    public ResponseEntity<TopicoResponseDto> crearTopico(@Valid @RequestBody TopicoCreateDto dto) {
        TopicoResponseDto creado = topicoService.crearTopico(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.id())
                .toUri();

        return ResponseEntity.created(location).body(creado);
    }

    /**
     * GET /api/topicos
     * Listado paginado de tópicos activos.
     * @ParameterObject mejora cómo springdoc/swaggeR UI renderiza los campos de Pageable.
     */
    @GetMapping
    public ResponseEntity<Page<TopicoResponseDto>> listarTopicos(@ParameterObject Pageable pageable) {
        Page<TopicoResponseDto> page = topicoService.listarTopicos(pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/topicos/{id}
     * Detalle de un tópico activo.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponseDto> detalle(@PathVariable Long id) {
        TopicoResponseDto dto = topicoService.obtenerDetalle(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * PUT /api/topicos/{id}
     * Actualiza un tópico existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TopicoResponseDto> actualizar(@PathVariable Long id,
                                                        @Valid @RequestBody TopicoUpdateDto dto) {
        TopicoResponseDto actualizado = topicoService.actualizarTopico(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/topicos/{id}
     * Borrado lógico del tópico.
     * SOLO ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        topicoService.eliminarTopico(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/topicos/{id}/reactivar
     * Reactiva un tópico previamente borrado (activo = true).
     * Cambiado a POST para compatibilidad. SOLO ADMIN.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/reactivar")
    public ResponseEntity<TopicoResponseDto> reactivar(@PathVariable Long id) {
        TopicoResponseDto reactivado = topicoService.reactivarTopico(id);
        return ResponseEntity.ok(reactivado);
    }
}
