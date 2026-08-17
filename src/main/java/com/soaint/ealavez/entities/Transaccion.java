package com.soaint.ealavez.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.soaint.ealavez.entities.enums.TransaccionEstatus;

import lombok.Getter;
import lombok.Setter;

/**
 * Entidad JPA que representa una transacción persistida
 */
@Entity
@Table(name = "transacciones")
@Getter
@Setter
public class Transaccion {

	/**
	 * Identificador autoincremental PK
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * Tipo o nombre de la operación
	 */
	@Column(name = "operacion", nullable = false, length = 250)
	private String operacion;

	/**
	 * Importe monetario de la operación
	 */
	@Column(name = "importe", nullable = false, precision = 16, scale = 2)
	private BigDecimal importe;

	/**
	 * Cliente asociado a la transacción
	 */
	@Column(name = "cliente", nullable = false, length = 500)
	private String cliente;

	/**
	 * Referencia numérica de 6 dígitos generada en el servicio
	 */
	@Column(name = "referencia", nullable = false, length = 6)
	private String referencia;

	/**
	 * Estatus de la transacción
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "estatus", nullable = false, length = 100)
	private TransaccionEstatus estatus;

	/**
	 * Secreto en texto plano
	 */
	@Column(name = "secreto", nullable = false, length = 250)
	private String secreto;
}
