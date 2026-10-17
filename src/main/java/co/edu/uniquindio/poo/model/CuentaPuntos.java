package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoMovimientoPuntos;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.exceptions.PuntosInsuficientesException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class CuentaPuntos {
    private final List<LotePuntos> lotes = new ArrayList<>();
    private final List<MovimientoPuntos> movimientos = new ArrayList<>();

    public LotePuntos acumular(int puntos, LocalDateTime fecha, String origen) {
        if (puntos <= 0) throw new DatosInvalidosException("La cantidad de puntos debe ser positiva");
        LotePuntos lote = new LotePuntos(UUID.randomUUID().toString(), puntos, fecha.toLocalDate(), origen);
        lotes.add(lote);
        movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.ACUMULACION, puntos, fecha, origen));
        return lote;
    }

    public int getPuntosDisponibles(LocalDate hoy) {
        return lotes.stream().filter(l -> l.estaVigente(hoy)).mapToInt(LotePuntos::getDisponibles).sum();
    }

    public int getPuntosVencidos(LocalDate hoy) {
        return lotes.stream().filter(l -> !l.estaVigente(hoy)).mapToInt(LotePuntos::getDisponibles).sum();
    }

    public void verificarPuntos(int requeridos, LocalDate hoy) {
        int disponibles = getPuntosDisponibles(hoy);
        if (disponibles < requeridos) {
            throw new PuntosInsuficientesException("Puntos insuficientes. Disponibles: " + disponibles
                    + " - Requeridos: " + requeridos);
        }
    }

    public List<ConsumoPuntos> redimir(int puntos, LocalDateTime ahora, String descripcion) {
        LocalDate hoy = ahora.toLocalDate();
        if (puntos <= 0) return List.of();
        verificarPuntos(puntos, hoy);
        List<ConsumoPuntos> consumos = new ArrayList<>();
        int restantes = puntos;
        List<LotePuntos> vigentes = lotes.stream()
                .filter(l -> l.estaVigente(hoy) && l.getDisponibles() > 0)
                .sorted(Comparator.comparing(LotePuntos::getFechaVencimiento))
                .toList();
        for (LotePuntos lote : vigentes) {
            if (restantes == 0) break;
            int usados = lote.consumir(restantes);
            restantes -= usados;
            consumos.add(new ConsumoPuntos(lote, usados));
        }
        movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.REDENCION, puntos, ahora, descripcion));
        return consumos;
    }

    public int devolver(List<ConsumoPuntos> consumos, String descripcion, LocalDateTime fecha) {
        int total = 0;
        for (ConsumoPuntos c : consumos) {
            c.lote().devolver(c.cantidad());
            total += c.cantidad();
        }
        if (total > 0) {
            movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.DEVOLUCION, total, fecha, descripcion));
        }
        return total;
    }

    public int revertir(LotePuntos lote, String descripcion, LocalDateTime fecha) {
        if (lote == null) return 0;
        int retirados = lote.consumir(lote.getDisponibles());
        if (retirados > 0) {
            movimientos.add(new MovimientoPuntos(TipoMovimientoPuntos.REVERSION, retirados, fecha, descripcion));
        }
        return retirados;
    }

    public List<LotePuntos> getLotes() { return Collections.unmodifiableList(lotes); }
    public List<MovimientoPuntos> getMovimientos() { return Collections.unmodifiableList(movimientos); }
}
