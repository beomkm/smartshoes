#include <SPI.h>
#include <WiFi.h>
#include <WiFiUdp.h>
#include <Wire.h>
#include <I2Cdev.h>
#include <MPU6050.h>

//pressure
int FSRpin = 0;
int Vo;
float Rfsr;

//imu
MPU6050 accelgyro;
int ax, ay, az;
int gx, gy, gz;
int mx, my, mz;

int sendable = 0;

char datas[20];

char ssid[] = "AndroidHotspot9036";          //  your network SSID (name) 
char pass[] = "88888888";   // your network password

int status = WL_IDLE_STATUS;
IPAddress serverIP(192, 168, 43, 1); 
int serverPort = 12345;

// Initialize the client library
WiFiUDP client;

void setup() {
  Wire.begin();
  Serial.begin(9600);
  accelgyro.initialize();
  
  
  Serial.println("Attempting to connect to WPA network...");
  Serial.print("SSID: ");
  Serial.println(ssid);

  status = WiFi.begin(ssid, pass);
  if ( status != WL_CONNECTED) { 
    Serial.println("Couldn't get a wifi connection");
    // don't do anything else:
    while(true);
  } 
  else {
    Serial.println("Connected to wifi");
    
    Serial.println("\nUDP is ready");
    // if you get a connection, report back via serial:
    sendable = 1;
    //client.begin(serverPort);
  }
  
}

void loop() {
  
  //imu
  accelgyro.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
   
  //pressure
  Vo = analogRead(FSRpin);
  Rfsr = ((9.78 * Vo)/(1-(Vo/1023.0))); 
  
  
  if(sendable) {
    Serial.println(Vo);
    //client.write(lowByte((int)Vo));
    //client.write(highByte((int)Vo));
    
    //imu 
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
    
    //pressure
    datas[18] = lowByte((int)Vo);
    datas[19] = highByte((int)Vo);
  
    client.beginPacket(serverIP, serverPort);
    client.write(datas);
    //client.write(lowByte((int)Vo));
    //client.write(highByte((int)Vo));
    client.endPacket();
  }
  
  if (client.available()) {

  }
  
  
  delay(50);
}
