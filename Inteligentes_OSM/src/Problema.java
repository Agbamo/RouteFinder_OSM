import java.util.ArrayList;
import java.util.Hashtable;

public class Problema {
	private static EspacioDeEstados espacioEstados;
	private static Estado estadoInicial;

	public Problema(EspacioDeEstados espacioEstados, Estado estadoInicial) {
		this.espacioEstados = espacioEstados;
		this.estadoInicial = estadoInicial;
	}
	
	public static Estado getEstadoInicial() {
		return estadoInicial;
	}
	
	public void setEstadoInicial(Estado estadoInicial) {
		this.estadoInicial = estadoInicial;
	}
	
	public static EspacioDeEstados getEspacioEstados() {
		return espacioEstados;
	}
	
	public void setEspacioEstados(EspacioDeEstados espacioEstados) {
		this.espacioEstados = espacioEstados;
	}

	public boolean testObjetivo(Estado estadoActual) {
		if(estadoActual.getObjetivos().isEmpty()) return true;
		else return false;
	}
	
	public ArrayList<NodoVecino> sucesores(Nodo nodo) {
		ArrayList<NodoVecino> sucesores = nodo.getVecinos();
		//System.out.println(nodo.getVecinos());
		return sucesores;
				
	}
	
	public static double heuristica(Hashtable nodos, Estado estado) {
		double mayorDistancia = Double.MIN_VALUE;
		double distanciaAux = Double.MIN_VALUE;
		if(!estado.getObjetivos().isEmpty()) {
			for(int i=0; i<estado.getObjetivos().size(); i++) {
				distanciaAux = Mercator.distanciaNodos((Nodo)nodos.get(estado.getLocalizacion()), (Nodo)nodos.get(estado.getObjetivos().get(i)));
				if(distanciaAux > mayorDistancia)
					mayorDistancia = distanciaAux;
			}
		}		
		return mayorDistancia;		
	}
}

