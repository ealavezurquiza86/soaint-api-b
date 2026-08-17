package com.soaint.ealavez.models.dto.transaccion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para cancelar una transacción vía PATCH.
 * <p>
 * Solo admite la acción {@code estatus = "cancelar"} junto con {@code id} y {@code referencia}
 * para verificación cruzada (mitigación BOLA — OWASP API1).
 * </p>
 */
@Getter
@Setter
@Schema(description = "Solicitud de cancelación: id + referencia + acción cancelar")
public class CancelacionTransaccionRequestDto {

	/**
	 * Identificador de la transacción (PK numérica como cadena).
	 */
	@NotBlank(message = "El id es obligatorio")
	@Pattern(
			regexp = "^[1-9]\\d*$",
			message = "El id debe ser un número entero positivo")
	@Schema(
			description = "PK numérica como String (entero positivo)",
			example = "2376",
			pattern = "^[1-9]\\d*$",
			type = "string",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private String id;

	/**
	 * Referencia numérica de 6 dígitos generada al registrar la transacción.
	 */
	@NotBlank(message = "La referencia es obligatoria")
	@Pattern(
			regexp = "^\\d{6}$",
			message = "La referencia debe ser numérica de 6 dígitos")
	@Schema(
			description = "Referencia de 6 dígitos",
			example = "262737",
			pattern = "^\\d{6}$",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private String referencia;

	/**
	 * Acción solicitada; único valor permitido: {@code cancelar} (insensible a mayúsculas).
	 */
	@NotBlank(message = "El estatus es obligatorio")
	@Pattern(
			regexp = "(?i)^cancelar$",
			message = "El estatus debe ser 'cancelar'")
	@Schema(
			description = "Acción de cancelación (no es el valor persistido). Único valor: cancelar",
			example = "cancelar",
			pattern = "(?i)^cancelar$",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private String estatus;
}
