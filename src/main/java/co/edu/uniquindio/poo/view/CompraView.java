package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.controller.ClienteController.SeleccionConfiteria;
import co.edu.uniquindio.poo.controller.ClienteController;
import co.edu.uniquindio.poo.enums.TipoAsiento;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.patrones.builder.ResumenCompra;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.util.Formato;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompraView {
    private final ClienteController controller;
    private final Funcion funcion;
    private final Stage ventana = new Stage();

    private final Map<String, CheckBox> asientosElegidos = new LinkedHashMap<>();
    private final List<SeleccionConfiteria> confiteriaElegida = new ArrayList<>();

    private final GridPane mapaAsientos = new GridPane();
    private final VBox listaAsientosElegidos = new VBox(6);
    private final ListView<String> listaConfiteria = new ListView<>();
    private final Label lblResumen = new Label();

    public CompraView(ClienteController controller, Funcion funcion, Window ventanaPrincipal) {
        this.controller = controller;
        this.funcion = funcion;

        BorderPane raiz = new BorderPane();
        raiz.setPadding(new Insets(20));
        raiz.setTop(crearEncabezado());
        raiz.setCenter(crearZonaAsientos());
        raiz.setRight(crearPanelDerecho());
        BorderPane.setMargin(raiz.getRight(), new Insets(0, 0, 0, 16));

        Scene escena = new Scene(raiz, 1150, 760);
        ventana.setScene(escena);
        ventana.setTitle("Comprar entradas - " + funcion.getPelicula().getTitulo());
        ventana.initOwner(ventanaPrincipal);
        ventana.initModality(Modality.WINDOW_MODAL);

        dibujarMapa();
        actualizarResumen();
    }

    public void mostrar() {
        ventana.showAndWait();
    }

    private VBox crearEncabezado() {
        Label titulo = Componentes.titulo(funcion.getPelicula().getTitulo());
        Label datos = Componentes.textoGris(Formato.fechaHora(funcion.getFechaHora()) + "  ·  "
                + funcion.getSala().getNombre() + " (" + funcion.getSala().getTipo() + ")  ·  Precio base "
                + Formato.moneda(funcion.getPrecioBase()));
        VBox encabezado = new VBox(4, titulo, datos);
        encabezado.setPadding(new Insets(0, 0, 14, 0));
        return encabezado;
    }

    private VBox crearZonaAsientos() {
        Label pantalla = new Label("PANTALLA");
        StackPane barraPantalla = new StackPane(pantalla);
        Estilos.fondo(barraPantalla, Estilos.GRIS_BORDE, 0);
        barraPantalla.setPadding(new Insets(3));
        barraPantalla.setMaxWidth(Double.MAX_VALUE);

        mapaAsientos.setHgap(6);
        mapaAsientos.setVgap(6);
        mapaAsientos.setAlignment(Pos.CENTER);

        VBox sala = new VBox(20, barraPantalla, mapaAsientos);
        sala.setAlignment(Pos.TOP_CENTER);
        sala.setPadding(new Insets(10));
        ScrollPane scroll = new ScrollPane(sala);
        scroll.setFitToWidth(true);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        FlowPane leyenda = new FlowPane(14, 8,
                cuadroLeyenda("Libre", Estilos.ASIENTO_LIBRE),
                cuadroLeyenda("Preferencial (+$2.000)", Estilos.ASIENTO_PREFERENCIAL),
                cuadroLeyenda("Movilidad reducida", Estilos.ASIENTO_DISCAPACIDAD),
                cuadroLeyenda("Seleccionado", Estilos.ASIENTO_SELECCIONADO),
                cuadroLeyenda("Ocupado", Estilos.ASIENTO_OCUPADO));

        VBox tarjeta = Componentes.tarjeta(Componentes.subtitulo("Elija sus asientos (máximo "
                + controller.maxEntradas() + ")"), scroll, leyenda);
        VBox.setVgrow(tarjeta, Priority.ALWAYS);
        return tarjeta;
    }

    private HBox cuadroLeyenda(String texto, Color color) {
        Label cuadro = new Label();
        Estilos.cuadroLeyenda(cuadro, color);
        HBox fila = new HBox(6, cuadro, new Label(texto));
        fila.setAlignment(Pos.CENTER_LEFT);
        return fila;
    }

    private void dibujarMapa() {
        mapaAsientos.getChildren().clear();
        for (Asiento asiento : funcion.getSala().getAsientos()) {
            String codigo = asiento.getCodigo();
            Button boton = new Button(codigo);

            if (!asiento.isHabilitado()) {
                Estilos.asiento(boton, Estilos.ASIENTO_INHABILITADO);
                boton.setDisable(true);
            } else if (!funcion.isAsientoDisponible(codigo)) {
                Estilos.asiento(boton, Estilos.ASIENTO_OCUPADO);
                boton.setDisable(true);
            } else if (asientosElegidos.containsKey(codigo)) {
                Estilos.asiento(boton, Estilos.ASIENTO_SELECCIONADO);
            } else if (asiento.getTipo() == TipoAsiento.PREFERENCIAL) {
                Estilos.asiento(boton, Estilos.ASIENTO_PREFERENCIAL);
            } else if (asiento.getTipo() == TipoAsiento.DISCAPACIDAD) {
                Estilos.asiento(boton, Estilos.ASIENTO_DISCAPACIDAD);
            } else {
                Estilos.asiento(boton, Estilos.ASIENTO_LIBRE);
            }
            boton.setOnAction(e -> elegirAsiento(codigo));

            int fila = asiento.getFila() - 'A';
            mapaAsientos.add(boton, asiento.getNumero(), fila);
        }
    }

    private void elegirAsiento(String codigo) {
        if (asientosElegidos.containsKey(codigo)) {
            asientosElegidos.remove(codigo);
        } else {
            CheckBox conPuntos = new CheckBox("Pagar con puntos");
            conPuntos.setOnAction(e -> actualizarResumen());
            asientosElegidos.put(codigo, conPuntos);
            try {
                controller.previsualizar(funcion, new ArrayList<>(asientosElegidos.keySet()), asientosConPuntos(), confiteriaElegida);
            } catch (CinemaException ex) {
                asientosElegidos.remove(codigo);
                Mensajes.error(ex.getMessage());
                return;
            }
        }

        listaAsientosElegidos.getChildren().clear();
        for (String asiento : asientosElegidos.keySet()) {
            Label lbl = new Label("Asiento " + asiento);
            lbl.setMinWidth(90);
            listaAsientosElegidos.getChildren().add(Componentes.fila(lbl, asientosElegidos.get(asiento)));
        }
        dibujarMapa();
        actualizarResumen();
    }

    private ScrollPane crearPanelDerecho() {
        VBox tarjetaAsientos = Componentes.tarjeta(Componentes.subtitulo("Asientos elegidos"),
                listaAsientosElegidos,
                Componentes.textoGris("Puntos disponibles: " + controller.puntosDisponibles()));

        ComboBox<ItemConfiteria> cmbProductos = new ComboBox<>();
        cmbProductos.getItems().setAll(controller.confiteria());
        cmbProductos.setPromptText("Producto o combo");
        cmbProductos.setMaxWidth(Double.MAX_VALUE);
        Spinner<Integer> spCantidad = new Spinner<>(1, 10, 1);
        spCantidad.setPrefWidth(75);
        CheckBox chkPuntos = new CheckBox("Con puntos");

        Button btnAgregar = new Button("Agregar");
        btnAgregar.setOnAction(e -> {
            ItemConfiteria item = cmbProductos.getValue();
            if (item == null) {
                Mensajes.error("Seleccione un producto o combo.");
                return;
            }
            confiteriaElegida.add(new SeleccionConfiteria(item, spCantidad.getValue(), chkPuntos.isSelected()));
            actualizarListaConfiteria();
        });

        Button btnQuitar = new Button("Quitar");
        btnQuitar.setOnAction(e -> {
            int posicion = listaConfiteria.getSelectionModel().getSelectedIndex();
            if (posicion >= 0) {
                confiteriaElegida.remove(posicion);
                actualizarListaConfiteria();
            }
        });
        listaConfiteria.setPrefHeight(80);

        VBox tarjetaConfiteria = Componentes.tarjeta(Componentes.subtitulo("Confitería"), cmbProductos,
                Componentes.fila(spCantidad, chkPuntos, btnAgregar), listaConfiteria, btnQuitar);

        lblResumen.setWrapText(true);
        Button btnPagar = Componentes.botonPrincipal("Pagar con mi tarjeta");
        btnPagar.setMaxWidth(Double.MAX_VALUE);
        btnPagar.setOnAction(e -> pagar());
        Button btnCerrar = new Button("Cancelar");
        btnCerrar.setMaxWidth(Double.MAX_VALUE);
        btnCerrar.setOnAction(e -> ventana.close());

        VBox tarjetaResumen = Componentes.tarjeta(Componentes.subtitulo("Resumen"), lblResumen, btnPagar, btnCerrar);

        VBox columna = new VBox(14, tarjetaAsientos, tarjetaConfiteria, tarjetaResumen);
        columna.setPrefWidth(360);
        ScrollPane scroll = new ScrollPane(columna);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(385);
        return scroll;
    }

    private void actualizarListaConfiteria() {
        listaConfiteria.getItems().clear();
        for (SeleccionConfiteria s : confiteriaElegida) {
            String texto = s.cantidad() + " x " + s.item().getNombre();
            if (s.conPuntos()) texto += "  (con puntos)";
            else texto += "  " + Formato.moneda(s.item().getPrecio() * s.cantidad());
            listaConfiteria.getItems().add(texto);
        }
        actualizarResumen();
    }

    private Set<String> asientosConPuntos() {
        Set<String> resultado = new HashSet<>();
        for (String codigo : asientosElegidos.keySet()) {
            if (asientosElegidos.get(codigo).isSelected()) resultado.add(codigo);
        }
        return resultado;
    }

    private void actualizarResumen() {
        if (asientosElegidos.isEmpty()) {
            lblResumen.setText("Elija al menos un asiento en el mapa.");
            return;
        }
        try {
            ResumenCompra r = controller.previsualizar(funcion, new ArrayList<>(asientosElegidos.keySet()),
                    asientosConPuntos(), confiteriaElegida);
            String texto = "Entradas: " + Formato.moneda(r.subtotalEntradas())
                    + "\nConfitería: " + Formato.moneda(r.subtotalConfiteria())
                    + "\nDescuentos: -" + Formato.moneda(r.descuento());
            if (!r.promocionesAplicadas().isEmpty()) {
                texto += "\nPromociones: " + String.join(", ", r.promocionesAplicadas());
            }
            texto += "\nPuntos a usar: " + r.puntosRequeridos()
                    + "\n\nTOTAL A PAGAR: " + Formato.moneda(r.total())
                    + "\nSaldo de su tarjeta: " + Formato.moneda(controller.getCliente().getTarjeta().getSaldo());
            lblResumen.setText(texto);
        } catch (CinemaException ex) {
            lblResumen.setText("Atención: " + ex.getMessage());
        }
    }

    private void pagar() {
        if (asientosElegidos.isEmpty()) {
            Mensajes.error("Elija al menos un asiento.");
            return;
        }
        try {
            Compra compra = controller.comprar(funcion, new ArrayList<>(asientosElegidos.keySet()),
                    asientosConPuntos(), confiteriaElegida);
            Mensajes.informacion("¡Compra realizada!\n\nCódigo: " + compra.getId()
                    + "\nTotal pagado: " + Formato.moneda(compra.getTotal())
                    + "\nPuntos ganados: " + compra.getPuntosGanados()
                    + "\nNuevo saldo: " + Formato.moneda(controller.getCliente().getTarjeta().getSaldo()));
            ventana.close();
        } catch (CinemaException ex) {
            Mensajes.error(ex.getMessage());
            dibujarMapa();
            actualizarResumen();
        }
    }
}
