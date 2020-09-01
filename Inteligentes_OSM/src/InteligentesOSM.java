import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import org.openstreetmap.osmosis.core.container.v0_6.EntityContainer;
import org.openstreetmap.osmosis.core.domain.v0_6.Entity;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.osmosis.core.domain.v0_6.Tag;
import org.openstreetmap.osmosis.core.domain.v0_6.Way;
import org.openstreetmap.osmosis.core.domain.v0_6.WayNode;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlDownloader;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;

public class InteligentesOSM {

	static Scanner leer = new Scanner(System.in);
	static EspacioDeEstados espacioEstados;
	static Hashtable<Long, Nodo> nodos = new Hashtable<Long, Nodo>();
	static Hashtable<String, Double> nodosPoda = new Hashtable<String, Double>();
	static Nodo nodo = null;
	static NodoVecino vecino = null;
	static Frontera frontera = new Frontera();
	static NodoArbol nodoActual;
	
	static public void main(String[] args) {
		double lonMin = 0;
		double latMin = 0;
		double lonMax = 0;
		double latMax = 0; 
		boolean archivo= false;
		boolean seguir = true;
		File file= new File("C:/Users/Antonio/Desktop/mapaa.osm");
		//File file= new File("pruebaClase.osm");	
		do {
			System.out.println("Que desea hacer");
			System.out.println("[1] Leer Mapa En Local");
			System.out.println("[2] Introducir Coordenadas");
			int opcion = leer.nextInt();		
			switch (opcion) {
			case 1:
				espacioEstados = Util.coordenadasXML(file);
				archivo = true;
				seguir = false;
				break;
			case 2:				
				latMin = coordenada(latMin, "Introduzca la latitud minima: ");
				lonMin = coordenada(lonMin, "Introduzca la longitud minima: ");
				latMax = coordenada(latMax, "Introduzca la latitud maxima: ");
				lonMax = coordenada(lonMax, "Introduzca la longitud maxima: ");
				espacioEstados = new EspacioDeEstados(latMin, lonMin, latMax, lonMax);	
				archivo = false;
				seguir = false;
				break;
			default:
				System.out.println("No ha elegido una opcion");
				break;
			}
		} while (seguir);
		

		/*archivo = false;
		lonMax = -3.9265;
		latMin = 38.9846;
		lonMin = -3.9226;
		latMax = 38.9897;*/
		
		leerMapa(file,lonMin, latMin, lonMax, latMax, archivo);
		mostrarMenu();
				
	}
	
