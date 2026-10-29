package co.edu.uniquindio.poo.patrones.proxy;

import co.edu.uniquindio.poo.model.Cliente;

public interface ServicioRecarga {
    void recargar(Cliente cliente, double monto, String descripcion);
}
