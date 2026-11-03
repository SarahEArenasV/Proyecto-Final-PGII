package co.edu.uniquindio.poo.patrones.strategy;

import co.edu.uniquindio.poo.model.Compra;
import lombok.Getter;

import java.util.Objects;

@Getter
public class ContextoPuntos {
    private ReglaAcumulacionPuntos regla;

    public ContextoPuntos(ReglaAcumulacionPuntos regla) { setRegla(regla); }

    public void setRegla(ReglaAcumulacionPuntos regla) {
        this.regla = Objects.requireNonNull(regla, "La regla de acumulación es obligatoria");
    }

    public int calcularPuntos(Compra compra) { return regla.calcularPuntos(compra); }

    public String getDescripcion() { return regla.getDescripcion(); }
}
