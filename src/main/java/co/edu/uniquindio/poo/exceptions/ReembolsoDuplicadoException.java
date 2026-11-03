package co.edu.uniquindio.poo.exceptions;

public class ReembolsoDuplicadoException extends CinemaException {
    public ReembolsoDuplicadoException() { super("El reembolso ya fue ejecutado"); }
    public ReembolsoDuplicadoException(String mensaje) { super(mensaje); }
}
