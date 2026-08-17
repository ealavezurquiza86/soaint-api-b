package com.soaint.ealavez.services;

import java.security.SecureRandom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soaint.ealavez.entities.Transaccion;
import com.soaint.ealavez.entities.enums.TransaccionEstatus;
import com.soaint.ealavez.exceptions.custom.TransaccionNoCancelableException;
import com.soaint.ealavez.mappers.TransaccionMapper;
import com.soaint.ealavez.models.dto.transaccion.CancelacionTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionResponseDto;
import com.soaint.ealavez.models.dto.transaccion.TransaccionListItemDto;
import com.soaint.ealavez.models.dto.transaccion.TransaccionPageResponseDto;
import com.soaint.ealavez.repositories.TransaccionRepository;
import com.soaint.ealavez.services.support.TransaccionPageableValidator;

/**
 * Implementación del registro de transacciones: enriquecimiento, persistencia y mapeo de salida
 */
@Service
public class TransaccionServiceImpl implements TransaccionService {

	private final SecureRandom secureRandom = new SecureRandom();

	private final TransaccionRepository transaccionRepository;
	private final TransaccionMapper transaccionMapper;
	private final TransaccionPageableValidator pageableValidator;

	/**
	 * @param transaccionRepository repositorio JPA de transacciones
	 * @param transaccionMapper     mapper transaccion
	 * @param pageableValidator     validador de paginación y ordenamiento
	 */
	public TransaccionServiceImpl(
			TransaccionRepository transaccionRepository,
			TransaccionMapper transaccionMapper,
			TransaccionPageableValidator pageableValidator) {

		this.transaccionRepository 	= transaccionRepository;
		this.transaccionMapper 		= transaccionMapper;
		this.pageableValidator 		= pageableValidator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public RegistroTransaccionResponseDto registrar( RegistroTransaccionRequestDto request ) {

		Transaccion transaccion = transaccionMapper.toEntity(request);

		// Asignamos la referencia y el estatus antes de guardar
		transaccion.setReferencia( generarReferencia() );
		transaccion.setEstatus(TransaccionEstatus.APROBADA);

		Transaccion guardada = transaccionRepository.save(transaccion);
		return transaccionMapper.toRegistroResponseDto(guardada);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = true)
	public TransaccionPageResponseDto consultar( Pageable pageable) {

		// Validamos y normalizamos los parámetros de paginación y ordenamiento de acuerdo a las reglas internas del validator
		Pageable pageableValido = pageableValidator.validate(pageable);

		Page<Transaccion> pagina = transaccionRepository.findAll(pageableValido);

		// Recorremos el contenido de pagina, los mapeamos a DTOs y creamos un Page<DTO> incluyendo los metadatos
		Page<TransaccionListItemDto> paginaDto = pagina.map( transaccionMapper::toListItemDto );

		// Convertimos el Page<DTO> a nuestro wrapper TransaccionPageResponseDto de respuesta paginada
		return TransaccionPageResponseDto.from(paginaDto);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void cancelar( CancelacionTransaccionRequestDto request ) {
		Long id = parseId( request.getId() );

		int filasActualizadas = transaccionRepository.actualizarEstatus(
				id,
				request.getReferencia(),
				TransaccionEstatus.APROBADA,
				TransaccionEstatus.CANCELADA
			);

		// Si se actualizó una fila, todo esta OK y fin del flujo
		if (filasActualizadas > 0)  return;
		
		// Si no se actualizó ninguna fila, verificamos si el id existe y lanzamos la excepción no encontrada
		Transaccion existente = transaccionRepository.findById(id)
				.orElseThrow( TransaccionNoCancelableException::noEncontrada );

		// Si existe pero no estaba en estatus Aprobada, verificamos si ya estaba cancelada y lanzamos la excepción ya cancelada (Idempotencia)
		if ( existente.getEstatus() == TransaccionEstatus.CANCELADA ) {
			throw TransaccionNoCancelableException.yaCancelada();
		}

		// Si existe pero no estaba en estatus Aprobada y tampoco estaba cancelada, lanzamos excepción estándar (no cancelable)
		throw TransaccionNoCancelableException.noEncontrada();
	}

	/**
	 * Transforma el id del request a Long de forma segura, convirtiendo cualquier error de parseo en excepción de transacción no encontrada.
	 *
	 * @param id cadena numérica positiva
	 * @return PK parseada
	 */
	private Long parseId(String id) {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException ex) {
			throw TransaccionNoCancelableException.noEncontrada();
		}
	}

	/**
	 * Genera una referencia numérica segura de 6 dígitos con ceros a la izquierda
	 *
	 * @return String referencia formateada (000000–999999)
	 */
	private String generarReferencia() {
		// Usamos SecureRandom en lugar de Random para evitar predecibilidad y ataques de enumeración (OWASP API1)
		return String.format("%06d", secureRandom.nextInt(1_000_000));
	}
}
