import java.util.ArrayList;

public class EspacioDeEstados {
	
	private double latMin;
	private double lonMin;
	private double latMax;
	private double lonMax;
	
	public EspacioDeEstados(double latMin, double lonMin, double latMax, double lonMax) {
		this.latMin = latMin;
		this.lonMin = lonMin;
		this.latMax = latMax;
		this.lonMax = lonMax;
	}

	public double getLonMin() {
		return lonMin;
	}

	public void setLonMin(double lonMin) {
		this.lonMin = lonMin;
	}

	public double getLatMin() {
		return latMin;
	}

	public void setLatMin(double latMin) {
		this.latMin = latMin;
	}

	public double getLonMax() {
		return lonMax;
	}

	public void setLonMax(double lonMax) {
		this.lonMax = lonMax;
	}

	public double getLatMax() {
		return latMax;
	}

	public void setLatMax(double latMax) {
		this.latMax = latMax;
	}

	@Override
	public String toString() {
		return "["+latMin + ","+ lonMin + "," + latMax + "," + lonMax+ "]";
	}
	

}
