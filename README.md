# ForoHub

**API REST para un foro** — Spring Boot 3, JWT, MySQL, Flyway, JPA.

Este README reúne **todo** lo necesario para arrancar, probar y entender el proyecto: variables de entorno, migraciones, endpoints, ejemplos para Insomnia/Postman, instrucciones para JWT, cómo resetear contraseñas, y pasos recomendados.

---

## Índice

1. Resumen del proyecto
2. Requisitos
3. Variables de entorno / `.env`
4. Cómo ejecutar (local)
5. Migraciones (Flyway) y seeds
6. Cómo generar una `JWT_SECRET` segura
7. Cómo resetear contraseña de un usuario (BCrypt)
8. Endpoints principales (descripción breve)
9. Requests para Insomnia / Postman (todos los ejemplos con método, ruta, headers, body y respuesta esperada)
10. Roles y pruebas de autorización (qué esperar: 401 vs 403)
11. Errores comunes y soluciones rápidas
12. Siguientes pasos recomendados

---

## 1 — Resumen del proyecto

ForoHub es una API REST que permite:

- Registrar usuarios (dev/testing) y autenticarse con JWT.
- Crear, listar, detallar, actualizar, borrar (borrado lógico) y reactivar **tópicos**.
- Crear, listar, detallar, actualizar, borrar (borrado lógico) y reactivar **respuestas**.
- Control de acceso básico con roles (`ROLE_USER`, `ROLE_ADMIN`).
- Persistencia con MySQL y migraciones con Flyway.

Código clave presente en el repo: entidades (`Topico`, `Respuesta`, `Usuario`), controllers (`TopicoController`, `RespuestaController`, `AuthController`), services, repositorios, `JwtFilter`, `JwtUtil`, `SecurityConfig` y migraciones en `src/main/resources/db/migration`.

---

## 2 — Requisitos

- Java 17+
- Maven (4+ recomendado; si usás `mvn` estándar funciona también)
- MySQL 8+
- IDE (IntelliJ recomendado)
- Postman o Insomnia para pruebas

---

## 3 — Variables de entorno (recomendado usar `.env` o exportarlas en tu shell)

- `DB_USERNAME` — usuario MySQL (ej: `root`)
- `DB_PASSWORD` — contraseña MySQL
- `JWT_SECRET` — clave para firmar JWT (OBLIGATORIO: **mínimo 32 bytes**)
- `JWT_EXPIRATION` — (opcional) duración en ms del token, ej: `86400000` (1 día). Si no existe, hay un default en `application.properties`.

Ejemplo `.env` (NO subir a GitHub):
```env
DB_USERNAME=root
DB_PASSWORD=tu_password_mysql
JWT_SECRET=una_clave_muy_larga_y_segura_de_al_menos_32_caracters!
JWT_EXPIRATION=86400000
```

## 4 — Cómo ejecutar (local)
 1.Clonar repo:
````
git clone https://github.com/Pablo-Andrada/Foro-Hub.git
cd Foro-Hub
````

2.Ajustar la base de datos:

-Crear la base forohub en MySQL si no existe:
````
CREATE DATABASE forohub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

````
3.Exportar variables (ejemplo Linux/macOS):
````
export DB_USERNAME=root
export DB_PASSWORD=tu_pass
export JWT_SECRET="clave_super_segura_de_32_chars_min"
export JWT_EXPIRATION=86400000
````
En Windows PowerShell:
````
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_pass"
$env:JWT_SECRET="clave_super_segura_de_32_chars_min"
$env:JWT_EXPIRATION="86400000"
````
4.Ejecutar: 
````
mvn -DskipTests spring-boot:run
# o si usas wrapper
./mvnw -DskipTests spring-boot:run
````
La app arrancará en http://localhost:8080. Flyway aplicará las migraciones automáticamente.

## 5 — Migraciones (Flyway) y seeds
Las migraciones están en src/main/resources/db/migration:
- V1__create_tables.sql — crea tablas usuarios, topicos, respuestas.
- V2__seed_users.sql — inserta usuarios de prueba (por ej testuser, pablo, usuario1, etc).

 
  Nota: Si arrancás y Flyway dice "Schema is up to date", estás listo. Si necesitás limpiar y volver a aplicar migraciones, borrá la BD forohub y recreala, o usa herramientas de administración de Flyway con cuidado.
  
