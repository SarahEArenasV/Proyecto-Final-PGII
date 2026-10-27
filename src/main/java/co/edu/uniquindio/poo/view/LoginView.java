package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.controller.LoginController;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class LoginView {
    private final LoginController controller;
    private final Navegador navegador;
    private final VBox raiz;
    private final StackPane zonaFormulario;

    public LoginView(LoginController controller, Navegador navegador) {
        this.controller = controller;
        this.navegador = navegador;

        Label titulo = new Label("CinemaUQ");
        Estilos.fuente(titulo, 30, true);
        titulo.setTextFill(Estilos.ROJO_OSCURO);
        Label lema = new Label("Sistema de gestión de cine");

        zonaFormulario = new StackPane();

        raiz = new VBox(15, titulo, lema, zonaFormulario);
        raiz.setAlignment(Pos.CENTER);
        raiz.setPadding(new Insets(30));
        mostrarFormularioLogin();
    }

    public Parent getVista() {
        return raiz;
    }

    private void mostrarFormularioLogin() {
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("correo@ejemplo.com");
        PasswordField txtClave = new PasswordField();
        txtClave.setPromptText("Contraseña");

        GridPane formulario = Componentes.formulario();
        Componentes.agregarCampo(formulario, "Correo", txtCorreo);
        Componentes.agregarCampo(formulario, "Contraseña", txtClave);

        Button btnIngresar = Componentes.botonPrincipal("Ingresar");
        btnIngresar.setMaxWidth(Double.MAX_VALUE);
        btnIngresar.setOnAction(e -> ingresar(txtCorreo.getText(), txtClave.getText()));
        txtClave.setOnAction(e -> ingresar(txtCorreo.getText(), txtClave.getText()));

        Hyperlink linkRegistro = new Hyperlink("¿No tienes cuenta? Regístrate aquí");
        linkRegistro.setOnAction(e -> mostrarFormularioRegistro());

        VBox caja = Componentes.tarjeta(Componentes.titulo("Iniciar sesión"),
                Componentes.textoGris("Ingresa con tu correo y contraseña."),
                formulario, btnIngresar, linkRegistro);
        caja.setMaxWidth(380);
        zonaFormulario.getChildren().setAll(caja);
    }

    private void ingresar(String correo, String clave) {
        try {
            Usuario usuario = controller.iniciarSesion(correo, clave);
            if (usuario instanceof Cliente cliente) {
                navegador.mostrarCliente(cliente);
            } else {
                navegador.mostrarAdministrador();
            }
        } catch (CinemaException ex) {
            Mensajes.error(ex.getMessage());
        }
    }

    private void mostrarFormularioRegistro() {
        TextField txtNombre = new TextField();
        TextField txtDocumento = new TextField();
        TextField txtCorreo = new TextField();
        TextField txtTelefono = new TextField();
        PasswordField txtClave = new PasswordField();
        PasswordField txtConfirmar = new PasswordField();

        GridPane formulario = Componentes.formulario();
        Componentes.agregarCampo(formulario, "Nombre completo", txtNombre);
        Componentes.agregarCampo(formulario, "Documento", txtDocumento);
        Componentes.agregarCampo(formulario, "Correo", txtCorreo);
        Componentes.agregarCampo(formulario, "Teléfono", txtTelefono);
        Componentes.agregarCampo(formulario, "Contraseña", txtClave);
        Componentes.agregarCampo(formulario, "Confirmar contraseña", txtConfirmar);

        Button btnRegistrar = Componentes.botonPrincipal("Crear cuenta");
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);
        btnRegistrar.setOnAction(e -> {
            try {
                controller.registrar(txtNombre.getText(), txtDocumento.getText(), txtCorreo.getText(),
                        txtTelefono.getText(), txtClave.getText(), txtConfirmar.getText());
                Mensajes.informacion("Cuenta creada. Ya puedes iniciar sesión.");
                mostrarFormularioLogin();
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Hyperlink linkVolver = new Hyperlink("Ya tengo cuenta");
        linkVolver.setOnAction(e -> mostrarFormularioLogin());

        VBox caja = Componentes.tarjeta(Componentes.titulo("Crear cuenta"),
                Componentes.textoGris("Al registrarte se crea tu tarjeta virtual CinemaUQ."),
                formulario, btnRegistrar, linkVolver);
        caja.setMaxWidth(420);
        zonaFormulario.getChildren().setAll(caja);
    }
}
