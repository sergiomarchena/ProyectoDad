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
  String response = "";
	int statusCode = client.get("/api/sensores/1",&response);
	Serial.println(statusCode);
	Serial.println(response);

	const int size_t_capacity =
		JSON_OBJECT_SIZE(2) +
		JSON_ARRAY_SIZE(5) + 120;

	DynamicJsonBuffer jsonBuffer(size_t_capacity);
	JsonObject& root =jsonBuffer.parseObject(response);

	const char* id = root["id"];
	double valor = root["valor"];
  Serial.println(id);
  Serial.println(valor);

}
