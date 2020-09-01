
public class NodoArbol implements Comparable<NodoArbol> {
	
	private NodoArbol padre;
	private Estado estado;
	private double costo;
	private double accion;
	private int profundidad;
	private double valor;
	
	public NodoArbol(NodoArbol padre, Estado estado, double costo, double accion, int profundidad, double valor) {
		this.padre = padre;
		this.estado = estado;
		this.costo = costo;
		this.accion = accion;
		this.profundidad = profundidad;
		this.valor = valor;
	}
	
	public NodoArbol getPadre() {
		return padre;
	}
	public void setPadre(NodoArbol padre) {
		this.padre = padre;
	}
	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public double getCosto() {
		return costo;
	}
	public void setCosto(double costo) {
		this.costo = costo;
	}
	public double getAccion() {
		return accion;
	}
	public void setAccion(double accion) {
		this.accion = accion;
	}
	public int getProfundidad() {
		return profundidad;
	}
	public void setProfundidad(int profundidad) {
		this.profundidad = profundidad;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	
	@Override
	public String toString() {
		return "NodoArbol [padre=" + padre + ", estado=" + estado + ", costo=" + costo + ", accion=" + accion
				+ ", profundidad=" + profundidad + ", valor=" + valor + "]";
	}

	public int compareTo(NodoArbol arg0) {
			int aux = 0;
			if (this.valor < (arg0.getValor()))
				aux = -1;
			if (this.valor > (arg0.getValor()))
				aux = 1;
			return aux;
		}

}

