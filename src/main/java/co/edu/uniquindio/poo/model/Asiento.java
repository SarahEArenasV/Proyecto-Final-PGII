package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoAsiento;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Asiento {
    private final char fila;
    private final int numero;
    private TipoAsiento tipo;
    private boolean habilitado = true;

    public Asiento(char fila, int numero, TipoAsiento tipo) {
        this.fila = fila;
        this.numero = numero;
        this.tipo = tipo;
    }

    public String getCodigo() {
        return "" + fila + numero;
    }

    @Override
    public String toString() {
        return getCodigo();
    }
}
