package co.edu.uniquindio.poo.patrones.singleton;

import co.edu.uniquindio.poo.model.Identificable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BaseDatos {
    private static volatile BaseDatos instancia;

    private final Map<Class<?>, Map<String, Object>> tablas = new HashMap<>();
    private final Map<String, Integer> consecutivos = new HashMap<>();

    private BaseDatos() {
        if (instancia != null) throw new IllegalStateException("BaseDatos ya fue creada; use getInstancia()");
    }

    public static BaseDatos getInstancia() {
        if (instancia == null) {
            synchronized (BaseDatos.class) {
                if (instancia == null) instancia = new BaseDatos();
            }
        }
        return instancia;
    }

    @SuppressWarnings("unchecked")
    public <T extends Identificable> Map<String, T> tabla(Class<T> tipo) {
        return (Map<String, T>) (Map<String, ?>) tablas.computeIfAbsent(tipo, k -> new LinkedHashMap<>());
    }

    public synchronized String siguienteId(String prefijo) {
        int n = consecutivos.merge(prefijo, 1, Integer::sum);
        return String.format("%s-%04d", prefijo, n);
    }

    public synchronized int contar(Class<? extends Identificable> tipo) {
        Map<String, Object> t = tablas.get(tipo);
        return t == null ? 0 : t.size();
    }

    public synchronized void limpiar() {
        tablas.clear();
        consecutivos.clear();
    }

    @Override protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("BaseDatos es un Singleton");
    }
}
