package co.edu.uniquindio.poo.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Estilos {
    public static final Color ROJO = Color.web("#C62828");
    public static final Color ROJO_OSCURO = Color.web("#B71C1C");
    public static final Color GRIS_BORDE = Color.web("#C8C8C8");
    public static final Color GRIS_TEXTO = Color.web("#555555");

    public static final Color ASIENTO_LIBRE = Color.web("#E0E0E0");
    public static final Color ASIENTO_PREFERENCIAL = Color.web("#FFE082");
    public static final Color ASIENTO_DISCAPACIDAD = Color.web("#90CAF9");
    public static final Color ASIENTO_SELECCIONADO = Color.web("#43A047");
    public static final Color ASIENTO_OCUPADO = Color.web("#E53935");
    public static final Color ASIENTO_INHABILITADO = Color.web("#757575");

    public static void fuente(Labeled control, double tamano, boolean negrita) {
        control.setFont(Font.font("System", negrita ? FontWeight.BOLD : FontWeight.NORMAL, tamano));
    }

    public static void fondo(Region region, Color color, double radio) {
        region.setBackground(new Background(new BackgroundFill(color, new CornerRadii(radio), Insets.EMPTY)));
    }

    public static void borde(Region region, Color color, double radio) {
        region.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, new CornerRadii(radio), BorderWidths.DEFAULT)));
    }

    public static void bordeInferior(Region region) {
        region.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, Color.TRANSPARENT, GRIS_BORDE, Color.TRANSPARENT,
                BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
                CornerRadii.EMPTY, BorderWidths.DEFAULT, Insets.EMPTY)));
    }

    public static void tarjeta(Region region) {
        fondo(region, Color.WHITE, 4);
        borde(region, GRIS_BORDE, 4);
        region.setPadding(new Insets(10));
    }

    public static void botonPrincipal(Button boton) {
        fondo(boton, ROJO, 4);
        boton.setTextFill(Color.WHITE);
        fuente(boton, 13, true);
        boton.setOnMouseEntered(e -> fondo(boton, ROJO_OSCURO, 4));
        boton.setOnMouseExited(e -> fondo(boton, ROJO, 4));
    }

    public static void asiento(Button boton, Color color) {
        boton.setMinSize(36, 28);
        fuente(boton, 11, false);
        fondo(boton, color, 4);
        borde(boton, color.darker(), 4);
        boolean oscuro = color.equals(ASIENTO_SELECCIONADO) || color.equals(ASIENTO_OCUPADO) || color.equals(ASIENTO_INHABILITADO);
        boton.setTextFill(oscuro ? Color.WHITE : Color.BLACK);
    }

    public static void cuadroLeyenda(Label cuadro, Color color) {
        cuadro.setMinSize(18, 18);
        fondo(cuadro, color, 3);
        borde(cuadro, color.darker(), 3);
    }
}
