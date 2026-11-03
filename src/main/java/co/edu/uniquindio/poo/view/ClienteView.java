package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.controller.ClienteController;
import co.edu.uniquindio.poo.controller.LoginController;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.LotePuntos;
import co.edu.uniquindio.poo.model.MovimientoTarjeta;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.model.Reembolso;
import co.edu.uniquindio.poo.model.SolicitudRecarga;
import co.edu.uniquindio.poo.patrones.factory.Entrada;
import co.edu.uniquindio.poo.util.Formato;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Map;

public class ClienteView {
    private final ClienteController controller;
    private final BorderPane raiz = new BorderPane();
    private final Label lblSaldo = new Label();
    private final Label lblPuntos = new Label();

    public ClienteView(ClienteController controller, LoginController login, Navegador navegador) {
        this.controller = controller;

        Button btnSalir = new Button("Cerrar sesión");
        btnSalir.setOnAction(e -> {
            login.cerrarSesion();
            navegador.mostrarLogin();
        });
        raiz.setTop(crearBarraSuperior(btnSalir));

        Tab tabCartelera = Componentes.pestana("Cartelera", this::crearCartelera);
        TabPane pestanas = new TabPane(tabCartelera,
                Componentes.pestana("Mi tarjeta", this::crearMiTarjeta),
                Componentes.pestana("Mis compras", this::crearMisCompras),
                Componentes.pestana("Mis puntos", this::crearMisPuntos));
        pestanas.getSelectionModel().selectedItemProperty().addListener((obs, a, b) -> actualizarBarra());
        tabCartelera.setContent(crearCartelera());
        raiz.setCenter(pestanas);
        actualizarBarra();
    }

    public Parent getVista() {
        return raiz;
    }

