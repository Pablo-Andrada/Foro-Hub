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
  
## 6 — Cómo generar una JWT_SECRET segura
- Linux/macOS:
````
openssl rand -base64 48
````
- Windows PowerShell:
````
[Convert]::ToBase64String((New-Object Security.Cryptography.RNGCryptoServiceProvider).GetBytes(48))
````
Debe producir un string largo; guardalo en JWT_SECRET. JJWT/HMAC requiere una clave suficientemente larga para HS256 (32 bytes mínimo recomendado).

## 7 — Resetear contraseña de un usuario (BCrypt)
Si necesitás forzar la contraseña de pablo a admin123:

1. Generar hash BCrypt (desde la clase util del proyecto PasswordHashGenerator o usando una herramienta):

   - Ejecutá la clase com.alura.forohub.util.PasswordHashGenerator con argumento admin123 desde tu IDE. Se imprime el hash.


2. En MySQL:

````
USE forohub;
UPDATE usuarios
SET password = '$2a$10$...'  -- reemplazá por el hash generado
WHERE username = 'pablo';

````

3. Ahora podés loguearte con:

````
{ "username": "pablo", "password": "admin123" }
````

## 8 — Endpoints principales (resumen)
Base URL: http://localhost:8080/api
### Auth 

- POST /api/auth/register — registrar usuario (dev/test)
- POST /api/auth/login — obtener JWT { "token": "..." }
### Tópicos

- POST /api/topicos — crear tópico (auth)

- GET /api/topicos — listar tópicos activos (auth)

- GET /api/topicos/{id} — detalle tópico (auth)

- PUT /api/topicos/{id} — actualizar tópico (auth; validaciones)

- DELETE /api/topicos/{id} — borrar tópico (admin) → 204

- POST /api/topicos/{id}/reactivar — reactivar tópico (admin) → 200
### Respuestas

- POST /api/respuestas — crear respuesta (auth)

- GET /api/respuestas?topicoId={id} — listar respuestas (auth)

- GET /api/respuestas/{id} — detalle respuesta (auth)

- PUT /api/respuestas/{id} — actualizar respuesta (auth)

- DELETE /api/respuestas/{id} — borrar respuesta (admin) → 204
- PATCH /api/respuestas/{id}/reactivar o POST /api/respuestas/{id}/reactivar — reactivar respuesta (admin) → 200
````
Usá el verbo que coincida con tu controller actual (si tu controller tiene @PostMapping usa POST; si @PatchMapping, usa PATCH).
````

## 9 — Requests para Insomnia / Postman (DETALLE — copiar y pegar)
````
Reemplaza {{BASE}} por http://localhost:8080/api.
Para requests protegidos, primero genera token con /api/auth/login y usa header Authorization: Bearer {TOKEN}.
````
### 0) Login — obtener token

- Method: POST

- URL: {{BASE}}/auth/login

- Headers: Content-Type: application/json

- Body:
````
{ "username": "pablo", "password": "LA_PASSWORD_PLAINTEXT_DE_PABLO" }
````
- Expected: 200 OK

- Response example:
````
{ "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..." }
````
### 0b) Register (opcional)

- Method: POST

- URL: {{BASE}}/auth/register

- Body:
````
{ "username":"miuser","password":"miPass123!","nombre":"Mi Usuario","email":"mi@ejemplo.com" }
````

- Expected: 201 Created

### A1) Actualizar tópico — PUT /topicos/{id}

- Method: PUT

- URL: {{BASE}}/topicos/6

- Headers: Authorization: Bearer {TOKEN_PABLO}, Content-Type: application/json

- Body:
````
{
  "titulo": "TITULO_ACTUALIZADO",
  "mensaje": "MENSAJE_ACTUALIZADO",
  "status": "ABIERTO",
  "curso": "Java Avanzado"
}
````
- Expected: 200 OK + DTO actualizado

### A2) Actualizar tópico por otro usuario (no admin) — expect 403

- Igual que A1 pero con TOKEN_LIMITADO → 403 Forbidden

### A3) Actualizar tópico con admin — expect 200