	public static void leerMapa (File file, double latMax, double lonMin, double latMin, double lonMax, boolean archivo){
		RunnableSource reader = null;
		Sink sinkImplementation = new Sink() {
			public void initialize(Map<String, Object> metaData) {
			};
			public void process(EntityContainer entityContainer) {
				Entity entity = entityContainer.getEntity();
				if (entity instanceof Node) {
					nodo = new  Nodo(((Node) entity).getId(),((Node) entity).getLongitude(), ((Node) entity).getLatitude());					
					nodos.put(((Node)entity).getId(), nodo);
					//System.out.println(ID: " + nodo.getId() + "\t Latitud: " + nodo.getLatitud() + "  \t Longitud: " + nodo.getLongitud());				
					// System.out.println("Node");
					//System.out.println("Nodo: " + entity.getId());
					// Hacer algo con el Nodo
					// http://osmosis.openstreetmap.de/osmosis-SNAPSHOT/apidocs/org/openstreetmap/osmosis/core/domain/v0_6/Node.html
				} else if (entity instanceof Way) {
					Object[] tags = entity.getTags().toArray();
					for (int i=0; i<tags.length; i++){
						Tag tag = (Tag) tags[i];
						//System.out.println(tag);				
						if(tag.getKey().equalsIgnoreCase("highway")){
							if(tag.getValue().equalsIgnoreCase("trunk") || tag.getValue().equalsIgnoreCase("residential") || tag.getValue().equalsIgnoreCase("residential")){
								ArrayList<NodoVecino> vecinos;
								List<WayNode> ways = ((Way) entity).getWayNodes();
								for (int wayNodo=0 ; wayNodo < ways.size() - 1 ; wayNodo++){
									long nodoInicio = ways.get(wayNodo).getNodeId();
									long nodoVecino = ways.get(wayNodo+1).getNodeId();
									
									double distancia = Mercator.distanciaNodos(nodos.get(nodoInicio),nodos.get(nodoVecino));
									
									vecinos = nodos.get(nodoInicio).getVecinos();
									vecino = new NodoVecino(nodoVecino,distancia);
									vecinos.add(vecino);
									nodos.get(nodoInicio).setVecinos(vecinos);
								
									distancia = Mercator.distanciaNodos(nodos.get(nodoVecino),nodos.get(nodoInicio));
									vecinos = nodos.get(nodoVecino).getVecinos();
									vecino = new NodoVecino(nodoInicio,distancia);
									vecinos.add(vecino);
									nodos.get(nodoVecino).setVecinos(vecinos);
									//System.out.println("nodo vecino"+nodoVecino);
									//System.out.println(" Nodo"+nodoInicio + "" +vecinos);
									//System.out.println(nodos.get(nodoInicio));
								}
							}
						}
					}

					// System.out.println("Way");
					//System.out.println("Way: " + entity.toString());
					// Hacer algo con Way
					// http://osmosis.openstreetmap.de/osmosis-SNAPSHOT/apidocs/org/openstreetmap/osmosis/core/domain/v0_6/Way.html
				}
			}

			public void release() {}

			public void complete() {}

		};

		CompressionMethod compression = CompressionMethod.None;
		
		// Offline
		// Para usar el fichero XML descargado.
		if(archivo) {
			reader = new XmlReader(file, false, compression);	
		}	
		// Online
		// Para acceder directamente desde la BD de OSM online
		// Utilizar XmlDownloader
		else {
			reader = new XmlDownloader(latMin, latMax, lonMax, lonMin, "http://www.openstreetmap.org/api/0.6");		
		}		
		
		reader.setSink(sinkImplementation);
		
		Thread readerThread = new Thread(reader);
		readerThread.start();
		System.out.println("Comenzamos");
		while (readerThread.isAlive()) {
			try {
				readerThread.join();
			} catch (InterruptedException e) {
				/* No hacer nada */
			}
		}
	}
	
	public static void mostrarMenu(){
		Scanner leer = new Scanner(System.in);
		leer.useLocale(Locale.US);
		boolean seguir = true;
		do {
			System.out.println("Selecciona una opcion");
			System.out.println("[1] Mostrar todos los nodos");
			System.out.println("[2] Mostrar los vecinos de un nodo");
			System.out.println("[3] Resolver Problema");
			System.out.println("[4] Casos de Prueba");
			System.out.println("[5] Test");
			System.out.println("[6] Salir");
			int opcion = leer.nextInt();		
			switch (opcion) {
			case 1:
				imprimirNodos();
				break;
			case 2:
				imprimirVecinos();
				break;
			case 3:
				problema(espacioEstados);
				break;
			case 4:
				seleccionarCasoDePrueba();
				break;
			case 5:
				problemaTest(espacioEstados);
				break;
			case 6:
				seguir=false;
				break;
			default:
				System.out.println("No ha elegido una opcion");
				break;
			}
		} while (seguir);
	}
	
