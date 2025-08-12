package com.alura.forohub.service;

import com.alura.forohub.dto.TopicoCreateDto;
import com.alura.forohub.dto.TopicoResponseDto;
import com.alura.forohub.dto.TopicoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Contrato del servicio de tópicos.
 */
public interface TopicoService {

    TopicoResponseDto crearTopico(TopicoCreateDto dto);

    Page<TopicoResponseDto> listarTopicos(Pageable pageable);

    TopicoResponseDto obtenerDetalle(Long id);

    TopicoResponseDto actualizarTopico(Long id, TopicoUpdateDto dto);

    void eliminarTopico(Long id);
}
