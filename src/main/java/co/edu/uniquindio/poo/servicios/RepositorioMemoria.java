package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.exceptions.EntidadNoEncontradaException;
import co.edu.uniquindio.poo.model.Identificable;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositorioMemoria<T extends Identificable> implements Repositorio<T> {
    private final Map<String, T> tabla;
    private final String nombreEntidad;

    public RepositorioMemoria(Class<T> tipo) {
        this.tabla = BaseDatos.getInstancia().tabla(tipo);
        this.nombreEntidad = tipo.getSimpleName();
    }

    @Override public void guardar(T entidad) { tabla.put(entidad.getId(), entidad); }
    @Override public Optional<T> buscar(String id) { return Optional.ofNullable(tabla.get(id)); }
    @Override public T obtener(String id) {
        return buscar(id).orElseThrow(() -> new EntidadNoEncontradaException(nombreEntidad + " " + id + " no encontrado"));
    }
    @Override public List<T> listar() { return new ArrayList<>(tabla.values()); }
    @Override public void eliminar(String id) { tabla.remove(id); }
    @Override public boolean existe(String id) { return tabla.containsKey(id); }
}
