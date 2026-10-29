package co.edu.uniquindio.poo.enums;

import lombok.Getter;

@Getter
public enum TipoReporte {
    VENTAS_PERIODO("Ventas por periodo"),
    ENTRADAS_POR_PELICULA("Entradas vendidas por película/función"),
    OCUPACION_SALAS("Ocupación de salas"),
    PRODUCTOS_VENDIDOS("Productos y combos vendidos"),
    RECARGAS("Recargas realizadas"),
    CANCELACIONES("Cancelaciones y reembolsos"),
    PUNTOS("Puntos acumulados y redimidos");
    private final String titulo;
    TipoReporte(String titulo) { this.titulo = titulo; }
    @Override public String toString() { return titulo; }
}
