# ForoHub - API (Spring Boot + MySQL)

**ForoHub** es una API REST desarrollada con Spring Boot (Java 17) para gestionar tópicos de un foro.  
Este repositorio contiene el backend con CRUD de tópicos, borrado lógico (`activo`), reactivación, migraciones con Flyway y validaciones básicas.

**Colocar este `README.md` en la raíz del repositorio** (misma carpeta que `pom.xml`).

---

## Índice
- [Requisitos](#requisitos)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Configuración](#configuración)
- [Migraciones (Flyway)](#migraciones-flyway)
- [Ejecutar la aplicación](#ejecutar-la-aplicación)
- [Endpoints principales](#endpoints-principales)
- [DTOs (resumen)](#dtos-resumen)
- [Pruebas manuales (Insomnia / curl)](#pruebas-manuales-insomnia--curl)
- [Pruebas automatizadas](#pruebas-automatizadas)
- [Comprobaciones y troubleshooting](#comprobaciones-y-troubleshooting)
- [Buenas prácticas de Git antes de push](#buenas-prácticas-de-git-antes-de-push)
- [Siguientes pasos sugeridos](#siguientes-pasos-sugeridos)
- [Contacto / notas](#contacto--notas)

---

## Requisitos
- Java 17+ (JDK 17)
- Maven (compatible con Spring Boot 3.x)
- MySQL 8.x (base de datos `forohub`)
- IDE recomendado: IntelliJ IDEA
- (Opcional) Insomnia o Postman para probar la API

---

## Estructura del proyecto

> Resumen de ubicaciones relevantes. Está en bloque de código para que al pegar en GitHub/IDE mantenga el formato.

/ (raíz del repo - README.md aquí)
├─ pom.xml
├─ .gitignore
├─ src/
│ ├─ main/
│ │ ├─ java/
│ │ │ └─ com/alura/forohub/
│ │ │ ├─ controller/ <- controladores REST (TopicoController, AuthController si se agrega)
│ │ │ ├─ dto/ <- DTOs (records): TopicoCreateDto, TopicoUpdateDto, TopicoResponseDto
│ │ │ ├─ exception/ <- excepciones personalizadas y GlobalExceptionHandler
│ │ │ ├─ model/ <- entidades JPA: Topico, Usuario, Respuesta
│ │ │ ├─ repository/ <- interfaces JpaRepository
│ │ │ ├─ service/ <- interfaces y clases impl (TopicoService/TopicoServiceImpl)
│ │ │ └─ ForohubApplication.java
│ │ └─ resources/
│ │ ├─ application.properties
│ │ └─ db/migration/ <- scripts Flyway (V1__create_tables.sql, ...)
│ └─ test/ <- tests unitarios / integración
└─ README.md

