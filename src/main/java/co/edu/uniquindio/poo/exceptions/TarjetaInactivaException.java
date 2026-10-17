package co.edu.uniquindio.poo.exceptions;

public class TarjetaInactivaException extends CinemaException {
    public TarjetaInactivaException() { super("La tarjeta virtual no se encuentra activa"); }
    public TarjetaInactivaException(String mensaje) { super(mensaje); }
}
