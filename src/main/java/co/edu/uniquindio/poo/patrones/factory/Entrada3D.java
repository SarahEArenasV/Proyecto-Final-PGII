package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;

public class Entrada3D extends Entrada {
    public static final double MULTIPLICADOR = 1.3;
    public static final int PUNTOS_REDENCION = 200;

    public Entrada3D(String id, Funcion funcion, Asiento asiento) { super(id, funcion, asiento); }

    @Override protected double getMultiplicador() { return MULTIPLICADOR; }
    @Override public String getTipo() { return "3D"; }
    @Override public int getPuntosRedencion() { return PUNTOS_REDENCION; }
    @Override public String getBeneficios() { return "Gafas 3D incluidas y sonido envolvente"; }
}
