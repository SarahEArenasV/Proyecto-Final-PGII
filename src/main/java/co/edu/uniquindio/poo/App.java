package co.edu.uniquindio.poo;

import co.edu.uniquindio.poo.controller.AdminController;
import co.edu.uniquindio.poo.controller.ClienteController;
import co.edu.uniquindio.poo.controller.LoginController;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.patrones.facade.CinemaFacade;
import co.edu.uniquindio.poo.view.AdminView;
import co.edu.uniquindio.poo.view.ClienteView;
import co.edu.uniquindio.poo.view.LoginView;
import co.edu.uniquindio.poo.view.Navegador;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application implements Navegador {
    private Stage stage;
    private CinemaFacade fachada;
    private LoginController loginController;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        this.fachada = CinemaFacade.getInstancia();
        this.loginController = new LoginController(fachada);
        stage.setTitle("CinemaUQ - Sistema Integral de Gestión de Cine");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        mostrarLogin();
        stage.show();
    }

    private void cambiar(Parent raiz, double ancho, double alto) {
        Scene escena = new Scene(raiz, ancho, alto);
        stage.setScene(escena);
        stage.centerOnScreen();
    }

    @Override public void mostrarLogin() { cambiar(new LoginView(loginController, this).getVista(), 1100, 700); }

    @Override public void mostrarCliente(Cliente cliente) {
        cambiar(new ClienteView(new ClienteController(fachada, cliente), loginController, this).getVista(), 1320, 800);
    }

    @Override public void mostrarAdministrador() {
        cambiar(new AdminView(new AdminController(fachada), loginController, this).getVista(), 1380, 820);
    }

    public static void main(String[] args) { Main.main(args); }
}
