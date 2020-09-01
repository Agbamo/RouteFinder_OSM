import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URL;

public class Util {
	public static EspacioDeEstados coordenadasXML(File fichero) {
		EspacioDeEstados espacioEstados = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document xml = dBuilder.parse(fichero);

			xml.getDocumentElement().normalize();

			NodeList bounds = xml.getElementsByTagName("bounds");

			System.out.println("----------------------------");

			for (int i = 0; i < bounds.getLength(); i++) {

				Node nodo = bounds.item(i);

				if (nodo.getNodeType() == Node.ELEMENT_NODE) {

					Element elemento = (Element) nodo;
					
					double minLat = (Double.parseDouble(elemento.getAttribute("minlat")));
					double minLon = (Double.parseDouble(elemento.getAttribute("minlon")));
					double maxLat = (Double.parseDouble(elemento.getAttribute("maxlat")));
					double maxLon = (Double.parseDouble(elemento.getAttribute("maxlon")));
					espacioEstados = new EspacioDeEstados(minLat,minLon,maxLat,maxLon);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return espacioEstados;
	}
	
	public static void URL() {
	    try {
	      Desktop escritorio = Desktop.getDesktop();
	      escritorio.browse(new URI("www.google.es"));
	    }
	    catch (Exception err) {
	      err.printStackTrace();
	    }
	   
	  }



}