package src;
public class UtilString {
    // mètode que normalitza un String (usualment un nom) i li treu els espais en blanc, convertint-los només en un espai en blanc
    public static String normalitzaString(String cadena) {
        if (cadena == null || cadena.strip().isEmpty()) {
            throw new IllegalArgumentException("La cadena no és vàlida, sisplau ingresa una altra cadena");
        }
        if (!cadena.matches("[\\p{L} ]+")) {
            throw new IllegalArgumentException("La cadena ha de contenir només lletres i espais");
        }
        return cadena.replaceAll("\\s+", " ").strip();
    }
}
    