	public static void problema(EspacioDeEstados espacioEstados){
		boolean solucion = false;
		Scanner leer = new Scanner(System.in);
		leer.useLocale(Locale.US);
		int profundidadIterativa = 0;
		ArrayList <Long> objetivos = new ArrayList <Long>();
		System.out.println("Introduce el nodo Inicio");
		long nodoInicio = nodoExiste();
		System.out.println("Introduce el numero de objetivos");
		int nObjetivos = numero();
		for(int i= 0; i< nObjetivos; i++){
			System.out.println("Introduce el nodo Objetivo");
			objetivos.add(nodoExiste());
			}
		Estado estadoInicial = new Estado(nodoInicio,objetivos);
		Problema problema = new Problema(espacioEstados, estadoInicial);
		System.out.println("Introduce la profundidad maxima");
		int profundidadMax = numero();
		System.out.println("Introduce la profundidad iterativa");
		profundidadIterativa = numero();
		
		boolean poda = hayPoda();
		
		String estrategia = seleccionarEstrategia();	
		
		long tiempoInicial;
		if(profundidadIterativa < profundidadMax  ){
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busqueda(problema, estrategia, profundidadMax, profundidadIterativa,poda);
			
		}else{
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busquedaAcotada(problema, estrategia, profundidadMax,poda);
		}
		
		long tiempoFinal=System.nanoTime();
		System.out.println("Tiempo Final: "+tiempoFinal);
		long tiempoTotal = tiempoFinal - tiempoInicial;
		System.out.println("Tiempo Total: "+tiempoTotal);
		double tiempoTotalSegundos = ((double)tiempoTotal / 1000000000);
		
		if(solucion){
			crearSolucion(nodoActual,problema,estrategia,frontera.getTamanoMax(),tiempoTotalSegundos);
		}else{
			System.out.println("No se encontro una solución");
		}
	}
	
	public static boolean hayPoda(){
		boolean poda = false;		
		boolean seguir = true;
		do {
			System.out.println("Desea realizar la poda?");
			System.out.println("[1] Si");
			System.out.println("[2] No");
			int opcion = leer.nextInt();		
			switch (opcion) {
			case 1:
				poda = true;
				seguir = false;
				break;
			case 2:
				poda = false;
				seguir = false;
				break;
			default:
				System.out.println("No ha elegido una opcion");
				seguir = true;
				break;
			}
		} while (seguir);
		return poda;
	}
	
	public static String seleccionarEstrategia() {
		boolean seguir;
		String estrategia = null;
		
		do {
			System.out.println("[1] Anchura");
			System.out.println("[2] Profundidad Simple");
			System.out.println("[3] Profundidad Acotada"); 
			System.out.println("[4] Profundidad Iterativa"); 
			System.out.println("[5] Costo Uniforme");
			System.out.println("[6] Voraz");
			System.out.println("[7] A*");
			int opcion = leer.nextInt();
			
			switch(opcion) {
				case 1:
					estrategia = "Anchura";
					seguir = false;
					break;
				case 2:
					estrategia = "Profundidad Simple";
					seguir = false;
					break;
				case 3:
					estrategia = "Profundidad Acotada";
					seguir = false;
					break;
				case 4:
					estrategia = "Profundidad Iterativa";
					seguir = false;
					break;
				case 5:
					estrategia = "Costo Uniforme";
					seguir = false;
					break;
				case 6:
					estrategia = "Voraz";
					seguir = false;
					break;
				case 7:
					estrategia = "A*";
					seguir = false;
					break;
				default:
					System.out.println("No ha elegido una opcion");
					seguir = true;
			}
		} while(seguir);
		
		return estrategia;
	}
	
	public static boolean busqueda(Problema problema, String estrategia ,int profundidadMax, int profundidadIterativa ,boolean poda ){
		int profundidadActual = profundidadIterativa;
		boolean solucion = false;		
		while(solucion == false && profundidadActual <= profundidadMax){
			solucion = busquedaAcotada(problema,estrategia,profundidadActual,poda);
			profundidadActual = profundidadActual + profundidadIterativa;
		}		
		return solucion;		
	}
	
	public static boolean busquedaAcotada(Problema problema, String estrategia, int profundidadMax,boolean poda){
		NodoArbol nodoInicial = new NodoArbol(null, problema.getEstadoInicial(),0.0,0.0,0,0.0);
		frontera.insertarFrontera(nodoInicial);
		
		//System.out.println(nodoInicial);
		
		boolean solucion = false;
		
		while(solucion == false && !frontera.esVacia()){
			
			nodoActual = frontera.eliminarFrontera();
			if(problema.testObjetivo(nodoActual.getEstado())){
				solucion = true;
			}
			else if(nodoActual.getProfundidad() < profundidadMax) {				
				ArrayList<NodoVecino> vecinos = problema.sucesores(nodos.get(nodoActual.getEstado().getLocalizacion()));
				//System.out.println(vecinos);
				ArrayList<NodoArbol> sucesores = crearSucesores(problema, vecinos,nodoActual,estrategia,profundidadMax,poda);
			}
		}

		return solucion;
	}
	
