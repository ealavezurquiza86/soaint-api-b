# API_B — Persistencia de Transacciones Soaint

API 2 del ecosistema Soaint: microservicio **Spring Boot 3.4.5** / **Java 21** de **persistencia, consulta paginada y cancelación** de transacciones.

Responsabilidades:

- Recibir el JSON de transacción (`operacion`, `importe`, `cliente`, `secreto` en texto plano).
- Validar con Bean Validation y `@RestControllerAdvice`.
- Guardar en **H2 en memoria** (JPA / `JpaRepository`).
- Generar referencia numérica de 6 dígitos y estatus `APROBADA` al registrar.
- Listar con paginación JPA (`page`, `size`, `sort`).
- Cancelar con `PATCH` y `@Query` (`APROBADA` → `CANCELADA`).

No incluye autenticación: los endpoints de negocio están **abiertos**. El secreto se almacena **sin cifrar** (el descifrado ocurre en API_A).

Artefacto Maven: `prueba-api-b` (`com.soaint.ealavez`).

## Contrato OpenAPI

Se adjunta el archivo OpenAPI 3 de este servicio:

- **[`openapi_api2.yaml`](openapi_api2.yaml)** — contrato estático de paths, esquemas y códigos HTTP.

También está disponible en ejecución:

- Swagger UI: `http://localhost:8443/swagger-ui.html` (perfil predeterminado) o `http://localhost:8773/swagger-ui.html` (perfil `dev`)
- OpenAPI JSON: `/v3/api-docs`
- Consola H2: `/h2-console` (JDBC: `jdbc:h2:mem:transaccionesdb`, usuario `sa`)

## Requisitos e instalaciones

| Dependencia | Versión / notas |
|-------------|-----------------|
| JDK | **21** |
| Maven | **3.9+** |
| Base de datos | **H2 embebida** (no requiere instalar un motor externo) |

API_B no necesita MySQL. Los datos se pierden al detener el proceso (H2 en memoria).

## Comandos

```bash
cd API_B

# Compilar
mvn -q compile

# Tests
mvn test

# Arranque con perfil predeterminado → puerto 8443
mvn spring-boot:run

# Arranque con perfil local (application-dev.yml) → puerto 8773
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

| Perfil | Puerto | Uso típico |
|--------|--------|------------|
| Predeterminado (`application.yml`) | **8443** | API_A sin perfil `dev` |
| `dev` (`application-dev.yml`) | **8773** | API_A con `-Dspring-boot.run.profiles=dev` |

## Endpoints

| Método | Ruta | Auth | Descripción |
|--------|------|------|-------------|
| GET | `/api/transaccion` | No | Listado paginado (`page`, `size`, `sort`) |
| POST | `/api/transaccion` | No | Registrar transacción |
| PATCH | `/api/transaccion/cancelar` | No | Cancelar (body: `id`, `referencia`, `estatus: cancelar`) |

## Documentación adicional

| Archivo | Contenido |
|---------|-----------|
| [`openapi_api2.yaml`](openapi_api2.yaml) | Contrato OpenAPI 3 (adjunto) |
| `documentacion_tecnica_api2.md` | Arquitectura y reglas |
| `tareas_cursor_api2.md` | Plan de implementación |
