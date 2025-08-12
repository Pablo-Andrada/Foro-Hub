package com.alura.forohub.controller;

import com.alura.forohub.dto.TopicoCreateDto;
import com.alura.forohub.dto.TopicoResponseDto;
import com.alura.forohub.dto.TopicoUpdateDto;
import com.alura.forohub.service.TopicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para manejar operaciones CRUD sobre Tópicos.
 */
@RestController
@RequestMapping("/api/topicos") // prefijo API estándar
public class TopicoController {

    private final TopicoService topicoService;

    public TopicoController(TopicoService topicoService) {
        this.topicoService = topicoService;
    }

    // Crear un nuevo tópico
    @PostMapping
    public ResponseEntity<TopicoResponseDto> crearTopico(@Valid @RequestBody TopicoCreateDto dto) {
        TopicoResponseDto creado = topicoService.crearTopico(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Listar tópicos paginados (solo activos)
    @GetMapping
    public ResponseEntity<Page<TopicoResponseDto>> listarTopicos(Pageable pageable) {
        Page<TopicoResponseDto> page = topicoService.listarTopicos(pageable);
        return ResponseEntity.ok(page);
    }

    // Obtener detalle de un tópico por id
    @GetMapping("/{id}")
    public ResponseEntity<TopicoResponseDto> obtenerDetalle(@PathVariable Long id) {
        TopicoResponseDto dto = topicoService.obtenerDetalle(id);
        return ResponseEntity.ok(dto);
    }

    // Actualizar un tópico por id (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<TopicoResponseDto> actualizarTopico(
            @PathVariable Long id,
            @Valid @RequestBody TopicoUpdateDto dto) {

        TopicoResponseDto actualizado = topicoService.actualizarTopico(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar un tópico por id (DELETE) - borrado lógico
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {
        topicoService.eliminarTopico(id);
        return ResponseEntity.noContent().build();  // 204 No Content, borrado lógico OK
    }
}
