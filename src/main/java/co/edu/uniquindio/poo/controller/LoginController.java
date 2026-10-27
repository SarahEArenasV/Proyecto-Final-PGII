package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.patrones.facade.FachadaAutenticacion;

public class LoginController {
    private final FachadaAutenticacion fachada;

    public LoginController(FachadaAutenticacion fachada) { this.fachada = fachada; }

    public Usuario iniciarSesion(String correo, String clave) { return fachada.iniciarSesion(correo, clave); }

    public Cliente registrar(String nombre, String documento, String correo, String telefono, String clave, String confirmacion) {
        return fachada.registrarCliente(nombre, documento, correo, telefono, clave, confirmacion);
    }

    public void cerrarSesion() { fachada.cerrarSesion(); }
}
