package co.edu.uniquindio.poo.exceptions;

public class DatosInvalidosException extends CinemaException {
    public DatosInvalidosException() { super("Datos inválidos"); }
    public DatosInvalidosException(String mensaje) { super(mensaje); }
}
