package com.soaint.ealavez.exceptions.custom;

import org.springframework.http.HttpStatus;

/**
 * La transacción no puede cancelarse: no existe, ya está cancelada, o no está en
 * estatus {@code Aprobada} con el {@code id} indicado.
 * <p>
 * El {@link #getHttpStatus()} permite al handler mapear 404 vs 409 (OWASP API3 / API8).
 * </p>
 */
public class TransaccionNoCancelableException extends RuntimeException {

	/**
	 * Motivo de rechazo de la cancelación.
	 */
	public enum Motivo {
		/**
		 * Transacción inexistente o id/referencia no coinciden.
		 */
		NO_ENCONTRADA(HttpStatus.NOT_FOUND),

		/**
		 * Transacción ya tiene estatus {@code Cancelar} (idempotencia).
		 */
		YA_CANCELADA(HttpStatus.CONFLICT);

		private final HttpStatus httpStatus;

		Motivo(HttpStatus httpStatus) {
			this.httpStatus = httpStatus;
		}

		/**
		 * @return código HTTP asociado al motivo
		 */
		public HttpStatus getHttpStatus() {
			return httpStatus;
		}
	}

	private static final String MENSAJE_NO_ENCONTRADA = "No se encontró una transacción cancelable con el id y referencia indicados.";

	private static final String MENSAJE_YA_CANCELADA  = "La transacción ya se encuentra cancelada.";

	private final Motivo motivo;

	/**
	 * @param motivo  causa tipificada del rechazo
	 * @param message descripción segura para el cliente
	 */
	public TransaccionNoCancelableException(Motivo motivo, String message) {
		super(message);
		this.motivo = motivo;
	}

	/**
	 * @return motivo tipificado (404 o 409)
	 */
	public Motivo getMotivo() {
		return motivo;
	}

	/**
	 * @return código HTTP derivado del {@link Motivo}
	 */
	public HttpStatus getHttpStatus() {
		return motivo.getHttpStatus();
	}

	/**
	 * Transacción no encontrada o no cancelable (estatus distinto de {@code Aprobada}).
	 *
	 * @return excepción lista para el handler (HTTP 404)
	 */
	public static TransaccionNoCancelableException noEncontrada() {
		return new TransaccionNoCancelableException(Motivo.NO_ENCONTRADA, MENSAJE_NO_ENCONTRADA);
	}

	/**
	 * La transacción ya tiene estatus {@code Cancelar}.
	 *
	 * @return excepción lista para el handler (HTTP 409)
	 */
	public static TransaccionNoCancelableException yaCancelada() {
		return new TransaccionNoCancelableException(Motivo.YA_CANCELADA, MENSAJE_YA_CANCELADA);
	}
}
