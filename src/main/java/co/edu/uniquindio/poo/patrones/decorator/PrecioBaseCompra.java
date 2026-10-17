package co.edu.uniquindio.poo.patrones.decorator;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PrecioBaseCompra implements PrecioCompra {
    private final double entradas;
    private final double confiteria;

    public PrecioBaseCompra(double entradas, double confiteria) {
        if (entradas < 0 || confiteria < 0) throw new DatosInvalidosException("Los valores de la compra no pueden ser negativos");
        this.entradas = entradas;
        this.confiteria = confiteria;
    }

    @Override public double getPrecioBase() { return entradas + confiteria; }
    @Override public List<String> getDetalle() { return new ArrayList<>(); }
}