- Igual que A1 pero con TOKEN_ADMIN → 200 OK

### A4) Borrar tópico (admin)

- Method: DELETE

- URL: {{BASE}}/topicos/4

- Headers: Authorization: Bearer {TOKEN_ADMIN}

- Expected: 204 No Content

### A5) Reactivar tópico (admin)

- Method: POST (o PATCH según tu controller)

- URL: {{BASE}}/topicos/4/reactivar

- Headers: Authorization: Bearer {TOKEN_ADMIN}

- Expected: 200 OK + DTO con activo=true

### B1) Listar respuestas por tópico

- Method: GET

- URL: {{BASE}}/respuestas?topicoId=3&page=0&size=20&sort=fechaCreacion,asc

- Headers: Authorization: Bearer {TOKEN_PABLO}

- Expected: 200 OK + página (puede estar vacía)

### B2) Crear respuesta

- Method: POST

- URL: {{BASE}}/respuestas

- Headers: Authorization: Bearer {TOKEN_PABLO}, Content-Type: application/json

- Body:
````
{ "mensaje": "Respuesta de prueba", "autorId": 1, "topicoId": 3 }
````

- Expected: 201 Created + Location header + DTO

### B3) Crear respuesta a tópico inactivo — expect 404

- Intentar con topicoId que esté activo=false.

- Expected: 404 Not Found

### B4) Borrar respuesta (admin)

- Method: DELETE

- URL: {{BASE}}/respuestas/1

- Headers: Authorization: Bearer {TOKEN_ADMIN}

- Expected: 204 No Content

### B5) Reactivar respuesta (admin)

- Method: PATCH o POST (según controller)

- URL: {{BASE}}/respuestas/1/reactivar

- Headers: Authorization: Bearer {TOKEN_ADMIN}

- Expected: 200 OK + DTO con activo=true

### C1) Crear tópico duplicado — expect 400

- Method: POST

- URL: {{BASE}}/topicos

- Body:
````
{ "titulo": "DUPLICADO_TEST", "mensaje": "Mensaje duplicado test", "autorId": 1, "curso": "Java Test" }
````

- Expected: 400 Bad Request + mensaje sobre duplicado

### C2) Acceso sin JWT — GET /topicos — expect 401

- Quitar header Authorization y hacer:

- Method: GET

- URL: {{BASE}}/topicos

- Expected: 401 Unauthorized

### C3) Acceso con JWT expirado — GET /topicos — expect 401

- Generar token con expiración corta o usar token viejo → 401 Unauthorized

### C4) DELETE /topicos/3 con token de pablo (no admin) — expect 403

- Method: DELETE

- URL: {{BASE}}/topicos/3

- Headers: Authorization: Bearer {TOKEN_PABLO}

- Expected: 403 Forbidden (si token válido y pablo no es admin).
Si recibís 401, revisá token (formato/expiración) o logs del servidor.

## 10 — Roles y comportamiento esperado (401 vs 403)

- 401 Unauthorized = no autenticado → token faltante, mal formado o expirado.

- 403 Forbidden = autenticado pero sin permiso → token válido, user no posee rol requerido.

Ejemplo:

- Si pablo (ROLE_USER) envía DELETE /api/topicos/3:

   - Si token válido → 403 (no tiene rol ADMIN).

   - Si token inválido/expirado o no enviado → 401.

## 11 — Errores comunes y soluciones rápidas

- Illegal base64 character o JWT strings must contain exactly 2 period characters → pegaste algo que no es un JWT en la cabecera (verifica Bearer <token>).

- Request method 'PATCH' is not supported → el mapping en el controller no es PATCH; usa POST/PUT según lo implementado.

- 401 en requests protegidos → regenerá token con /api/auth/login, asegurate que la cabecera sea exactamente Authorization: Bearer <token>.

- 403 en requests donde esperás éxito → revisar rol en DB y el GrantedAuthority que tu CustomUserDetailsService devuelve.

- Duplicate entry al crear tópicos → validación de duplicado activada. Revisa titulo y mensaje (trim).