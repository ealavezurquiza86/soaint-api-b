package com.soaint.ealavez.services.support;

import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.soaint.ealavez.exceptions.custom.InvalidPaginationException;

/**
 * Valida y normaliza {@link Pageable} para consultas de transacciones.
 * <p>
 * Responsabilidad única (SRP): reglas de paginación/ordenamiento permitido.
 * Mitiga ordenamiento por propiedades no autorizadas (OWASP API8) y limita el tamaño de página (OWASP API4).
 * Usamos @Component para que la IoC de Spring lo resuelva sus dependencias, lo instancie y podamos hacer DI en otros beans o componentes
 * </p>
 */
@Component
public class TransaccionPageableValidator {

	/**
	 * Campos de la entidad {@code Transaccion} autorizados para ordenamiento
	 */
	private static final Set<String> CAMPOS_ORDEN_PERMITIDOS = Set.of( "id", "operacion", "importe", "cliente", "referencia", "estatus" );

	private final int maxPageSize;

	/**
	 * @param maxPageSize tope de registros por página (desde configuración)
	 */
	public TransaccionPageableValidator( @Value("${spring.data.web.pageable.max-page-size:100}") int maxPageSize ) {
        this.maxPageSize = maxPageSize;
    }

	/**
	 * Valida número de página, tamaño y campos de orden; devuelve un {@link Pageable} seguro.
	 *
	 * @param pageable parámetros recibidos del cliente (construidos automáticamente por Spring Data Web)
	 * @return {@link PageRequest} normalizado listo para el repositorio
	 * @throws InvalidPaginationException si algún parámetro viola las reglas de negocio o seguridad
	 */
	public Pageable validate( Pageable pageable ) {
        if ( pageable.getPageNumber() < 0 ) 
            throw new InvalidPaginationException("El número de página debe ser mayor o igual a 0.");
        

        if ( pageable.getPageSize() <= 0 ) 
            throw new InvalidPaginationException("El número de registros por página debe ser mayor a 0.");
        

        int size = Math.min( pageable.getPageSize(), maxPageSize );
        Sort sort = validateSort( pageable.getSort() );

        return PageRequest.of( pageable.getPageNumber(), size, sort );
    }

	/**
	 * Verifica que cada propiedad de orden esté en la whitelist permitida.
	 *
	 * @param sort orden solicitado
	 * @return el mismo {@link Sort} si es válido, o un ordenamiento por defecto si viene vacío
	 * @throws InvalidPaginationException si algún campo no está permitido
	 */
	private Sort validateSort( Sort sort ) {

        if ( sort.isUnsorted() )  return Sort.by(Sort.Direction.DESC, "id"); 
            
        for (Sort.Order order : sort) {

            if ( !CAMPOS_ORDEN_PERMITIDOS.contains(order.getProperty()) ) {
                throw new InvalidPaginationException( "Campo de ordenamiento no permitido: " + order.getProperty() );
            }
        }

        return sort;
    }

}
