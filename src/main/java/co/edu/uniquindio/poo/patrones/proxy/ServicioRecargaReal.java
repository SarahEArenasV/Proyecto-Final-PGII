package co.edu.uniquindio.poo.patrones.proxy;

import co.edu.uniquindio.poo.enums.TipoMovimiento;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.util.Reloj;

public class ServicioRecargaReal implements ServicioRecarga {
    private final Reloj reloj;

    public ServicioRecargaReal(Reloj reloj) {
        this.reloj = reloj;
    }

    @Override public void recargar(Cliente cliente, double monto, String descripcion) {
        cliente.getTarjeta().acreditar(monto, TipoMovimiento.RECARGA, descripcion, reloj.ahora());
    }
}
