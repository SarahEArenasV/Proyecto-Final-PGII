package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.patrones.composite.Combo;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.composite.Producto;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;

import java.util.List;

public class ConfiteriaService {
    private final Repositorio<ItemConfiteria> items;

    public ConfiteriaService(Repositorio<ItemConfiteria> items) { this.items = items; }

    public Producto crearProducto(String nombre, double precio, CategoriaProducto categoria, int puntos) {
        validarProducto(nombre, precio, categoria, puntos);
        Producto p = new Producto(BaseDatos.getInstancia().siguienteId("PRO"), nombre.trim(), precio, categoria, puntos);
        items.guardar(p);
        return p;
    }

    public void actualizarProducto(Producto p, String nombre, double precio, CategoriaProducto categoria, int puntos, boolean activo) {
        validarProducto(nombre, precio, categoria, puntos);
        p.setNombre(nombre.trim());
        p.setPrecio(precio);
        p.setCategoria(categoria);
        p.setPuntosRedencion(puntos);
        p.setActivo(activo);
    }

    public Combo crearCombo(String nombre, double descuento, List<ItemConfiteria> componentes) {
        Validador.requerido(nombre, "nombre");
        if (descuento < 0 || descuento >= 100) throw new DatosInvalidosException("Descuento del combo entre 0 y 99 %");
        if (componentes == null || componentes.size() < 2) throw new DatosInvalidosException("Un combo necesita mínimo 2 componentes");
        Combo c = new Combo(BaseDatos.getInstancia().siguienteId("CMB"), nombre.trim(), descuento);
        componentes.forEach(c::agregar);
        items.guardar(c);
        return c;
    }

    public void actualizarCombo(Combo c, String nombre, double descuento, List<ItemConfiteria> componentes, boolean activo) {
        Validador.requerido(nombre, "nombre");
        if (componentes == null || componentes.size() < 2) throw new DatosInvalidosException("Un combo necesita mínimo 2 componentes");
        if (componentes.stream().anyMatch(i -> i.contiene(c)))
            throw new DatosInvalidosException("Un combo no puede contenerse a sí mismo");
        c.setNombre(nombre.trim());
        c.setPorcentajeDescuento(descuento);
        c.limpiar();
        componentes.forEach(c::agregar);
        c.setActivo(activo);
    }

    public void eliminar(ItemConfiteria item) {
        boolean enCombo = items.listar().stream().anyMatch(i -> !i.equals(item) && i.contiene(item));
        if (enCombo) throw new DatosInvalidosException(item.getNombre() + " hace parte de un combo; desactívelo en su lugar");
        items.eliminar(item.getId());
    }

    public List<ItemConfiteria> listar() { return items.listar(); }
    public List<ItemConfiteria> disponibles() { return items.listar().stream().filter(ItemConfiteria::isActivo).toList(); }
    public List<Producto> productos() {
        return items.listar().stream().filter(Producto.class::isInstance).map(Producto.class::cast).toList();
    }

    private void validarProducto(String nombre, double precio, CategoriaProducto categoria, int puntos) {
        Validador.requerido(nombre, "nombre");
        Validador.positivo(precio, "precio");
        Validador.positivo(puntos, "puntos de redención");
        if (categoria == null || categoria == CategoriaProducto.COMBO) throw new DatosInvalidosException("Seleccione una categoría válida");
    }

    public void guardarProducto(Producto p, String nombre, double precio, CategoriaProducto categoria, int puntos, boolean activo) {
        if (p == null) crearProducto(nombre, precio, categoria, puntos);
        else actualizarProducto(p, nombre, precio, categoria, puntos, activo);
    }

    public void guardarCombo(Combo c, String nombre, double descuento, List<ItemConfiteria> componentes, boolean activo) {
        if (c == null) crearCombo(nombre, descuento, componentes);
        else actualizarCombo(c, nombre, descuento, componentes, activo);
    }
}
