package co.edu.uniquindio.poo.patrones.builder;

import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;

import java.time.LocalDateTime;
import java.util.List;

public interface ConstructorCompra {
    ConstructorCompra reiniciar();
    ConstructorCompra cliente(Cliente cliente);
    ConstructorCompra funcion(Funcion funcion);
    ConstructorCompra fecha(LocalDateTime fecha);
    ConstructorCompra conPromociones(List<Promocion> promociones);
    ConstructorCompra agregarEntrada(String codigoAsiento, boolean conPuntos);
    ConstructorCompra agregarConfiteria(ItemConfiteria item, int cantidad, boolean conPuntos);
}
