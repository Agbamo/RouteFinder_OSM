import java.util.ArrayList;

public class Estado {
	
	private long localizacion;
	private ArrayList<Long> objetivos;
	
	public Estado(long localizacion, ArrayList<Long> objetivos) {
		this.localizacion = localizacion;
		this.objetivos = objetivos;
	}
	
	public long getLocalizacion() {
		return localizacion;
	}

	public ArrayList<Long> getObjetivos() {
		return objetivos;
	}
	
	public void setLocalizacion(long localizacion) {
		this.localizacion = localizacion;
	}

	public void setObjetivos(ArrayList<Long> objetivos) {
		this.objetivos = objetivos;
	}
	
	
	@Override
	public String toString() {
		return "Estado [localizacion=" + localizacion + ", objetivos=" + objetivos + "]";
	}

}