	public static ArrayList<NodoArbol> crearSucesores(Problema problema, ArrayList<NodoVecino> vecinos, NodoArbol nodoActual, String estrategia, int profundidadMax, boolean poda){
		ArrayList<NodoArbol> sucesores = new ArrayList<NodoArbol>();
		for (int i=0;i<vecinos.size();i++){		
			Estado aux = new Estado (vecinos.get(i).getId(), nodoActual.getEstado().getObjetivos());			
			Estado estado = new Estado (vecinos.get(i).getId(), comprobarObjetivos(aux));
			
			NodoArbol padre = nodoActual;
			
			double costo = vecinos.get(i).getDistancia()+nodoActual.getCosto();
			double accion = 0.0;
			int profundidad = nodoActual.getProfundidad()+1;
			double valor = 0.0;
			
			if (estrategia.equalsIgnoreCase("anchura")) { // Anchura
                valor = profundidad;
            } else if (estrategia.equalsIgnoreCase("profundidad simple")) { // Prof Simple
            	valor = -profundidad;
            } else if (estrategia.equalsIgnoreCase("profundidad acotada")) { // Prof acotada            	
                valor = -profundidad;            	
            } else if (estrategia.equalsIgnoreCase("profundidad iterativa")) { // Prof iterativa           	
                valor = -profundidad;            	            
            } else if (estrategia.equalsIgnoreCase("Costo Uniforme")) {  // Costo Uniforme           	
                valor = costo;            	
            } else if (estrategia.equalsIgnoreCase("voraz")) {  // Voraz           	
                valor = problema.heuristica(nodos, estado);            	
            } else if (estrategia.equalsIgnoreCase("a*")) {  // A*            	
            	valor = problema.heuristica(nodos, estado) + costo;            	
            }			
			NodoArbol nodo=new NodoArbol(padre,estado,costo,accion,profundidad,valor);	
			
			if (poda){
				if (!sePoda(nodo)){
					nodosPoda.put(nodo.getEstado().toString(), nodo.getValor());
					frontera.insertarFrontera(nodo);
				}
			}else{
				frontera.insertarFrontera(nodo);
			}
		}
		frontera.actualizarTamanoMax();
		return sucesores;	
	}
	
	public static ArrayList<Long> comprobarObjetivos(Estado estado){
		ArrayList<Long> objetivosVecino = new ArrayList <Long>();
		for (int i=0; i< estado.getObjetivos().size() ; i++){
			if(estado.getObjetivos().get(i) != estado.getLocalizacion()){
				objetivosVecino.add(estado.getObjetivos().get(i));
			}
		}
		return objetivosVecino;		
	}
			
	public static boolean sePoda (NodoArbol nodo){
		boolean sePoda = false;
		
		if(nodosPoda.containsKey(nodo.getEstado().toString())){
			if(nodosPoda.get(nodo.getEstado().toString()) < nodo.getValor()){
				nodosPoda.remove(nodo.getEstado().toString());
				nodosPoda.put(nodo.getEstado().toString(),nodo.getValor());
				sePoda = true;
			}
		}
		return sePoda;
	}
	
	public static void crearSolucion(NodoArbol nodoActual, Problema problema, String estrategia, long complejidadEspacial, double tiempoTotal){
		Scanner leer = new Scanner(System.in);
		leer.useLocale(Locale.US);
		Stack <NodoArbol> solucion = new Stack <NodoArbol>();
		int profundidadTotal = 0;
		double costoTotal = nodoActual.getCosto();
		while(nodoActual != null) { 
			solucion.push(nodoActual);
			nodoActual = nodoActual.getPadre();
			if (nodoActual != null){
				profundidadTotal += 1; 
            }
		}		
		Stack<NodoArbol>solucionImprimir=(Stack<NodoArbol>) solucion.clone();
		imprimirSolucion(solucionImprimir,problema,profundidadTotal,costoTotal,complejidadEspacial,estrategia,tiempoTotal);
		imprimirFicheroGPX(solucion,problema,profundidadTotal,costoTotal,complejidadEspacial,estrategia,tiempoTotal);
		
		//Util.URL();
			
	}
	
