package dad.us.dadVertx;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
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
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttEndpoint;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;




public class Api extends AbstractVerticle{
	
	private SQLClient mySQLClient;
	private static Multimap<String, MqttEndpoint> clientTopics;
	
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
		router.get("/api/mqttmanualabre/").handler(this::getMqttManualAbre);
		router.get("/api/mqttmanualcierra/").handler(this::getMqttManualCierra);
		router.get("/api/mqttautomatico/").handler(this::getMqttAutomatico);
		
		
		clientTopics = HashMultimap.create();

		
		MqttServer mqttServer = MqttServer.create(vertx);
		init(mqttServer);
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		
		

		MqttClient mqttClient2 = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient2.connect(1883, "192.168.43.100", s -> {

			mqttClient2.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
					
					mqttClient2.publishHandler(new Handler<MqttPublishMessage>() {
						@Override
						public void handle(MqttPublishMessage arg0) {
							
							System.out.println("Mensaje recibido por el cliente 2: " + arg0.payload().toString());							
						}
					});
				}
			});

		});
	}
	
	private static void init(MqttServer mqttServer) {
		mqttServer.endpointHandler(endpoint -> {
			
			System.out.println("Nuevo cliente MQTT [" + endpoint.clientIdentifier()
					+ "] solicitando suscribirse [Id de sesión: " + endpoint.isCleanSession() + "]");
			
			endpoint.accept(false);
			handleSubscription(endpoint);
			handleUnsubscription(endpoint);
			publishHandler(endpoint);
			handleClientDisconnect(endpoint);
		}).listen(ar -> {
			if (ar.succeeded()) {
				System.out.println("MQTT server está a la escucha por el puerto " + ar.result().actualPort());
			} else {
				System.out.println("Error desplegando el MQTT server");
				ar.cause().printStackTrace();
			}
		});
	}
	private static void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subscribe -> {
			
			List<MqttQoS> grantedQosLevels = new ArrayList<>();
			for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
				System.out.println("Suscripción al topic " + s.topicName() + " con QoS " + s.qualityOfService());
				grantedQosLevels.add(s.qualityOfService());
				
				// Añadimos al cliente en la lista de clientes suscritos al topic
				clientTopics.put(s.topicName(), endpoint);
			}
		
			
			endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
		});
	}

	
	private static void handleUnsubscription(MqttEndpoint endpoint) {
		endpoint.unsubscribeHandler(unsubscribe -> {
			for (String t : unsubscribe.topics()) {
				
				clientTopics.remove(t, endpoint);
				System.out.println("Eliminada la suscripción del topic " + t);
			}
			// Informamos al cliente que la desuscripción se ha realizado
			endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
		});
	}

	
	private static void publishHandler(MqttEndpoint endpoint) {
		endpoint.publishHandler(message -> {
			
			handleMessage(message, endpoint);
		}).publishReleaseHandler(messageId -> {
			
			endpoint.publishComplete(messageId);
		});
	}

	
	private static void handleMessage(MqttPublishMessage message, MqttEndpoint endpoint) {
		System.out.println("Mensaje publicado por el cliente " + endpoint.clientIdentifier() + " en el topic "
				+ message.topicName());
		System.out.println("    Contenido del mensaje: " + message.payload().toString());
		
		
		System.out.println("Origen: " + endpoint.clientIdentifier());
		for (MqttEndpoint client: clientTopics.get(message.topicName())) {
			System.out.println("Destino: " + client.clientIdentifier());
			if (!client.clientIdentifier().equals(endpoint.clientIdentifier()))
				client.publish(message.topicName(), message.payload(), message.qosLevel(), message.isDup(), message.isRetain());
		}
		
		if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			String topicName = message.topicName();
			switch (topicName) {
			}
			endpoint.publishAcknowledge(message.messageId());
		} else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
			
			endpoint.publishRelease(message.messageId());
		}
	}

	
	private static void handleClientDisconnect(MqttEndpoint endpoint) {
		endpoint.disconnectHandler(h -> {
			
			Stream.of(clientTopics.keySet())
				.filter(e -> clientTopics.containsEntry(e, endpoint))
				.forEach(s -> clientTopics.remove(s, endpoint));
			System.out.println("El cliente remoto se ha desconectado [" + endpoint.clientIdentifier() + "]");
		});
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
									connection.close();
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows().get(0)));
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
									connection.close();
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows().get(0)));
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
									connection.close();
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows().get(0)));
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
									connection.close();
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows().get(0)));
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
									connection.close();
									if (res.succeeded()) {
										routingContext.response().end(Json.encodePrettily(res.result().getRows().get(0)));
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
	
	private void getMqttManualAbre(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.100", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("manual,abre"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();
		});
	}
	private void getMqttManualCierra(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.100", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("manual,cierra"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();
		});
	}
	private void getMqttAutomatico(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.100", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("automatico"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();

		});
	
	}
}
