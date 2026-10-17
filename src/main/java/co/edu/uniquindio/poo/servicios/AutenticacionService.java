package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.exceptions.AutenticacionException;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.exceptions.EntidadDuplicadaException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.TarjetaVirtual;
import co.edu.uniquindio.poo.model.Usuario;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;
import co.edu.uniquindio.poo.patrones.singleton.SesionUsuario;

import java.util.List;
import java.util.Random;

public class AutenticacionService {
    private final Repositorio<Usuario> usuarios;
    private final Random random = new Random();

    public AutenticacionService(Repositorio<Usuario> usuarios) { this.usuarios = usuarios; }

    public Usuario iniciarSesion(String correo, String clave) {
        Usuario u = usuarios.listar().stream().filter(x -> x.getCorreo().equalsIgnoreCase(correo == null ? "" : correo.trim()))
                .findFirst().orElseThrow(() -> new AutenticacionException("Usuario o contraseña incorrectos"));
        if (!u.validarContrasena(clave)) throw new AutenticacionException("Usuario o contraseña incorrectos");
        SesionUsuario.getInstancia().iniciar(u);
        return u;
    }

    public void cerrarSesion() { SesionUsuario.getInstancia().cerrar(); }

    public Cliente registrarCliente(String nombre, String documento, String correo, String telefono, String clave) {
        nombre = Validador.requerido(nombre, "nombre");
        documento = Validador.requerido(documento, "documento");
        correo = Validador.requerido(correo, "correo").toLowerCase();
        telefono = Validador.requerido(telefono, "teléfono");
        Validador.requerido(clave, "contraseña");
        if (!correo.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) throw new DatosInvalidosException("Correo no válido");
        if (!documento.matches("\\d{5,12}")) throw new DatosInvalidosException("El documento debe tener entre 5 y 12 dígitos");
        if (clave.length() < 4) throw new DatosInvalidosException("La contraseña debe tener mínimo 4 caracteres");
        final String c = correo, d = documento;
        if (usuarios.listar().stream().anyMatch(u -> u.getCorreo().equalsIgnoreCase(c)))
            throw new EntidadDuplicadaException("Ya existe un usuario con ese correo");
        if (clientes().stream().anyMatch(x -> x.getDocumento().equals(d)))
            throw new EntidadDuplicadaException("Ya existe un cliente con ese documento");
        TarjetaVirtual tarjeta = new TarjetaVirtual(generarNumeroTarjeta());
        Cliente cliente = new Cliente(BaseDatos.getInstancia().siguienteId("CLI"), nombre, documento, correo, telefono, clave, tarjeta);
        usuarios.guardar(cliente);
        return cliente;
    }

    public void registrarUsuario(Usuario u) { usuarios.guardar(u); }

    public List<Cliente> clientes() {
        return usuarios.listar().stream().filter(Cliente.class::isInstance).map(Cliente.class::cast).toList();
    }

    private String generarNumeroTarjeta() {
        return String.format("5311 %04d %04d %04d", random.nextInt(10000), random.nextInt(10000), random.nextInt(10000));
    }

    public Cliente registrarCliente(String nombre, String documento, String correo, String telefono,
                                    String clave, String confirmacion) {
        if (clave == null || !clave.equals(confirmacion)) throw new DatosInvalidosException("Las contraseñas no coinciden");
        return registrarCliente(nombre, documento, correo, telefono, clave);
    }
}
