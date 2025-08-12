package com.alura.forohub.service;

import com.alura.forohub.dto.TopicoCreateDto;
import com.alura.forohub.dto.TopicoResponseDto;
import com.alura.forohub.dto.TopicoUpdateDto;
import com.alura.forohub.exception.DuplicadoException;
import com.alura.forohub.exception.RecursoNoEncontradoException;
import com.alura.forohub.model.Topico;
import com.alura.forohub.model.Usuario;
import com.alura.forohub.repository.TopicoRepository;
import com.alura.forohub.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de tópicos.
 * Maneja transacciones, validaciones de negocio y mapeo entidad ↔ DTO.
 */
@Service
public class TopicoServiceImpl implements TopicoService {

    private final TopicoRepository topicoRepository;
    private final UsuarioRepository usuarioRepository;

    public TopicoServiceImpl(TopicoRepository topicoRepository, UsuarioRepository usuarioRepository) {
        this.topicoRepository = topicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public TopicoResponseDto crearTopico(TopicoCreateDto dto) {
        Usuario autor = usuarioRepository.findById(dto.autorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado (id=" + dto.autorId() + ")"));

        // Normalizar trim para evitar falsos negativos por espacios
        String tituloTrim = dto.titulo() != null ? dto.titulo().trim() : null;
        String mensajeTrim = dto.mensaje() != null ? dto.mensaje().trim() : null;

        if (topicoRepository.existsByTituloAndMensaje(tituloTrim, mensajeTrim)) {
            throw new DuplicadoException("Ya existe un tópico con el mismo título y mensaje.");
        }

        Topico topico = new Topico();
        topico.setTitulo(tituloTrim);
        topico.setMensaje(mensajeTrim);
        topico.setCurso(dto.curso().trim());
        topico.setAutor(autor);
        topico.setFechaCreacion(LocalDateTime.now());
        topico.setStatus("ABIERTO");
        topico.setActivo(true);

        // Guardar y devolver DTO
        Topico guardado = topicoRepository.save(topico);
        return mapToResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicoResponseDto> listarTopicos(Pageable pageable) {
        // Sólo tópicos activos (excluye los borrados lógicamente)
        return topicoRepository.findByActivoTrue(pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public TopicoResponseDto obtenerDetalle(Long id) {
        Topico topico = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + id + ")"));
        return mapToResponseDto(topico);
    }

    @Override
    @Transactional
    public TopicoResponseDto actualizarTopico(Long id, TopicoUpdateDto dto) {
        Topico existente = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + id + ")"));

        boolean tituloChanged = !existente.getTitulo().equals(dto.titulo().trim());
        boolean mensajeChanged = !existente.getMensaje().equals(dto.mensaje().trim());

        if ((tituloChanged || mensajeChanged) && topicoRepository.existsByTituloAndMensaje(dto.titulo(), dto.mensaje())) {
            throw new DuplicadoException("Otro tópico ya tiene ese título y mensaje.");
        }

        existente.setTitulo(dto.titulo().trim());
        existente.setMensaje(dto.mensaje().trim());
        existente.setStatus(dto.status().trim());
        existente.setCurso(dto.curso().trim());

        Topico actualizado = topicoRepository.save(existente);
        return mapToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminarTopico(Long id) {
        Topico existente = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + id + ")"));

        // Borrado lógico: poner activo en false
        existente.setActivo(false);
        topicoRepository.save(existente);
    }

    /**
     * Reactiva un tópico marcado como eliminado (activo = false).
     * - Si no existe el tópico -> lanza RecursoNoEncontradoException (404).
     * - Si ya estaba activo -> devuelve el DTO tal como está (idempotente).
     */
    @Override
    @Transactional
    public TopicoResponseDto reactivarTopico(Long id) {
        // Usamos findById (sin filtrar por activo) porque queremos poder reactivar los inactivos.
        Topico existente = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + id + ")"));

        // Si ya está activo no hacemos nada (comportamiento idempotente)
        if (Boolean.TRUE.equals(existente.getActivo())) {
            return mapToResponseDto(existente);
        }

        // Reactivar
        existente.setActivo(true);
        Topico reactivado = topicoRepository.save(existente);

        return mapToResponseDto(reactivado);
    }

    // ---- util privado para mapear entidad -> DTO de respuesta
    private TopicoResponseDto mapToResponseDto(Topico t) {
        Long autorId = null;
        String autorNombre = null;
        Usuario autor = t.getAutor();
        if (autor != null) {
            autorId = autor.getId();
            autorNombre = autor.getNombre();
        }
        return new TopicoResponseDto(
                t.getId(),
                t.getTitulo(),
                t.getMensaje(),
                t.getFechaCreacion(),
                t.getStatus(),
                autorId,
                autorNombre,
                t.getCurso()
        );
    }
}
