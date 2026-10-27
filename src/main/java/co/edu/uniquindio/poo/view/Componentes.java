package co.edu.uniquindio.poo.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Componentes {
    public static Label titulo(String texto) {
        Label label = new Label(texto);
        Estilos.fuente(label, 18, true);
        return label;
    }

    public static Label subtitulo(String texto) {
        Label label = new Label(texto);
        Estilos.fuente(label, 13, true);
        return label;
    }

    public static Label textoGris(String texto) {
        Label label = new Label(texto);
        label.setTextFill(Estilos.GRIS_TEXTO);
        label.setWrapText(true);
        return label;
    }

    public static Button botonPrincipal(String texto) {
        Button boton = new Button(texto);
        Estilos.botonPrincipal(boton);
        return boton;
    }

    public static Button botonPeligro(String texto) {
        Button boton = new Button(texto);
        return boton;
    }

    public static VBox tarjeta(Node... contenido) {
        VBox caja = new VBox(10, contenido);
        Estilos.tarjeta(caja);
        return caja;
    }

    public static HBox fila(Node... nodos) {
        HBox fila = new HBox(8, nodos);
        fila.setAlignment(Pos.CENTER_LEFT);
        for (Node nodo : nodos) {
            if (nodo instanceof Button || nodo instanceof Label) ((Region) nodo).setMinWidth(Region.USE_PREF_SIZE);
            if (nodo instanceof TextField) HBox.setHgrow(nodo, Priority.ALWAYS);
        }
        return fila;
    }

    public static GridPane formulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        return grid;
    }

    public static void agregarCampo(GridPane formulario, String etiqueta, Node campo) {
        int fila = formulario.getRowCount();
        formulario.add(new Label(etiqueta), 0, fila);
        formulario.add(campo, 1, fila);
        if (campo instanceof Region region) region.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(campo, Priority.ALWAYS);
    }

    public static <T> void agregarColumna(TableView<T> tabla, String titulo, Function<T, Object> valor) {
        TableColumn<T, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(dato -> new SimpleStringProperty(String.valueOf(valor.apply(dato.getValue()))));
        tabla.getColumns().add(columna);
    }

    public static <T> void configurarTabla(TableView<T> tabla) {
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay registros"));
        VBox.setVgrow(tabla, Priority.ALWAYS);
    }

    public static <T> void llenarTabla(TableView<T> tabla, List<T> datos) {
        tabla.getItems().setAll(datos);
    }

    public static Tab pestana(String titulo, Supplier<Node> contenido) {
        Tab tab = new Tab(titulo);
        tab.setClosable(false);
        tab.setOnSelectionChanged(e -> {
            if (tab.isSelected()) tab.setContent(contenido.get());
        });
        return tab;
    }

    public static VBox seccion(String titulo, Node... contenido) {
        VBox caja = new VBox(10);
        caja.getChildren().add(titulo(titulo));
        caja.getChildren().addAll(contenido);
        caja.setPadding(new Insets(12));
        return caja;
    }
}
