package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.EstadoSolicitudRecarga;
import co.edu.uniquindio.poo.enums.EstadoTarjeta;
import co.edu.uniquindio.poo.exceptions.AccesoDenegadoException;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.MovimientoTarjeta;
import co.edu.uniquindio.poo.model.SolicitudRecarga;
import co.edu.uniquindio.poo.patrones.proxy.ServicioRecarga;
import co.edu.uniquindio.poo.patrones.proxy.ServicioRecargaProxy;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;
import co.edu.uniquindio.poo.patrones.singleton.SesionUsuario;
import co.edu.uniquindio.poo.util.Formato;

import java.util.Comparator;
import java.util.List;

public class TarjetaService {
    private final Repositorio<SolicitudRecarga> solicitudes;
    private final ServicioRecarga servicioRecarga;

    public TarjetaService(Repositorio<SolicitudRecarga> solicitudes, ServicioRecarga servicioRecarga) {
        this.solicitudes = solicitudes;
        this.servicioRecarga = servicioRecarga;
    }

    public SolicitudRecarga solicitarRecarga(Cliente cliente, double monto) {
        if (monto < ServicioRecargaProxy.MONTO_MINIMO || monto > ServicioRecargaProxy.MONTO_MAXIMO)
            throw new DatosInvalidosException("El monto debe estar entre " + Formato.moneda(ServicioRecargaProxy.MONTO_MINIMO)
                    + " y " + Formato.moneda(ServicioRecargaProxy.MONTO_MAXIMO));
        boolean pendiente = solicitudes.listar().stream()
                .anyMatch(s -> s.getCliente().equals(cliente) && s.getEstado() == EstadoSolicitudRecarga.PENDIENTE);
        if (pendiente) throw new DatosInvalidosException("Ya tiene una solicitud de recarga pendiente");
        SolicitudRecarga s = new SolicitudRecarga(BaseDatos.getInstancia().siguienteId("SOL"), cliente, monto);
        solicitudes.guardar(s);
        return s;
    }

    public void recargar(Cliente cliente, double monto) {
        servicioRecarga.recargar(cliente, monto, "Recarga realizada por el administrador");
    }

    public void aprobarSolicitud(SolicitudRecarga s) {
        validarPendiente(s);
        servicioRecarga.recargar(s.getCliente(), s.getMonto(), "Recarga por solicitud " + s.getId());
        s.setEstado(EstadoSolicitudRecarga.APROBADA);
    }

    public void rechazarSolicitud(SolicitudRecarga s) {
        validarAdmin();
        validarPendiente(s);
        s.setEstado(EstadoSolicitudRecarga.RECHAZADA);
    }

    public void cambiarEstadoTarjeta(Cliente cliente, EstadoTarjeta estado) {
        validarAdmin();
        cliente.getTarjeta().setEstado(estado);
    }

    public List<SolicitudRecarga> solicitudes() {
        return solicitudes.listar().stream().sorted(Comparator.comparing(SolicitudRecarga::getFecha).reversed()).toList();
    }

    public List<SolicitudRecarga> solicitudesPorAtender() {
        return solicitudes.listar().stream()
                .sorted(Comparator.comparing((SolicitudRecarga s) -> s.getEstado() != EstadoSolicitudRecarga.PENDIENTE)
                        .thenComparing(SolicitudRecarga::getFecha, Comparator.reverseOrder()))
                .toList();
    }

    public List<MovimientoTarjeta> movimientos(Cliente c) {
        if (c == null) return List.of();
        return c.getTarjeta().getMovimientos().stream()
                .sorted(Comparator.comparing(MovimientoTarjeta::getFecha).reversed()).toList();
    }

    public List<SolicitudRecarga> solicitudesDe(Cliente c) {
        return solicitudes().stream().filter(s -> s.getCliente().equals(c)).toList();
    }

    private void validarPendiente(SolicitudRecarga s) {
        if (s.getEstado() != EstadoSolicitudRecarga.PENDIENTE) throw new DatosInvalidosException("La solicitud ya fue procesada");
    }

    private void validarAdmin() {
        if (!SesionUsuario.getInstancia().esAdministrador()) throw new AccesoDenegadoException();
    }
}
