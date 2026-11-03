package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;

public abstract class ReglaAcumulacionBase implements ReglaAcumulacionPuntos {
    public static final int ENTRADAS_PARA_BONO = 4;
    public static final double PORCENTAJE_BONO = 10;

    @Override public final int calcularPuntos(Compra compra) {
        if (compra == null || compra.getTotal() <= 0) return 0;
        int base = calcularBase(compra);
        if (compra.getCantidadEntradas() >= ENTRADAS_PARA_BONO) base = (int) Math.round(base * (1 + PORCENTAJE_BONO / 100));
        return Math.max(0, base);
    }

    protected abstract int calcularBase(Compra compra);
}
