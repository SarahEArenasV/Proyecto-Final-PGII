package co.edu.uniquindio.poo.patrones.state;

import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Reembolso;

public interface EstadoCompra {
    String getNombre();
    void pagar(Compra compra);
    void reembolsar(Compra compra, Reembolso reembolso);
    void anular(Compra compra);
    boolean permiteCancelacion();

    boolean esFinal();
}
