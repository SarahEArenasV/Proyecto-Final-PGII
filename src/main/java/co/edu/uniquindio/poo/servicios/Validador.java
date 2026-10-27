package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;

final class Validador {
    private Validador() { }

    static String requerido(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new DatosInvalidosException("El campo " + campo + " es obligatorio");
        return valor.trim();
    }

    static void positivo(double valor, String campo) {
        if (valor <= 0) throw new DatosInvalidosException("El campo " + campo + " debe ser mayor que cero");
    }

    static void porcentaje(double valor) {
        if (valor <= 0 || valor > 100) throw new DatosInvalidosException("El porcentaje debe estar entre 1 y 100");
    }
}
