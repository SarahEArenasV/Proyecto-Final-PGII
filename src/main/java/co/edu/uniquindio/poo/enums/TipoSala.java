package co.edu.uniquindio.poo.enums;

public enum TipoSala {
    GENERAL_2D("General 2D"), SALA_3D("3D"), VIP("VIP");
    private final String etiqueta;
    TipoSala(String etiqueta) { this.etiqueta = etiqueta; }
    @Override public String toString() { return etiqueta; }
}
