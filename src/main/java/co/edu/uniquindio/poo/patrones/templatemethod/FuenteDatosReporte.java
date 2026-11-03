package co.edu.uniquindio.poo.patrones.templatemethod;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;

import java.util.List;

public interface FuenteDatosReporte {
    List<Compra> compras();
    List<Cliente> clientes();
    List<Funcion> funciones();
}
