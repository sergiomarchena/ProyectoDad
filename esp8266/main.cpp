#include <Arduino.h>
#include <ArduinoJson.h>
#include <ESP8266WebServer.h>
#include <ESP8266Wifi.h>
#include <RestClient.h>

const char* ssid = "penya";
const char* pass = "12345678";
RestClient client = RestClient("192.168.43.100",8083);

void setup() {
  pinMode(D1,OUTPUT);
  Serial.begin(115200);

	WiFi.mode(WIFI_STA);
	WiFi.begin(ssid, pass);

	while(WiFi.status() != WL_CONNECTED){
		delay(1000);
		Serial.print(".");
	}

	Serial.println("Conexión establecida");
	Serial.print("IP asignada: ");
	Serial.println(WiFi.localIP());
}


void loop() {
  //leemos datos del sensor
  int sensorLuz = analogRead(A0);
  int sensorHumedad = random(800,1000);
  //motor gira derecha "cierra" 1
  //motor gira izquierda "abre" 0

  //get de la base de datos
  String localizaciones = "";
  String persianas = "";
	int statusCodeLocalizaciones = client.get("/api/localizaciones/Habitacion",&localizaciones);
  int statusCodepersianas = client.get("/api/persianas/1",&persianas);
  const int size_t_capacity = 300;
  //const int size_t_capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(2) + 60;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& getLocalizacion = jsonBuffer.parseObject(localizaciones);
  JsonObject& getPersianas = jsonBuffer.parseObject(persianas);

 //variables de estado
 int luz_min = getLocalizacion["luz_min"];
 int lluvia_min = getLocalizacion["lluvia_min"];
 int estado_persiana = getPersianas["estado"];
  //Funcionalidad del programa
  if(sensorHumedad > lluvia_min && estado_persiana == 1){
    Serial.println("Esta lloviendo: cierra la ventana y enciende la luz");
    digitalWrite(D1, HIGH);
    //put luz_interior
    StaticJsonBuffer<300> JSONbuffer;
    JsonObject& JSONLuz_interior = JSONbuffer.createObject();
    JSONLuz_interior["id"] = 7;JSONLuz_interior["estado"] = 1;JSONLuz_interior["localizacion_nombre"] = "Habitacion";
    char JSONmessageLuz[300];
    JSONLuz_interior.printTo(JSONmessageLuz);
    int statusPutLuz = client.put("/api/luces_interior/",JSONmessageLuz);
    // put persiana
    JsonObject& JSONPersiana = JSONbuffer.createObject();
    JSONPersiana["id"] = 1;JSONPersiana["estado"] = 0;JSONPersiana["localizacion_nombre"] = "Habitacion";
    char JSONmessagePersiana[300];
    JSONPersiana.printTo(JSONmessagePersiana);
    int statusPutpersiana = client.put("/api/persianas/",JSONmessagePersiana);
    //put actuador
    JsonObject& JSONActuador = JSONbuffer.createObject();
    JSONActuador["id"] = 1;JSONActuador["velocidad"] = 20;JSONActuador["sentido"] = 1;
    char JSONmessageActuador[300];
    JSONActuador.printTo(JSONmessageActuador);
    int statusPutActuador = client.put("/api/actuador/",JSONmessageActuador);

  }else if (sensorHumedad > lluvia_min && estado_persiana == 0){
    Serial.println("Esta lloviendo pero la ventana esta cerrada y la luz encendida");
    digitalWrite(D1, HIGH);
    //put luz_interior
    StaticJsonBuffer<300> JSONbuffer;
    JsonObject& JSONLuz_interior = JSONbuffer.createObject();
    JSONLuz_interior["id"] = 7;JSONLuz_interior["estado"] = 1;JSONLuz_interior["localizacion_nombre"] = "Habitacion";
    char JSONmessageLuz[300];
    JSONLuz_interior.printTo(JSONmessageLuz);
    int statusPutLuz = client.put("/api/luces_interior/",JSONmessageLuz);
    //put persiana
    JsonObject& JSONPersiana = JSONbuffer.createObject();
    JSONPersiana["id"] = 1;JSONPersiana["estado"] = 0;JSONPersiana["localizacion_nombre"] = "Habitacion";
    char JSONmessagePersiana[300];
    JSONPersiana.printTo(JSONmessagePersiana);
    int statusPutPersiana = client.put("/api/persianas/",JSONmessagePersiana);

  }else{
    if(sensorLuz < luz_min && estado_persiana == 1){
      Serial.println("Es de noche y la ventana esta abierta: cierra la ventana y enciende la luz");
      digitalWrite(D1, HIGH);
      Serial.println(sensorLuz);
      Serial.println(estado_persiana);
      //put Luz interior
      StaticJsonBuffer<300> JSONbuffer;
      JsonObject& JSONLuz_interior = JSONbuffer.createObject();
      JSONLuz_interior["id"] = 7;JSONLuz_interior["estado"] = 1;JSONLuz_interior["localizacion_nombre"] = "Habitacion";
      char JSONmessageLuz[300];
      JSONLuz_interior.printTo(JSONmessageLuz);
      int statusPutLuz = client.put("/api/luces_interior/",JSONmessageLuz);
      // put persiana
      JsonObject& JSONPersiana = JSONbuffer.createObject();
      JSONPersiana["id"] = 1;JSONPersiana["estado"] = 0;JSONPersiana["localizacion_nombre"] = "Habitacion";
      char JSONmessagePersiana[300];
      JSONPersiana.printTo(JSONmessagePersiana);
      int statusPutPersiana = client.put("/api/persianas/",JSONmessagePersiana);
      //put actuador
      JsonObject& JSONActuador = JSONbuffer.createObject();
      JSONActuador["id"] = 1;JSONActuador["velocidad"] = 20;JSONActuador["sentido"] = 1;
      char JSONmessageActuador[300];
      JSONActuador.printTo(JSONmessageActuador);
      int statusPutActuador = client.put("/api/actuador/",JSONmessageActuador);

    }else if (sensorLuz > luz_min && estado_persiana == 0 && sensorHumedad < lluvia_min){
      Serial.println("Es de dia y la ventana esta cerrada: apaga la luz abre ventana");
      digitalWrite(D1, LOW);
      //put luz_interior
      StaticJsonBuffer<300> JSONbuffer;
      JsonObject& JSONLuz_interior = JSONbuffer.createObject();
      JSONLuz_interior["id"] = 7;JSONLuz_interior["estado"] = 0;JSONLuz_interior["localizacion_nombre"] = "Habitacion";
      char JSONmessageLuz[300];
      JSONLuz_interior.printTo(JSONmessageLuz);
      int statusPutLuz = client.put("/api/luces_interior/",JSONmessageLuz);
      //put persiana
      JsonObject& JSONPersiana = JSONbuffer.createObject();
      JSONPersiana["id"] = 1;JSONPersiana["estado"] = 1;JSONPersiana["localizacion_nombre"] = "Habitacion";
      char JSONmessagePersiana[300];
      JSONPersiana.printTo(JSONmessagePersiana);
      int statusPutPersiana = client.put("/api/persianas/",JSONmessagePersiana);
      //put Actuador
      JsonObject& JSONActuador = JSONbuffer.createObject();
      JSONActuador["id"] = 1;JSONActuador["velocidad"] = 20;JSONActuador["sentido"] = 0;
      char JSONmessageActuador[300];
      JSONActuador.printTo(JSONmessageActuador);
      int statusPutActuador = client.put("/api/actuador/",JSONmessageActuador);

    }else if(sensorLuz > luz_min && estado_persiana == 1 && sensorHumedad < lluvia_min){
      Serial.println("Es de dia y la ventana esta abierta");
      digitalWrite(D1, LOW);
      //put luz_interior
      StaticJsonBuffer<300> JSONbuffer;
      JsonObject& JSONLuz_interior = JSONbuffer.createObject();
      JSONLuz_interior["id"] = 7;JSONLuz_interior["estado"] = 0;JSONLuz_interior["localizacion_nombre"] = "Habitacion";
      char JSONmessageLuz[300];
      JSONLuz_interior.printTo(JSONmessageLuz);
      int statusPutLuz = client.put("/api/luces_interior/",JSONmessageLuz);
      //put persiana
      JsonObject& JSONPersiana = JSONbuffer.createObject();
      JSONPersiana["id"] = 1;JSONPersiana["estado"] = 1;JSONPersiana["localizacion_nombre"] = "Habitacion";
      char JSONmessagePersiana[300];
      JSONPersiana.printTo(JSONmessagePersiana);
      int statusPutPersiana = client.put("/api/persianas/",JSONmessagePersiana);
    }else if (sensorLuz < luz_min && estado_persiana == 0){
      Serial.println("Es de noche y la ventana esta cerrada");
      digitalWrite(D1, HIGH);
      //put luz interior
      StaticJsonBuffer<300> JSONbuffer;
      JsonObject& JSONLuz_interior = JSONbuffer.createObject();
      JSONLuz_interior["id"] = 7;JSONLuz_interior["estado"] = 1;JSONLuz_interior["localizacion_nombre"] = "Habitacion";
      char JSONmessageLuz[300];
      JSONLuz_interior.printTo(JSONmessageLuz);
      int statusPutLuz = client.put("/api/luces_interior/",JSONmessageLuz);
      //put persianas
      JsonObject& JSONPersiana = JSONbuffer.createObject();
      JSONPersiana["id"] = 1;JSONPersiana["estado"] = 0;JSONPersiana["localizacion_nombre"] = "Habitacion";
      char JSONmessagePersiana[300];
      JSONPersiana.printTo(JSONmessagePersiana);
      int statusPutPersiana = client.put("/api/persianas/",JSONmessagePersiana);
    }
  }

//put Sensor de luz
  StaticJsonBuffer<300> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["id"] = 7;JSONencoder["fecha"] = 23;JSONencoder["nombre"] = "delos";JSONencoder["valor"] = sensorLuz;JSONencoder["localizacion_nombre"] = "Habitacion";
  char JSONmessageBuffer[300];
  JSONencoder.printTo(JSONmessageBuffer);
  int statusPut = client.put("/api/sensores/",JSONmessageBuffer);
  Serial.println("Sensor Actualizado");
  delay(20000);
}