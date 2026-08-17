package com.soaint.ealavez.models.dto.exceptions;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * DTO estándar de error para las respuestas HTTP de la API.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Envelope de error de la API")
public class ErrorResponseDto {

	/**
	 * Momento en que se construyó la respuesta de error (zona horaria del servidor).
	 */
	@Schema(description = "Timestamp del error")
	private final LocalDateTime timestamp;

	/**
	 * Código de estado HTTP numérico.
	 */
	@Schema(description = "Código HTTP (400, 404, 409, 500, …)")
	private final int status;

	/**
	 * Frase basada del estado HTTP.
	 */
	@Schema(description = "Reason phrase HTTP (p. ej. Bad Request, Not Found, Conflict)")
	private final String error;

	/**
	 * Mensaje orientado al cliente, sin detalles internos sensibles.
	 */
	@Schema(description = "Mensaje seguro para el cliente")
	private final String message;

	/**
	 * URI de la petición que originó el error.
	 */
	@Schema(description = "Path de la petición")
	private final String path;

	/**
	 * Identificador único de correlación para localizar el evento en los logs.
	 */
	@Schema(description = "ID de correlación para logs")
	private final String traceId;

	/**
	 * Mapa opcional de errores de validación ({@code campo → mensaje}).
	 */
	@Schema(description = "Detalle campo→mensaje (validación u otros); puede omitirse")
	private final Map<String, String> details;

	/**
	 * Construye una respuesta de error sin detalles de validación.
	 *
	 * @param status  estado HTTP a devolver al cliente
	 * @param message descripción segura del error
	 * @param request petición HTTP que provocó el fallo (usada para obtener el path)
	 */
	public ErrorResponseDto(HttpStatus status, String message, HttpServletRequest request) {
		this(status, message, request, null);
	}

	/**
	 * Construye una respuesta de error con detalles opcionales de validación.
	 *
	 * @param status  estado HTTP a devolver al cliente
	 * @param message descripción segura del error
	 * @param request petición HTTP que provocó el fallo (usada para obtener el path)
	 * @param details mapa campo→mensaje, o {@code null} si no aplica
	 */
	public ErrorResponseDto(
			HttpStatus status,
			String message,
			HttpServletRequest request,
			Map<String, String> details) {

		this.timestamp = LocalDateTime.now();
		this.status = status.value();
		this.error = status.getReasonPhrase();
		this.message = message;
		this.path = request.getRequestURI();
		this.traceId = UUID.randomUUID().toString();
		this.details = details;
	}
}
