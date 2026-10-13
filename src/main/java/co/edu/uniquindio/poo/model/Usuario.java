package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.Rol;
import lombok.Getter;
import lombok.Setter;

@Setter
public abstract class Usuario implements Identificable {
    @Getter private final String id;
    @Getter @Setter private String nombre;
    @Getter @Setter private String correo;
    private String contrasena;

    protected Usuario(String id, String nombre, String correo, String contrasena) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public abstract Rol getRol();

    public boolean validarContrasena(String clave) { return contrasena.equals(clave); }

    @Override public String toString() { return nombre + " (" + correo + ")"; }
}
