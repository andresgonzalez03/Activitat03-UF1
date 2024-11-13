package src;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*; 
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.util.Date;


public class Gestor {
    private static final String PATH = System.getProperty("user.home");
    public static void crearDirectori() throws IOException {
        File carpeta = new File(PATH + "/XML");
        if(!carpeta.exists()) {
            if(carpeta.mkdirs()) {
                System.out.println("Directori creat: " + carpeta.getPath());
            } else {
                System.out.println("No s'ha pogut fer el directori: " + carpeta.getPath());
            }
        } else {
            System.out.println("El directori ja existeix: " + carpeta.getPath());
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
    public static void writeDOM(ArrayList<Encarrec> encargos) throws Exception {
        crearDirectori();
        String ruta = PATH + "/Serializable/";
        String nomArxiu = ruta + "encarrecs_client_" + new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss").format(new Date(System.currentTimeMillis())) + ".xml";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document document = implementation.createDocument(null, "encarrecs", null);
            document.setXmlVersion("1.0");

            for(Encarrec emp : encargos) {
                Element encarrec = document.createElement("encarrec");
                encarrec.setAttribute("id", Integer.toString(emp.getId()));
                document.getDocumentElement().appendChild(encarrec);

                crearElement("nombre", emp.getNomClient(), encarrec, document);
                crearElement("telefono", emp.getTelefonClient(), encarrec, document);
                crearElement("fecha", emp.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), encarrec, document);

                Element articles = document.createElement("articles"); // crear element articles
                encarrec.appendChild(articles); // afegir etiqueta articles dins d'encarrec

                for(Article a : emp.getArticles()) {
                    Element article = document.createElement("article"); // crear etiqueta article
                    articles.appendChild(article); // afegir etiqueta article dins d'articles.
                    crearElement("nombre_Articulo", a.getNom(), article, document);
                    crearElement("unitat", a.getUnitat().toString(), article, document);
                    crearElement("quantitat", Double.toString(a.getQuantitat()), article, document);
                    crearElement("preu", Double.toString(a.getPreu()), article, document);
                    
                }
                emp.calcularPreuTotal();
                crearElement("precioTotal", emp.getPreuStr(), encarrec, document);
            }
            Source source = new DOMSource(document);
            Result result = new StreamResult(new FileWriter(nomArxiu));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
            transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "5");
            transformer.transform (source, result);
            System.out.println("XML generat correctament");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String readDOM(String ruta) throws Exception {
        ArrayList<Encarrec> encargos = new ArrayList<>();

        String albaran = "";
        for(Encarrec encarrec : encargos) {
            albaran+=generarAlbara(encarrec);
        }
        return albaran;
    }

    public static String readSAX(String ruta, boolean varios) throws Exception {
        String albaran = "";
        
        if(varios) { // especifica un unico cliente 

        } else {
            // leemos todos los encargos
        }

        return albaran;
    }
    public static void crearElement(String dadaEmpleat, String valor, Element arrel, Document document) {
        Element element = document.createElement(dadaEmpleat);
        Text text = document.createTextNode(valor);
        element.appendChild(text);
        arrel.appendChild(element);
    }
}