

public class NodoVecino {
	private long id;
	private double distancia;
	
	public NodoVecino(long id, double distancia) {
		this.id=id;
		this.distancia = distancia;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public double getDistancia() {
		return distancia;
	}

	public void setDistancia(double distancia) {
		this.distancia = distancia;
	}
	

	@Override
	public String toString() {
		return "NodoVecino [id=" + id + ", distancia=" + distancia + "]";
	}
	
	

}
