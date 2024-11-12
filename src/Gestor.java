package src;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;


public class Gestor {
    private static final String PATH = System.getProperty("user.home");
    public static void crearDirectoris() throws IOException {
        String directoris[] = {PATH + "/Serializable/",PATH + "/fitxersRandom"};
        File carpeta[] = new File[directoris.length];
        for (int i = 0; i < carpeta.length; i++) {
            carpeta[i] = new File(directoris[i]);
        }
        for(File f : carpeta) {
            if(!f.exists()) {
                if(f.mkdirs()) {
                    System.out.println("Directori creat: " + f.getPath());
                } else {
                    System.out.println("No s'ha pogut fer el directori: " + f.getPath());
                }
            } else {
                System.out.println("El directori ja existeix: " + f.getPath());
            }
        }
    }
    public static void EscriureEncarrecRandom(ArrayList<Encarrec> encarrecs) throws IOException {
        crearDirectoris();
        String ruta = PATH + "/fitxersRandom/";
        String nomArxiu = ruta + "encarrecs_client_" + new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss").format(new Date()) + ".bin";
        try(RandomAccessFile random = new RandomAccessFile(nomArxiu, "rw")) {
            for(Encarrec encarrec: encarrecs) {
                random.writeInt(encarrec.getId()); // 4
                random.writeChars(String.format("%-50s", encarrec.getNomClient())); // 100
                random.writeChars(String.format("%-9s", encarrec.getTelefonClient())); // 18
                random.writeChars(String.format("%-10s", encarrec.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))); // 20
                ArrayList<Article> articles = encarrec.getArticles();
                random.writeInt(articles.size()); // 4

                for(Article article: articles) {
                    random.writeChars(String.format("%-50s", article.getNom()));
                    random.writeChars(String.format("%-9s", article.getUnitat().toString()));
                    random.writeDouble(article.getQuantitat());
                    random.writeDouble(article.getPreu());
                }
            }
            System.out.println("S'ha creat el fitxer amb nom: " + nomArxiu + "\n");
            System.out.println("Mostrant el fitxer binari generat...\n");
            LlegirBinariRandom(nomArxiu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    public static void modificarEncarrec(String ruta, int id) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try (RandomAccessFile random = new RandomAccessFile(ruta, "rw")) {
            boolean trobat = false;
            while (random.getFilePointer() < random.length()) {
                int idActual = random.readInt();
                long posicio = random.getFilePointer();
                if(idActual == id) {
                    trobat = true;
                    posicio += 100;
                    random.seek(posicio);
                    System.out.println("Introdueix un nou telèfon:");
                    String telefon = reader.readLine();
                    System.out.println("Introdueix una nova data:");
                    String data = reader.readLine();

                    random.writeChars(String.format("%-9s", telefon));
                    random.writeChars(String.format("%-10s", data));
                    System.out.println("\nS'ha modificat l'encàrrec correctament\n");
                    break;
                } else {
                    random.skipBytes(100+18+20);
                    int articlesSize = random.readInt();
                    random.skipBytes(articlesSize * (100 + 18 + 8 + 8));
                } 
            }
            System.out.println("Mostrant el fitxer binari modificat...\n");
            LlegirBinariRandom(ruta);
            if(!trobat) {
                System.out.println("No s'ha trobat cap encàrrec amb ID: "+ id);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void LlegirBinariRandom(String ruta) throws IOException {
        try (RandomAccessFile random = new RandomAccessFile(ruta, "r")) {
            while (random.getFilePointer() < random.length()) {
                int id = random.readInt();
                String nomClient = readString(random, 50);
                String telefonClient = readString(random, 9);
                String dataString = readString(random, 10);
                LocalDate data = LocalDate.parse(dataString, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                int articlesCount = random.readInt();
                ArrayList<Article> articles = new ArrayList<>();

                for (int i = 0; i < articlesCount; i++) {
                    String articleNom = readString(random, 50); 
                    String unitat = readString(random, 9);
                    double quantitat = random.readDouble();
                    double preu = random.readDouble();

                    Article article = new Article(articleNom, Unitat.fromString(unitat), quantitat, preu);
                    articles.add(article);
                }

                Encarrec encarrec = new Encarrec(id, nomClient, telefonClient, data, articles);
                encarrec.calcularPreuTotal();
                System.out.println(generarAlbara(encarrec));
            }
        } catch (EOFException e) {
            System.out.println("Se ha alcanzado el final del archivo.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void EscriureEncarrecSerialitzable(ArrayList<Encarrec> encarrecs) throws IOException {
        crearDirectoris();
        String ruta = PATH + "/Serializable/";
        String nomArxiu = ruta + "encarrecs_client_" + new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss").format(new Date(System.currentTimeMillis())) + ".bin";
        try(ObjectOutputStream serializador = new ObjectOutputStream(new FileOutputStream(nomArxiu))) {
            serializador.writeObject(encarrecs);
            System.out.println("S'ha creat el fitxer amb nom: " + nomArxiu);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    @SuppressWarnings("unchecked")
    public static void LlegirEncarrecSerialitzable(String ruta) {
        try(ObjectInputStream deserializador = new ObjectInputStream(new FileInputStream(ruta))) {
            ArrayList<Encarrec> encarrecs = (ArrayList<Encarrec>) deserializador.readObject();
            for(Encarrec encarrec: encarrecs) {
                encarrec.calcularPreuTotal();
                System.out.println(generarAlbara(encarrec));
            }
        } catch(FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static String generarAlbara(Encarrec encarrec) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nId: " + encarrec.getId() +"\n");
        sb.append("Nom del client: " + encarrec.getNomClient() + "\n");
        sb.append("Telefon del client: " + encarrec.getTelefonClient() + "\n");
        sb.append("Data de l'encàrrec: " + encarrec.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n");
        sb.append(String.format("%-10s %-10s %-10s %-10s%n", "Quantitat", "Unitats", "Article", "Preu"));
        sb.append(String.format("%-10s %-10s %-10s %-10s%n", "=========", "=======", "=======", "===="));
        for (Article a : encarrec.getArticles()) {
            sb.append(String.format("%-10s %-10s %-10s %-10s%n", a.getQuantitat(), a.getUnitat(), a.getNom(), a.getPreu()));
        }
        sb.append("\nPreu total: " + encarrec.getPreuTotal() + "€\n");
        return sb.toString();
    }
    private static String readString(RandomAccessFile random, int pos) throws IOException {
        StringBuilder sb = new StringBuilder(pos);
        for(int i = 0; i < pos; i++) {
            sb.append(random.readChar());
        }
        return sb.toString();
    }
}