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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de tópicos.
 *
 * Cambios principales:
 *  - En actualizarTopico() se valida que el usuario autenticado sea:
 *      a) el autor del tópico, o
 *      b) tenga rol ADMIN
 *    Si no, se lanza AccessDeniedException (mapeada por GlobalExceptionHandler a 403).
 *
 *  - Se mantienen las validaciones de duplicados (excluyendo el propio id).
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
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Usuario no encontrado (id=" + dto.autorId() + ")"
                ));

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
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tópico no encontrado (id=" + id + ")"
                ));
        return mapToResponseDto(topico);
    }

    /**
     * Actualizar un tópico: ahora con control de ownership.
     *
     * Reglas:
     *  - Recupera el tópico (debe estar activo).
     *  - Obtiene el username del usuario autenticado desde SecurityContext.
     *  - Si el username coincide con el autor del tópico -> permite.
     *  - Si el usuario tiene ROLE_ADMIN -> permite.
     *  - Si no, lanza AccessDeniedException -> 403.
     */
    @Override
    @Transactional
    public TopicoResponseDto actualizarTopico(Long id, TopicoUpdateDto dto) {
        Topico existente = topicoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tópico no encontrado (id=" + id + ")"
                ));

        // --- Ownership check: obtengo username del contexto ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameAutenticado = null;
        boolean isAdmin = false;

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            // principal puede ser UserDetails o String (depende cómo esté configurado)
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails ud) {
                usernameAutenticado = ud.getUsername();
                isAdmin = ud.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            } else if (principal instanceof String s) {
                usernameAutenticado = s;
                isAdmin = auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            } else {
                // fallback: verificar autoridades
                isAdmin = auth.getAuthorities().stream()
                        .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            }
        }

        // Si no autenticado -> AccessDenied (no debería entrar aquí porque la ruta está protegida)
        if (usernameAutenticado == null && !isAdmin) {
            throw new AccessDeniedException("Acceso denegado: usuario no autenticado.");
        }

        // Comparo con el autor del tópico
        Usuario autor = existente.getAutor();
        String autorUsername = autor != null ? autor.getUsername() : null;

        // Permitir si es admin o si es el autor
        if (!isAdmin && (autorUsername == null || !autorUsername.equals(usernameAutenticado))) {
            throw new AccessDeniedException("Acceso denegado: solo el autor o admin puede modificar este tópico.");
        }

        // --- Validaciones y lógica de duplicado (igual que antes) ---
        String tituloTrim = dto.titulo() != null ? dto.titulo().trim() : "";
        String mensajeTrim = dto.mensaje() != null ? dto.mensaje().trim() : "";
        String statusTrim = dto.status() != null ? dto.status().trim() : "";
        String cursoTrim = dto.curso() != null ? dto.curso().trim() : "";

        boolean tituloChanged = !existente.getTitulo().equals(tituloTrim);
        boolean mensajeChanged = !existente.getMensaje().equals(mensajeTrim);

        if (tituloChanged || mensajeChanged) {
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
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tópico no encontrado (id=" + id + ")"
                ));

        existente.setActivo(false);
        topicoRepository.save(existente);
    }

    @Override
    @Transactional
    public TopicoResponseDto reactivarTopico(Long id) {
        Topico existente = topicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Tópico no encontrado (id=" + id + ")"
                ));

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
