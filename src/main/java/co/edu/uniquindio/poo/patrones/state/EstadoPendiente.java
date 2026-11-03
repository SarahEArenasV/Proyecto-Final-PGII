package co.edu.uniquindio.poo.patrones.state;

import co.edu.uniquindio.poo.exceptions.AsientoNoDisponibleException;
import co.edu.uniquindio.poo.exceptions.EstadoCompraInvalidoException;
import co.edu.uniquindio.poo.exceptions.FuncionNoDisponibleException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Reembolso;
import co.edu.uniquindio.poo.patrones.factory.Entrada;

import java.time.LocalDateTime;

public class EstadoPendiente extends EstadoCompraBase {
    @Override public String getNombre() { return "PENDIENTE"; }

    @Override public void pagar(Compra compra) {
        Cliente cliente = compra.getCliente();
        Funcion funcion = compra.getFuncion();
        LocalDateTime fecha = compra.getFecha();

        if (!funcion.estaDisponibleParaVenta(fecha))
            throw new FuncionNoDisponibleException("La función ya no está disponible para la venta");
        for (Entrada entrada : compra.getEntradas()) {
            String codigo = entrada.getAsiento().getCodigo();
            if (!funcion.isAsientoDisponible(codigo))
                throw new AsientoNoDisponibleException("El asiento " + codigo + " ya fue vendido");
        }
        cliente.getTarjeta().verificarActiva();
        if (compra.getTotal() > 0) cliente.getTarjeta().verificarSaldo(compra.getTotal());
        if (compra.getPuntosUsados() > 0) cliente.getCuentaPuntos().verificarPuntos(compra.getPuntosUsados(), fecha.toLocalDate());

        if (compra.getTotal() > 0) {
            cliente.getTarjeta().debitar(compra.getTotal(), "Pago compra " + compra.getId(), fecha);
        }
        if (compra.getPuntosUsados() > 0) {
            compra.setConsumosPuntos(cliente.getCuentaPuntos()
                    .redimir(compra.getPuntosUsados(), fecha, "Redención en compra " + compra.getId()));
        }
        for (Entrada entrada : compra.getEntradas()) {
            funcion.ocuparAsiento(entrada.getAsiento().getCodigo());
        }

        transicionar(compra, new EstadoPagada());
    }

    @Override public void anular(Compra compra) { transicionar(compra, new EstadoAnulada()); }

    @Override public void reembolsar(Compra compra, Reembolso r) {
        throw new EstadoCompraInvalidoException("No se puede reembolsar una compra que no ha sido pagada");
    }

    @Override public boolean esFinal() { return false; }
}
