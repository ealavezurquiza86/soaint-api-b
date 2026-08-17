package com.soaint.ealavez.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import com.soaint.ealavez.entities.Transaccion;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionRequestDto;
import com.soaint.ealavez.models.dto.transaccion.RegistroTransaccionResponseDto;
import com.soaint.ealavez.models.dto.transaccion.TransaccionListItemDto;

/**
 * Mapper MapStruct entre DTOs de registro y la entidad {@link Transaccion}.
 */
@Mapper(componentModel = "spring")
public interface TransaccionMapper {

	/**
	 * Convierte el request en entidad; {@code id}, {@code referencia} y {@code estatus}
	 *
	 * @param dto datos de entrada
	 * @return entidad sin enriquecer
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "referencia", ignore = true)
	@Mapping(target = "estatus", ignore = true)
	Transaccion toEntity(RegistroTransaccionRequestDto dto);

	/**
	 * Convierte la entidad persistida al DTO de respuesta (id como String).
	 *
	 * @param entity entidad guardada
	 * @return comprobante de registro
	 */
	@Mapping(target = "id", expression = "java(entity.getId() == null ? null : String.valueOf(entity.getId()))")
	RegistroTransaccionResponseDto toRegistroResponseDto(Transaccion entity);

	/**
	 * Convierte una entidad al DTO de listado (sin {@code secreto}).
	 *
	 * @param entity entidad persistida
	 * @return ítem de listado paginado
	 */
	@Mapping(target = "id", expression = "java(entity.getId() == null ? null : String.valueOf(entity.getId()))")
	TransaccionListItemDto toListItemDto(Transaccion entity);

	/**
	 * Convierte una lista de entidades a DTOs de listado.
	 *
	 * @param entities registros de la página actual
	 * @return lista de ítems sin datos sensibles
	 */
	List<TransaccionListItemDto> toListItemDtoList(List<Transaccion> entities);
}