	public static void problemaTest(EspacioDeEstados espacioEstados){
		boolean solucion = false;
		int profundidadIterativa = 0;
		ArrayList <Long> objetivos = new ArrayList <Long>();
		
		//Ruta pequeña
		//Coste optimo: 726.907
		//Profundidad de la solución optima: 11
		
		
		//long nodoInicio = Long.parseLong("368287515");
		//objetivos.add(Long.parseLong("806369190"));	
		
		//Ruta Grande
		//Coste optimo: 1989.78
		//Profundidad de la solución optima: 26
				
				
		//long nodoInicio = Long.parseLong("368287515");
		//objetivos.add(Long.parseLong("806369190"));	
		//objetivos.add(Long.parseLong("803292583"));	
		
		//RutaClase
		
		//long nodoInicio = 812954564;
		//objetivos.add(Long.parseLong("803292583"));
		//objetivos.add(Long.parseLong("812954600"));
		
		//Ruta Correo
		
		//long nodoInicio = 804689126;
		//objetivos.add(Long.parseLong("368287515"));
		//objetivos.add(Long.parseLong("804689060"));
		//objetivos.add(Long.parseLong("803292813"));
		
		
		//Ruta Miguel
		
		/*long nodoInicio = 803292611;
		objetivos.add(Long.parseLong("803292570"));
		objetivos.add(Long.parseLong("803292487"));
		objetivos.add(Long.parseLong("803292856"));*/
		long nodoInicio = 368287512;
		objetivos.add(Long.parseLong("806368904"));
		objetivos.add(Long.parseLong("812954860"));
			
		Estado estadoInicial = new Estado(nodoInicio,objetivos);
		Problema problema = new Problema(espacioEstados, estadoInicial);
		int profundidadMax = 500000000;
		profundidadIterativa = 100005 ;
		/*System.out.println("[1] Anchura");
		System.out.println("[2] Profundidad Simple");
		System.out.println("[3] Profundidad Acotada"); 
		System.out.println("[4] Profundidad Iterativa"); 
		System.out.println("[5] Costo Uniforme");
		System.out.println("[6] Voraz");
		System.out.println("[7] A*");*/
		boolean poda = true ;
		String estrategia = "A*";
		long tiempoInicial;
		
		if(profundidadIterativa < profundidadMax  ){
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busqueda(problema, estrategia, profundidadMax, profundidadIterativa,poda);
			
		}else{
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busquedaAcotada(problema, estrategia, profundidadMax,poda);
		}
		
		long tiempoFinal=System.nanoTime();
		System.out.println("Tiempo Final: "+tiempoFinal);
		long tiempoTotal = tiempoFinal - tiempoInicial;
		System.out.println("Tiempo Total: "+tiempoTotal);
		double tiempoTotalSegundos = ((double)tiempoTotal / 1000000000);
		
		
		if(solucion){
			crearSolucion(nodoActual,problema,estrategia,frontera.getTamanoMax(),tiempoTotalSegundos);
		}else{
			System.out.println("No se encontro una solución");
		}
	}
	
	public static void seleccionarCasoDePrueba() {
		boolean seguir;	
		do {
			System.out.println("[1] Caso A");
			System.out.println("[2] Caso B");
			System.out.println("[3] Caso C"); 
			int opcion = leer.nextInt();			
			switch(opcion) {
				case 1:
					problemaCasoDePruebaA(espacioEstados);
					seguir = false;
					break;
				case 2:
					problemaCasoDePruebaB(espacioEstados);
					seguir = false;
					break;
				case 3:
					problemaCasoDePruebaC(espacioEstados);
					seguir = false;
					break;
				default:
					System.out.println("No ha elegido una opcion");
					seguir = true;
			}
		} while(seguir);
		
	}
	
