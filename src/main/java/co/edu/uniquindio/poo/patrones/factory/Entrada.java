package co.edu.uniquindio.poo.patrones.factory;

import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Funcion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entrada {
    private final String id;
    private final Funcion funcion;
    private final Asiento asiento;
    private boolean redimidaConPuntos;

    protected Entrada(String id, Funcion funcion, Asiento asiento) {
        this.id = id;
        this.funcion = funcion;
        this.asiento = asiento;
    }

    protected abstract double getMultiplicador();
    public abstract String getTipo();
    public abstract int getPuntosRedencion();

    public abstract String getBeneficios();

    public double calcularPrecio() {
        double recargo = switch (asiento.getTipo()) {
            case PREFERENCIAL -> 2000;
            case DISCAPACIDAD, ESTANDAR -> 0;
        };
        return funcion.getPrecioBase() * getMultiplicador() + recargo;
    }

    @Override public String toString() { return getTipo() + " - Asiento " + asiento.getCodigo(); }
}
