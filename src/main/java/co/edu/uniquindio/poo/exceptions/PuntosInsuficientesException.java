package co.edu.uniquindio.poo.exceptions;

public class PuntosInsuficientesException extends CinemaException {
    public PuntosInsuficientesException() { super("No tiene puntos suficientes"); }
    public PuntosInsuficientesException(String mensaje) { super(mensaje); }
}
