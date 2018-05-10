package dad.us.dadVertx;

public class Actuador {
		private Integer id;
		private Double velocidad;
		private Boolean sentido;
		
		public Actuador(){
			this(0,null,false);
		}

		public Actuador(Integer id, Double velocidad, Boolean sentido) {
			super();
			this.id = id;
			this.velocidad = velocidad;
			this.sentido = sentido;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Double getVelocidad() {
			return velocidad;
		}

		public void setFecha(Double velocidad) {
			this.velocidad = velocidad;
		}

		public Boolean getSentido() {
			return sentido;
		}

		public void setSentido(Boolean sentido) {
			this.sentido = sentido;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((sentido == null) ? 0 : sentido.hashCode());
			result = prime * result + ((velocidad == null) ? 0 : velocidad.hashCode());
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
			Actuador other = (Actuador) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			if (sentido == null) {
				if (other.sentido != null)
					return false;
			} else if (!sentido.equals(other.sentido))
				return false;
			if (velocidad == null) {
				if (other.velocidad != null)
					return false;
			} else if (!velocidad.equals(other.velocidad))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Actuador [id=" + id + ", velocidad=" + velocidad + ", sentido=" + sentido + "]";
		}
}