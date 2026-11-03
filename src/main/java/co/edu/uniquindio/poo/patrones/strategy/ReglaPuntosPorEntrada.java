package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;

public class ReglaPuntosPorEntrada extends ReglaAcumulacionBase {
    private final int puntosPorEntrada;

    public ReglaPuntosPorEntrada(int puntosPorEntrada) {
        if (puntosPorEntrada <= 0) throw new IllegalArgumentException("Los puntos por entrada deben ser positivos");
        this.puntosPorEntrada = puntosPorEntrada;
    }

    @Override protected int calcularBase(Compra compra) {
        long pagadas = compra.getEntradas().stream().filter(e -> !e.isRedimidaConPuntos()).count();
        return (int) (pagadas * puntosPorEntrada + compra.getSubtotalConfiteria() / 2000);
    }

    @Override public String getDescripcion() {
        return puntosPorEntrada + " puntos por entrada pagada + 1 punto por cada $2.000 de confitería";
    }
}
