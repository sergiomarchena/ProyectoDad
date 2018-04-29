#include <Arduino.h>
#include <ArduinoJson.h>
#include <ESP8266WebServer.h>
#include <ESP8266Wifi.h>
#include <RestClient.h>


const char* ssid = "Orange-12A1";
const char* pass = "375772F7";

RestClient client = RestClient("192.168.1.102",8083);


void setup() {
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
  //get de la base de datos
  String response = "";
	int statusCode = client.get("/api/sensores/7",&response);
	Serial.println(statusCode);
  Serial.println(response);

  const int size_t_capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(2) + 60;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& root = jsonBuffer.parseObject(response);

   if(!root.success()){
         Serial.println("Parse Failed...");
       }

	double valor = root["valor"];
  Serial.println(valor);

//put de la base de datos
  StaticJsonBuffer<300> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["id"] = 7;JSONencoder["fecha"] = 23;JSONencoder["nombre"] = "delos";JSONencoder["valor"] = 89.0;JSONencoder["localizacion_nombre"] = "Habitacion";
  char JSONmessageBuffer[300];
  JSONencoder.printTo(JSONmessageBuffer);
  int statusPut = client.put("/api/sensores/",JSONmessageBuffer);
  Serial.println(statusPut);
 delay(50000);
}