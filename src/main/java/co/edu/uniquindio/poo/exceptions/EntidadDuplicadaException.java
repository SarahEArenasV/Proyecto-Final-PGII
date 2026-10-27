package co.edu.uniquindio.poo.exceptions;

public class EntidadDuplicadaException extends CinemaException {
    public EntidadDuplicadaException() { super("El registro ya existe"); }
    public EntidadDuplicadaException(String mensaje) { super(mensaje); }
}
