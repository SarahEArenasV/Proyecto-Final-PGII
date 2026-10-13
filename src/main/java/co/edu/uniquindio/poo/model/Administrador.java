package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.Rol;

public class Administrador extends Usuario {
    public Administrador(String id, String nombre, String correo, String contrasena) {
        super(id, nombre, correo, contrasena);
    }
    @Override public Rol getRol() { return Rol.ADMINISTRADOR; }
}
