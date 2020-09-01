import java.util.PriorityQueue;

public class Frontera {
	
	private static PriorityQueue<NodoArbol> frontera = new PriorityQueue<NodoArbol>();
	private static long maxSize;
	
	public static void insertarFrontera(NodoArbol nodoActual){
		frontera.add(nodoActual);
	}
	
	public static NodoArbol eliminarFrontera(){
		NodoArbol nodoActual = frontera.poll();
		return nodoActual;
		
	}
	
	public static boolean esVacia(){
		if(frontera.isEmpty()){
			return true;
		}
		return false;
	}
	
	public int sizeFrontera() {
		return frontera.size();
	}
	
	public static long getTamanoMax() {
		return maxSize;
	}
	
	public void setTamanoMax(int maxSize) {
		this.maxSize = maxSize;
	}
	
	public void actualizarTamanoMax(){
		maxSize = frontera.size() + maxSize;
		
	}
	
	
}
