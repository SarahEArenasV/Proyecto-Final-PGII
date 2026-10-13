package co.edu.uniquindio.poo.exceptions;

public class AccesoDenegadoException extends CinemaException {
    public AccesoDenegadoException() { super("No tiene permisos para esta operación"); }
    public AccesoDenegadoException(String mensaje) { super(mensaje); }
}
