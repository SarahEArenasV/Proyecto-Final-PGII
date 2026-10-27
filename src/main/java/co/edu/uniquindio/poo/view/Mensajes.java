package co.edu.uniquindio.poo.view;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class Mensajes {
    public static void informacion(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static void error(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("CinemaUQ");
        alerta.setHeaderText("No se pudo realizar la operación");
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    public static boolean confirmar(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.YES, ButtonType.NO);
        alerta.setTitle("Confirmar");
        alerta.setHeaderText(null);
        return alerta.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
    }
}
