package co.edu.uniquindio.poo.exceptions;

public class AutenticacionException extends CinemaException {
    public AutenticacionException() { super("Credenciales inválidas"); }
    public AutenticacionException(String mensaje) { super(mensaje); }
}
