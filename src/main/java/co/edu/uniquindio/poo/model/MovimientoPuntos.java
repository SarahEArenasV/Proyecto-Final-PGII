package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoMovimientoPuntos;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MovimientoPuntos {
    private final TipoMovimientoPuntos tipo;
    private final int cantidad;
    private final LocalDateTime fecha;
    private final String descripcion;
}