	public static void problemaCasoDePruebaA(EspacioDeEstados espacioEstados ){
		boolean solucion = false;
			
		int profundidadIterativa = 0;
		ArrayList <Long> objetivos = new ArrayList <Long>();
		
		long nodoInicio = 368287512;
		objetivos.add(Long.parseLong("806368904"));
		objetivos.add(Long.parseLong("812954860"));
		
		Estado estadoInicial = new Estado(nodoInicio,objetivos);
		
		Problema problema = new Problema(espacioEstados, estadoInicial);
		int profundidadMax = 20;
		profundidadIterativa = 20 ;
		boolean poda = hayPoda() ;
		String estrategia = seleccionarEstrategia();
		long tiempoInicial;
		
		if(profundidadIterativa < profundidadMax  ){
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busqueda(problema, estrategia, profundidadMax, profundidadIterativa,poda);
			
		}else{
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busquedaAcotada(problema, estrategia, profundidadMax,poda);
		}
		
		long tiempoFinal=System.nanoTime();
		System.out.println("Tiempo Final: "+tiempoFinal);
		long tiempoTotal = tiempoFinal - tiempoInicial;
		System.out.println("Tiempo Total: "+tiempoTotal);
		double tiempoTotalSegundos = ((double)tiempoTotal / 1000000000);
		
		
		if(solucion){
			crearSolucion(nodoActual,problema,estrategia,frontera.getTamanoMax(),tiempoTotalSegundos);
		}else{
			System.out.println("No se encontro una solución");
		}
	}
	
	public static void problemaCasoDePruebaB(EspacioDeEstados espacioEstados ){
		boolean solucion = false;
			
		int profundidadIterativa = 0;
		ArrayList <Long> objetivos = new ArrayList <Long>();
		
		long nodoInicio = 812954647;
		objetivos.add(Long.parseLong("368287510"));
		objetivos.add(Long.parseLong("803292581"));
		objetivos.add(Long.parseLong("812954573"));
		objetivos.add(Long.parseLong("803292576"));
		
		Estado estadoInicial = new Estado(nodoInicio,objetivos);
		
		Problema problema = new Problema(espacioEstados, estadoInicial);
		int profundidadMax = 50;
		profundidadIterativa = 50 ;
		boolean poda = hayPoda() ;
		String estrategia = seleccionarEstrategia();
		long tiempoInicial;
		
		if(profundidadIterativa < profundidadMax  ){
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busqueda(problema, estrategia, profundidadMax, profundidadIterativa,poda);
			
		}else{
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busquedaAcotada(problema, estrategia, profundidadMax,poda);
		}
		
		long tiempoFinal=System.nanoTime();
		System.out.println("Tiempo Final: "+tiempoFinal);
		long tiempoTotal = tiempoFinal - tiempoInicial;
		System.out.println("Tiempo Total: "+tiempoTotal);
		double tiempoTotalSegundos = ((double)tiempoTotal / 1000000000);
		
		
		if(solucion){
			crearSolucion(nodoActual,problema,estrategia,frontera.getTamanoMax(),tiempoTotalSegundos);
		}else{
			System.out.println("No se encontro una solución");
		}
	}
	
	public static void problemaCasoDePruebaC(EspacioDeEstados espacioEstados ){
		boolean solucion = false;
			
		int profundidadIterativa = 0;
		ArrayList <Long> objetivos = new ArrayList <Long>();
		
		long nodoInicio = 525959937;
		objetivos.add(Long.parseLong("812954573"));		
		objetivos.add(Long.parseLong("504656546"));
		objetivos.add(Long.parseLong("803292576"));
		objetivos.add(Long.parseLong("803292445"));
		objetivos.add(Long.parseLong("765309500"));
		objetivos.add(Long.parseLong("803292856"));
		objetivos.add(Long.parseLong("803292594"));
		objetivos.add(Long.parseLong("803292481"));
		objetivos.add(Long.parseLong("803292210"));
		
		
		Estado estadoInicial = new Estado(nodoInicio,objetivos);
		
		Problema problema = new Problema(espacioEstados, estadoInicial);
		int profundidadMax = 100;
		profundidadIterativa = 100 ;
		boolean poda = hayPoda() ;
		String estrategia = seleccionarEstrategia();
		long tiempoInicial;
		
		if(profundidadIterativa < profundidadMax  ){
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busqueda(problema, estrategia, profundidadMax, profundidadIterativa,poda);
			
		}else{
			tiempoInicial=System.nanoTime();
			System.out.println("Tiempo Inicial: "+tiempoInicial);
			solucion = busquedaAcotada(problema, estrategia, profundidadMax,poda);
		}
		
		long tiempoFinal=System.nanoTime();
		System.out.println("Tiempo Final: "+tiempoFinal);
		long tiempoTotal = tiempoFinal - tiempoInicial;
		System.out.println("Tiempo Total: "+tiempoTotal);
		double tiempoTotalSegundos = ((double)tiempoTotal / 1000000000);
		
		
		if(solucion){
			crearSolucion(nodoActual,problema,estrategia,frontera.getTamanoMax(),tiempoTotalSegundos);
		}else{
			System.out.println("No se encontro una solución");
		}
	}
	
