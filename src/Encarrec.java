package src;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Encarrec implements Serializable {
    private final int id;
    private static int nextId = 1;
    private String nomClient;
    private String telefonClient;
    private LocalDate data;
    private ArrayList<Article> articles;
    private double preuTotal;

    public Encarrec() {
        this.id = nextId++;
        this.articles = new ArrayList<>();
        this.nomClient = "";
        this.telefonClient = "";
        this.data = LocalDate.now(); 
        this.preuTotal = 0.0; 
    }
    public Encarrec(int id, String nomClient, String telefonClient, LocalDate data, ArrayList<Article> articles) {
        this.id = id;
        setNomClient(nomClient);
        setTelefonClient(telefonClient);
        setData(data);
        setArticles(articles);
    }
    public int getId() {return id;}
    public void setData(LocalDate data) {
        if (data == null) {
        } else if (!data.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Has d'indicar una data posterior a la actual");
        } else {
            this.data = data;
        }
    }
    public ArrayList<Article> getArticles() {return articles;}
    public LocalDate getData() {return data;}
    public void setArticles(ArrayList<Article> articles) {
        if(articles == null || articles.isEmpty()) {
            throw new IllegalArgumentException("Com a mínim pots fer encàrrecs d'un article.\n Siusplau, intenta-ho de nou");
        }
        this.articles = new ArrayList<>(articles);
        calcularPreuTotal();
    }
    public String getNomClient() {return nomClient;}
    public String getTelefonClient() {return telefonClient;}
    public LocalDate formatejarData(String data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            return LocalDate.parse(data, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Siusplau, indica una data correcta en format dd/mm/aaaa");
        }
    }
    public void setNomClient(String nomClient) {
        this.nomClient = UtilString.normalitzaString(nomClient);
    }
    public void setTelefonClient(String telefonClient) {
        if (telefonClient == null || telefonClient.length() != 9) {
            throw new IllegalArgumentException("El telèfon ha de ser de nou dígits");
        }
        for(int i = 0; i < telefonClient.length(); i++) {
            if(!Character.isDigit(telefonClient.charAt(i))) {
                throw new IllegalArgumentException("El telèfon només pot contenir números");
            }
        }
        this.telefonClient = telefonClient;
    }
    public double getPreuTotal() {return preuTotal;}
    public void calcularPreuTotal() {
        this.preuTotal = 0;
        for(Article article : articles) {
            this.preuTotal += article.getPreu() * article.getQuantitat();
        }
    }
    public static void reiniciarId() {
        nextId = 1;
    }
    @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Encàrrec ID: ").append(id).append("\n");
    sb.append("Nom del client: ").append(nomClient).append("\n");
    sb.append("Telèfon del client: ").append(telefonClient).append("\n");
    sb.append("Data: ").append(data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
    sb.append("Preu total: ").append(String.format("%.2f", preuTotal)).append("\n");
    sb.append("Articles: \n");

    for (Article article : articles) {
        sb.append(" - ").append(article.toString()).append("\n"); // Asumiendo que la clase Article tiene su propio toString()
    }

    return sb.toString();
}

}