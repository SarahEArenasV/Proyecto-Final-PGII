package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;

public interface ReglaAcumulacionPuntos {
    int calcularPuntos(Compra compra);
    String getDescripcion();
}
