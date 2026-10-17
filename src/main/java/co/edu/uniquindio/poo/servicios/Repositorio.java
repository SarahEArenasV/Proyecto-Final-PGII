package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.model.Identificable;

import java.util.List;
import java.util.Optional;

public interface Repositorio<T extends Identificable> {
    void guardar(T entidad);
    Optional<T> buscar(String id);
    T obtener(String id);
    List<T> listar();
    void eliminar(String id);
    boolean existe(String id);
}
