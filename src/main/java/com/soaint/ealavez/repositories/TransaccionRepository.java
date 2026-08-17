package com.soaint.ealavez.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.soaint.ealavez.entities.Transaccion;
import com.soaint.ealavez.entities.enums.TransaccionEstatus;

/**
 * Repositorio Spring Data JPA para {@link Transaccion}.
 */
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

	/**
	 * Actualiza el estatus solo si coinciden {@code id} y {@code referencia}
	 * y el estatus actual es el esperado (p. ej. {@code Aprobada}).
	 *
	 * @param id            primary key de la transacción
	 * @param referencia    referencia de 6 dígitos
	 * @param estatusActual estatus requerido antes del cambio
	 * @param nuevoEstatus  estatus a persistir (p. ej. {@code Cancelar})
	 * @return cantidad de registros actualizados (0 o 1)
	 */
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
			UPDATE Transaccion t
			   SET t.estatus = :nuevoEstatus
			 WHERE t.id = :id
			   AND t.estatus = :estatusActual
			   AND t.referencia = :referencia
			""")
	int actualizarEstatus(
			@Param("id") Long id,
			@Param("referencia") String referencia,
			@Param("estatusActual") TransaccionEstatus estatusActual,
			@Param("nuevoEstatus") TransaccionEstatus nuevoEstatus);
}
