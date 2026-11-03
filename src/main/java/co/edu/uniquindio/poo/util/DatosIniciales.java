package co.edu.uniquindio.poo.util;

import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.enums.ClasificacionEdad;
import co.edu.uniquindio.poo.enums.GeneroPelicula;
import co.edu.uniquindio.poo.enums.TipoPromocion;
import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.poo.model.Administrador;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.LineaConfiteria;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.model.Sala;
import co.edu.uniquindio.poo.patrones.composite.Combo;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.composite.Producto;
import co.edu.uniquindio.poo.patrones.facade.CinemaFacade;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;
import co.edu.uniquindio.poo.patrones.singleton.SesionUsuario;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class DatosIniciales {
    private static final LocalTime[] HORARIOS = { LocalTime.of(14, 0), LocalTime.of(17, 30), LocalTime.of(21, 0) };

    private DatosIniciales() { }

    public static void cargar(CinemaFacade f) {
        Random rnd = new Random(2026);
        Reloj reloj = f.getReloj();
        LocalDate hoy = LocalDate.now();

        Administrador admin = new Administrador(BaseDatos.getInstancia().siguienteId("ADM"), "Administrador CinemaUQ",
                "admin@cinemauq.com", "admin123");
        f.registrarUsuario(admin);
        List<Cliente> clientes = new ArrayList<>();
        clientes.add(f.registrarCliente("Eli", "1094567890", "eli@correo.com", "3104567890", "eli123"));
        clientes.add(f.registrarCliente("David", "1094123456", "david@correo.com", "3159876543", "david123"));
        clientes.add(f.registrarCliente("Pablo", "1097654321", "pablo@correo.com", "3001234567", "pablo123"));

        Cliente eli = clientes.get(0);
        eli.getCuentaPuntos().acumular(400, hoy.minusMonths(13).atTime(10, 0), "Bono de bienvenida (vencido)");
        eli.getCuentaPuntos().acumular(350, hoy.minusMonths(11).atTime(10, 0), "Bono de cumpleaños");
        clientes.get(1).getCuentaPuntos().acumular(200, hoy.minusMonths(10).atTime(10, 0), "Bono de bienvenida");

        List<Pelicula> peliculas = List.of(
                f.crearPelicula("Horizonte Rojo", GeneroPelicula.ACCION, 128, ClasificacionEdad.MAYORES_12,
                        "Un equipo de rescate enfrenta una tormenta solar para salvar una estación orbital."),
                f.crearPelicula("La Casa del Eco", GeneroPelicula.TERROR, 104, ClasificacionEdad.MAYORES_15,
                        "Una familia descubre que su nueva casa repite las voces de sus antiguos dueños."),
                f.crearPelicula("Pequeños Gigantes", GeneroPelicula.ANIMACION, 95, ClasificacionEdad.TODO_PUBLICO,
                        "Un grupo de hormigas emprende un viaje para salvar su jardín."),
                f.crearPelicula("Café para Dos", GeneroPelicula.ROMANCE, 112, ClasificacionEdad.MAYORES_12,
                        "Dos baristas rivales en Armenia descubren que tienen más en común de lo que creen."),
                f.crearPelicula("Código Andrómeda", GeneroPelicula.CIENCIA_FICCION, 142, ClasificacionEdad.MAYORES_12,
                        "Una ingeniera recibe una señal que podría cambiar el destino de la humanidad."),
                f.crearPelicula("El Último Testigo", GeneroPelicula.SUSPENSO, 118, ClasificacionEdad.MAYORES_15,
                        "Un detective retirado vuelve a un caso que nunca pudo cerrar."));

        List<Sala> salas = List.of(
                f.crearSala("Sala 1", TipoSala.GENERAL_2D, 8, 12),
                f.crearSala("Sala 2", TipoSala.GENERAL_2D, 7, 10),
                f.crearSala("Sala 3", TipoSala.SALA_3D, 7, 10),
                f.crearSala("Sala VIP", TipoSala.VIP, 4, 8));
        double[] precios = { 12000, 11000, 15000, 20000 };

        Producto crispM = f.crearProducto("Crispetas medianas", 9000, CategoriaProducto.CRISPETAS, 90);
        Producto crispG = f.crearProducto("Crispetas grandes", 12000, CategoriaProducto.CRISPETAS, 120);
        Producto gaseosa = f.crearProducto("Gaseosa 16 oz", 6000, CategoriaProducto.BEBIDA, 60);
        Producto agua = f.crearProducto("Agua 600 ml", 4000, CategoriaProducto.BEBIDA, 40);
        Producto nachos = f.crearProducto("Nachos con queso", 11000, CategoriaProducto.COMIDA, 110);
        Producto perro = f.crearProducto("Perro caliente", 10000, CategoriaProducto.COMIDA, 100);
        Producto chocolatina = f.crearProducto("Chocolatina", 4500, CategoriaProducto.DULCERIA, 45);
        Producto gomitas = f.crearProducto("Gomitas", 5000, CategoriaProducto.DULCERIA, 50);
        Combo individual = f.crearCombo("Combo Individual", 10, List.of(crispM, gaseosa));
        Combo pareja = f.crearCombo("Combo Pareja", 15, List.of(crispG, gaseosa, gaseosa));
        Combo familiar = f.crearCombo("Combo Familiar", 10, List.of(pareja, nachos, gaseosa, gaseosa, chocolatina));
        List<ItemConfiteria> items = List.of(crispM, crispG, gaseosa, agua, nachos, perro, chocolatina, gomitas,
                individual, pareja, familiar);

        f.crearPromocion("Martes de Cine", "30 % en entradas todos los martes", 30, TipoPromocion.ENTRADAS,
                hoy.minusMonths(2), hoy.plusMonths(6), DayOfWeek.TUESDAY);
        f.crearPromocion("Miércoles Dulce", "20 % en confitería los miércoles", 20, TipoPromocion.CONFITERIA,
                hoy.minusMonths(2), hoy.plusMonths(6), DayOfWeek.WEDNESDAY);
        f.crearPromocion("Semana UQ", "10 % sobre toda la compra durante la semana universitaria", 10, TipoPromocion.TOTAL,
                hoy.minusDays(3), hoy.plusDays(4), null);

        SesionUsuario.getInstancia().iniciar(admin);
        reloj.ajustar(hoy.minusDays(31).atTime(8, 0));
        clientes.forEach(c -> f.recargarTarjeta(c, 300_000));

        for (int dia = 30; dia >= 1; dia--) {
            LocalDate fecha = hoy.minusDays(dia);
            reloj.ajustar(fecha.atTime(8, 0));
            if (dia % 7 == 0) {
                Cliente c = clientes.get(rnd.nextInt(clientes.size()));
                f.recargarTarjeta(c, 50_000 + rnd.nextInt(4) * 50_000);
            }
            List<Funcion> delDia = programarDia(f, peliculas, salas, precios, fecha, dia);
            LocalDateTime momento = fecha.atTime(9, 0);
            for (Funcion funcion : delDia) {
                int compras = 1 + rnd.nextInt(4);
                for (int i = 0; i < compras; i++) {
                    momento = momento.plusMinutes(1 + rnd.nextInt(2));
                    reloj.ajustar(momento);
                    comprar(f, clientes.get(rnd.nextInt(clientes.size())), funcion, items, rnd);
                }
            }

            if (dia % 5 == 0 && !delDia.isEmpty()) {
                reloj.ajustar(fecha.atTime(11, 30));
                Funcion funcion = delDia.get(delDia.size() - 1);
                f.listarCompras().stream().filter(c -> c.getFuncion().equals(funcion) && c.puedeCancelarse()).findFirst()
                        .ifPresent(c -> f.cancelarCompra(c, c.getCliente()));
            }

            if (dia == 12 && !delDia.isEmpty()) {
                reloj.ajustar(fecha.atTime(12, 0));
                f.cancelarFuncion(delDia.get(0));
            }
        }

        reloj.restablecer();
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime momento = ahora.minusHours(40);
        for (int dia = 0; dia <= 7; dia++) {
            List<Funcion> delDia = programarDia(f, peliculas, salas, precios, hoy.plusDays(dia), dia);
            if (dia <= 2) {
                for (Funcion funcion : delDia) {
                    for (int i = 0; i < 1 + rnd.nextInt(3); i++) {
                        momento = momento.plusMinutes(5 + rnd.nextInt(20));
                        if (momento.isAfter(ahora.minusMinutes(5))) momento = ahora.minusMinutes(5);
                        reloj.ajustar(momento);
                        comprar(f, clientes.get(rnd.nextInt(clientes.size())), funcion, items, rnd);
                    }
                }
            }
        }
        reloj.restablecer();
        SesionUsuario.getInstancia().cerrar();

        f.solicitarRecarga(clientes.get(2), 80_000);
    }

    private static List<Funcion> programarDia(CinemaFacade f, List<Pelicula> peliculas, List<Sala> salas,
                                              double[] precios, LocalDate fecha, int semilla) {
        List<Funcion> lista = new ArrayList<>();
        LocalDateTime ahora = f.getReloj().ahora();
        for (int s = 0; s < salas.size(); s++) {
            for (int h = 0; h < HORARIOS.length; h++) {
                if (!fecha.atTime(HORARIOS[h]).isAfter(ahora.plusMinutes(30))) continue;
                Pelicula p = peliculas.get((s * 3 + h + semilla) % peliculas.size());
                lista.add(f.crearFuncion(p, salas.get(s), fecha, HORARIOS[h], precios[s]));
            }
        }
        return lista;
    }

    private static void comprar(CinemaFacade f, Cliente cliente, Funcion funcion, List<ItemConfiteria> items, Random rnd) {
        try {
            List<Asiento> libres = funcion.getSala().getAsientos().stream()
                    .filter(a -> a.isHabilitado() && funcion.isAsientoDisponible(a.getCodigo())).toList();
            if (libres.size() < 4) return;
            int cantidad = 1 + rnd.nextInt(4);
            int inicio = rnd.nextInt(libres.size() - cantidad);
            List<String> asientos = libres.subList(inicio, inicio + cantidad).stream().map(Asiento::getCodigo).toList();
            boolean usarPuntos = f.puntosDisponibles(cliente) > 400 && rnd.nextInt(3) == 0;
            List<ItemConfiteria> combos = items.stream().filter(ItemConfiteria::esCompuesto).toList();
            ItemConfiteria combo = !combos.isEmpty() && rnd.nextInt(5) == 0 ? combos.get(rnd.nextInt(combos.size())) : null;
            List<LineaConfiteria> lineas = new ArrayList<>();
            for (int i = 0, n = rnd.nextInt(3); i < n; i++)
                lineas.add(new LineaConfiteria(items.get(rnd.nextInt(items.size())), 1 + rnd.nextInt(2), false));
            Set<String> conPuntos = usarPuntos ? Set.of(asientos.get(0)) : Set.of();
            try {
                registrarCompra(f, cliente, funcion, asientos, conPuntos, lineas, combo);
            } catch (SaldoInsuficienteException e) {
                f.recargarTarjeta(cliente, 200_000);
                registrarCompra(f, cliente, funcion, asientos, conPuntos, lineas, combo);
            }
        } catch (CinemaException e) {
        }
    }

    private static void registrarCompra(CinemaFacade f, Cliente cliente, Funcion funcion, List<String> asientos,
                                        Set<String> conPuntos, List<LineaConfiteria> lineas, ItemConfiteria combo) {
        if (combo != null) f.comprarPaqueteConCombo(cliente, funcion, asientos, combo);
        else f.comprar(cliente, funcion, asientos, conPuntos, lineas);
    }
}
