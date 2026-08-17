package com.soaint.ealavez.exceptions.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.soaint.ealavez.exceptions.custom.InvalidPaginationException;
import com.soaint.ealavez.exceptions.custom.TransaccionNoCancelableException;
import com.soaint.ealavez.models.dto.exceptions.ErrorResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Manejador global de excepciones de la API.
 * <p>
 * Traduce fallos de validación, peticiones mal formadas y errores no controlados
 * a {@link ErrorResponseDto} con códigos HTTP reales, sin filtrar detalles internos
 * al cliente en respuestas 500.
 * </p>
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

	private static final String MSG_VALIDATION 	= "Los parámetros de la petición son incorrectos o estan incompletos.";
	private static final String MSG_BAD_REQUEST = "El formato de la petición es inválido. Verifique los tipos de datos enviados.";
	private static final String MSG_INTERNAL 	= "Ha ocurrido un error interno en el servidor. Contacte a soporte con el ID de seguimiento.";
	private static final String MSG_PAGINATION 	= "Los parámetros de paginación u ordenamiento son inválidos.";
	private static final String MSG_NO_CANCELABLE = "No se pudo cancelar la transacción solicitada.";
	private static final String MSG_YA_CANCELADA = "La transacción ya se encuentra cancelada.";

	/**
	 * Maneja errores de validación de campos ({@code @Valid}).
	 * Retorna HTTP 400.
	 *
	 * @param ex      excepción de Bean Validation
	 * @param request petición HTTP actual
	 * @return cuerpo de error con mapa de detalles por campo u objeto
	 */
	@ExceptionHandler( MethodArgumentNotValidException.class )
	public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
					MethodArgumentNotValidException ex, 
					HttpServletRequest request) {

		Map<String, String> validationErrors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> putValidationError(validationErrors, error));

		ErrorResponseDto errorResponse = new ErrorResponseDto( HttpStatus.BAD_REQUEST, MSG_VALIDATION, request, validationErrors );

		log.warn("Fallo de validación [TraceID: {}] - Errores: {}", errorResponse.getTraceId(), validationErrors);

		return respond(HttpStatus.BAD_REQUEST, errorResponse);
	}

	/**
	 * Maneja JSON mal formado o tipos de argumento incompatibles.
	 * Retorna HTTP 400.
	 *
	 * @param ex      excepción de lectura/mismatch
	 * @param request petición HTTP actual
	 * @return cuerpo de error sin detalles de campo
	 */
	@ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
	public ResponseEntity<ErrorResponseDto> handleBadRequestExceptions( Exception ex, HttpServletRequest request ) {

		ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.BAD_REQUEST, MSG_BAD_REQUEST, request);

		log.warn("Petición mal formada [TraceID: {}] - Causa: {}", errorResponse.getTraceId(), ex.getMessage());
		return respond(HttpStatus.BAD_REQUEST, errorResponse);
	}

	/**
	 * Parámetros de paginación u ordenamiento no permitidos (whitelist / límites).
	 * Retorna HTTP 400 con mensaje seguro (OWASP API8).
	 *
	 * @param ex      excepción de paginación
	 * @param request petición HTTP actual
	 * @return cuerpo de error con detalle del parámetro inválido
	 */
	@ExceptionHandler(InvalidPaginationException.class)
	public ResponseEntity<ErrorResponseDto> handleInvalidPagination(
			InvalidPaginationException ex, HttpServletRequest request) {

		ErrorResponseDto errorResponse = new ErrorResponseDto(
				HttpStatus.BAD_REQUEST,
				MSG_PAGINATION,
				request,
				Map.of("pagination", ex.getMessage()));

		log.warn("Paginación inválida [TraceID: {}] - {}", errorResponse.getTraceId(), ex.getMessage());
		return respond(HttpStatus.BAD_REQUEST, errorResponse);
	}

	/**
	 * Cancelación rechazada: no encontrada (404) o ya cancelada (409).
	 *
	 * @param ex      excepción de cancelación tipificada
	 * @param request petición HTTP actual
	 * @return cuerpo de error sin revelar detalles internos innecesarios
	 */
	@ExceptionHandler(TransaccionNoCancelableException.class)
	public ResponseEntity<ErrorResponseDto> handleTransaccionNoCancelable(
			TransaccionNoCancelableException ex, HttpServletRequest request) {

		HttpStatus status = ex.getHttpStatus();
		String message = status == HttpStatus.CONFLICT ? MSG_YA_CANCELADA : MSG_NO_CANCELABLE;

		ErrorResponseDto errorResponse = new ErrorResponseDto(
				status,
				message,
				request,
				Map.of("detalle", ex.getMessage()));

		log.warn("Cancelación rechazada [TraceID: {}] - motivo: {}",
				errorResponse.getTraceId(), ex.getMotivo());
		return respond(status, errorResponse);
	}

	/**
	 * Catch-all para excepciones no controladas.
	 * Evita la fuga de información sensible y genera trazabilidad.
	 * Retorna HTTP 500.
	 *
	 * @param ex      excepción no manejada
	 * @param request petición HTTP actual
	 * @return cuerpo de error genérico con {@code traceId}
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDto> handleAllUncaughtException(
			Exception ex, HttpServletRequest request) {

		ErrorResponseDto errorResponse = new ErrorResponseDto(HttpStatus.INTERNAL_SERVER_ERROR, MSG_INTERNAL, request);

		log.error("Error no controlado [TraceID: {}] en {}: ", errorResponse.getTraceId(), request.getRequestURI(), ex);
		return respond(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse);
	}

	/**
	 * Agrega un error de validación al mapa, distinguiendo errores de campo y de objeto.
	 *
	 * @param target mapa destino campo/objeto → mensaje
	 * @param error  error reportado por el binding
	 */
	private void putValidationError(Map<String, String> target, ObjectError error) {
		if (error instanceof FieldError fieldError) {
			target.put(fieldError.getField(), fieldError.getDefaultMessage());
		} else {
			target.put(error.getObjectName(), error.getDefaultMessage());
		}
	}

	/**
	 * Empaqueta el DTO de error en un {@link ResponseEntity} con el status HTTP indicado.
	 *
	 * @param status estado HTTP de la respuesta
	 * @param body   cuerpo de error ya construido
	 * @return respuesta lista para el cliente
	 */
	private ResponseEntity<ErrorResponseDto> respond(HttpStatus status, ErrorResponseDto body) {
		return ResponseEntity.status(status).body(body);
	}
}
