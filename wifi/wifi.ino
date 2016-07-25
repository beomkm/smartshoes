#include <SPI.h>
#include <WiFi.h>
#include <Wire.h>
#include <I2Cdev.h>
#include <MPU6050.h>

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
    Serial.println("\nStarting connection...");
    // if you get a connection, report back via serial:
    if (client.connect(serverIP, 12345)) {
      Serial.println("connected");
      // Make a HTTP request:
      sendable = 1;
    }
  }
  
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
    Serial.println(Vo);
    Serial.println(ax);
    
    //imu 
    /*
    client.write(lowByte(ax) ); 
    client.write(highByte(ax)); 
    client.write(lowByte(ay) ); 
    client.write(highByte(ay)); 
    client.write(lowByte(az) ); 
    client.write(highByte(az)); 
    
    client.write(lowByte(gx) ); 
    client.write(highByte(gx)); 
    client.write(lowByte(gy) ); 
    client.write(highByte(gy)); 
    client.write(lowByte(gz) ); 
    client.write(highByte(gz)); 
    
    client.write(lowByte(mx) ); 
    client.write(highByte(mx)); 
    client.write(lowByte(my) ); 
    client.write(highByte(my)); 
    client.write(lowByte(mz) ); 
    client.write(highByte(mz)); 
    
    //pressure
    client.write(lowByte((int)Vo));
    client.write(highByte((int)Vo));
    */
    
    //sprintf(datas,"%ds%ds%ds%d",-1,-2,1,2);
    //client.write(datas);
    
    
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
  
  
  if (client.available()) {

  }

  if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
    for(;;)
      ;
  }
  delay(50);
}