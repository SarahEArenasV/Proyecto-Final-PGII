package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;

public class EntradaGeneral extends Entrada {
    public static final double MULTIPLICADOR = 1.0;
    public static final int PUNTOS_REDENCION = 150;

    public EntradaGeneral(String id, Funcion funcion, Asiento asiento) { super(id, funcion, asiento); }

    @Override protected double getMultiplicador() { return MULTIPLICADOR; }
    @Override public String getTipo() { return "General 2D"; }
    @Override public int getPuntosRedencion() { return PUNTOS_REDENCION; }
    @Override public String getBeneficios() { return "Silla estándar"; }
}
