package com.soaint.ealavez.entities.enums;

import com.soaint.ealavez.entities.Transaccion;

/**
 * Estados permitidos para el ciclo de vida de una {@link Transaccion}.
 */
public enum TransaccionEstatus {
    /**
     * Estatus asignado al registrar una transacción (POST).
     */
    APROBADA,

    /**
     * Estatus persistido tras una cancelación exitosa (PATCH).
     */
    CANCELADA
}
