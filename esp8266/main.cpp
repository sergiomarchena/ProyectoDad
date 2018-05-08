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

	Serial.println("Conexi�n establecida");
	Serial.print("IP asignada: ");
	Serial.println(WiFi.localIP());
}


void loop() {
  //leemos datos del sensor
  int sensorLuz = analogRead(A0);
  int sensorHumedad = random(800,1000);
  int estado_persiana = random(0,2); // borrar cuando tengamos el get
  int lluvia_min = 900;// borrar cuando tengamos el get
  int luz_min = 500;// borrar cuando tengamos el get

  //get de la base de datos
  String sensores = "";
  //String sensorHumedad="";
  String localizaciones = "";
  String persianas = "";

	int statusCodeSensorLuz = client.get("/api/sensores/7",&sensores);
	//int statusCodeSensorHumedad = client.get("/api/sensores/1",&sensorHumedad);
	int statusCodeLocalizaciones = client.get("/api/Localizaciones/Habitacion",&localizaciones);
  int statusCodepersianas = client.get("/api/persianas/7",&persianas);
	//Serial.println(statusCode);
  //Serial.println(response);

  const int size_t_capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(2) + 60;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& root = jsonBuffer.parseObject(sensores);

  //  if(!root.success()){
  //        Serial.println("Parse Failed...");
  //      }
  //
	// double valor = root["valor"];
  // Serial.println(valor);


  //actuador
  Serial.println(sensorLuz);
  Serial.println(sensorHumedad);
  Serial.println(estado_persiana);
  if(sensorHumedad > lluvia_min && estado_persiana == 1){
    digitalWrite(D1, HIGH);
    Serial.println("Led on");//put luz_interior
    Serial.println("cierra ventana"); // put persiana
    Serial.println("motor derecha"); //put actuador
  }else if (sensorHumedad > lluvia_min && estado_persiana == 0){
    digitalWrite(D1, HIGH);
    Serial.println("Led on");
    Serial.println("ventana ya cerrada"); 
    Serial.println("no hacer nada");
  }else{
    if(sensorLuz < luz_min && estado_persiana == 1){
      digitalWrite(D1, HIGH);
      Serial.println(sensorLuz);
      Serial.println(estado_persiana);
      Serial.println("Led on"); // put luz_interior
      Serial.println("cierra ventana"); // put persiana
      Serial.println("motor derecha"); //put actuador
    }else if (sensorLuz > luz_min && estado_persiana == 0 && sensorHumedad < lluvia_min){
      digitalWrite(D1, LOW);
      Serial.println("Led off");//put luz_interior
      Serial.println("abre ventana");//put persiana
      Serial.println("motor izquierda"); //put actuado
    }else if(sensorLuz > luz_min && estado_persiana == 1 && sensorHumedad < lluvia_min){
      digitalWrite(D1, LOW);
      Serial.println("Led off");
      Serial.println("ventana abierta no muevas motor");
    }else if (sensorLuz < luz_min && estado_persiana == 0){
      digitalWrite(D1, HIGH);
      Serial.println("Led on"); 
      Serial.println("ventana ya cerrada");
    }
  }



//put de la base de datos
  StaticJsonBuffer<300> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["id"] = 7;JSONencoder["fecha"] = 23;JSONencoder["nombre"] = "delos";JSONencoder["valor"] = sensorLuz;JSONencoder["localizacion_nombre"] = "Habitacion";
  char JSONmessageBuffer[300];
  JSONencoder.printTo(JSONmessageBuffer);
  int statusPut = client.put("/api/sensores/",JSONmessageBuffer);
  Serial.println("isnserccion:");
  Serial.println(statusPut);
  delay(20000);
}
