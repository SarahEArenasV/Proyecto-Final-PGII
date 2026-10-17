package co.edu.uniquindio.poo.patrones.facade;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Usuario;

public interface FachadaAutenticacion {
    Usuario iniciarSesion(String correo, String clave);
    Cliente registrarCliente(String nombre, String documento, String correo, String telefono, String clave);
    Cliente registrarCliente(String nombre, String documento, String correo, String telefono, String clave, String confirmacion);
    void cerrarSesion();
}
