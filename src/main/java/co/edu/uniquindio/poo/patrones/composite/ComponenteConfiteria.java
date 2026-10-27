package co.edu.uniquindio.poo.patrones.composite;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public abstract class ComponenteConfiteria implements ItemConfiteria {
    @Getter private final String id;
    @Getter private String nombre;
    @Setter private boolean activo = true;

    protected ComponenteConfiteria(String id, String nombre) {
        this.id = id;
        setNombre(nombre);
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) throw new DatosInvalidosException("El nombre es obligatorio");
        this.nombre = nombre.trim();
    }

    protected boolean isActivoPropio() { return activo; }

    @Override public boolean equals(Object o) {
        return this == o || (o instanceof ComponenteConfiteria c && Objects.equals(id, c.id));
    }

    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() { return nombre; }
}
