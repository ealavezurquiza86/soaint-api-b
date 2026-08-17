package com.soaint.ealavez.models.dto.transaccion;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Representación de una transacción en consultas paginadas.
 * <p>
 * Omite {@code secreto} de forma intencional (OWASP API3).
 * </p>
 */
@Getter
@Setter
@Schema(description = "Ítem de listado paginado (sin secreto)")
public class TransaccionListItemDto {

	/**
	 * Identificador de la transacción (PK como {@code String}).
	 */
	@Schema(description = "PK como String", example = "2376")
	private String id;

	/**
	 * Tipo o nombre de la operación.
	 */
	@Schema(description = "Operación", example = "venta")
	private String operacion;

	/**
	 * Importe monetario de la operación.
	 */
	@Schema(description = "Importe monetario", example = "100.00")
	private BigDecimal importe;

	/**
	 * Cliente asociado a la transacción.
	 */
	@Schema(description = "Cliente", example = "Angel")
	private String cliente;

	/**
	 * Referencia numérica de seis dígitos.
	 */
	@Schema(description = "Referencia de 6 dígitos", example = "262737")
	private String referencia;

	/**
	 * Estatus de la transacción (enum serializado).
	 */
	@Schema(
			description = "Estatus (enum TransaccionEstatus)",
			example = "APROBADA",
			allowableValues = { "APROBADA", "CANCELADA" })
	private String estatus;
}
