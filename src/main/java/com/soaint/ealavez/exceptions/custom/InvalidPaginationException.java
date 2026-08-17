package com.soaint.ealavez.exceptions.custom;

/**
 * Excepción de negocio para parámetros de paginación u ordenamiento inválidos.
 * <p>
 * Se traduce a HTTP 400 sin filtrar detalles internos del servidor (OWASP API8).
 * </p>
 */
public class InvalidPaginationException extends RuntimeException {

	/**
	 * @param message descripción segura del parámetro inválido
	 */
	public InvalidPaginationException(String message) {
		super(message);
	}
}
