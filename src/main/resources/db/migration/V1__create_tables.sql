-- V1__create_tables.sql
-- Script inicial para crear las tablas principales del foro
-- No contiene errores de sintaxis para MySQL

SET NAMES utf8mb4;

-- ===============================
-- Tabla usuarios
-- ===============================
-- Aquí almacenamos usuarios que pueden crear tópicos / respuestas.
CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  username VARCHAR(100) NOT NULL,
  nombre VARCHAR(150) NOT NULL,
  email VARCHAR(150) NOT NULL,
  password VARCHAR(255) NOT NULL,    -- password hasheada (guardar hashed)
  activo TINYINT(1) NOT NULL DEFAULT 1,
  rol VARCHAR(50) NOT NULL DEFAULT 'ROLE_USER',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_usuario_username (username),
  UNIQUE KEY uq_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ===============================
-- Tabla topicos
-- ===============================
-- Contiene los tópicos del foro (el foco del challenge).
-- Se añade UNIQUE (titulo+mensaje) para evitar duplicados según requisito.
CREATE TABLE IF NOT EXISTS topicos (
  id BIGINT NOT NULL AUTO_INCREMENT,
  titulo VARCHAR(255) NOT NULL,
  mensaje TEXT NOT NULL,
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  status VARCHAR(50) NOT NULL DEFAULT 'ABIERTO', -- ejemplos: ABIERTO, CERRADO, RESPONDIDO
  autor_id BIGINT NOT NULL,   -- FK a usuarios.id
  curso VARCHAR(150) NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1, -- para borrado lógico
  PRIMARY KEY (id),
  UNIQUE KEY uq_topico_titulo_mensaje (titulo(191), mensaje(191)),
  CONSTRAINT fk_topico_autor FOREIGN KEY (autor_id) REFERENCES usuarios (id)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices para optimizar búsquedas
CREATE INDEX idx_topicos_curso ON topicos (curso);
CREATE INDEX idx_topicos_fecha ON topicos (fecha_creacion);

-- ===============================
-- Tabla respuestas
-- ===============================
-- Respuestas a tópicos
CREATE TABLE IF NOT EXISTS respuestas (
  id BIGINT NOT NULL AUTO_INCREMENT,
  mensaje TEXT NOT NULL,
  fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  autor_id BIGINT NOT NULL,
  topico_id BIGINT NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (id),
  CONSTRAINT fk_respuesta_autor FOREIGN KEY (autor_id) REFERENCES usuarios(id)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT fk_respuesta_topico FOREIGN KEY (topico_id) REFERENCES topicos(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
