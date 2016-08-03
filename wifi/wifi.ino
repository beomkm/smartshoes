#include <SPI.h>
#include <WiFi.h>
#include <Wire.h>
#include <I2Cdev.h>
#include <MPU6050.h>

#define AP_RECONN_TIME 4
#define SV_RECONN_TIME 4

//imu
MPU6050 accelgyro;
int ax, ay, az;
int gx, gy, gz;
int mx, my, mz;

//pressure
int FSRpin = 0;
int Vo;
float Rfsr;

int sendable = 0;

char datas[70];


char ssid[] = "AndroidHotspot9036";          //  your network SSID (name) 
char pass[] = "88888888";   // your network password

int status = WL_IDLE_STATUS;
IPAddress serverIP(192, 168, 43, 1);  // Server

// Initialize the client library
WiFiClient client;


void connectSv() {
  Serial.println("Connected to wifi.\n");
  Serial.println("Starting connection...");
  while(1) {
    client.flush();
    client.stop();
    if (client.connect(serverIP, 12345)) {
      Serial.println("connected");
      sendable = 1;
      break;
    }
    else {
      Serial.println("Couldn't connect to the server!");
      Serial.println("Reconnect after few second.");
      delay(SV_RECONN_TIME*1000);
    }
  }
}


void setup() {
  Wire.begin();
  Serial.begin(9600);
  accelgyro.initialize();
  
  Serial.println("Attempting to connect to AP");
  Serial.print("SSID: ");
  Serial.println(ssid);
  
  while(1) {
    status = WiFi.begin(ssid, pass);
    if (status != WL_CONNECTED) { 
      Serial.println("Couldn't connect AP!");
      Serial.println("Reconnect after few second.");
      delay(AP_RECONN_TIME*1000);
    }
    else break;
  }
  
  connectSv();
}

void loop() {
  int i;
  
  //imu
  accelgyro.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
   
  //pressure
  Vo = analogRead(FSRpin);
  Rfsr = ((9.78 * Vo)/(1-(Vo/1023.0))); 

  //sendable = 1;
  if(sendable) {
    //Serial.println(Vo);
    //Serial.println(ax);
   
    datas[0] = lowByte(ax);
    datas[1] = highByte(ax);
    datas[2] = lowByte(ay);
    datas[3] = highByte(ay);
    datas[4] = lowByte(az);
    datas[5] = highByte(az);
    
    datas[6] = lowByte(gx);
    datas[7] = highByte(gx);
    datas[8] = lowByte(gy);
    datas[9] = highByte(gy);
    datas[10] = lowByte(gz);
    datas[11] = highByte(gz);
    
    datas[12] = lowByte(mx);
    datas[13] = highByte(mx);
    datas[14] = lowByte(my);
    datas[15] = highByte(my);
    datas[16] = lowByte(mz);
    datas[17] = highByte(mz);
    
    datas[18] = lowByte((int)Vo);
    datas[19] = highByte((int)Vo);
   
   //Serial.println(ax);
   
   
    for(i=0; i<20; i++) {
      if(datas[i]==0) datas[i] = 0x80;
      else if(datas[i]==0x80) datas[i] = 0x81;
    } 
    client.write(datas);
  }
  
  /*
  if (client.available()) {
  }
  */

  if (!client.connected()) {
    Serial.println("\ndisconnected.");
    client.stop();
    sendable = 0;
    
    delay(1000);
    Serial.println("Trying to reconnect..");
    connectSv();
  }
  delay(50);
}
