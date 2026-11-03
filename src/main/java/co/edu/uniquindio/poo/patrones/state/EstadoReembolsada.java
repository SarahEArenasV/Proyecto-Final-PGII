package co.edu.uniquindio.poo.patrones.state;

import co.edu.uniquindio.poo.exceptions.ReembolsoDuplicadoException;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Reembolso;

public class EstadoReembolsada extends EstadoCompraBase {
    @Override public String getNombre() { return "CANCELADA - REEMBOLSADA"; }

    @Override public void reembolsar(Compra compra, Reembolso r) {
        throw new ReembolsoDuplicadoException("La compra " + compra.getId() + " ya fue reembolsada");
    }
}
