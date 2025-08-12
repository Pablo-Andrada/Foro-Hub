package com.alura.forohub.service;

import com.alura.forohub.dto.RespuestaCreateDto;
import com.alura.forohub.dto.RespuestaResponseDto;
import com.alura.forohub.dto.RespuestaUpdateDto;
import com.alura.forohub.exception.RecursoNoEncontradoException;
import com.alura.forohub.model.Respuesta;
import com.alura.forohub.model.Topico;
import com.alura.forohub.model.Usuario;
import com.alura.forohub.repository.RespuestaRepository;
import com.alura.forohub.repository.TopicoRepository;
import com.alura.forohub.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de respuestas.
 * - Maneja transacciones
 * - Usa repositorios para persistencia
 * - Mapea entidad <-> DTO de respuesta
 */
@Service
public class RespuestaServiceImpl implements RespuestaService {

    private final RespuestaRepository respuestaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TopicoRepository topicoRepository;

    public RespuestaServiceImpl(RespuestaRepository respuestaRepository,
                                UsuarioRepository usuarioRepository,
                                TopicoRepository topicoRepository) {
        this.respuestaRepository = respuestaRepository;
        this.usuarioRepository = usuarioRepository;
        this.topicoRepository = topicoRepository;
    }

    @Override
    @Transactional
    public RespuestaResponseDto crearRespuesta(RespuestaCreateDto dto) {
        // Verificar autor
        Usuario autor = usuarioRepository.findById(dto.autorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado (id=" + dto.autorId() + ")"));

        // Verificar tópico
        Topico topico = topicoRepository.findById(dto.topicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + dto.topicoId() + ")"));

        // Construcción y persistencia
        Respuesta r = new Respuesta();
        r.setMensaje(dto.mensaje().trim());
        r.setAutor(autor);
        r.setTopico(topico);
        r.setFechaCreacion(LocalDateTime.now());
        r.setActivo(true);

        Respuesta guardada = respuestaRepository.save(r);
        return mapToResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RespuestaResponseDto> listarPorTopico(Long topicoId, Pageable pageable) {
        // Usamos el método que filtra por activo = true para no devolver borrados lógicos
        return respuestaRepository.findByTopicoIdAndActivoTrue(topicoId, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaResponseDto obtenerDetalle(Long id) {
        Respuesta r = respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")"));

        if (!Boolean.TRUE.equals(r.getActivo())) {
            // Si está inactiva, para el consumidor equivale a "no encontrada"
            throw new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")");
        }
        return mapToResponseDto(r);
    }

    @Override
    @Transactional
    public RespuestaResponseDto actualizarRespuesta(Long id, RespuestaUpdateDto dto) {
        Respuesta existente = respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")"));

        if (!Boolean.TRUE.equals(existente.getActivo())) {
            throw new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")");
        }

        existente.setMensaje(dto.mensaje().trim());
        Respuesta actualizada = respuestaRepository.save(existente);
        return mapToResponseDto(actualizada);
    }

    @Override
    @Transactional
    public void eliminarRespuesta(Long id) {
        Respuesta existente = respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")"));

        // Borrado lógico: marcamos activo = false
        existente.setActivo(false);
        respuestaRepository.save(existente);
    }

    @Override
    @Transactional
    public RespuestaResponseDto reactivarRespuesta(Long id) {
        Respuesta existente = respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")"));

        if (Boolean.TRUE.equals(existente.getActivo())) {
            // Si ya está activa devolvemos su DTO (no es error)
            return mapToResponseDto(existente);
        }

        existente.setActivo(true);
        Respuesta reactivada = respuestaRepository.save(existente);
        return mapToResponseDto(reactivada);
    }

    // ---- util privado para mapear entidad -> DTO
    private com.alura.forohub.dto.RespuestaResponseDto mapToResponseDto(Respuesta r) {
        Long autorId = null;
        String autorNombre = null;
        if (r.getAutor() != null) {
            autorId = r.getAutor().getId();
            autorNombre = r.getAutor().getNombre();
        }
        Long topicoId = r.getTopico() != null ? r.getTopico().getId() : null;

        return new com.alura.forohub.dto.RespuestaResponseDto(
                r.getId(),
                r.getMensaje(),
                r.getFechaCreacion(),
                autorId,
                autorNombre,
                topicoId
        );
    }
}
