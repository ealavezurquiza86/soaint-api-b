package com.soaint.ealavez.services;

import org.springframework.data.domain.Pageable;

import com.soaint.ealavez.models.dto.transaccion.CancelacionTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionResponseDto;
import com.soaint.ealavez.models.dto.transaccion.TransaccionPageResponseDto;

/**
 * Contrato del servicio de registro de transacciones
 */
public interface TransaccionService {

	/**
	 * Persiste una transacción, genera referencia y estatus, y devuelve el comprobante
	 *
	 * @param RegistroTransaccionRequestDto request datos de entrada 
	 * @return RegistroTransaccionResponseDto comprobante con id, estatus, referencia y operación
	 */
	RegistroTransaccionResponseDto registrar(RegistroTransaccionRequestDto request);

	/**
	 * Consulta transacciones paginadas y ordenadas.
	 *
	 * @param pageable parámetros de página, tamaño y orden (base 0)
	 * @return wrapper con contenido y metadatos de paginación
	 */
	TransaccionPageResponseDto consultar(Pageable pageable);

	/**
	 * Cancela una transacción en estatus {@code Aprobada}
	 * <p>
	 * Requiere coincidencia de {@code id} y {@code referencia}. Distingue no encontrada (404) de ya cancelada (409).
	 * </p>
	 *
	 * @param request id, referencia y acción cancelar
	 * @throws IllegalArgumentException si el id no es un número válido
	 * @throws com.soaint.ealavez.exceptions.custom.TransaccionNoCancelableException
	 *         si no existe el id o ya está cancelada
	 */
	void cancelar(CancelacionTransaccionRequestDto request);
}
