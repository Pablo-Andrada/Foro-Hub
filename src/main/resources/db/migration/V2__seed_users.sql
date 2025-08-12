-- V2__seed_users.sql
-- Inserta un usuario de prueba con contraseña BCrypt (recomendado)
INSERT INTO usuarios (id, username, nombre, email, password, activo, rol, created_at)
VALUES (1, 'testuser', 'Usuario de Prueba', 'test@example.com', '$2a$10$$2a$10$cAvr3dZqSC.XNAiEGiu51O96lc/moUMku5ida3pKBzWzp41ficdtS', 1, 'ROLE_USER', NOW());
