package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoMovimiento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MovimientoTarjeta {
    private final TipoMovimiento tipo;
    private final double monto;
    private final double saldoResultante;
    private final LocalDateTime fecha;
    private final String descripcion;
}
