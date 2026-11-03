package co.edu.uniquindio.poo.patrones.state;

import co.edu.uniquindio.poo.enums.TipoMovimiento;
import co.edu.uniquindio.poo.exceptions.EstadoCompraInvalidoException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Reembolso;
import co.edu.uniquindio.poo.patrones.factory.Entrada;

public class EstadoPagada extends EstadoCompraBase {
    @Override public String getNombre() { return "PAGADA"; }

    @Override public void pagar(Compra compra) { throw new EstadoCompraInvalidoException("La compra ya fue pagada"); }

    @Override public void reembolsar(Compra compra, Reembolso reembolso) {
        if (reembolso == null) throw new EstadoCompraInvalidoException("El reembolso es obligatorio");
        Cliente cliente = compra.getCliente();

        compra.asignarReembolso(reembolso);
        if (reembolso.getMontoReembolsado() > 0) {
            cliente.getTarjeta().acreditar(reembolso.getMontoReembolsado(), TipoMovimiento.REEMBOLSO,
                    "Reembolso " + (int) reembolso.getPorcentaje() + "% compra " + compra.getId(), reembolso.getFecha());
        }
        cliente.getCuentaPuntos().devolver(compra.getConsumosPuntos(),
                "Devolución por cancelación " + compra.getId(), reembolso.getFecha());
        cliente.getCuentaPuntos().revertir(compra.getLoteGenerado(),
                "Reversión puntos ganados en " + compra.getId(), reembolso.getFecha());
        for (Entrada entrada : compra.getEntradas()) {
            compra.getFuncion().liberarAsiento(entrada.getAsiento().getCodigo());
        }
        transicionar(compra, new EstadoReembolsada());
    }

    @Override public void anular(Compra compra) {
        throw new EstadoCompraInvalidoException("Una compra pagada debe cancelarse con reembolso");
    }

    @Override public boolean permiteCancelacion() { return true; }
    @Override public boolean esFinal() { return false; }
}
