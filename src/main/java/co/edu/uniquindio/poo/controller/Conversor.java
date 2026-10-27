package co.edu.uniquindio.poo.controller;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

final class Conversor {
    private Conversor() { }

    static int entero(String texto, String campo) {
        try { return Integer.parseInt(texto.trim()); }
        catch (Exception e) { throw new DatosInvalidosException("El campo " + campo + " debe ser un número entero"); }
    }

    static double decimal(String texto, String campo) {
        try { return Double.parseDouble(texto.trim().replace(".", "").replace(",", ".").replace("$", "")); }
        catch (Exception e) { throw new DatosInvalidosException("El campo " + campo + " debe ser numérico"); }
    }

    static LocalTime hora(String texto) {
        try { return LocalTime.parse(texto.trim().length() == 4 ? "0" + texto.trim() : texto.trim()); }
        catch (DateTimeParseException | NullPointerException e) { throw new DatosInvalidosException("La hora debe tener formato HH:mm"); }
    }
}
