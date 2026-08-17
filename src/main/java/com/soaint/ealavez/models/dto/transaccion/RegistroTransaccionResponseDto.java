package com.soaint.ealavez.models.dto.transaccion;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO Response del comprobante de registro de transacción.
 */
@Getter
@Setter
@Schema(description = "Comprobante plano tras registrar una transacción")
public class RegistroTransaccionResponseDto {

	/**
	 * Primary key como String.
	 */
	@Schema(description = "PK generada, expuesta como String", example = "2376")
	private String id;

	/**
	 * Estatus de la operación (enum: APROBADA).
	 */
	@Schema(
			description = "Estatus persistido (enum TransaccionEstatus)",
			example = "Aprobada",
			allowableValues = { "Aprobada", "Cancelada" })
	private String estatus;

	/**
	 * Referencia numérica de 6 dígitos.
	 */
	@Schema(description = "Referencia de 6 dígitos", example = "262737")
	private String referencia;

	/**
	 * Nombre de la operación registrada.
	 */
	@Schema(description = "Operación registrada", example = "venta")
	private String operacion;
}
