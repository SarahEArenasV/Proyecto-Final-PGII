package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.ClasificacionEdad;
import co.edu.uniquindio.poo.enums.GeneroPelicula;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.patrones.singleton.BaseDatos;

import java.util.List;

public class PeliculaService {
    private final Repositorio<Pelicula> peliculas;
    private final Repositorio<Funcion> funciones;

    public PeliculaService(Repositorio<Pelicula> peliculas, Repositorio<Funcion> funciones) {
        this.peliculas = peliculas;
        this.funciones = funciones;
    }

    public Pelicula crear(String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion, String sinopsis) {
        validar(titulo, genero, duracion, clasificacion);
        Pelicula p = new Pelicula(BaseDatos.getInstancia().siguienteId("PEL"), titulo.trim(), genero, duracion, clasificacion,
                sinopsis == null ? "" : sinopsis.trim());
        peliculas.guardar(p);
        return p;
    }

    public void actualizar(Pelicula p, String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion,
                           String sinopsis, boolean enCartelera) {
        validar(titulo, genero, duracion, clasificacion);
        p.setTitulo(titulo.trim());
        p.setGenero(genero);
        p.setDuracionMinutos(duracion);
        p.setClasificacion(clasificacion);
        p.setSinopsis(sinopsis);
        p.setEnCartelera(enCartelera);
    }

    public void eliminar(Pelicula p) {
        if (funciones.listar().stream().anyMatch(f -> f.getPelicula().equals(p))) {
            throw new DatosInvalidosException("La película tiene funciones registradas. Retírela de cartelera en su lugar.");
        }
        peliculas.eliminar(p.getId());
    }

    public List<Pelicula> listar() { return peliculas.listar(); }
    public List<Pelicula> enCartelera() { return peliculas.listar().stream().filter(Pelicula::isEnCartelera).toList(); }

    private void validar(String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion) {
        Validador.requerido(titulo, "título");
        if (genero == null || clasificacion == null) throw new DatosInvalidosException("Seleccione género y clasificación");
        if (duracion < 30 || duracion > 300) throw new DatosInvalidosException("La duración debe estar entre 30 y 300 minutos");
    }

    public void guardar(Pelicula p, String titulo, GeneroPelicula genero, int duracion, ClasificacionEdad clasificacion,
                        String sinopsis, boolean enCartelera) {
        if (p == null) crear(titulo, genero, duracion, clasificacion, sinopsis);
        else actualizar(p, titulo, genero, duracion, clasificacion, sinopsis, enCartelera);
    }
}
