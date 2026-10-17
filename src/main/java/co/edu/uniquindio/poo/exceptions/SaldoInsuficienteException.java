package co.edu.uniquindio.poo.exceptions;

public class SaldoInsuficienteException extends CinemaException {
    public SaldoInsuficienteException() { super("La tarjeta no tiene saldo suficiente"); }
    public SaldoInsuficienteException(String mensaje) { super(mensaje); }
}
