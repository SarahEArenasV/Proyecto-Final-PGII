package co.edu.uniquindio.poo.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Reloj {
    private Duration desfase = Duration.ZERO;

    public LocalDateTime ahora() { return LocalDateTime.now().plus(desfase); }
    public LocalDate hoy() { return ahora().toLocalDate(); }

    public void ajustar(LocalDateTime momento) { desfase = Duration.between(LocalDateTime.now(), momento); }
    public void restablecer() { desfase = Duration.ZERO; }
}
