package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.patrones.facade.CinemaFacade;
import co.edu.uniquindio.poo.util.DatosIniciales;
import javafx.application.Application;

import java.util.Locale;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        Locale.setDefault(Locale.forLanguageTag("es-CO"));
        CinemaFacade sistema = CinemaFacade.getInstancia();
        DatosIniciales.cargar(sistema);
        Application.launch(App.class, args);
    }
}
