package src;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*; 
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;


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
    public static void writeDOM(ArrayList<Encarrec> encargos) throws Exception {
        crearDirectori();
        String ruta = PATH + "/XML/";
        String nomArxiu = ruta + "encarrecs_client_" + new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss").format(new Date(System.currentTimeMillis())) + ".xml";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            Collections.sort(encargos, new Comparator<Encarrec>() {
                @Override
                public int compare(Encarrec e1, Encarrec e2) {
                    return e1.getNomClient().compareTo(e2.getNomClient()); // Ordenar alfabéticamente por nombre de cliente
                }
            });
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
            System.out.println("XML generat correctament a la ruta: " + nomArxiu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void readDOM(String ruta) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(ruta);
            NodeList llistaEncarrecs = document.getElementsByTagName("encarrec");

            ArrayList<Encarrec> encarrecs = new ArrayList<>();
            for(int i = 0; i < llistaEncarrecs.getLength(); i++) {
                Node nodeEncarrec = llistaEncarrecs.item(i);
                if(nodeEncarrec.getNodeType() == Node.ELEMENT_NODE) {
                    Element elementEncarrec = (Element) nodeEncarrec;
                    int id = Integer.parseInt(elementEncarrec.getAttribute("id"));
                    String nomClient = elementEncarrec.getElementsByTagName("nombre").item(0).getTextContent();
                    String telefonClient = elementEncarrec.getElementsByTagName("telefono").item(0).getTextContent();
                    LocalDate data = LocalDate.parse(elementEncarrec.getElementsByTagName("fecha").item(0).getTextContent(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

                    ArrayList<Article> articles = new ArrayList<>();
                    NodeList llistaArticles = elementEncarrec.getElementsByTagName("article");

                    for(int j = 0; j < llistaArticles.getLength(); j++) {
                        Node nodeArticle = llistaArticles.item(j);
                        if(nodeArticle.getNodeType() == Node.ELEMENT_NODE) {
                            Element elementArticle = (Element) nodeArticle;
                            String nomArticle = elementArticle.getElementsByTagName("nombre_Articulo").item(0).getTextContent();
                            Unitat unitat = Unitat.fromString(elementArticle.getElementsByTagName("unitat").item(0).getTextContent());
                            Double quantitat = Double.parseDouble(elementArticle.getElementsByTagName("quantitat").item(0).getTextContent());
                            Double preu = Double.parseDouble(elementArticle.getElementsByTagName("preu").item(0).getTextContent());
                            Article article = new Article(nomArticle, unitat, quantitat, preu);
                            articles.add(article);
                        }
                    }
                    Encarrec encarrec = new Encarrec(id, nomClient, telefonClient, data, articles);
                    encarrecs.add(encarrec);
                }
            }
            for(Encarrec encarrec : encarrecs) {
                System.out.println(generarAlbara(encarrec));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void crearElement(String dadaEmpleat, String valor, Element arrel, Document document) {
        Element element = document.createElement(dadaEmpleat);
        Text text = document.createTextNode(valor);
        element.appendChild(text);
        arrel.appendChild(element);
    }
    public static void readSAX(String ruta, String nomClient) throws Exception {
        SAXParserFactory saxpf = SAXParserFactory.newInstance();
        SAXParser parser = saxpf.newSAXParser();
        XMLReader procesadorXML = parser.getXMLReader();
        GestioContingut gestor = new GestioContingut(nomClient);
        procesadorXML.setContentHandler(gestor);
        InputSource fileXML = new InputSource(ruta);
        procesadorXML.parse(fileXML);
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
}
class GestioContingut extends DefaultHandler {
    private StringBuilder contingut;
    private int id;
    private String nomClient;
    private String telefonClient;
    private LocalDate data;
    private ArrayList<Article> articles;
    private ArrayList<Encarrec> encarrecs;

    private String nombreArticulo;
    private Unitat unitat;
    private double quantitat;
    private double preu;

    private String nomClientXML;

    public GestioContingut(String nombre) {
        this.nomClient = nombre;
        this.contingut = new StringBuilder();
        this.encarrecs = new ArrayList<>();
    }
    @Override
    public void startElement (String uri, String nom, String nomC, Attributes atts) {
        this.contingut.setLength(0);
        if (nomC.equals("encarrec")) {
            this.id = Integer.parseInt(atts.getValue("id"));
            this.articles = new ArrayList<>();
        }
    }
    @Override
    public void characters(char[] ch, int inicio, int longitud) throws SAXException {
        this.contingut.append(ch, inicio, longitud);
    }
    @Override
    public void endElement(String uri, String nom, String nomC) {
        switch (nomC) {
            case "nombre":
                this.nomClientXML = contingut.toString();
                break;
            case "telefono":
                this.telefonClient = contingut.toString();
                break;
            case "fecha":
                this.data = LocalDate.parse(contingut.toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                break;
            case "nombre_Articulo":
                this.nombreArticulo = contingut.toString();
                break;
            case "unitat":
                this.unitat = Unitat.fromString(contingut.toString());
                break;
            case "quantitat":
                this.quantitat = Double.parseDouble(contingut.toString());
                break;
            case "preu":
                this.preu = Double.parseDouble(contingut.toString());
                break;
            case "article":
                Article article = new Article(nombreArticulo, unitat, quantitat, preu);
                this.articles.add(article);
                break;
            case "encarrec":
                if (this.nomClient != null && !this.nomClient.isEmpty()) {
                    if (this.nomClientXML.equals(this.nomClient)) {
                        Encarrec encarrec = new Encarrec(id, nomClientXML, telefonClient, data, articles);
                        this.encarrecs.add(encarrec);
                    }
                } else {
                    Encarrec encarrec = new Encarrec(id, nomClientXML, telefonClient, data, articles);
                    this.encarrecs.add(encarrec);
                }
                break;
        }
    }
    public void endDocument() {
        for(Encarrec e: encarrecs) {
            System.out.println(Gestor.generarAlbara(e));
        }
    }
}