#include "FirebaseESP8266.h"
#include <ESP8266WiFi.h>
#include <DHT.h>


#define FIREBASE_HOST "temperaturedb-6ec55-default-rtdb.asia-southeast1.firebasedatabase.app"
#define FIREBASE_AUTH "fEPOVA1EzHPMYFTjMCu6dYpeEXX1i3ANNFFbKkJh"
#define WIFI_SSID "ALVIAN"
#define WIFI_PASSWORD "alvian21"

#define DHTPIN 2
#define ac1 D0
#define ac2 D1

#define DHTTYPE    DHT11
DHT dht(DHTPIN, DHTTYPE);

FirebaseData firebaseData1;
FirebaseData firebaseData2;
FirebaseData firebaseData;
FirebaseJson json;

const int pinsoil=A0;

String ac1Status;
String ac2Status;

void setup()
{

  Serial.begin(9600);
  dht.begin();
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);

  pinMode(ac1, OUTPUT);
  pinMode(ac2, OUTPUT);
  digitalWrite(ac1, LOW);
  digitalWrite(ac2, LOW);

}
void bacaDB1(){
          
          Firebase.get(firebaseData1, "/TemperatureDB/ac1");
          ac1Status = firebaseData1.stringData();
     
          if (ac1Status == "ON"){
            Serial.println("AC1 ON");
            digitalWrite(ac1, HIGH);
            }
          else if (ac1Status == "OFF"){
            Serial.println("AC1 OFF");
            digitalWrite(ac1, LOW);
            }
          else {Serial.println("error");}
}

void bacaDB2(){
          
          Firebase.get(firebaseData2, "/TemperatureDB/ac2");
          ac2Status = firebaseData2.stringData();  

          if (ac2Status == "ON"){
            Serial.println("AC2 ON");
            digitalWrite(ac2, HIGH);
            }
          else if (ac2Status == "OFF"){
            Serial.println("AC2 OFF");
            digitalWrite(ac2, LOW);
            }
          else {Serial.println("error");}
}
  
void sensorUpdate(){
  int h = dht.readHumidity();
  int t = dht.readTemperature();
  int s = analogRead(pinsoil);

  if (isnan(h) || isnan(t)) {
    Serial.println(F("Failed to read from DHT sensor!"));
    return;
  }

  Serial.print("Soil: ");
  Serial.print(s);
  Serial.print("Humidity: ");
  Serial.print(h);
  Serial.print("%  Temperature: ");
  Serial.print(t);
  Serial.println("C  ,");

  Firebase.setFloat(firebaseData, "/TemperatureDB/temperature", t);
  Firebase.setFloat(firebaseData, "/TemperatureDB/humidity", h);
  Firebase.setFloat(firebaseData, "/TemperatureDB/soil", s);
}

void loop() {
  bacaDB1();
  bacaDB2();
  sensorUpdate();
  delay(50);
}
