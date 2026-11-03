package co.edu.uniquindio.poo.view;

import co.edu.uniquindio.poo.controller.AdminController;
import co.edu.uniquindio.poo.controller.LoginController;
import co.edu.uniquindio.poo.enums.CategoriaProducto;
import co.edu.uniquindio.poo.enums.ClasificacionEdad;
import co.edu.uniquindio.poo.enums.GeneroPelicula;
import co.edu.uniquindio.poo.enums.TipoAsiento;
import co.edu.uniquindio.poo.enums.TipoPromocion;
import co.edu.uniquindio.poo.enums.TipoSala;
import co.edu.uniquindio.poo.exceptions.CinemaException;
import co.edu.uniquindio.poo.model.Asiento;
import co.edu.uniquindio.poo.model.Cliente;
import co.edu.uniquindio.poo.model.Compra;
import co.edu.uniquindio.poo.model.Funcion;
import co.edu.uniquindio.poo.model.Pelicula;
import co.edu.uniquindio.poo.model.Promocion;
import co.edu.uniquindio.poo.model.Sala;
import co.edu.uniquindio.poo.model.SolicitudRecarga;
import co.edu.uniquindio.poo.patrones.composite.Combo;
import co.edu.uniquindio.poo.patrones.composite.ItemConfiteria;
import co.edu.uniquindio.poo.patrones.composite.Producto;
import co.edu.uniquindio.poo.util.Formato;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class AdminView {
    private static final String[] DIAS = {"Todos los días", "Lunes", "Martes", "Miércoles", "Jueves",
            "Viernes", "Sábado", "Domingo"};

    private final AdminController controller;
    private final BorderPane raiz = new BorderPane();

    public AdminView(AdminController controller, LoginController login, Navegador navegador) {
        this.controller = controller;

        Button btnSalir = new Button("Cerrar sesión");
        btnSalir.setOnAction(e -> {
            login.cerrarSesion();
            navegador.mostrarLogin();
        });
        raiz.setTop(crearBarraSuperior(btnSalir));

        Tab tabPeliculas = Componentes.pestana("Películas", this::crearPeliculas);
        TabPane pestanas = new TabPane(tabPeliculas,
                Componentes.pestana("Salas y asientos", this::crearSalas),
                Componentes.pestana("Funciones", this::crearFunciones),
                Componentes.pestana("Productos y combos", this::crearConfiteria),
                Componentes.pestana("Promociones", this::crearPromociones),
                Componentes.pestana("Clientes y recargas", this::crearClientes),
                Componentes.pestana("Compras", this::crearCompras),
                Componentes.pestana("Estadísticas", () -> new EstadisticasView(controller).getVista()),
                Componentes.pestana("Reportes", () -> new ReportesView(controller).getVista()));
        tabPeliculas.setContent(crearPeliculas());
        raiz.setCenter(pestanas);
    }

    public Parent getVista() {
        return raiz;
    }

    private HBox crearBarraSuperior(Button btnSalir) {
        Label logo = Componentes.titulo("CinemaUQ");
        Label perfil = new Label("Administrador");
        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);
        Label fecha = new Label("Hoy: " + Formato.fecha(controller.hoy()));
        HBox barra = new HBox(15, logo, perfil, espacio, fecha, btnSalir);
        barra.setAlignment(Pos.CENTER_LEFT);
        barra.setPadding(new Insets(8, 12, 8, 12));
        Estilos.bordeInferior(barra);
        return barra;
    }

    private HBox formularioYTabla(VBox formulario, VBox tabla) {
        formulario.setPrefWidth(320);
        formulario.setMinWidth(320);
        HBox.setHgrow(tabla, Priority.ALWAYS);
        HBox contenido = new HBox(10, formulario, tabla);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return contenido;
    }

    private VBox crearPeliculas() {
        TableView<Pelicula> tabla = new TableView<>();
        Componentes.agregarColumna(tabla, "Código", Pelicula::getId);
        Componentes.agregarColumna(tabla, "Título", Pelicula::getTitulo);
        Componentes.agregarColumna(tabla, "Género", Pelicula::getGenero);
        Componentes.agregarColumna(tabla, "Duración", p -> p.getDuracionMinutos() + " min");
        Componentes.agregarColumna(tabla, "Clasificación", Pelicula::getClasificacion);
        Componentes.agregarColumna(tabla, "En cartelera", p -> p.isEnCartelera() ? "Sí" : "No");
        Componentes.configurarTabla(tabla);
        Componentes.llenarTabla(tabla, controller.peliculas());

        TextField txtTitulo = new TextField();
        ComboBox<GeneroPelicula> cmbGenero = new ComboBox<>();
        cmbGenero.getItems().setAll(GeneroPelicula.values());
        TextField txtDuracion = new TextField();
        ComboBox<ClasificacionEdad> cmbClasificacion = new ComboBox<>();
        cmbClasificacion.getItems().setAll(ClasificacionEdad.values());
        TextArea txtSinopsis = new TextArea();
        txtSinopsis.setPrefRowCount(4);
        txtSinopsis.setWrapText(true);
        CheckBox chkCartelera = new CheckBox("En cartelera");
        chkCartelera.setSelected(true);

        GridPane form = Componentes.formulario();
        Componentes.agregarCampo(form, "Título", txtTitulo);
        Componentes.agregarCampo(form, "Género", cmbGenero);
        Componentes.agregarCampo(form, "Duración (min)", txtDuracion);
        Componentes.agregarCampo(form, "Clasificación", cmbClasificacion);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, p) -> {
            if (p != null) {
                txtTitulo.setText(p.getTitulo());
                cmbGenero.setValue(p.getGenero());
                txtDuracion.setText(String.valueOf(p.getDuracionMinutos()));
                cmbClasificacion.setValue(p.getClasificacion());
                txtSinopsis.setText(p.getSinopsis());
                chkCartelera.setSelected(p.isEnCartelera());
            }
        });

        Button btnNuevo = new Button("Nuevo");
        btnNuevo.setOnAction(e -> {
            tabla.getSelectionModel().clearSelection();
            txtTitulo.clear();
            txtDuracion.clear();
            txtSinopsis.clear();
            cmbGenero.setValue(null);
            cmbClasificacion.setValue(null);
            chkCartelera.setSelected(true);
        });

        Button btnGuardar = Componentes.botonPrincipal("Guardar");
        btnGuardar.setOnAction(e -> {
            try {
                controller.guardarPelicula(tabla.getSelectionModel().getSelectedItem(), txtTitulo.getText(),
                        cmbGenero.getValue(), txtDuracion.getText(), cmbClasificacion.getValue(),
                        txtSinopsis.getText(), chkCartelera.isSelected());
                Componentes.llenarTabla(tabla, controller.peliculas());
                btnNuevo.fire();
                Mensajes.informacion("Película guardada.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnEliminar = Componentes.botonPeligro("Eliminar");
        btnEliminar.setOnAction(e -> {
            Pelicula p = tabla.getSelectionModel().getSelectedItem();
            if (p == null || !Mensajes.confirmar("¿Eliminar la película " + p.getTitulo() + "?")) return;
            try {
                controller.eliminarPelicula(p);
                Componentes.llenarTabla(tabla, controller.peliculas());
                btnNuevo.fire();
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox formulario = Componentes.tarjeta(Componentes.subtitulo("Datos de la película"), form,
                new Label("Sinopsis"), txtSinopsis, chkCartelera, Componentes.fila(btnNuevo, btnGuardar, btnEliminar));
        VBox listado = Componentes.tarjeta(Componentes.subtitulo("Películas registradas"), tabla);
        return Componentes.seccion("Películas", formularioYTabla(formulario, listado));
    }

    private VBox crearSalas() {
        TableView<Sala> tabla = new TableView<>();
        Componentes.agregarColumna(tabla, "Código", Sala::getId);
        Componentes.agregarColumna(tabla, "Nombre", Sala::getNombre);
        Componentes.agregarColumna(tabla, "Tipo", Sala::getTipo);
        Componentes.agregarColumna(tabla, "Filas", Sala::getFilas);
        Componentes.agregarColumna(tabla, "Columnas", Sala::getColumnas);
        Componentes.agregarColumna(tabla, "Capacidad", Sala::getCapacidad);
        Componentes.configurarTabla(tabla);
        tabla.setPrefHeight(200);
        VBox.setVgrow(tabla, Priority.NEVER);
        Componentes.llenarTabla(tabla, controller.salas());

        TextField txtNombre = new TextField();
        ComboBox<TipoSala> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().setAll(TipoSala.values());
        TextField txtFilas = new TextField();
        TextField txtColumnas = new TextField();

        GridPane form = Componentes.formulario();
        Componentes.agregarCampo(form, "Nombre", txtNombre);
        Componentes.agregarCampo(form, "Tipo", cmbTipo);
        Componentes.agregarCampo(form, "Filas (1 a 26)", txtFilas);
        Componentes.agregarCampo(form, "Columnas", txtColumnas);

        GridPane mapa = new GridPane();
        mapa.setHgap(5);
        mapa.setVgap(5);
        mapa.setAlignment(Pos.CENTER);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, s) -> {
            if (s != null) {
                txtNombre.setText(s.getNombre());
                cmbTipo.setValue(s.getTipo());
                txtFilas.setText(String.valueOf(s.getFilas()));
                txtColumnas.setText(String.valueOf(s.getColumnas()));
            }
            dibujarAsientosSala(mapa, s);
        });

        Button btnNuevo = new Button("Nuevo");
        btnNuevo.setOnAction(e -> {
            tabla.getSelectionModel().clearSelection();
            txtNombre.clear();
            txtFilas.clear();
            txtColumnas.clear();
            cmbTipo.setValue(null);
        });

        Button btnGuardar = Componentes.botonPrincipal("Guardar");
        btnGuardar.setOnAction(e -> {
            try {
                controller.guardarSala(tabla.getSelectionModel().getSelectedItem(), txtNombre.getText(),
                        cmbTipo.getValue(), txtFilas.getText(), txtColumnas.getText());
                Componentes.llenarTabla(tabla, controller.salas());
                btnNuevo.fire();
                Mensajes.informacion("Sala guardada.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnEliminar = Componentes.botonPeligro("Eliminar");
        btnEliminar.setOnAction(e -> {
            Sala s = tabla.getSelectionModel().getSelectedItem();
            if (s == null || !Mensajes.confirmar("¿Eliminar la sala " + s.getNombre() + "?")) return;
            try {
                controller.eliminarSala(s);
                Componentes.llenarTabla(tabla, controller.salas());
                btnNuevo.fire();
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox formulario = Componentes.tarjeta(Componentes.subtitulo("Datos de la sala"), form,
                Componentes.textoGris("Si cambia filas o columnas se vuelven a crear los asientos."),
                Componentes.fila(btnNuevo, btnGuardar, btnEliminar));

        ScrollPane scrollMapa = new ScrollPane(mapa);
        scrollMapa.setFitToWidth(true);
        VBox.setVgrow(scrollMapa, Priority.ALWAYS);
        VBox listado = Componentes.tarjeta(Componentes.subtitulo("Salas registradas"), tabla,
                Componentes.subtitulo("Asientos"),
                Componentes.textoGris("Haga clic en un asiento para habilitarlo o deshabilitarlo."), scrollMapa);
        return Componentes.seccion("Salas y asientos", formularioYTabla(formulario, listado));
    }

    private void dibujarAsientosSala(GridPane mapa, Sala sala) {
        mapa.getChildren().clear();
        if (sala == null) return;
        for (Asiento asiento : sala.getAsientos()) {
            Button boton = new Button(asiento.getCodigo());
            if (!asiento.isHabilitado()) Estilos.asiento(boton, Estilos.ASIENTO_INHABILITADO);
            else if (asiento.getTipo() == TipoAsiento.PREFERENCIAL) Estilos.asiento(boton, Estilos.ASIENTO_PREFERENCIAL);
            else if (asiento.getTipo() == TipoAsiento.DISCAPACIDAD) Estilos.asiento(boton, Estilos.ASIENTO_DISCAPACIDAD);
            else Estilos.asiento(boton, Estilos.ASIENTO_LIBRE);

            boton.setOnAction(e -> {
                try {
                    controller.alternarAsiento(sala, asiento);
                    dibujarAsientosSala(mapa, sala);
                } catch (CinemaException ex) {
                    Mensajes.error(ex.getMessage());
                }
            });
            mapa.add(boton, asiento.getNumero(), asiento.getFila() - 'A');
        }
    }

    private VBox crearFunciones() {
        TableView<Funcion> tabla = new TableView<>();
        Componentes.agregarColumna(tabla, "Código", Funcion::getId);
        Componentes.agregarColumna(tabla, "Película", f -> f.getPelicula().getTitulo());
        Componentes.agregarColumna(tabla, "Sala", f -> f.getSala().getNombre());
        Componentes.agregarColumna(tabla, "Fecha", f -> Formato.fecha(f.getFecha()));
        Componentes.agregarColumna(tabla, "Hora", Funcion::getHora);
        Componentes.agregarColumna(tabla, "Precio", f -> Formato.moneda(f.getPrecioBase()));
        Componentes.agregarColumna(tabla, "Ocupación", f -> String.format("%.1f %%", f.getPorcentajeOcupacion()));
        Componentes.agregarColumna(tabla, "Estado", Funcion::getEstado);
        Componentes.configurarTabla(tabla);
        Componentes.llenarTabla(tabla, controller.funcionesRecientesPrimero());

        ComboBox<Pelicula> cmbPelicula = new ComboBox<>();
        cmbPelicula.getItems().setAll(controller.peliculas());
        ComboBox<Sala> cmbSala = new ComboBox<>();
        cmbSala.getItems().setAll(controller.salas());
        DatePicker dpFecha = new DatePicker(controller.hoy().plusDays(1));
        TextField txtHora = new TextField();
        txtHora.setPromptText("HH:mm, ej: 19:30");
        TextField txtPrecio = new TextField();
        txtPrecio.setPromptText("Ej: 12000");

        GridPane form = Componentes.formulario();
        Componentes.agregarCampo(form, "Película", cmbPelicula);
        Componentes.agregarCampo(form, "Sala", cmbSala);
        Componentes.agregarCampo(form, "Fecha", dpFecha);
        Componentes.agregarCampo(form, "Hora", txtHora);
        Componentes.agregarCampo(form, "Precio base", txtPrecio);

        Button btnCrear = Componentes.botonPrincipal("Crear función");
        btnCrear.setOnAction(e -> {
            try {
                controller.crearFuncion(cmbPelicula.getValue(), cmbSala.getValue(), dpFecha.getValue(),
                        txtHora.getText(), txtPrecio.getText());
                Componentes.llenarTabla(tabla, controller.funcionesRecientesPrimero());
                txtHora.clear();
                txtPrecio.clear();
                Mensajes.informacion("Función creada.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnCancelar = Componentes.botonPeligro("Cancelar función (reembolso 100 %)");
        btnCancelar.setOnAction(e -> {
            Funcion f = tabla.getSelectionModel().getSelectedItem();
            if (f == null) {
                Mensajes.error("Seleccione una función de la tabla.");
                return;
            }
            if (!Mensajes.confirmar("Se cancelará la función y se devolverá el 100 % a los clientes. ¿Continuar?")) return;
            try {
                int reembolsadas = controller.cancelarFuncion(f);
                Componentes.llenarTabla(tabla, controller.funcionesRecientesPrimero());
                Mensajes.informacion("Función cancelada. Compras reembolsadas: " + reembolsadas);
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnEliminar = new Button("Eliminar función (sin ventas)");
        btnEliminar.setOnAction(e -> {
            try {
                controller.eliminarFuncion(tabla.getSelectionModel().getSelectedItem());
                Componentes.llenarTabla(tabla, controller.funcionesRecientesPrimero());
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox formulario = Componentes.tarjeta(Componentes.subtitulo("Programar función"), form,
                Componentes.textoGris("Entre funciones de la misma sala se dejan 20 minutos para el aseo."),
                btnCrear, Componentes.subtitulo("Función seleccionada"), btnCancelar, btnEliminar);
        VBox listado = Componentes.tarjeta(Componentes.subtitulo("Funciones programadas"), tabla);
        return Componentes.seccion("Funciones", formularioYTabla(formulario, listado));
    }

    private VBox crearConfiteria() {
        TableView<ItemConfiteria> tabla = new TableView<>();
        Componentes.agregarColumna(tabla, "Código", ItemConfiteria::getId);
        Componentes.agregarColumna(tabla, "Nombre", ItemConfiteria::getNombre);
        Componentes.agregarColumna(tabla, "Categoría", ItemConfiteria::getCategoria);
        Componentes.agregarColumna(tabla, "Precio", i -> Formato.moneda(i.getPrecio()));
        Componentes.agregarColumna(tabla, "Puntos", ItemConfiteria::getPuntosRedencion);
        Componentes.agregarColumna(tabla, "Activo", i -> i.isActivo() ? "Sí" : "No");
        Componentes.agregarColumna(tabla, "Contenido", ItemConfiteria::getDescripcion);
        Componentes.configurarTabla(tabla);
        Componentes.llenarTabla(tabla, controller.confiteria());

        TextField txtNombreProducto = new TextField();
        TextField txtPrecio = new TextField();
        ComboBox<CategoriaProducto> cmbCategoria = new ComboBox<>();
        cmbCategoria.getItems().setAll(CategoriaProducto.deProductos());
        TextField txtPuntos = new TextField();
        CheckBox chkProductoActivo = new CheckBox("Activo");
        chkProductoActivo.setSelected(true);

        GridPane formProducto = Componentes.formulario();
        Componentes.agregarCampo(formProducto, "Nombre", txtNombreProducto);
        Componentes.agregarCampo(formProducto, "Precio", txtPrecio);
        Componentes.agregarCampo(formProducto, "Categoría", cmbCategoria);
        Componentes.agregarCampo(formProducto, "Puntos", txtPuntos);

        TextField txtNombreCombo = new TextField();
        TextField txtDescuento = new TextField();
        txtDescuento.setPromptText("Ej: 15");
        ListView<ItemConfiteria> listaComponentes = new ListView<>();
        listaComponentes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listaComponentes.getItems().setAll(controller.confiteria());
        listaComponentes.setPrefHeight(130);
        CheckBox chkComboActivo = new CheckBox("Activo");
        chkComboActivo.setSelected(true);

        GridPane formCombo = Componentes.formulario();
        Componentes.agregarCampo(formCombo, "Nombre", txtNombreCombo);
        Componentes.agregarCampo(formCombo, "Descuento %", txtDescuento);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, item) -> {
            if (item instanceof Producto p) {
                txtNombreProducto.setText(p.getNombre());
                txtPrecio.setText(String.valueOf((long) p.getPrecio()));
                cmbCategoria.setValue(p.getCategoria());
                txtPuntos.setText(String.valueOf(p.getPuntosRedencion()));
                chkProductoActivo.setSelected(p.isActivo());
            } else if (item instanceof Combo c) {
                txtNombreCombo.setText(c.getNombre());
                txtDescuento.setText(String.valueOf(c.getPorcentajeDescuento()));
                chkComboActivo.setSelected(c.isActivo());
                listaComponentes.getSelectionModel().clearSelection();
                for (ItemConfiteria componente : c.getComponentes()) {
                    listaComponentes.getSelectionModel().select(componente);
                }
            }
        });

        Button btnGuardarProducto = Componentes.botonPrincipal("Guardar producto");
        btnGuardarProducto.setOnAction(e -> {
            ItemConfiteria seleccionado = tabla.getSelectionModel().getSelectedItem();
            Producto producto = seleccionado instanceof Producto p ? p : null;
            try {
                controller.guardarProducto(producto, txtNombreProducto.getText(), txtPrecio.getText(),
                        cmbCategoria.getValue(), txtPuntos.getText(), chkProductoActivo.isSelected());
                Componentes.llenarTabla(tabla, controller.confiteria());
                listaComponentes.getItems().setAll(controller.confiteria());
                Mensajes.informacion("Producto guardado.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnGuardarCombo = Componentes.botonPrincipal("Guardar combo");
        btnGuardarCombo.setOnAction(e -> {
            ItemConfiteria seleccionado = tabla.getSelectionModel().getSelectedItem();
            Combo combo = seleccionado instanceof Combo c ? c : null;
            List<ItemConfiteria> componentes = new ArrayList<>(listaComponentes.getSelectionModel().getSelectedItems());
            try {
                controller.guardarCombo(combo, txtNombreCombo.getText(), txtDescuento.getText(),
                        componentes, chkComboActivo.isSelected());
                Componentes.llenarTabla(tabla, controller.confiteria());
                listaComponentes.getItems().setAll(controller.confiteria());
                Mensajes.informacion("Combo guardado.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnNuevo = new Button("Nuevo");
        btnNuevo.setOnAction(e -> {
            tabla.getSelectionModel().clearSelection();
            txtNombreProducto.clear();
            txtPrecio.clear();
            txtPuntos.clear();
            cmbCategoria.setValue(null);
            txtNombreCombo.clear();
            txtDescuento.clear();
            listaComponentes.getSelectionModel().clearSelection();
        });

        Button btnEliminar = Componentes.botonPeligro("Eliminar seleccionado");
        btnEliminar.setOnAction(e -> {
            try {
                controller.eliminarItem(tabla.getSelectionModel().getSelectedItem());
                Componentes.llenarTabla(tabla, controller.confiteria());
                listaComponentes.getItems().setAll(controller.confiteria());
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox tarjetaProducto = Componentes.tarjeta(Componentes.subtitulo("Producto"), formProducto,
                chkProductoActivo, btnGuardarProducto);
        VBox tarjetaCombo = Componentes.tarjeta(Componentes.subtitulo("Combo"), formCombo,
                Componentes.textoGris("Componentes (Ctrl + clic para elegir varios):"), listaComponentes,
                chkComboActivo, btnGuardarCombo);
        VBox columna = new VBox(14, tarjetaProducto, tarjetaCombo, Componentes.fila(btnNuevo, btnEliminar));
        ScrollPane scroll = new ScrollPane(columna);
        scroll.setFitToWidth(true);
        scroll.setPrefWidth(360);
        scroll.setMinWidth(360);

        VBox listado = Componentes.tarjeta(Componentes.subtitulo("Productos y combos"), tabla);
        HBox.setHgrow(listado, Priority.ALWAYS);
        HBox contenido = new HBox(16, scroll, listado);
        VBox.setVgrow(contenido, Priority.ALWAYS);
        return Componentes.seccion("Productos y combos", contenido);
    }

    private VBox crearPromociones() {
        TableView<Promocion> tabla = new TableView<>();
        Componentes.agregarColumna(tabla, "Nombre", Promocion::getNombre);
        Componentes.agregarColumna(tabla, "Aplica a", Promocion::getTipo);
        Componentes.agregarColumna(tabla, "%", p -> p.getPorcentaje() + " %");
        Componentes.agregarColumna(tabla, "Desde", p -> Formato.fecha(p.getFechaInicio()));
        Componentes.agregarColumna(tabla, "Hasta", p -> Formato.fecha(p.getFechaFin()));
        Componentes.agregarColumna(tabla, "Día", p -> nombreDia(p.getDiaAplicable()));
        Componentes.agregarColumna(tabla, "Activa", p -> p.isActiva() ? "Sí" : "No");
        Componentes.agregarColumna(tabla, "Descripción", Promocion::getDescripcion);
        Componentes.configurarTabla(tabla);
        Componentes.llenarTabla(tabla, controller.promociones());

        TextField txtNombre = new TextField();
        TextField txtDescripcion = new TextField();
        TextField txtPorcentaje = new TextField();
        ComboBox<TipoPromocion> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().setAll(TipoPromocion.values());
        DatePicker dpInicio = new DatePicker(controller.hoy());
        DatePicker dpFin = new DatePicker(controller.hoy().plusMonths(1));
        ComboBox<String> cmbDia = new ComboBox<>();
        cmbDia.getItems().setAll(DIAS);
        cmbDia.getSelectionModel().selectFirst();
        CheckBox chkActiva = new CheckBox("Activa");
        chkActiva.setSelected(true);

        GridPane form = Componentes.formulario();
        Componentes.agregarCampo(form, "Nombre", txtNombre);
        Componentes.agregarCampo(form, "Descripción", txtDescripcion);
        Componentes.agregarCampo(form, "Porcentaje", txtPorcentaje);
        Componentes.agregarCampo(form, "Aplica a", cmbTipo);
        Componentes.agregarCampo(form, "Desde", dpInicio);
        Componentes.agregarCampo(form, "Hasta", dpFin);
        Componentes.agregarCampo(form, "Día", cmbDia);

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, p) -> {
            if (p != null) {
                txtNombre.setText(p.getNombre());
                txtDescripcion.setText(p.getDescripcion());
                txtPorcentaje.setText(String.valueOf(p.getPorcentaje()));
                cmbTipo.setValue(p.getTipo());
                dpInicio.setValue(p.getFechaInicio());
                dpFin.setValue(p.getFechaFin());
                cmbDia.getSelectionModel().select(p.getDiaAplicable() == null ? 0 : p.getDiaAplicable().getValue());
                chkActiva.setSelected(p.isActiva());
            }
        });

        Button btnNuevo = new Button("Nuevo");
        btnNuevo.setOnAction(e -> {
            tabla.getSelectionModel().clearSelection();
            txtNombre.clear();
            txtDescripcion.clear();
            txtPorcentaje.clear();
            cmbTipo.setValue(null);
            cmbDia.getSelectionModel().selectFirst();
            chkActiva.setSelected(true);
        });

        Button btnGuardar = Componentes.botonPrincipal("Guardar");
        btnGuardar.setOnAction(e -> {
            int posicionDia = cmbDia.getSelectionModel().getSelectedIndex();
            DayOfWeek dia = posicionDia <= 0 ? null : DayOfWeek.of(posicionDia);
            try {
                controller.guardarPromocion(tabla.getSelectionModel().getSelectedItem(), txtNombre.getText(),
                        txtDescripcion.getText(), txtPorcentaje.getText(), cmbTipo.getValue(),
                        dpInicio.getValue(), dpFin.getValue(), dia, chkActiva.isSelected());
                Componentes.llenarTabla(tabla, controller.promociones());
                btnNuevo.fire();
                Mensajes.informacion("Promoción guardada.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnEliminar = Componentes.botonPeligro("Eliminar");
        btnEliminar.setOnAction(e -> {
            try {
                controller.eliminarPromocion(tabla.getSelectionModel().getSelectedItem());
                Componentes.llenarTabla(tabla, controller.promociones());
                btnNuevo.fire();
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox formulario = Componentes.tarjeta(Componentes.subtitulo("Datos de la promoción"), form, chkActiva,
                Componentes.textoGris("El descuento total de una compra nunca supera el 70 %."),
                Componentes.fila(btnNuevo, btnGuardar, btnEliminar));
        VBox listado = Componentes.tarjeta(Componentes.subtitulo("Promociones"), tabla);
        return Componentes.seccion("Promociones", formularioYTabla(formulario, listado));
    }

    private String nombreDia(DayOfWeek dia) {
        return dia == null ? "Todos" : DIAS[dia.getValue()];
    }

    private VBox crearClientes() {
        TableView<Cliente> tablaClientes = new TableView<>();
        Componentes.agregarColumna(tablaClientes, "Nombre", Cliente::getNombre);
        Componentes.agregarColumna(tablaClientes, "Documento", Cliente::getDocumento);
        Componentes.agregarColumna(tablaClientes, "Correo", Cliente::getCorreo);
        Componentes.agregarColumna(tablaClientes, "Saldo", c -> Formato.moneda(c.getTarjeta().getSaldo()));
        Componentes.agregarColumna(tablaClientes, "Tarjeta", c -> c.getTarjeta().getEstado());
        Componentes.agregarColumna(tablaClientes, "Puntos", controller::puntos);
        Componentes.configurarTabla(tablaClientes);
        Componentes.llenarTabla(tablaClientes, controller.clientes());

        TableView<SolicitudRecarga> tablaSolicitudes = new TableView<>();
        Componentes.agregarColumna(tablaSolicitudes, "Cliente", s -> s.getCliente().getNombre());
        Componentes.agregarColumna(tablaSolicitudes, "Monto", s -> Formato.moneda(s.getMonto()));
        Componentes.agregarColumna(tablaSolicitudes, "Estado", SolicitudRecarga::getEstado);
        Componentes.configurarTabla(tablaSolicitudes);
        Componentes.llenarTabla(tablaSolicitudes, controller.solicitudesPorAtender());

        TextField txtMonto = new TextField();
        txtMonto.setPromptText("Entre 10.000 y 2.000.000");

        Button btnRecargar = Componentes.botonPrincipal("Recargar");
        btnRecargar.setOnAction(e -> {
            Cliente c = tablaClientes.getSelectionModel().getSelectedItem();
            try {
                controller.recargar(c, txtMonto.getText());
                txtMonto.clear();
                Componentes.llenarTabla(tablaClientes, controller.clientes());
                Mensajes.informacion("Recarga realizada.");
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        Button btnAprobar = Componentes.botonPrincipal("Aprobar");
        btnAprobar.setOnAction(e -> {
            try {
                controller.aprobar(tablaSolicitudes.getSelectionModel().getSelectedItem());
                Componentes.llenarTabla(tablaSolicitudes, controller.solicitudesPorAtender());
                Componentes.llenarTabla(tablaClientes, controller.clientes());
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });
        Button btnRechazar = Componentes.botonPeligro("Rechazar");
        btnRechazar.setOnAction(e -> {
            try {
                controller.rechazar(tablaSolicitudes.getSelectionModel().getSelectedItem());
                Componentes.llenarTabla(tablaSolicitudes, controller.solicitudesPorAtender());
            } catch (CinemaException ex) {
                Mensajes.error(ex.getMessage());
            }
        });

        VBox acciones = Componentes.tarjeta(Componentes.subtitulo("Recargar tarjeta del cliente seleccionado"),
                Componentes.fila(txtMonto, btnRecargar),
                Componentes.subtitulo("Solicitudes de recarga"), tablaSolicitudes,
                Componentes.fila(btnAprobar, btnRechazar));
        VBox listado = Componentes.tarjeta(Componentes.subtitulo("Clientes"), tablaClientes);
        return Componentes.seccion("Clientes y recargas", formularioYTabla(acciones, listado));
    }

    private VBox crearCompras() {
        TableView<Compra> tabla = new TableView<>();
        Componentes.agregarColumna(tabla, "Código", Compra::getId);
        Componentes.agregarColumna(tabla, "Fecha", c -> Formato.fechaHora(c.getFecha()));
        Componentes.agregarColumna(tabla, "Cliente", c -> c.getCliente().getNombre());
        Componentes.agregarColumna(tabla, "Película", c -> c.getFuncion().getPelicula().getTitulo());
        Componentes.agregarColumna(tabla, "Asientos", Compra::getCodigosAsientos);
        Componentes.agregarColumna(tabla, "Total", c -> Formato.moneda(c.getTotal()));
        Componentes.agregarColumna(tabla, "Puntos +/-", c -> "+" + c.getPuntosGanados() + " / -" + c.getPuntosUsados());
        Componentes.agregarColumna(tabla, "Estado", Compra::getNombreEstado);
        Componentes.configurarTabla(tabla);
        Componentes.llenarTabla(tabla, controller.compras());

        VBox tarjeta = Componentes.tarjeta(tabla);
        VBox.setVgrow(tarjeta, Priority.ALWAYS);
        return Componentes.seccion("Compras", tarjeta);
    }
}