    private HBox crearBarraSuperior(Button btnSalir) {
        Label logo = Componentes.titulo("CinemaUQ");
        Label saludo = new Label("Cliente: " + controller.getCliente().getNombre());
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        HBox barra = new HBox(15, logo, saludo, espacio, lblSaldo, lblPuntos, btnSalir);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(8, 12, 8, 12));
        Estilos.bordeInferior(barra);
        return barra;
    }

    private void actualizarBarra() {
        lblSaldo.setText("Saldo: " + Formato.moneda(controller.getCliente().getTarjeta().getSaldo()));
        lblPuntos.setText("Puntos: " + controller.puntosDisponibles());
    }

    private VBox crearCartelera() {
        ListView<Pelicula> listaPeliculas = new ListView<>();
        listaPeliculas.getItems().setAll(controller.cartelera());
        listaPeliculas.setPrefWidth(240);
        VBox.setVgrow(listaPeliculas, Priority.ALWAYS);

        Label lblTitulo = Componentes.subtitulo("Seleccione una película");
        Label lblInfo = Componentes.textoGris("");

        TableView<Funcion> tablaFunciones = new TableView<>();
        Componentes.agregarColumna(tablaFunciones, "Fecha", f -> Formato.fecha(f.getFecha()));
        Componentes.agregarColumna(tablaFunciones, "Hora", Funcion::getHora);
        Componentes.agregarColumna(tablaFunciones, "Sala", f -> f.getSala().getNombre());
        Componentes.agregarColumna(tablaFunciones, "Tipo", f -> f.getSala().getTipo());
        Componentes.agregarColumna(tablaFunciones, "Precio", f -> Formato.moneda(f.getPrecioBase()));
        Componentes.agregarColumna(tablaFunciones, "Sillas libres", Funcion::getAsientosDisponibles);
        Componentes.configurarTabla(tablaFunciones);

        listaPeliculas.getSelectionModel().selectedItemProperty().addListener((obs, anterior, pelicula) -> {
            if (pelicula != null) {
                lblTitulo.setText(pelicula.getTitulo());
                lblInfo.setText(pelicula.getGenero() + "  ·  " + pelicula.getDuracionMinutos() + " min  ·  "
                        + pelicula.getClasificacion() + "\n" + pelicula.getSinopsis());
                Componentes.llenarTabla(tablaFunciones, controller.funciones(pelicula));
            }
        });

        Button btnComprar = Componentes.botonPrincipal("Elegir asientos y comprar");
        btnComprar.setOnAction(e -> abrirCompra(tablaFunciones));

        StringBuilder promos = new StringBuilder("Promociones de hoy: ");
        for (Promocion p : controller.promociones(controller.hoy())) {
            promos.append(p.getNombre()).append(" (").append((int) p.getPorcentaje()).append("%)   ");
        }

        VBox izquierda = Componentes.tarjeta(Componentes.subtitulo("En cartelera"), listaPeliculas);
        VBox derecha = Componentes.tarjeta(lblTitulo, lblInfo, tablaFunciones, btnComprar,
                Componentes.textoGris(promos.toString()));
        HBox.setHgrow(derecha, Priority.ALWAYS);

        HBox contenido = new HBox(16, izquierda, derecha);
        VBox.setVgrow(contenido, Priority.ALWAYS);

        if (!listaPeliculas.getItems().isEmpty()) listaPeliculas.getSelectionModel().selectFirst();
        return Componentes.seccion("Cartelera", contenido);
    }

    private void abrirCompra(TableView<Funcion> tablaFunciones) {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Mensajes.error("Seleccione una función de la tabla.");
            return;
        }
        CompraView ventana = new CompraView(controller, funcion, raiz.getScene().getWindow());
        ventana.mostrar();
        tablaFunciones.refresh();
        actualizarBarra();
    }

    private VBox crearMiTarjeta() {
        Cliente cliente = controller.getCliente();

        Label lblNumero = new Label("Número: " + cliente.getTarjeta().getNumero());
        Label lblEstado = new Label("Estado: " + cliente.getTarjeta().getEstado());
        Label lblSaldoTarjeta = Componentes.titulo(Formato.moneda(cliente.getTarjeta().getSaldo()));

        TextField txtMonto = new TextField();
        txtMonto.setPromptText("Ejemplo: 50000");

        TableView<SolicitudRecarga> tablaSolicitudes = new TableView<>();
        Componentes.agregarColumna(tablaSolicitudes, "Fecha", s -> Formato.fechaHora(s.getFecha()));
        Componentes.agregarColumna(tablaSolicitudes, "Monto", s -> Formato.moneda(s.getMonto()));
        Componentes.agregarColumna(tablaSolicitudes, "Estado", SolicitudRecarga::getEstado);
        Componentes.configurarTabla(tablaSolicitudes);
        Componentes.llenarTabla(tablaSolicitudes, controller.solicitudes());

        Button btnSolicitar = Componentes.botonPrincipal("Solicitar recarga");
        btnSolicitar.setOnAction(e -> {
            try {
                controller.solicitarRecarga(txtMonto.getText());
                txtMonto.clear();
                Componentes.llenarTabla(tablaSolicitudes, controller.solicitudes());
                Mensajes.informacion("Solicitud enviada. El administrador la revisará.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox datosTarjeta = Componentes.tarjeta(Componentes.subtitulo("Tarjeta virtual"),
                Componentes.textoGris("Saldo disponible"), lblSaldoTarjeta, lblNumero, lblEstado);
        VBox recarga = Componentes.tarjeta(Componentes.subtitulo("Solicitar recarga"),
                Componentes.textoGris("Solo el administrador puede recargar tu tarjeta."),
                Componentes.fila(txtMonto, btnSolicitar), new Label("Mis solicitudes"), tablaSolicitudes);
        VBox.setVgrow(recarga, Priority.ALWAYS);
        VBox columnaIzquierda = new VBox(16, datosTarjeta, recarga);
        columnaIzquierda.setPrefWidth(360);

        TableView<MovimientoTarjeta> tablaMovimientos = new TableView<>();
        Componentes.agregarColumna(tablaMovimientos, "Fecha", m -> Formato.fechaHora(m.getFecha()));
        Componentes.agregarColumna(tablaMovimientos, "Tipo", MovimientoTarjeta::getTipo);
        Componentes.agregarColumna(tablaMovimientos, "Monto", m -> Formato.moneda(m.getMonto()));
        Componentes.agregarColumna(tablaMovimientos, "Saldo", m -> Formato.moneda(m.getSaldoResultante()));
        Componentes.agregarColumna(tablaMovimientos, "Descripción", MovimientoTarjeta::getDescripcion);
        Componentes.configurarTabla(tablaMovimientos);
        Componentes.llenarTabla(tablaMovimientos, controller.movimientosTarjeta());

        VBox movimientos = Componentes.tarjeta(Componentes.subtitulo("Movimientos"), tablaMovimientos);
        HBox.setHgrow(movimientos, Priority.ALWAYS);

        HBox contenido = new HBox(16, columnaIzquierda, movimientos);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return Componentes.seccion("Mi tarjeta", contenido);
    }

    private VBox crearMisCompras() {
        TableView<Compra> tablaCompras = new TableView<>();
        Componentes.agregarColumna(tablaCompras, "Código", Compra::getId);
        Componentes.agregarColumna(tablaCompras, "Película", c -> c.getFuncion().getPelicula().getTitulo());
        Componentes.agregarColumna(tablaCompras, "Función", c -> Formato.fechaHora(c.getFuncion().getFechaHora()));
        Componentes.agregarColumna(tablaCompras, "Entradas", Compra::getCantidadEntradas);
        Componentes.agregarColumna(tablaCompras, "Total", c -> Formato.moneda(c.getTotal()));
        Componentes.agregarColumna(tablaCompras, "Estado", Compra::getNombreEstado);
        Componentes.configurarTabla(tablaCompras);
        Componentes.llenarTabla(tablaCompras, controller.historial());

        TextArea txtDetalle = new TextArea("Seleccione una compra para ver el detalle.");
        txtDetalle.setEditable(false);
        txtDetalle.setWrapText(true);
        VBox.setVgrow(txtDetalle, Priority.ALWAYS);

        tablaCompras.getSelectionModel().selectedItemProperty().addListener((obs, anterior, compra) -> {
            if (compra != null) txtDetalle.setText(detalleCompra(compra));
        });

        Button btnCancelar = Componentes.botonPeligro("Cancelar compra seleccionada");
        btnCancelar.setOnAction(e -> {
            Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
            if (compra == null) {
                Mensajes.error("Seleccione una compra.");
                return;
            }
            cancelarCompra(compra);
            Componentes.llenarTabla(tablaCompras, controller.historial());
            txtDetalle.setText(detalleCompra(compra));
        });

        VBox tabla = Componentes.tarjeta(Componentes.subtitulo("Historial"), tablaCompras, btnCancelar,
                Componentes.textoGris("Reembolso: más de 24 h = 100 %  ·  entre 2 y 24 h = 80 %  ·  menos de 2 h = no se permite."));
        HBox.setHgrow(tabla, Priority.ALWAYS);
        VBox detalle = Componentes.tarjeta(Componentes.subtitulo("Detalle"), txtDetalle);
        detalle.setPrefWidth(340);

        HBox contenido = new HBox(16, tabla, detalle);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return Componentes.seccion("Mis compras", contenido);
    }

    private void cancelarCompra(Compra compra) {
        try {
            double porcentaje = controller.porcentajeReembolso(compra);
            boolean acepta = Mensajes.confirmar("Se devolverá el " + (int) porcentaje + " % de "
                    + Formato.moneda(compra.getTotal()) + " a su tarjeta.\n¿Desea cancelar la compra " + compra.getId() + "?");
            if (!acepta) return;
            Reembolso reembolso = controller.cancelar(compra);
            Mensajes.informacion("Compra cancelada. Se devolvieron " + Formato.moneda(reembolso.getMontoReembolsado())
                    + " a su tarjeta.");
            actualizarBarra();
        } catch (CinemaException ex) {
            Mensajes.error(ex.getMessage());
        }
    }

    private String detalleCompra(Compra c) {
        StringBuilder sb = new StringBuilder();
        sb.append("Compra: ").append(c.getId()).append("\n");
        sb.append("Estado: ").append(c.getNombreEstado()).append("\n");
        sb.append("Película: ").append(c.getFuncion().getPelicula().getTitulo()).append("\n");
        sb.append("Función: ").append(Formato.fechaHora(c.getFuncion().getFechaHora()))
          .append(" - ").append(c.getFuncion().getSala().getNombre()).append("\n\n");

        sb.append("Entradas:\n");
        for (Entrada e : c.getEntradas()) {
            sb.append("  • ").append(e);
            if (e.isRedimidaConPuntos()) sb.append("  (con puntos)");
            else sb.append("  ").append(Formato.moneda(e.calcularPrecio()));
            sb.append("\n");
        }
        if (!c.getConfiteria().isEmpty()) {
            sb.append("\nConfitería:\n");
            c.getConfiteria().forEach(linea -> sb.append("  • ").append(linea).append("\n"));
        }
        sb.append("\nDescuentos: ").append(Formato.moneda(c.getDescuento()));
        sb.append("\nTotal pagado: ").append(Formato.moneda(c.getTotal()));
        sb.append("\nPuntos usados: ").append(c.getPuntosUsados());
        sb.append("\nPuntos ganados: ").append(c.getPuntosGanados());

        Reembolso r = c.getReembolso();
        if (r != null) {
            sb.append("\n\nReembolso: ").append(Formato.moneda(r.getMontoReembolsado()))
              .append(" (").append((int) r.getPorcentaje()).append(" %)");
            sb.append("\nMotivo: ").append(r.getMotivo());
        }
        return sb.toString();
    }

    private VBox crearMisPuntos() {
        Label lblTotal = Componentes.titulo(controller.puntosDisponibles() + " puntos");
        Label lblRegla = new Label(controller.reglaPuntos());
        lblRegla.setWrapText(true);

        VBox equivalencias = new VBox(4);
        for (Map.Entry<String, String> e : controller.equivalenciasPuntos().entrySet()) {
            equivalencias.getChildren().add(new Label("• " + e.getKey() + ": " + e.getValue()));
        }

        VBox info = Componentes.tarjeta(Componentes.subtitulo("Puntos disponibles"), lblTotal,
                Componentes.subtitulo("¿Cómo gano puntos?"), lblRegla,
                Componentes.subtitulo("¿Qué puedo redimir?"), equivalencias,
                Componentes.textoGris("Los puntos vencen un año después de obtenidos. "
                        + "Siempre se usan primero los que están por vencer."));
        info.setPrefWidth(340);

        TableView<LotePuntos> tablaLotes = new TableView<>();
        Componentes.agregarColumna(tablaLotes, "Puntos", LotePuntos::getCantidadInicial);
        Componentes.agregarColumna(tablaLotes, "Disponibles", LotePuntos::getDisponibles);
        Componentes.agregarColumna(tablaLotes, "Obtenidos", l -> Formato.fecha(l.getFechaObtencion()));
        Componentes.agregarColumna(tablaLotes, "Vencen", l -> Formato.fecha(l.getFechaVencimiento()));
        Componentes.agregarColumna(tablaLotes, "Estado", l -> controller.loteVigente(l) ? "Vigente" : "Vencido");
        Componentes.agregarColumna(tablaLotes, "Origen", LotePuntos::getOrigen);
        Componentes.configurarTabla(tablaLotes);
        Componentes.llenarTabla(tablaLotes, controller.lotesPuntos());

        VBox tablas = Componentes.tarjeta(Componentes.subtitulo("Mis puntos por fecha de obtención"), tablaLotes);
        HBox.setHgrow(tablas, Priority.ALWAYS);

        HBox contenido = new HBox(16, info, tablas);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return Componentes.seccion("Mis puntos", contenido);
    }
}
