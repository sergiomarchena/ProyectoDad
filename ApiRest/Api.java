package dad.us.dadVertx;

import java.util.Calendar;
import java.util.GregorianCalendar;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.asyncsql.MySQLClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.UpdateResult;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;



public class Api extends AbstractVerticle{
	
	private SQLClient mySQLClient;
	
	public void start(Future<Void> startFuture) {
		
		JsonObject mySQLClientConfig = new JsonObject()
				.put("host", "127.0.0.1")
				.put("port", 3306)
				.put("database", "dad")
				.put("username", "root")
				.put("password", "sergio");
		
		mySQLClient = MySQLClient.createShared
				(vertx, mySQLClientConfig);

		Router router = Router.router(vertx);

		vertx.createHttpServer().requestHandler(router::accept).listen(8083, res -> {
			if (res.succeeded()) {
				System.out.println("Servidor REST desplegado");
			} else {
				System.out.println("Error: " + res.cause());
			}
		});

		router.route("/api/*").handler(BodyHandler.create());
		router.get("/api/localizaciones/:nombre").handler(this::getLocalizaciones);
		router.get("/api/luces_interior/:id").handler(this::getLucesInterior);
		router.get("/api/persianas/:id").handler(this::getPersianas);
		router.get("/api/sensores/:id").handler(this::getSensores);
		router.get("/api/actuador/:id").handler(this::getActuadores);
		router.put("/api/localizaciones").handler(this::putLocalizaciones);
		router.put("/api/luces_interior").handler(this::putLucesInterior);
		router.put("/api/persianas").handler(this::putPersianas);
		router.put("/api/sensores").handler(this::putSensores);
		router.put("/api/actuador").handler(this::putActuadores);	
	}