	public static double coordenada(double coordenada, String texto){
		Scanner leer = new Scanner(System.in);
		leer.useLocale(Locale.US);
		System.out.println(texto);
		coordenada = leer.nextDouble();
		return coordenada;
	}
	
	public static void imprimirEspacioEstados(){
		System.out.println("LatMax: "+espacioEstados.getLatMax());
		System.out.println("LatMin: "+espacioEstados.getLatMin());
		System.out.println("LonMax: "+espacioEstados.getLonMax());
		System.out.println("LonMin: "+espacioEstados.getLonMin());
	}
	
	public static void imprimirNodos(){
	    Enumeration<Nodo> nodo = nodos.elements();
	    while (nodo.hasMoreElements()) {
	      System.out.println(nodo.nextElement());
	    }
	}
	
	public static void imprimirVecinos(){
		Scanner leer = new Scanner(System.in);
		leer.useLocale(Locale.US);
		System.out.println("Introduzca el Nodo del que desea conocer los vecinos");
		Long nodo = leer.nextLong();
		if(nodos.get(nodo)== null){
			System.out.println("El nodo seleccionado no existe");
		}else{
			System.out.println(nodos.get(nodo).toString());
		}
	}
	
	public static void imprimirFrontera(){
		for(int i=0; i < frontera.sizeFrontera(); i++){
			System.out.println(i);
			System.out.println(frontera.eliminarFrontera().toString());
		}
	}
	
	public static long nodoExiste(){
		boolean seguir = true;
		long nodo = 0;
		do {
			nodo = leer.nextLong();
			if(nodos.get(nodo)== null){
				System.out.println("El nodo seleccionado no existe, vuelva ha introducirlo");
				seguir = true;
			}else{
				seguir = false;			
			}
		} while(seguir);
		
		return nodo;
	}
	
	public static int numero(){
		boolean seguir = true;
		int numero = 0;
		do {
			numero = leer.nextInt();
			if(numero <= 0){
				System.out.println("Por favor vuelva a introducirlo");
				seguir = true;
			}else{
				seguir = false;
			}
		} while(seguir);
		
		return numero;
	}
	
	public static void imprimirSolucion(Stack <NodoArbol> solucion, Problema problema,int profundidadTotal,double costoTotal, long complejidadEspacial, String estrategia,double tiempoTotal){
		ArrayList <NodoArbol> solucionCorrecta = new ArrayList <NodoArbol>();
		
		System.out.println("Espacio de Estados: " + problema.getEspacioEstados().toString());
		System.out.println("");
		System.out.println("Nodo Origen: "+nodos.get(problema.getEstadoInicial().getLocalizacion()).getId());
		
		for(int i=0; i< problema.getEstadoInicial().getObjetivos().size();i++){
			System.out.println("Nodo Destino " +(i+1)+ ":"+" "+nodos.get(problema.getEstadoInicial().getObjetivos().get(i)).getId());;
		}	
		System.out.println("");
		System.out.println("Estrategia: "+estrategia);
		System.out.println("Profundidad Maxima: "+ profundidadTotal );
		System.out.println("Coste de la Solucion: "+ costoTotal);
		System.out.println("Complejidad Espacial: "+ complejidadEspacial);
		System.out.println("Complejidad Temporal: "+ tiempoTotal + " Segundos" );
		System.out.println("");
		
		while(!solucion.empty()){
			solucionCorrecta.add(solucion.pop());
        }
		
		for(int i = 0; i< solucionCorrecta.size(); i++){
			System.out.println(solucionCorrecta.get(i).getEstado());
		}				
	}
	
