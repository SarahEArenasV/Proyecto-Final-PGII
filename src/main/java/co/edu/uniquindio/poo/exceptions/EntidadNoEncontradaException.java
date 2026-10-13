package co.edu.uniquindio.poo.exceptions;

public class EntidadNoEncontradaException extends CinemaException {
    public EntidadNoEncontradaException() { super("Registro no encontrado"); }
    public EntidadNoEncontradaException(String mensaje) { super(mensaje); }
}