	private void getLocalizaciones(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("nombre");
		if (paramStr != null) {
			try {
				String param = paramStr;
				
				mySQLClient.getConnection(conn -> {
					if (conn.succeeded()) {
						SQLConnection connection = conn.result();
						String query = "SELECT nombre, lluvia_max, lluvia_min, luz_max, luz_min, alarma  "
								+ "FROM localizaciones "
								+ "WHERE nombre = ?";
						JsonArray paramQuery = new JsonArray()
								.add(param);
						connection.queryWithParams(
								query, 
								paramQuery, 
								res -> {
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows()));
									}else {
										routingContext.response().setStatusCode(400).end(
												"Error: " + res.cause());	
									}
								});
					}else {
						routingContext.response().setStatusCode(400).end(
								"Error: " + conn.cause());
					}
				});
								
			}catch (ClassCastException e) {
				routingContext.response().setStatusCode(400).end();
			}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	private void getLucesInterior(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if (paramStr != null) {
			try {
				int param = Integer.parseInt(paramStr);
				
				mySQLClient.getConnection(conn -> {
					if (conn.succeeded()) {
						SQLConnection connection = conn.result();
						String query = "SELECT id, estado, localizacion_nombre  "
								+ "FROM luces_interior "
								+ "WHERE id = ?";
						JsonArray paramQuery = new JsonArray()
								.add(param);
						connection.queryWithParams(
								query, 
								paramQuery, 
								res -> {
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows()));
									}else {
										routingContext.response().setStatusCode(400).end(
												"Error: " + res.cause());	
									}
								});
					}else {
						routingContext.response().setStatusCode(400).end(
								"Error: " + conn.cause());
					}
				});
								
			}catch (ClassCastException e) {
				routingContext.response().setStatusCode(400).end();
			}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void getPersianas(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if (paramStr != null) {
			try {
				int param = Integer.parseInt(paramStr);
				
				mySQLClient.getConnection(conn -> {
					if (conn.succeeded()) {
						SQLConnection connection = conn.result();
						String query = "SELECT id, estado, localizacion_nombre, id_actuador  "
								+ "FROM persianas "
								+ "WHERE id = ?";
						JsonArray paramQuery = new JsonArray()
								.add(param);
						connection.queryWithParams(
								query, 
								paramQuery, 
								res -> {
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows()));
									}else {
										routingContext.response().setStatusCode(400).end(
												"Error: " + res.cause());	
									}
								});
					}else {
						routingContext.response().setStatusCode(400).end(
								"Error: " + conn.cause());
					}
				});
								
			}catch (ClassCastException e) {
				routingContext.response().setStatusCode(400).end();
			}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	private void getSensores(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if (paramStr != null) {
			try {
				int param = Integer.parseInt(paramStr);
				
				mySQLClient.getConnection(conn -> {
					if (conn.succeeded()) {
						SQLConnection connection = conn.result();
						String query = "SELECT id, fecha, nombre, valor, localizacion_nombre  "
								+ "FROM sensores "
								+ "WHERE id = ? and fecha = (SELECT max(fecha) from sensores)";
						JsonArray paramQuery = new JsonArray()
								.add(param);
						connection.queryWithParams(
								query, 
								paramQuery, 
								res -> {
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows()));
									}else {
										routingContext.response().setStatusCode(400).end(
												"Error: " + res.cause());	
									}
								});
					}else {
						routingContext.response().setStatusCode(400).end(
								"Error: " + conn.cause());
					}
				});
								
			}catch (ClassCastException e) {
				routingContext.response().setStatusCode(400).end();
			}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	private void getActuadores(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if (paramStr != null) {
			try {
				int param = Integer.parseInt(paramStr);
				
				mySQLClient.getConnection(conn -> {
					if (conn.succeeded()) {
						SQLConnection connection = conn.result();
						String query = "SELECT id, velocidad, sentido  "
								+ "FROM actuadores "
								+ "WHERE id = ?";
						JsonArray paramQuery = new JsonArray()
								.add(param);
						connection.queryWithParams(
								query, 
								paramQuery, 
								res -> {
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows()));
									}else {
										routingContext.response().setStatusCode(400).end(
												"Error: " + res.cause());	
									}
								});
					}else {
						routingContext.response().setStatusCode(400).end(
								"Error: " + conn.cause());
					}
				});
								
			}catch (ClassCastException e) {
				routingContext.response().setStatusCode(400).end();
			}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void putLocalizaciones(RoutingContext routingContext) {
		Localizacion state = Json.decodeValue(routingContext.getBodyAsString(), Localizacion.class);
				
		String update = "INSERT INTO localizaciones(nombre, lluvia_max, lluvia_min, luz_max, luz_min, alarma) VALUES ('"+state.getNombre() + "','" + state.getLluvia_max() + "','" + state.getLluvia_min()+"','"+ state.getLuz_max()+ "','"+ state.getLuz_min()+"','"+ state.getAlarma()+"')";
		mySQLClient.update(update, res -> {
		      if (res.succeeded()) {

		        UpdateResult result = res.result();
		        System.out.println("Updated no. of rows: " + result.getUpdated());
		        System.out.println("Generated keys: " + result.getKeys());
		        routingContext.response().setStatusCode(200).end();
		      } else {
		        routingContext.response().setStatusCode(400).end();
		      }
		    });
	}
	private void putLucesInterior(RoutingContext routingContext) {
		LuzInterior state = Json.decodeValue(routingContext.getBodyAsString(), LuzInterior.class);
				
		String update = "UPDATE luces_interior SET estado =" +state.isEstado()+ " where id ="  + state.getId();
		mySQLClient.update(update, res -> {
		      if (res.succeeded()) {

		        UpdateResult result = res.result();
		        System.out.println("Updated no. of rows: " + result.getUpdated());
		        System.out.println("Generated keys: " + result.getKeys());
		        routingContext.response().setStatusCode(200).end();
		      } else {
		    	  routingContext.response().setStatusCode(400).end();
		      }
		    });
	}
	
	private void putPersianas(RoutingContext routingContext) {
		Persiana state = Json.decodeValue(routingContext.getBodyAsString(), Persiana.class);
				
		String update = "UPDATE persianas SET estado=" + state.isEstado() + " WHERE id_actuador=" + state.getId();
		mySQLClient.update(update, res -> {
		      if (res.succeeded()) {

		        UpdateResult result = res.result();
		        System.out.println("Updated no. of rows: " + result.getUpdated());
		        System.out.println("Generated keys: " + result.getKeys());
		        routingContext.response().setStatusCode(200).end();
		      } else {
		    	routingContext.response().setStatusCode(400).end();
		      }
		    });
	}
	private void putSensores(RoutingContext routingContext) {
		Sensor state = Json.decodeValue(routingContext.getBodyAsString(), Sensor.class);
		Calendar fecha = new GregorianCalendar();
        int anyo = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        int hora = fecha.get(Calendar.HOUR_OF_DAY);
        int minuto = fecha.get(Calendar.MINUTE);
        int segundo = fecha.get(Calendar.SECOND);
        
        
		String update = "INSERT INTO sensores(id, fecha, nombre, valor, localizacion_nombre) VALUES ("+state.getId() + "," + "STR_TO_DATE('" + mes + "-" + dia + "-"+ anyo + " " + hora + ":" + minuto + ":" + segundo + "','%m-%d-%Y %H:%i:%s')" + ",'" + state.getNombre()+"',"+ state.getValor()+ ",'"+ state.getLocalizacion_nombre()+"')";
		mySQLClient.update(update, res -> {
		      if (res.succeeded()) {
		        UpdateResult result = res.result();
		        System.out.println("Updated no. of rows: " + result.getUpdated());
		        System.out.println("Generated keys: " + result.getKeys());
		        routingContext.response().setStatusCode(200).end();
		      } else {
		    	  routingContext.response().setStatusCode(400).end();
		      }
		    });
	}
	private void putActuadores(RoutingContext routingContext) {
		Actuador state = Json.decodeValue(routingContext.getBodyAsString(), Actuador.class);
				
		String update = "UPDATE actuadores SET sentido="+state.getSentido() + " Where id = " + state.getId();
		mySQLClient.update(update, res -> {
		      if (res.succeeded()) {

		        UpdateResult result = res.result();
		        System.out.println("Updated no. of rows: " + result.getUpdated());
		        System.out.println("Generated keys: " + result.getKeys());
		        routingContext.response().setStatusCode(200).end();
		      } else {
		    	  routingContext.response().setStatusCode(400).end();
		      }
		    });
	}
}