	public static void imprimirFicheroGPX(Stack <NodoArbol> solucion, Problema problema, int profundidadTotal, double costoTotal, long complejidadEspacial, String estrategia, double tiempoTotal){
		Scanner leer = new Scanner(System.in);
		leer.useLocale(Locale.US);

		ArrayList <NodoArbol> solucionCorrecta = new ArrayList <NodoArbol>();
		while(!solucion.empty()){
			solucionCorrecta.add(solucion.pop());
        }
		
		try {
			System.out.println("Introduzca el nombre del fichero");
			File archivo= new File(leer.nextLine()+".gpx");
			FileWriter fw = new FileWriter(archivo,true);
			fw.write("<gpx version= \"1.1\" creator= \"Alvaro\">");
			fw.write("\n\n");
			fw.write("<wpt lat= \""+nodos.get(problema.getEstadoInicial().getLocalizacion()).getLatitud()+"\" lon= \""+nodos.get(problema.getEstadoInicial().getLocalizacion()).getLongitud()+"\">");
			fw.write("\n");
			fw.write("<name>Origen</name>");
			fw.write("\n");
			fw.write("</wpt>");
			fw.write("\n");
			for(int i=0; i< problema.getEstadoInicial().getObjetivos().size();i++){
				fw.write("<wpt lat= \""+nodos.get(problema.getEstadoInicial().getObjetivos().get(i)).getLatitud()+"\" lon= \""+nodos.get(problema.getEstadoInicial().getObjetivos().get(i)).getLongitud()+"\">");
				fw.write("\n");
				fw.write("<name>Destino" +i+"</name>");
				fw.write("\n");
				fw.write("</wpt>");
			}					
			fw.write("\n\n");
			fw.write("<trk>");
			fw.write("\n");
			fw.write("\t<trkseg>");
			fw.write("\n");
			for(int i = 0; i< solucionCorrecta.size(); i++){
				fw.write("\t<trkpt lat= \""+nodos.get(solucionCorrecta.get(i).getEstado().getLocalizacion()).getLatitud()+"\" lon= \""+nodos.get(solucionCorrecta.get(i).getEstado().getLocalizacion()).getLongitud()+"\">");
				fw.write("\n");
				fw.write("\t\t<ele>0</ele>");
				fw.write("\n");
				fw.write("\t\t<name>"+solucionCorrecta.get(i).getEstado()+"</name>");
				fw.write("\n");
				fw.write("\t</trkpt>");
				fw.write("\n");
			}
			
			fw.write("\t</trkseg>");
			fw.write("\n");
			fw.write("<desc>");
			fw.write("\n");
			fw.write("\tEspacio de Estados: "+ problema.getEspacioEstados().toString());
			fw.write("\n");
			fw.write("\tNodo Origen: "+nodos.get(problema.getEstadoInicial().getLocalizacion()).getId());
			fw.write("\n");
			for(int i=0; i< problema.getEstadoInicial().getObjetivos().size();i++){
				fw.write("\tNodo Destino: "+nodos.get(problema.getEstadoInicial().getObjetivos().get(i)).getId());
				fw.write("\n");
			}			
			fw.write("\n");
			fw.write("\tEstrategia: "+estrategia);
			fw.write("\n");
			fw.write("\tProfundidad Maxima: "+ profundidadTotal );
			fw.write("\n");
			fw.write("\tCoste de la Solucion: "+ costoTotal);
			fw.write("\n");
			fw.write("\tComplejidad Espacial: "+ complejidadEspacial);
			fw.write("\n");
			fw.write("\tComplejidad Temporal: "+ tiempoTotal + " Segundos" );
			fw.write("\n");
			fw.write("</desc>");
			fw.write("</trk>");
			fw.write("\n");
			fw.write("</gpx>");
			
			
			fw.close();
			fw.close();
		}catch (Exception e) {
			System.out.println("Error al escribir el camino "+ e);
		}
	}
	
}