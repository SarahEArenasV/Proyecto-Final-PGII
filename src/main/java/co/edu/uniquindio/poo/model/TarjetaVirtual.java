package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.TipoMovimiento;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.poo.exceptions.TarjetaInactivaException;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TarjetaVirtual {
    @Getter private final String numero;
    @Getter private double saldo;
    @Getter @Setter private EstadoTarjeta estado;
    private final List<MovimientoTarjeta> movimientos = new ArrayList<>();

    public TarjetaVirtual(String numero) {
        this.numero = numero;
        this.estado = EstadoTarjeta.ACTIVA;
    }

    public void verificarActiva() {
        if (estado != EstadoTarjeta.ACTIVA) throw new TarjetaInactivaException();
    }

    public void verificarSaldo(double monto) {
        if (saldo < monto) {
            throw new SaldoInsuficienteException(String.format(
                    "Saldo insuficiente. Saldo: $%,.0f - Requerido: $%,.0f. Solicite una recarga.", saldo, monto));
        }
    }

    public void debitar(double monto, String descripcion, LocalDateTime fecha) {
        validarMonto(monto);
        verificarActiva();
        verificarSaldo(monto);
        saldo -= monto;
        movimientos.add(new MovimientoTarjeta(TipoMovimiento.PAGO, monto, saldo, fecha, descripcion));
    }

    public void acreditar(double monto, TipoMovimiento tipo, String descripcion, LocalDateTime fecha) {
        validarMonto(monto);
        if (tipo == TipoMovimiento.PAGO) throw new DatosInvalidosException("Un pago no puede acreditarse");
        if (tipo == TipoMovimiento.RECARGA) verificarActiva();
        saldo += monto;
        movimientos.add(new MovimientoTarjeta(tipo, monto, saldo, fecha, descripcion));
    }

    private void validarMonto(double monto) {
        if (monto <= 0) throw new DatosInvalidosException("El monto debe ser mayor que cero");
    }

    public List<MovimientoTarjeta> getMovimientos() { return Collections.unmodifiableList(movimientos); }
}
