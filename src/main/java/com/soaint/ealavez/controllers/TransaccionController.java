package com.soaint.ealavez.controllers;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soaint.ealavez.models.dto.exceptions.ErrorResponseDto;
import com.soaint.ealavez.models.dto.exceptions.ErrorResponseExamples;
import com.soaint.ealavez.models.dto.transaccion.CancelacionTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionResponseDto;
import com.soaint.ealavez.models.dto.transaccion.TransaccionPageResponseDto;
import com.soaint.ealavez.services.TransaccionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controlador REST para el almacenamiento, consulta y cancelación de transacciones.
 */
@RestController
@RequestMapping("/api/transaccion")
@Tag(name = "Transacciones", description = "Persistencia, consulta paginada y cancelación de transacciones")
public class TransaccionController {

	private final TransaccionService transaccionService;

	/**
	 * @param transaccionService servicio de dominio de transacciones
	 */
	public TransaccionController(TransaccionService transaccionService) {
		this.transaccionService = transaccionService;
	}

	/**
	 * Consulta transacciones con paginación y ordenamiento vía Spring Data JPA.
	 *
	 * @param pageable página (base 0), tamaño y orden ({@code sort=campo,direccion})
	 * @return HTTP 200 con wrapper {@link TransaccionPageResponseDto}
	 */
	@GetMapping
	@Operation(
			summary = "Consulta paginada de transacciones",
			description = "Lista transacciones. No expone secreto. "
					+ "Página base 0. Default: size=10, sort=id,DESC. "
					+ "Campos sort permitidos: id, operacion, importe, cliente, referencia, estatus. ")
	@ApiResponse(
			responseCode = "200",
			description = "Consulta paginada exitosa",
			content = @Content(schema = @Schema(implementation = TransaccionPageResponseDto.class)))
	@ApiResponse(
			responseCode = "400",
			description = "Parámetros de paginación u orden inválidos",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ErrorResponseDto.class),
					examples = @ExampleObject(
							name = "PaginacionInvalida",
							summary = "HTTP 400 — sort o page inválidos",
							value = ErrorResponseExamples.BAD_REQUEST_PAGINATION)))
	public ResponseEntity<TransaccionPageResponseDto> show(
			@ParameterObject
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(transaccionService.consultar(pageable));
	}

	/**
	 * Persiste una transacción y devuelve el comprobante plano.
	 *
	 * @param request cuerpo validado con operación, importe, cliente y secreto
	 * @return HTTP 200 con comprobante (estatus APROBADA)
	 */
	@PostMapping
	@Operation(
			summary = "Almacena una nueva transacción y genera su referencia",
			description = "Guarda los datos, genera referencia de 6 dígitos, asigna estatus Aprobada y retorna el comprobante.")
	@ApiResponse(
			responseCode = "200",
			description = "Transacción persistida exitosamente",
			content = @Content(schema = @Schema(implementation = RegistroTransaccionResponseDto.class)))
	@ApiResponse(
			responseCode = "400",
			description = "Parámetros inválidos o incompletos",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ErrorResponseDto.class),
					examples = @ExampleObject(
							name = "ValidacionFallida",
							summary = "HTTP 400 — Bean Validation",
							value = ErrorResponseExamples.BAD_REQUEST_VALIDATION)))
	public ResponseEntity<RegistroTransaccionResponseDto> store(
			@Valid @RequestBody RegistroTransaccionRequestDto request) {

		return ResponseEntity.ok(transaccionService.registrar(request));
	}

	/**
	 * Cancela una transacción en estatus Aprobada, cambiando su estatus a Cancelada.
	 *
	 * @param request id (string), referencia y acción {@code cancelar}
	 * @return HTTP 200 sin cuerpo si la cancelación fue exitosa
	 */
	@PatchMapping("/cancelar")
	@Operation(
			summary = "Cancela una transacción aprobada",
			description = "Actualiza Aprobada → Cancelada cuando coinciden id y referencia.")
	@ApiResponse(responseCode = "200", description = "Transacción cancelada exitosamente (sin cuerpo)")
	@ApiResponse(
			responseCode = "400",
			description = "Parámetros inválidos",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ErrorResponseDto.class),
					examples = @ExampleObject(
							name = "ValidacionFallida",
							summary = "HTTP 400 — body inválido",
							value = ErrorResponseExamples.BAD_REQUEST_VALIDATION)))
	@ApiResponse(
			responseCode = "404",
			description = "Transacción no encontrada o no cancelable",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ErrorResponseDto.class),
					examples = @ExampleObject(
							name = "NoEncontrada",
							summary = "HTTP 404 — no cancelable",
							value = ErrorResponseExamples.NOT_FOUND)))
	@ApiResponse(
			responseCode = "409",
			description = "La transacción ya se encuentra cancelada",
			content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ErrorResponseDto.class),
					examples = @ExampleObject(
							name = "YaCancelada",
							summary = "HTTP 409 — ya cancelada",
							value = ErrorResponseExamples.CONFLICT_ALREADY_CANCELLED)))
	public ResponseEntity<Void> cancel(@Valid @RequestBody CancelacionTransaccionRequestDto request) {

		transaccionService.cancelar(request);
		return ResponseEntity.ok().build();
	}
}
