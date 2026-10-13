package co.edu.uniquindio.poo.patrones.singleton;

import co.edu.uniquindio.poo.enums.Rol;
import co.edu.uniquindio.poo.exceptions.AccesoDenegadoException;
import co.edu.uniquindio.poo.model.Usuario;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public final class SesionUsuario {
    private Usuario usuarioActual;
    private LocalDateTime inicio;

    private SesionUsuario() {
        if (Holder.INSTANCIA != null) throw new IllegalStateException("SesionUsuario ya fue creada");
    }

    private static final class Holder {
        private static final SesionUsuario INSTANCIA = new SesionUsuario();
    }

    public static SesionUsuario getInstancia() { return Holder.INSTANCIA; }

    public synchronized void iniciar(Usuario usuario) {
        this.usuarioActual = usuario;
        this.inicio = LocalDateTime.now();
    }

    public synchronized void cerrar() {
        this.usuarioActual = null;
        this.inicio = null;
    }

    public boolean haySesion() { return usuarioActual != null; }

    public boolean esAdministrador() { return haySesion() && usuarioActual.getRol() == Rol.ADMINISTRADOR; }

    public boolean esCliente() { return haySesion() && usuarioActual.getRol() == Rol.CLIENTE; }

    public void exigirAdministrador(String operacion) {
        if (!esAdministrador())
            throw new AccesoDenegadoException("Únicamente el administrador puede " + operacion);
    }

    @Override protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("SesionUsuario es un Singleton");
    }
}
