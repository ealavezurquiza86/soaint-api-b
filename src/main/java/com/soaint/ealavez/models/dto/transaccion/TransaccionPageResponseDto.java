package com.soaint.ealavez.models.dto.transaccion;

import java.util.List;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * Wrapper de respuesta para consultas paginadas de transacciones.
 */
@Getter
@Schema(description = "Respuesta paginada de transacciones")
public class TransaccionPageResponseDto {

	/**
	 * Registros de la página actual.
	 */
	@Schema(description = "Elementos de la página actual")
	private final List<TransaccionListItemDto> content;

	/**
	 * Número de página solicitada (base 0).
	 */
	@Schema(description = "Número de página (base 0)", example = "0")
	private final int page;

	/**
	 * Tamaño de página (registros por página).
	 */
	@Schema(description = "Tamaño de página aplicado", example = "10")
	private final int size;

	/**
	 * Total de registros en la base de datos (todas las páginas).
	 */
	@Schema(description = "Total de registros", example = "57")
	private final long totalElements;

	/**
	 * Total de páginas calculadas a partir de {@link #totalElements} y {@link #size}.
	 */
	@Schema(description = "Total de páginas", example = "6")
	private final int totalPages;

	/**
	 * {@code true} si esta es la primera página.
	 */
	@Schema(description = "Indica si es la primera página", example = "true")
	private final boolean first;

	/**
	 * {@code true} si esta es la última página.
	 */
	@Schema(description = "Indica si es la última página", example = "false")
	private final boolean last;

	/**
	 * Construye el DTO de respuesta paginada.
	 *
	 * @param content        elementos de la página actual
	 * @param page           índice de página (base 0)
	 * @param size           tamaño de página aplicado
	 * @param totalElements  total de registros
	 * @param totalPages     total de páginas
	 * @param first          indica primera página
	 * @param last           indica última página
	 */
	public TransaccionPageResponseDto(
			List<TransaccionListItemDto> content,
			int page,
			int size,
			long totalElements,
			int totalPages,
			boolean first,
			boolean last) {
		this.content = content;
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.first = first;
		this.last = last;
	}

	/**
	 * Factory a partir de una {@link Page} de Spring Data ya mapeada a DTOs de listado.
	 *
	 * @param page página de ítems de transacción
	 * @return wrapper listo para serializar en JSON
	 */
	public static TransaccionPageResponseDto from(Page<TransaccionListItemDto> page) {
		return new TransaccionPageResponseDto(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isFirst(),
				page.isLast());
	}
}
