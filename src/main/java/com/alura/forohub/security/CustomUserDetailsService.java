package com.alura.forohub.security;

import com.alura.forohub.model.Usuario;
import com.alura.forohub.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado para que Spring Security cargue usuarios desde la DB.
 * Es utilizado por JwtFilter al validar el token.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca un usuario por su nombre en la base de datos.
     * Si no lo encuentra, lanza excepción.
     *
     * @param username nombre de usuario (String)
     * @return objeto UserDetails con username, password y roles
     * @throws UsernameNotFoundException si no existe el usuario
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Se retorna un User de Spring con los datos y rol
        return new User(
                usuario.getUsername(),
                usuario.getPassword(),
                Collections.singleton(() -> usuario.getRol()) // Autoridad con el rol real
        );
    }
}
