package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.controller.AdminController;
import co.edu.uniquindio.poo.enums.FormatoReporte;
import co.edu.uniquindio.poo.enums.TipoReporte;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.util.Formato;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReportesView {
    private final AdminController controller;
    private final VBox vista;
    private final TableView<List<String>> tabla = new TableView<>();
    private final ListView<String> listaTotales = new ListView<>();
    private final Label lblEncabezado = Componentes.textoGris("Elija el tipo de reporte y presione Generar.");
    private Reporte reporteActual;

    public ReportesView(AdminController controller) {
        this.controller = controller;

        ComboBox<TipoReporte> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().setAll(TipoReporte.values());
        cmbTipo.getSelectionModel().selectFirst();
        DatePicker dpDesde = new DatePicker(controller.hoy().minusDays(30));
        DatePicker dpHasta = new DatePicker(controller.hoy().plusDays(7));

        Button btnGenerar = Componentes.botonPrincipal("Generar");
        btnGenerar.setOnAction(e -> {
            try {
                mostrar(controller.generarReporte(cmbTipo.getValue(), dpDesde.getValue(), dpHasta.getValue()));
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });
        Button btnTxt = new Button("Exportar TXT");
        btnTxt.setOnAction(e -> exportar(FormatoReporte.TXT));
        Button btnPdf = new Button("Exportar PDF");
        btnPdf.setOnAction(e -> exportar(FormatoReporte.PDF));

        tabla.setPlaceholder(new Label("Sin datos"));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        listaTotales.setPrefHeight(120);

        VBox filtros = Componentes.tarjeta(
                Componentes.fila(new Label("Tipo de reporte"), cmbTipo, new Label("Desde"), dpDesde, new Label("Hasta"), dpHasta),
                Componentes.fila(btnGenerar, btnTxt, btnPdf));
        VBox resultado = Componentes.tarjeta(lblEncabezado, tabla, Componentes.subtitulo("Totales"), listaTotales);
        VBox.setVgrow(resultado, Priority.ALWAYS);
        vista = Componentes.seccion("Reportes", filtros, resultado);
    }

    public Parent getVista() {
        return vista;
    }

    private void mostrar(Reporte reporte) {
        reporteActual = reporte;
        lblEncabezado.setText(reporte.titulo() + "   ·   Generado: " + Formato.fechaHora(reporte.fechaGeneracion())
                + "   ·   Periodo: " + Formato.fecha(reporte.desde()) + " a " + Formato.fecha(reporte.hasta())
                + "   ·   Registros: " + reporte.getCantidadRegistros());

        tabla.getColumns().clear();
        for (int i = 0; i < reporte.encabezados().size(); i++) {
            int posicion = i;
            TableColumn<List<String>, String> columna = new TableColumn<>(reporte.encabezados().get(i));
            columna.setCellValueFactory(dato -> new SimpleStringProperty(dato.getValue().get(posicion)));
            columna.setPrefWidth(120);
            tabla.getColumns().add(columna);
        }
        tabla.getItems().setAll(reporte.filas());
        listaTotales.getItems().setAll(reporte.totales());
    }

    private void exportar(FormatoReporte formato) {
        if (reporteActual == null) {
            Mensajes.error("Primero genere un reporte.");
            return;
        }
        String extension = formato.name().toLowerCase();
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar reporte");
        selector.getExtensionFilters().add(new FileChooser.ExtensionFilter(formato.name(), "*." + extension));
        selector.setInitialFileName(reporteActual.titulo().replaceAll("[^A-Za-z0-9]+", "_") + "." + extension);
        File archivo = selector.showSaveDialog(vista.getScene().getWindow());
        if (archivo == null) return;
        try {
            controller.exportar(reporteActual, formato, archivo);
            Mensajes.informacion("Reporte guardado en:\n" + archivo.getAbsolutePath());
        } catch (IOException | CinemaException ex) {
            Mensajes.error("No se pudo exportar: " + ex.getMessage());
        }
    }
}
