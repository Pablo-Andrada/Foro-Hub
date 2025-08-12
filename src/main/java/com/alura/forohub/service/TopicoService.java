package com.alura.forohub.service;

import com.alura.forohub.dto.TopicoCreateDto;
import com.alura.forohub.dto.TopicoResponseDto;
import com.alura.forohub.dto.TopicoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contrato del servicio de tópicos.
 * Aquí se declaran las operaciones que el controlador podrá invocar.
 */
public interface TopicoService {

    TopicoResponseDto crearTopico(TopicoCreateDto dto);

    Page<TopicoResponseDto> listarTopicos(Pageable pageable);

    TopicoResponseDto obtenerDetalle(Long id);

    TopicoResponseDto actualizarTopico(Long id, TopicoUpdateDto dto);

    void eliminarTopico(Long id);

    /**
     * Reactiva un tópico que fue borrado lógicamente (activo = false).
     * Si el tópico no existe lanzará una excepción de tipo RecursoNoEncontradoException.
     *
     * Retorna el TopicoResponseDto actualizado (activo = true).
     */
    TopicoResponseDto reactivarTopico(Long id);
}
