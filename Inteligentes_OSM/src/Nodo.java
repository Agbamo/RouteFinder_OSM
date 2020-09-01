import java.util.ArrayList;

class Nodo{
	private long id;
	private double longitud;
	private double latitud;
	private ArrayList <NodoVecino> vecinos;
	
	public Nodo(long id, double longitud, double latitud) {
		this.id=id;
		this.longitud = longitud;
		this.latitud = latitud;
		this.vecinos =  new ArrayList <NodoVecino>();	
	}

	
	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}


	public ArrayList<NodoVecino> getVecinos() {
		return vecinos;
	}

	public void setVecinos(ArrayList<NodoVecino> vecinos) {
		this.vecinos = vecinos;
	}

	@Override
	public String toString() {
		return "Nodo [id=" + id + ", longitud=" + longitud + ", latitud=" + latitud + ", vecinos=" + vecinos + "]";
	}
	
	

	

	
}
