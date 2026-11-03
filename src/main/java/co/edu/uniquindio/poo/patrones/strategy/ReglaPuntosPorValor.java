package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;

public class ReglaPuntosPorValor extends ReglaAcumulacionBase {
    private final double valorPorPunto;

    public ReglaPuntosPorValor(double valorPorPunto) {
        if (valorPorPunto <= 0) throw new IllegalArgumentException("El valor por punto debe ser positivo");
        this.valorPorPunto = valorPorPunto;
    }

    @Override protected int calcularBase(Compra compra) { return (int) (compra.getTotal() / valorPorPunto); }

    @Override public String getDescripcion() {
        return String.format("1 punto por cada $%,.0f pagados (+%.0f%% con %d o más entradas)", valorPorPunto, PORCENTAJE_BONO, ENTRADAS_PARA_BONO);
    }
}
