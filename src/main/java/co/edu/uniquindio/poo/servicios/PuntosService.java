package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.LotePuntos;
import co.edu.uniquindio.poo.model.MovimientoPuntos;
import co.edu.uniquindio.poo.patrones.composite.Combo;
import co.edu.uniquindio.poo.patrones.factory.Entrada3D;
import co.edu.uniquindio.poo.patrones.factory.EntradaGeneral;
import co.edu.uniquindio.poo.patrones.factory.EntradaVIP;
import co.edu.uniquindio.poo.patrones.strategy.ContextoPuntos;
import co.edu.uniquindio.poo.patrones.strategy.ReglaAcumulacionPuntos;
import co.edu.uniquindio.poo.util.Reloj;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PuntosService {
    private final ContextoPuntos contexto;
    private final Reloj reloj;

    public PuntosService(ReglaAcumulacionPuntos regla, Reloj reloj) {
        this.contexto = new ContextoPuntos(regla);
        this.reloj = reloj;
    }

    public void acumularPorCompra(Compra compra) {
        int puntos = contexto.calcularPuntos(compra);
        if (puntos <= 0) return;
        LotePuntos lote = compra.getCliente().getCuentaPuntos().acumular(puntos, compra.getFecha(), "Compra " + compra.getId());
        compra.setPuntosGanados(puntos);
        compra.setLoteGenerado(lote);
    }

    public List<LotePuntos> lotes(Cliente c) {
        return c.getCuentaPuntos().getLotes().stream()
                .sorted(Comparator.comparing(LotePuntos::getFechaVencimiento)).toList();
    }

    public boolean loteVigente(LotePuntos lote) { return lote.estaVigente(reloj.hoy()); }

    public List<MovimientoPuntos> movimientos(Cliente c) {
        return c.getCuentaPuntos().getMovimientos().stream()
                .sorted(Comparator.comparing(MovimientoPuntos::getFecha).reversed()).toList();
    }

    public Map<String, String> equivalencias() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Entrada General 2D", EntradaGeneral.PUNTOS_REDENCION + " puntos");
        m.put("Entrada 3D", Entrada3D.PUNTOS_REDENCION + " puntos");
        m.put("Entrada VIP", EntradaVIP.PUNTOS_REDENCION + " puntos");
        m.put("Productos", "Según el producto (≈ 1 punto por cada $100)");
        m.put("Combos", Math.round(Combo.FACTOR_PUNTOS * 100) + " % de la suma de sus productos");
        return m;
    }

    public int disponibles(Cliente c) { return c.getCuentaPuntos().getPuntosDisponibles(reloj.hoy()); }

    public void cambiarRegla(ReglaAcumulacionPuntos nueva) { contexto.setRegla(nueva); }

    public String descripcionRegla() { return contexto.getDescripcion(); }
}
