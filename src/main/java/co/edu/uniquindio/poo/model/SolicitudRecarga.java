package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.EstadoSolicitudRecarga;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class SolicitudRecarga implements Identificable {
    private final String id;
    private final Cliente cliente;
    private final double monto;
    private final LocalDateTime fecha;

    @Setter
    private EstadoSolicitudRecarga estado = EstadoSolicitudRecarga.PENDIENTE;

    public SolicitudRecarga(String id, Cliente cliente, double monto) {
        this.id = id;
        this.cliente = cliente;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }
}
