package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.controller.AdminController;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.util.Formato;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class EstadisticasView {
    private final AdminController controller;
    private final ScrollPane vista;

    private final DatePicker dpDesde;
    private final DatePicker dpHasta;

    private final Label lblVentas = new Label();
    private final Label lblEntradas = new Label();
    private final Label lblProductos = new Label();
    private final Label lblReembolsos = new Label();

    private final BarChart<String, Number> graficoPeliculas = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private final BarChart<String, Number> graficoProductos = new BarChart<>(new CategoryAxis(), new NumberAxis());
    private final PieChart graficoOcupacion = new PieChart();
    private final PieChart graficoCategorias = new PieChart();
    private final LineChart<String, Number> graficoVentas = new LineChart<>(new CategoryAxis(), new NumberAxis());

    public EstadisticasView(AdminController controller) {
        this.controller = controller;
        dpDesde = new DatePicker(controller.hoy().minusDays(30));
        dpHasta = new DatePicker(controller.hoy());

        Button btnActualizar = Componentes.botonPrincipal("Actualizar");
        btnActualizar.setOnAction(e -> actualizar());
        HBox filtros = Componentes.fila(new Label("Desde"), dpDesde, new Label("Hasta"), dpHasta, btnActualizar);

        HBox indicadores = new HBox(14,
                indicador("Ventas del periodo", lblVentas),
                indicador("Entradas vendidas", lblEntradas),
                indicador("Productos vendidos", lblProductos),
                indicador("Reembolsos", lblReembolsos));

        configurarGrafico(graficoPeliculas, "Películas con más entradas vendidas");
        configurarGrafico(graficoProductos, "Productos y combos más vendidos");
        graficoOcupacion.setTitle("Ocupación promedio por sala (%)");
        graficoCategorias.setTitle("Ventas por categoría");
        graficoVentas.setTitle("Ventas y recargas por día");
        graficoVentas.setAnimated(false);

        GridPane graficos = new GridPane();
        graficos.setHgap(14);
        graficos.setVgap(14);
        graficos.add(Componentes.tarjeta(graficoPeliculas), 0, 0);
        graficos.add(Componentes.tarjeta(graficoProductos), 1, 0);
        graficos.add(Componentes.tarjeta(graficoOcupacion), 0, 1);
        graficos.add(Componentes.tarjeta(graficoCategorias), 1, 1);
        graficos.add(Componentes.tarjeta(graficoVentas), 0, 2, 2, 1);
        javafx.scene.layout.ColumnConstraints mitad = new javafx.scene.layout.ColumnConstraints();
        mitad.setPercentWidth(50);
        graficos.getColumnConstraints().addAll(mitad, mitad);

        VBox contenido = Componentes.seccion("Estadísticas", filtros, indicadores, graficos);
        vista = new ScrollPane(contenido);
        vista.setFitToWidth(true);
        actualizar();
    }

    public Parent getVista() {
        return vista;
    }

    private VBox indicador(String nombre, Label valor) {
        Estilos.fuente(valor, 20, true);
        VBox caja = Componentes.tarjeta(Componentes.textoGris(nombre), valor);
        HBox.setHgrow(caja, Priority.ALWAYS);
        caja.setMaxWidth(Double.MAX_VALUE);
        return caja;
    }

    private void configurarGrafico(BarChart<String, Number> grafico, String titulo) {
        grafico.setTitle(titulo);
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        grafico.setPrefHeight(300);
    }

    private void actualizar() {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();
        try {
            mostrar(desde, hasta);
        } catch (CinemaException ex) {
            Mensajes.error(ex.getMessage());
        }
    }

    private void mostrar(LocalDate desde, LocalDate hasta) {
        lblVentas.setText(Formato.moneda(controller.totalVentas(desde, hasta)));
        lblEntradas.setText(String.valueOf(controller.totalEntradas(desde, hasta)));
        lblProductos.setText(String.valueOf(controller.totalProductos(desde, hasta)));
        lblReembolsos.setText(Formato.moneda(controller.totalReembolsos(desde, hasta)));

        graficoPeliculas.getData().clear();
        graficoPeliculas.getData().add(crearSerie("Entradas", controller.topEntradasPorPelicula(desde, hasta, 8)));
        graficoProductos.getData().clear();
        graficoProductos.getData().add(crearSerie("Unidades", controller.topProductos(desde, hasta, 8)));

        graficoOcupacion.getData().clear();
        for (Map.Entry<String, Double> e : controller.ocupacionPorSala(desde, hasta).entrySet()) {
            String nombre = e.getKey() + String.format(" (%.1f %%)", e.getValue());
            graficoOcupacion.getData().add(new PieChart.Data(nombre, Math.max(e.getValue(), 0.1)));
        }
        graficoCategorias.getData().clear();
        Map<String, Double> ventas = controller.ventasPorCategoria(desde, hasta);
        for (Map.Entry<String, Double> e : controller.porcentajeVentasPorCategoria(desde, hasta).entrySet()) {
            String nombre = e.getKey() + String.format(" (%.1f %%)", e.getValue());
            graficoCategorias.getData().add(new PieChart.Data(nombre, ventas.get(e.getKey())));
        }

        graficoVentas.getData().clear();
        graficoVentas.getData().add(crearSerieDiaria("Ventas", controller.ventasPorDia(desde, hasta)));
        graficoVentas.getData().add(crearSerieDiaria("Recargas", controller.recargasPorDia(desde, hasta)));
    }

    private XYChart.Series<String, Number> crearSerie(String nombre, Map<String, Integer> datos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);
        for (Map.Entry<String, Integer> e : datos.entrySet()) {
            serie.getData().add(new XYChart.Data<>(e.getKey(), e.getValue()));
        }
        return serie;
    }

    private XYChart.Series<String, Number> crearSerieDiaria(String nombre, Map<LocalDate, Double> datos) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM");
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);
        for (Map.Entry<LocalDate, Double> e : datos.entrySet()) {
            serie.getData().add(new XYChart.Data<>(e.getKey().format(formato), e.getValue()));
        }
        return serie;
    }
}
