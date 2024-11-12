package src;
/* Classe que conté les propietats d'un article dintre d'una comanda d'un client. */

import java.io.Serializable;

public class Article implements Serializable {
    private String nom;
    private Unitat unitat;
    private double quantitat;
    private double preu;

    public Article (String nom, Unitat unitat, double quantitat, double preu) {
        this.nom = UtilString.normalitzaString(nom);
        this.unitat = unitat;
        this.quantitat = quantitat;
        this.preu = preu;
    }
    public String getNom() {return nom;}
    public Unitat getUnitat() {return unitat;}
    public double getQuantitat() {return quantitat;}
    public double getPreu() {return preu;}
    public String toString() {
        return String.format("%.2f\t%s\t%s\t%.2f", quantitat, unitat, nom, preu);
    }
    public String toCSV() {
        return nom + ";" + quantitat + ";" + unitat + ";";
    }
}
