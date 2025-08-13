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
 * - Se asegura que no se pueda responder a tópicos inactivos.
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
        Usuario autor = usuarioRepository.findById(dto.autorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado (id=" + dto.autorId() + ")"));

        Topico topico = topicoRepository.findById(dto.topicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + dto.topicoId() + ")"));

        // No permitir respuestas a tópicos inactivos
        if (!Boolean.TRUE.equals(topico.getActivo())) {
            throw new RecursoNoEncontradoException("No se puede responder a un tópico inactivo (id=" + topico.getId() + ")");
        }

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
        return respuestaRepository.findByTopicoIdAndActivoTrue(topicoId, pageable)
                .map(this::mapToResponseDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaResponseDto obtenerDetalle(Long id) {
        Respuesta r = respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")"));

        if (!Boolean.TRUE.equals(r.getActivo())) {
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

        existente.setActivo(false);
        respuestaRepository.save(existente);
    }

    @Override
    @Transactional
    public RespuestaResponseDto reactivarRespuesta(Long id) {
        Respuesta existente = respuestaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Respuesta no encontrada (id=" + id + ")"));

        if (Boolean.TRUE.equals(existente.getActivo())) {
            return mapToResponseDto(existente);
        }

        existente.setActivo(true);
        Respuesta reactivada = respuestaRepository.save(existente);
        return mapToResponseDto(reactivada);
    }

    private RespuestaResponseDto mapToResponseDto(Respuesta r) {
        Long autorId = null;
        String autorNombre = null;
        if (r.getAutor() != null) {
            autorId = r.getAutor().getId();
            autorNombre = r.getAutor().getNombre();
        }
        Long topicoId = r.getTopico() != null ? r.getTopico().getId() : null;

        return new RespuestaResponseDto(
                r.getId(),
                r.getMensaje(),
                r.getFechaCreacion(),
                autorId,
                autorNombre,
                topicoId
        );
    }
}
