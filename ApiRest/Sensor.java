package dad.us.dadVertx;

import java.sql.Timestamp;

public class Sensor {
	private Integer id;
	private Timestamp fecha;
	private String nombre;
	private Double valor;
	private String localizacion_nombre;
	
	public Sensor() {
		this(0,null,null,0.0,"");
	}

	@Override
	public String toString() {
		return "Sensor [id=" + id + ", fecha=" + fecha + ", nombre=" + nombre +",  valor=" + valor + ", localizacion_nombre="
				+ localizacion_nombre + "]";
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((localizacion_nombre == null) ? 0 : localizacion_nombre.hashCode());
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
		Sensor other = (Sensor) obj;
		if (fecha == null) {
			if (other.fecha != null)
				return false;
		} else if (!fecha.equals(other.fecha))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (localizacion_nombre == null) {
			if (other.localizacion_nombre != null)
				return false;
		} else if (!localizacion_nombre.equals(other.localizacion_nombre))
			return false;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getFecha() {
		return fecha;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getLocalizacion_nombre() {
		return localizacion_nombre;
	}

	public void setLocalizacion_nombre(String localizacion_nombre) {
		this.localizacion_nombre = localizacion_nombre;
	}

	public Sensor(Integer id, Timestamp fecha,String nombre, Double valor, String localizacion_nombre) {
		super();
		this.id = id;
		this.fecha = fecha;
		this.nombre = nombre;
		this.valor = valor;
		this.localizacion_nombre = localizacion_nombre;
	}

}
