package com.soaint.ealavez.models.dto.transaccion;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de entrada para registrar una transacción.
 */
@Getter
@Setter
@Schema(description = "Datos de entrada para registrar una transacción")
public class RegistroTransaccionRequestDto {

	/**
	 * Nombre de la operación (solo letras y espacios).
	 */
	@NotBlank(message = "El atributo operacion es obligatorio")
	@Pattern(
			regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
			message = "El atributo operacion solo admite letras y espacios")
	@Schema(
			description = "Nombre de la operación (solo letras y espacios)",
			example = "venta",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private String operacion;

	/**
	 * Importe monetario (máximo 16 enteros y 2 decimales; valor &gt;= 0).
	 */
	@NotNull(message = "El atributo importe es obligatorio")
	@Digits(
			integer = 16,
			fraction = 2,
			message = "El atributo importe debe ser moneda con hasta 16 enteros y 2 decimales")
	@DecimalMin(
			value = "0.00",
			inclusive = true,
			message = "El atributo importe no puede ser negativo")
	@Schema(
			description = "Importe monetario (hasta 16 enteros y 2 decimales, >= 0)",
			example = "100.00",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private BigDecimal importe;

	/**
	 * Nombre o identificador del cliente.
	 */
	@NotBlank(message = "El atributo cliente es obligatorio")
	@Size(max = 500, message = "El atributo cliente no debe exceder 500 caracteres")
	@Schema(
			description = "Cliente (máx. 500 caracteres)",
			example = "Angel",
			maxLength = 500,
			requiredMode = Schema.RequiredMode.REQUIRED)
	private String cliente;

	/**
	 * Secreto en texto plano.
	 */
	@NotBlank(message = "El atributo secreto es obligatorio")
	@Schema(
			description = "Secreto en texto plano (requerimiento de la prueba)",
			example = "miSecretoPlano123",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private String secreto;
}
