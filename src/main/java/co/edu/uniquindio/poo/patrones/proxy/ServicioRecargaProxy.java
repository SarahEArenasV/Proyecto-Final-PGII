package co.edu.uniquindio.poo.patrones.proxy;

import co.edu.uniquindio.poo.enums.EstadoTarjeta;
import co.edu.uniquindio.poo.exceptions.AccesoDenegadoException;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.patrones.singleton.SesionUsuario;
import co.edu.uniquindio.poo.util.Formato;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServicioRecargaProxy implements ServicioRecarga {
    public static final double MONTO_MINIMO = 10_000;
    public static final double MONTO_MAXIMO = 2_000_000;

    private final ServicioRecarga real;
    private final List<String> intentosRechazados = new ArrayList<>();

    public ServicioRecargaProxy(ServicioRecarga real) { this.real = real; }

    @Override public void recargar(Cliente cliente, double monto, String descripcion) {
        try {
            verificarAcceso();
            verificarDatos(cliente, monto);
        } catch (AccesoDenegadoException | DatosInvalidosException e) {
            registrarRechazo(cliente, monto, e.getMessage());
            throw e;
        }
        real.recargar(cliente, monto, descripcion);
    }

    private void verificarAcceso() {
        SesionUsuario.getInstancia().exigirAdministrador("recargar tarjetas");
    }

    private void verificarDatos(Cliente cliente, double monto) {
        if (cliente == null) throw new DatosInvalidosException("Seleccione un cliente");
        if (cliente.getTarjeta().getEstado() != EstadoTarjeta.ACTIVA)
            throw new DatosInvalidosException("La tarjeta del cliente está bloqueada; actívela antes de recargar");
        if (monto < MONTO_MINIMO || monto > MONTO_MAXIMO)
            throw new DatosInvalidosException("La recarga debe estar entre " + Formato.moneda(MONTO_MINIMO)
                    + " y " + Formato.moneda(MONTO_MAXIMO));
    }

    private void registrarRechazo(Cliente cliente, double monto, String motivo) {
        Usuario u = SesionUsuario.getInstancia().getUsuarioActual();
        intentosRechazados.add(Formato.fechaHora(LocalDateTime.now()) + " | " + (u == null ? "sin sesión" : u.getNombre())
                + " | " + (cliente == null ? "-" : cliente.getNombre()) + " | " + Formato.moneda(monto) + " | " + motivo);
    }

    public List<String> getIntentosRechazados() { return Collections.unmodifiableList(intentosRechazados); }
}
