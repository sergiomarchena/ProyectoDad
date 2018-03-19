package dad.us.dadVertx;

public class Persiana {
	private Integer id;
	private boolean estado;
	private String localizacion_nombre;
	
	public Persiana() {
		this(0,false,"");
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public String getLocalizacion_nombre() {
		return localizacion_nombre;
	}

	public void setLocalizacion_nombre(String localizacion_nombre) {
		this.localizacion_nombre = localizacion_nombre;
	}

	public Persiana(Integer id, boolean estado, String localizacion_nombre) {
		super();
		this.id = id;
		this.estado = estado;
		this.localizacion_nombre = localizacion_nombre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (estado ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((localizacion_nombre == null) ? 0 : localizacion_nombre.hashCode());
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
		Persiana other = (Persiana) obj;
		if (estado != other.estado)
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
		return true;
	}

	@Override
	public String toString() {
		return "Persiana [id=" + id + ", estado=" + estado + ", localizacion_nombre=" + localizacion_nombre + "]";
	}

}

