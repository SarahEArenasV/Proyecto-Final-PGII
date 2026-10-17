package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.Rol;
import lombok.Getter;
import lombok.Setter;

@Setter
public class Cliente extends Usuario {
    @Getter @Setter private String documento;
    @Getter @Setter private String telefono;
    @Getter private final TarjetaVirtual tarjeta;
    @Getter private final CuentaPuntos cuentaPuntos;

    public Cliente(String id, String nombre, String documento, String correo, String telefono,
                   String contrasena, TarjetaVirtual tarjeta) {
        super(id, nombre, correo, contrasena);
        this.documento = documento;
        this.telefono = telefono;
        this.tarjeta = tarjeta;
        this.cuentaPuntos = new CuentaPuntos();
    }

    @Override public Rol getRol() { return Rol.CLIENTE; }
}
