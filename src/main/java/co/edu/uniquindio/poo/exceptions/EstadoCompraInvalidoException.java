package co.edu.uniquindio.poo.exceptions;

public class EstadoCompraInvalidoException extends CinemaException {
    public EstadoCompraInvalidoException() { super("Operación no válida para el estado actual de la compra"); }
    public EstadoCompraInvalidoException(String mensaje) { super(mensaje); }
}
