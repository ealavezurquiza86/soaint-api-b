package com.soaint.ealavez.models.dto.exceptions;

/**
 * Ejemplos JSON de {@link ErrorResponseDto} para documentación OpenAPI / springdoc.
 * <p>
 * Swagger UI reutiliza el schema genérico si no se define un {@code @ExampleObject}
 * por cada código HTTP; estas constantes evitan el ejemplo fijo de status 400.
 * </p>
 */
public final class ErrorResponseExamples {

	/**
	 * Validación Bean Validation o paginación inválida.
	 */
	public static final String BAD_REQUEST_VALIDATION = """
			{
			  "timestamp": "2026-08-15T17:00:00",
			  "status": 400,
			  "error": "Bad Request",
			  "message": "Los parámetros de la petición son incorrectos o estan incompletos.",
			  "path": "/api/transaccion",
			  "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
			  "details": {
			    "operacion": "El atributo operacion es obligatorio",
			    "importe": "El atributo importe no puede ser negativo"
			  }
			}
			""";

	/**
	 * Parámetros de paginación u ordenamiento no permitidos.
	 */
	public static final String BAD_REQUEST_PAGINATION = """
			{
			  "timestamp": "2026-08-15T17:00:00",
			  "status": 400,
			  "error": "Bad Request",
			  "message": "Los parámetros de paginación u ordenamiento son inválidos.",
			  "path": "/api/transaccion",
			  "traceId": "b2c3d4e5-f6a7-8901-bcde-f12345678901",
			  "details": {
			    "pagination": "Campo de ordenamiento no permitido: secreto"
			  }
			}
			""";

	/**
	 * Transacción no encontrada o no cancelable.
	 */
	public static final String NOT_FOUND = """
			{
			  "timestamp": "2026-08-15T17:00:00",
			  "status": 404,
			  "error": "Not Found",
			  "message": "No se pudo cancelar la transacción solicitada.",
			  "path": "/api/transaccion/cancelar",
			  "traceId": "c3d4e5f6-a7b8-9012-cdef-123456789012",
			  "details": {
			    "detalle": "No se encontró una transacción cancelable con el id y referencia indicados."
			  }
			}
			""";

	/**
	 * Transacción ya cancelada (reintento).
	 */
	public static final String CONFLICT_ALREADY_CANCELLED = """
			{
			  "timestamp": "2026-08-15T17:00:00",
			  "status": 409,
			  "error": "Conflict",
			  "message": "La transacción ya se encuentra cancelada.",
			  "path": "/api/transaccion/cancelar",
			  "traceId": "d4e5f6a7-b8c9-0123-def0-234567890123",
			  "details": {
			    "detalle": "La transacción ya se encuentra cancelada."
			  }
			}
			""";

	/**
	 * Error no controlado.
	 */
	public static final String INTERNAL_SERVER_ERROR = """
			{
			  "timestamp": "2026-08-15T17:00:00",
			  "status": 500,
			  "error": "Internal Server Error",
			  "message": "Ha ocurrido un error interno en el servidor. Contacte a soporte con el ID de seguimiento.",
			  "path": "/api/transaccion",
			  "traceId": "e5f6a7b8-c9d0-1234-ef01-345678901234"
			}
			""";

	private ErrorResponseExamples() {
	}
}
