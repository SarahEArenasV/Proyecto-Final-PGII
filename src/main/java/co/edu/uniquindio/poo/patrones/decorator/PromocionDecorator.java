package co.edu.uniquindio.poo.patrones.decorator;

import co.edu.uniquindio.poo.model.Promocion;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class PromocionDecorator extends DecoradorPrecio {
    protected final Promocion promocion;

    protected PromocionDecorator(PrecioCompra envuelto, Promocion promocion) {
        super(envuelto);
        this.promocion = Objects.requireNonNull(promocion, "La promoción es obligatoria");
    }

    protected double factor() { return 1 - promocion.getPorcentaje() / 100.0; }

    @Override public List<String> getDetalle() {
        List<String> detalle = envuelto.getDetalle();
        double ahorro = envuelto.getTotal() - getTotal();
        if (ahorro > 0) detalle.add(String.format("%s: -$%,.0f", promocion.getNombre(), ahorro));
        return detalle;
    }

    public static PrecioCompra aplicar(PrecioCompra base, Promocion promocion) {
        return switch (promocion.getTipo()) {
            case ENTRADAS -> new DescuentoEntradasDecorator(base, promocion);
            case CONFITERIA -> new DescuentoConfiteriaDecorator(base, promocion);
            case TOTAL -> new DescuentoTotalDecorator(base, promocion);
        };
    }
}
