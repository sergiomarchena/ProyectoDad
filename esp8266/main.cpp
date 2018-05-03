#include <Arduino.h>
#include <ArduinoJson.h>
#include <ESP8266WebServer.h>
#include <ESP8266Wifi.h>
#include <RestClient.h>
#include <servo.h>

Servo servomotor;
int angulo = 90;

const char* ssid = "penya";
const char* pass = "12345678";
RestClient client = RestClient("192.168.43.100",8083);

void setup() {
  servomotor.attach(D2);
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
  int sensorValue = analogRead(A0);
  //configuracion servonmotor

  //get de la base de datos
  String response = "";
	int statusCode = client.get("/api/sensores/7",&response);
	//Serial.println(statusCode);
  //Serial.println(response);

  const int size_t_capacity = JSON_OBJECT_SIZE(5) + JSON_ARRAY_SIZE(2) + 60;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& root = jsonBuffer.parseObject(response);

   if(!root.success()){
         Serial.println("Parse Failed...");
       }

	double valor = root["valor"];
  Serial.println(valor);


  //actuador
  Serial.println(sensorValue);
  if(sensorValue > 500){
  digitalWrite(D1, HIGH);
  Serial.println("Led on");
}else{
  digitalWrite(D1, LOW);
  Serial.println("Led offf");
}


//put de la base de datos
  StaticJsonBuffer<300> JSONbuffer;
  JsonObject& JSONencoder = JSONbuffer.createObject();
  JSONencoder["id"] = 7;JSONencoder["fecha"] = 23;JSONencoder["nombre"] = "delos";JSONencoder["valor"] = sensorValue;JSONencoder["localizacion_nombre"] = "Habitacion";
  char JSONmessageBuffer[300];
  JSONencoder.printTo(JSONmessageBuffer);
  int statusPut = client.put("/api/sensores/",JSONmessageBuffer);
  Serial.println("isnserccion:");
  Serial.println(statusPut);
  delay(10000);
}