package co.edu.uniquindio.poo.exceptions;

public class CancelacionNoPermitidaException extends CinemaException {
    public CancelacionNoPermitidaException() { super("La cancelación no está permitida"); }
    public CancelacionNoPermitidaException(String mensaje) { super(mensaje); }
}
