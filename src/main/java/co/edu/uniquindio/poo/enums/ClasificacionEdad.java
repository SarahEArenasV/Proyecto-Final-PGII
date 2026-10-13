package co.edu.uniquindio.poo.enums;

public enum ClasificacionEdad {
    TODO_PUBLICO("T"), MAYORES_7("+7"), MAYORES_12("+12"), MAYORES_15("+15"), MAYORES_18("+18");
    private final String sigla;
    ClasificacionEdad(String sigla) { this.sigla = sigla; }
    @Override public String toString() { return sigla; }
}
