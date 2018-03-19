package dad.us.dadVertx;

import java.sql.Timestamp;


import org.joda.time.DateTime;

public class Localizacion {
	private String nombre;
	private Double lluvia_max;
	private Double lluvia_min;
	private Double luz_max;
	private Double luz_min;
	private Timestamp alarma;
	
	public Localizacion() {
		 
		this("",0.0,0.0,0.0,0.0,null);
	}

	@Override
	public String toString() {
		return "Localizacion [nombre=" + nombre + ", lluvia_max=" + lluvia_max + ", lluvia_min=" + lluvia_min
				+ ", luz_max=" + luz_max + ", luz_min=" + luz_min + ", alarma=" + alarma + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alarma == null) ? 0 : alarma.hashCode());
		result = prime * result + ((lluvia_max == null) ? 0 : lluvia_max.hashCode());
		result = prime * result + ((lluvia_min == null) ? 0 : lluvia_min.hashCode());
		result = prime * result + ((luz_max == null) ? 0 : luz_max.hashCode());
		result = prime * result + ((luz_min == null) ? 0 : luz_min.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Localizacion other = (Localizacion) obj;
		if (alarma == null) {
			if (other.alarma != null)
				return false;
		} else if (!alarma.equals(other.alarma))
			return false;
		if (lluvia_max == null) {
			if (other.lluvia_max != null)
				return false;
		} else if (!lluvia_max.equals(other.lluvia_max))
			return false;
		if (lluvia_min == null) {
			if (other.lluvia_min != null)
				return false;
		} else if (!lluvia_min.equals(other.lluvia_min))
			return false;
		if (luz_max == null) {
			if (other.luz_max != null)
				return false;
		} else if (!luz_max.equals(other.luz_max))
			return false;
		if (luz_min == null) {
			if (other.luz_min != null)
				return false;
		} else if (!luz_min.equals(other.luz_min))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getLluvia_max() {
		return lluvia_max;
	}

	public void setLluvia_max(Double lluvia_max) {
		this.lluvia_max = lluvia_max;
	}

	public Double getLluvia_min() {
		return lluvia_min;
	}

	public void setLluvia_min(Double lluvia_min) {
		this.lluvia_min = lluvia_min;
	}

	public Double getLuz_max() {
		return luz_max;
	}

	public void setLuz_max(Double luz_max) {
		this.luz_max = luz_max;
	}

	public Double getLuz_min() {
		return luz_min;
	}

	public void setLuz_min(Double luz_min) {
		this.luz_min = luz_min;
	}

	public Timestamp getAlarma() {
		return alarma;
	}

	public void setAlarma(Timestamp alarma) {
		this.alarma = alarma;
	}

	public Localizacion(String nombre, Double lluvia_max, Double lluvia_min, Double luz_max, Double luz_min,
			Timestamp alarma) {
		super();
		this.nombre = nombre;
		this.lluvia_max = lluvia_max;
		this.lluvia_min = lluvia_min;
		this.luz_max = luz_max;
		this.luz_min = luz_min;
		this.alarma = alarma;
	}

}