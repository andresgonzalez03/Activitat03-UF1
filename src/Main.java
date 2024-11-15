package src;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Main {

    public static ArrayList<Encarrec> encargos = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        mostrarMenu();
    }
    private static void mostrarMenu() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            System.out.println("Selecciona una opción:");
            System.out.println("1. Generar un nou encàrrec");
            System.out.println("2. Mostrar un encàrrec");
            System.out.println("3. Sortir");
            String resposta = reader.readLine();
    
            switch(resposta) {
                case "1" -> {
                    generaEncarrecs();
                }
                case "2" -> mostraEncarrecs();
                case "3" -> {
                    System.out.println("Adéu");
                    return;
                }
                default -> System.out.println("Opció no vàlida. Tria 1, 2 o 3");
            }
        }
    }
    private static void demanaDadesUsuari(Encarrec encarrec) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                while(true) {
                    System.out.println("Introdueix el seu nom:");
                    try {
                        String nom = reader.readLine();
                        encarrec.setNomClient(nom);
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                while (true) {
                    try {
                        System.out.println("Introdueix el seu telèfon:");
                        String telefon = reader.readLine();
                        encarrec.setTelefonClient(telefon);
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                LocalDate data = null;
                while(data == null) {
                    try{
                        System.out.println("La data en la que vol l'encàrrec (dd/MM/yyyy):");
                        String dataStr = reader.readLine();
                        encarrec.setData(encarrec.formatejarData(dataStr));
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            } catch (IOException e) {
                System.out.println("Error al llegir les dades, torna-ho a provar");
                e.printStackTrace();
            }
        }
    }
    private static void demanaArticles(Encarrec encarrec) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
        while (true) {
            String nomArticle = "";
            while(true) {
                System.out.println("Introdueix el nom de l'article:");
                try {
                    nomArticle = UtilString.normalitzaString(reader.readLine());
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            double quantitat = 0;

            while (true) {
                System.out.println("Introdueix la quantitat que vulguis:");
                try {
                    quantitat = Double.parseDouble(reader.readLine());
                    if(quantitat <= 0) {
                        throw new IllegalArgumentException("Has d'indicar almenys una unitat");
                    } 
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Siusplau, indica un número vàlid");

                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            Unitat unitat = null;
            while (true) {
                System.out.println("Introdueix una unitat: Kilogram (Kg), Litre (L), Unitat (Ud), Ampolles (ampolles) o Gram (G)");
                try {
                    unitat = Unitat.fromString(reader.readLine()); 
                    break; 
                } catch (IllegalArgumentException e) {
                   System.out.println(e.getMessage());
                }
            }
            double preu = 0;
            while(true) {
                System.out.println("Introdueix un preu per l'article:");
                try {
                    preu = Double.parseDouble(reader.readLine());
                    if(preu <= 0) {
                        throw new IllegalArgumentException("Siusplau, el preu ha de ser major a 0");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            Article article = new Article(nomArticle, unitat, quantitat, preu);
            ArrayList<Article> articles = encarrec.getArticles();
            articles.add(article);

            System.out.println("Article afegit!\n");

            System.out.println("Vols introduir un altre article? (sí o no)");
            String resposta = reader.readLine();
            if (resposta.equalsIgnoreCase("no")) {
                System.out.println();
                break;
            }
        }  
    }
    private static void mostraEncarrecs() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Vols cercar un encàrrec per nom?  (sí o no)");
        String resposta = reader.readLine();
        if(resposta.equals("sí") || resposta.equals("si")) {
            System.out.println("Indica la ruta de l'arxiu que vols veure:");
            String ruta = reader.readLine();
            System.out.println("Ara, introdueix el nom del client:");
            String nomClient = reader.readLine();
             Gestor.readSAX(ruta, nomClient);
        } else {
            System.out.println("Llavors, de quina manera vols llegir els encàrrecs, 1 (DOM) o 2 (SAX)?");
            String r = reader.readLine();
            if(r.equals("1")) {
                System.out.println("Indica la ruta de l'arxiu que vols veure:");
                String ruta = reader.readLine();
                Gestor.readDOM(ruta);
            } else if(r.equals("2")) {
                System.out.println("Indica la ruta de l'arxiu que vols veure:");
                String ruta = reader.readLine();
                Gestor.readSAX(ruta, null); 
            } else {
                System.out.println("Opció no vàlida. Si us plau, tria 1 o 2.");
            }
        }
        
    }
    private static void generaEncarrecs() throws Exception {
        ArrayList<Encarrec> encarrecs = new ArrayList<>();
        Encarrec.reiniciarId();
        while(true) {
            Encarrec encarrec = new Encarrec();
            demanaDadesUsuari(encarrec);
            demanaArticles(encarrec);
            encarrecs.add(encarrec);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Vols generar un altre encàrrec? (sí o no)");
            String resposta = reader.readLine();
            if(resposta.equalsIgnoreCase("no")) {
                Gestor.writeDOM(encarrecs);
                break;
            }
        }
    }
}