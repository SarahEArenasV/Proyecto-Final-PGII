package co.edu.uniquindio.poo.servicios;

import co.edu.uniquindio.poo.enums.FormatoReporte;
import co.edu.uniquindio.poo.exceptions.DatosInvalidosException;
import co.edu.uniquindio.poo.model.Reporte;
import co.edu.uniquindio.poo.util.Formato;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExportadorReportes {
    private static final int ANCHO_MAX = 28;
    private static final Charset WIN1252 = Charset.forName("windows-1252");
    private static final double ANCHO_UTIL_PDF = 742;
    private static final double ANCHO_CARACTER_PDF = 0.6;
    private static final int FUENTE_MAXIMA_PDF = 9;

    public void exportar(Reporte reporte, FormatoReporte formato, File destino) throws IOException {
        if (reporte == null) throw new DatosInvalidosException("Primero genere un reporte");
        if (formato == null) throw new DatosInvalidosException("Seleccione el formato del reporte");
        if (destino == null) throw new DatosInvalidosException("Seleccione el archivo de destino");
        Path ruta = destino.toPath();
        if (ruta.getParent() != null) Files.createDirectories(ruta.getParent());
        List<String> lineas = aLineas(reporte);
        if (formato == FormatoReporte.TXT) {
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } else {
            Files.write(ruta, generarPdf(lineas, tamanoFuente(lineas)));
        }
    }

    private static List<String> aLineas(Reporte r) {
        int columnas = r.encabezados().size();
        int[] anchos = new int[columnas];
        for (int i = 0; i < columnas; i++) anchos[i] = r.encabezados().get(i).length();
        for (List<String> fila : r.filas())
            for (int i = 0; i < columnas; i++) anchos[i] = Math.max(anchos[i], fila.get(i).length());
        for (int i = 0; i < columnas; i++) anchos[i] = Math.min(anchos[i], ANCHO_MAX);

        List<String> lineas = new ArrayList<>();
        String titulo = "CINEMAUQ - " + r.titulo().toUpperCase();
        int anchoTotal = Math.max(titulo.length(), Arrays.stream(anchos).sum() + 3 * columnas);
        String separador = "=".repeat(anchoTotal);
        lineas.add(separador);
        lineas.add(titulo);
        lineas.add(separador);
        lineas.add("Fecha de generación: " + Formato.fechaHora(r.fechaGeneracion()));
        lineas.add("Periodo consultado:  " + Formato.fecha(r.desde()) + " - " + Formato.fecha(r.hasta()));
        lineas.add("Registros encontrados: " + r.getCantidadRegistros());
        lineas.add("-".repeat(anchoTotal));
        lineas.add(fila(r.encabezados(), anchos));
        lineas.add("-".repeat(anchoTotal));
        if (r.filas().isEmpty()) lineas.add("No se encontraron registros para el periodo.");
        for (List<String> f : r.filas()) lineas.add(fila(f, anchos));
        lineas.add("-".repeat(anchoTotal));
        lineas.add("TOTALES");
        for (String t : r.totales()) lineas.add("  " + t);
        lineas.add(separador);
        return lineas;
    }

    private static String fila(List<String> celdas, int[] anchos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < anchos.length; i++) {
            String c = celdas.get(i);
            if (c.length() > anchos[i]) c = c.substring(0, anchos[i] - 1) + "…";
            sb.append(String.format("%-" + anchos[i] + "s", c));
            if (i < anchos.length - 1) sb.append(" | ");
        }
        return sb.toString();
    }

    private static int tamanoFuente(List<String> lineas) {
        int maxLongitud = lineas.stream().mapToInt(String::length).max().orElse(80);
        return (int) Math.min(FUENTE_MAXIMA_PDF, Math.floor(ANCHO_UTIL_PDF / (ANCHO_CARACTER_PDF * Math.max(1, maxLongitud))));
    }

    private static byte[] generarPdf(List<String> lineas, int tamanoFuente) {
        int interlineado = tamanoFuente + 3;
        int lineasPorPagina = 555 / interlineado;
        List<List<String>> paginas = new ArrayList<>();
        for (int i = 0; i < Math.max(1, lineas.size()); i += lineasPorPagina) {
            paginas.add(lineas.subList(Math.min(i, lineas.size()), Math.min(i + lineasPorPagina, lineas.size())));
        }
        List<byte[]> objetos = new ArrayList<>();
        int n = paginas.size();

        objetos.add(ascii("<< /Type /Catalog /Pages 2 0 R >>"));
        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < n; i++) kids.append(4 + i * 2).append(" 0 R ");
        objetos.add(ascii("<< /Type /Pages /Kids [" + kids + "] /Count " + n + " >>"));
        objetos.add(ascii("<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"));
        for (int i = 0; i < n; i++) {
            int contenidoId = 5 + i * 2;
            objetos.add(ascii("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 792 612] /Resources << /Font << /F1 3 0 R >> >> /Contents "
                    + contenidoId + " 0 R >>"));
            ByteArrayOutputStream flujo = new ByteArrayOutputStream();
            escribir(flujo, ascii("BT /F1 " + tamanoFuente + " Tf 25 585 Td " + interlineado + " TL\n"));
            for (String linea : paginas.get(i)) {
                escribir(flujo, ascii("("));
                escribir(flujo, escapar(linea));
                escribir(flujo, ascii(") Tj T*\n"));
            }
            escribir(flujo, ascii("ET"));
            byte[] contenido = flujo.toByteArray();
            ByteArrayOutputStream obj = new ByteArrayOutputStream();
            escribir(obj, ascii("<< /Length " + contenido.length + " >>\nstream\n"));
            escribir(obj, contenido);
            escribir(obj, ascii("\nendstream"));
            objetos.add(obj.toByteArray());
        }
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        escribir(pdf, ascii("%PDF-1.4\n"));
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objetos.size(); i++) {
            offsets.add(pdf.size());
            escribir(pdf, ascii((i + 1) + " 0 obj\n"));
            escribir(pdf, objetos.get(i));
            escribir(pdf, ascii("\nendobj\n"));
        }
        int xref = pdf.size();
        StringBuilder sb = new StringBuilder("xref\n0 " + (objetos.size() + 1) + "\n0000000000 65535 f \n");
        for (int off : offsets) sb.append(String.format("%010d 00000 n \n", off));
        sb.append("trailer\n<< /Size ").append(objetos.size() + 1).append(" /Root 1 0 R >>\nstartxref\n")
          .append(xref).append("\n%%EOF");
        escribir(pdf, ascii(sb.toString()));
        return pdf.toByteArray();
    }

    private static byte[] escapar(String texto) {
        String t = texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)").replace("…", "...");
        return t.getBytes(WIN1252);
    }

    private static byte[] ascii(String s) { return s.getBytes(WIN1252); }

    private static void escribir(ByteArrayOutputStream out, byte[] datos) { out.write(datos, 0, datos.length); }
}
