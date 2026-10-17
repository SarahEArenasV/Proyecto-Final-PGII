package co.edu.uniquindio.poo.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class  Formato {
    public static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Formato() { }

    public static String moneda(double valor) { return String.format("$%,.0f", valor); }
    public static String fecha(LocalDate f) { return f == null ? "" : f.format(FECHA); }
    public static String fechaHora(LocalDateTime f) { return f == null ? "" : f.format(FECHA_HORA); }
}
