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

        String tituloTrim = dto.titulo() != null ? dto.titulo().trim() : "";
        String mensajeTrim = dto.mensaje() != null ? dto.mensaje().trim() : "";
        String cursoTrim = dto.curso() != null ? dto.curso().trim() : "";

        if (topicoRepository.existsByTituloAndMensaje(tituloTrim, mensajeTrim)) {
            throw new DuplicadoException("Ya existe un tópico con el mismo título y mensaje.");
        }

        Topico topico = new Topico();
        topico.setTitulo(tituloTrim);
        topico.setMensaje(mensajeTrim);
        topico.setCurso(cursoTrim);
        topico.setAutor(autor);
        topico.setFechaCreacion(LocalDateTime.now());
        topico.setStatus("ABIERTO");
        topico.setActivo(true);

        Topico guardado = topicoRepository.save(topico);
        return mapToResponseDto(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TopicoResponseDto> listarTopicos(Pageable pageable) {
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

        String tituloTrim = dto.titulo() != null ? dto.titulo().trim() : "";
        String mensajeTrim = dto.mensaje() != null ? dto.mensaje().trim() : "";
        String statusTrim = dto.status() != null ? dto.status().trim() : "";
        String cursoTrim = dto.curso() != null ? dto.curso().trim() : "";

        boolean tituloChanged = !existente.getTitulo().equals(tituloTrim);
        boolean mensajeChanged = !existente.getMensaje().equals(mensajeTrim);

        if (tituloChanged || mensajeChanged) {
            // Usamos el método que excluye al propio id para evitar false-positives
            if (topicoRepository.existsByTituloAndMensajeAndIdNot(tituloTrim, mensajeTrim, id)) {
                throw new DuplicadoException("Otro tópico ya tiene ese título y mensaje.");
            }
        }

        existente.setTitulo(tituloTrim);
        existente.setMensaje(mensajeTrim);
        existente.setStatus(statusTrim);
        existente.setCurso(cursoTrim);

        Topico actualizado = topicoRepository.save(existente);
        return mapToResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminarTopico(Long id) {
        Topico existente = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + id + ")"));

        existente.setActivo(false);
        topicoRepository.save(existente);
    }

    @Override
    @Transactional
    public TopicoResponseDto reactivarTopico(Long id) {
        Topico existente = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Tópico no encontrado (id=" + id + ")"));

        if (Boolean.TRUE.equals(existente.getActivo())) {
            return mapToResponseDto(existente);
        }

        existente.setActivo(true);
        Topico reactivado = topicoRepository.save(existente);
        return mapToResponseDto(reactivado);
    }

    private TopicoResponseDto mapToResponseDto(Topico t) {
        Long autorId = null;
        String autorNombre = null;
        if (t.getAutor() != null) {
            autorId = t.getAutor().getId();
            autorNombre = t.getAutor().getNombre();
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
