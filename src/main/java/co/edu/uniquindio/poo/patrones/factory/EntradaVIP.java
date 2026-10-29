package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;

public class EntradaVIP extends Entrada {
    public static final double MULTIPLICADOR = 1.8;
    public static final int PUNTOS_REDENCION = 300;

    public EntradaVIP(String id, Funcion funcion, Asiento asiento) { super(id, funcion, asiento); }

    @Override protected double getMultiplicador() { return MULTIPLICADOR; }
    @Override public String getTipo() { return "VIP"; }
    @Override public int getPuntosRedencion() { return PUNTOS_REDENCION; }
    @Override public String getBeneficios() { return "Silla reclinable, manta y servicio de confitería a la silla"; }
}
