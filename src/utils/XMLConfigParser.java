package utils;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;

public class XMLConfigParser {
    public static Configurazione leggiConfigurazione(String filepath) throws Exception {
    // Parse del file XML
    File xmlFile = new File(filepath);
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse(xmlFile);
    doc.getDocumentElement().normalize();
    
    // Crea oggetto Configurazione
    Configurazione config = new Configurazione();
    
    // Leggi sezione server
    config.setHostServer(getTagValue("host", doc));
    config.setPortaServer(Integer.parseInt(getTagValue("porta", doc)));
    config.setMaxThread(Integer.parseInt(getTagValue("maxThread", doc)));
    
    // Leggi sezione DATABASE
    config.setDbUrl(getTagValue("url", doc));
    config.setDbUsername(getTagValue("username", doc));
    config.setDbPassword(getTagValue("password", doc));
    
    // Leggi sezione prestito
    config.setDurataPrestitoLibro(Integer.parseInt(getTagValue("DurataGiorniPerLibro", doc)));
    config.setDurataPrestitoRivista(Integer.parseInt(getTagValue("DurataGiorniPerRivista", doc)));
    config.setMaxPrestitiPerUtente(Integer.parseInt(getTagValue("MaxPerUtente", doc)));
    
    // Leggi sezione penali
    config.setPenaleGiornalieraLibro(Double.parseDouble(getTagValue("PenaleLibGiorno", doc)));
    config.setPenaleGiornalieraRivista(Double.parseDouble(getTagValue("PenaleRivGiorno", doc)));
    
    return config;
}
    // Metodo helper per estrarre il valore di un tag
    private static String getTagValue(String tag, Document doc) {
        NodeList nodeList = doc.getElementsByTagName(tag);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return null;
    }
}