package co.edu.uniquindio.poo.model;

import co.edu.uniquindio.poo.enums.ClasificacionEdad;
import co.edu.uniquindio.poo.enums.GeneroPelicula;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pelicula implements Identificable {
    private final String id;
    private String titulo;
    private GeneroPelicula genero;
    private int duracionMinutos;
    private ClasificacionEdad clasificacion;
    private String sinopsis;
    private boolean enCartelera;

    public Pelicula(String id, String titulo, GeneroPelicula genero, int duracionMinutos,
                    ClasificacionEdad clasificacion, String sinopsis) {
        this.id = id;
        this.titulo = titulo;
        this.genero = genero;
        this.duracionMinutos = duracionMinutos;
        this.clasificacion = clasificacion;
        this.sinopsis = sinopsis;
        this.enCartelera = true;
    }

    @Override
    public String toString() {
        return titulo;
    }
}
