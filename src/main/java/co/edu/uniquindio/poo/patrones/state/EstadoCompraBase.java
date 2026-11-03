package co.edu.uniquindio.poo.patrones.state;

import co.edu.uniquindio.poo.exceptions.EstadoCompraInvalidoException;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Reembolso;

public abstract class EstadoCompraBase implements EstadoCompra {
    @Override public void pagar(Compra compra) { rechazar("pagar"); }
    @Override public void reembolsar(Compra compra, Reembolso reembolso) { rechazar("reembolsar"); }
    @Override public void anular(Compra compra) { rechazar("anular"); }
    @Override public boolean permiteCancelacion() { return false; }
    @Override public boolean esFinal() { return true; }

    protected void transicionar(Compra compra, EstadoCompra siguiente) { compra.cambiarEstado(siguiente); }

    protected void rechazar(String operacion) {
        throw new EstadoCompraInvalidoException("No se puede " + operacion + " una compra en estado " + getNombre());
    }

    @Override public String toString() { return getNombre(); }
}
