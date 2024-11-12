package src;
public enum Unitat {
    KILO("Kg"),
    LITRE("L"),
    UNITAT("Ud"),
    AMPOLLES("ampolles"),
    GRAM("G");

    private final String nom;

    Unitat(String nom) {
        this.nom = nom;
    }
    @Override
    public String toString() {
        return nom;
    }
    // mètode per convertir una cadena de String a una unitat
    public static Unitat fromString(String nom) {
        if(nom == null || nom.isBlank()) return null;
        nom = UtilString.normalitzaString(nom);
        for(Unitat unitat : Unitat.values()) {
            if(unitat.nom.equalsIgnoreCase(nom)) {
                return unitat;
            }
        } 
        throw new IllegalArgumentException("Indica una unitat, ha de ser Kilogram (Kg), Litre (L), Unitat (Ud), Ampolles (ampolles) o Gram (G). Valor rebut: " + nom);
    }
}
