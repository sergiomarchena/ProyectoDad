package dad.us.dadVertx;

import java.sql.Date;

public class Actuador {
		private Integer id;
		private Date fecha;
		private Boolean sentido;
		
		public Actuador(){
			this(0,null,false);
		}

		public Actuador(Integer id, Date fecha, Boolean sentido) {
			super();
			this.id = id;
			this.fecha = fecha;
			this.sentido = sentido;
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Date getFecha() {
			return fecha;
		}

		public void setfecha(Date fecha) {
			this.fecha = fecha;
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
			result = prime * result + ((fecha == null) ? 0 : fecha.hashCode());
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
			if (fecha == null) {
				if (other.fecha != null)
					return false;
			} else if (!fecha.equals(other.fecha))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "Actuador [id=" + id + ", velocidad=" + fecha + ", sentido=" + sentido + "]";
		}
}