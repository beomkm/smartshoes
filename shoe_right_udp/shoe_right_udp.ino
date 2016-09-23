#include <SPI.h>
#include <WiFi.h>
#include <WiFiUdp.h>
#include <Wire.h>
#include <I2Cdev.h>
#include <MPU6050.h>

#define AP_RECONN_TIME 3
#define SV_RECONN_TIME 3

//imu
MPU6050 accelgyro;
int ax, ay, az;
int gx, gy, gz;
int mx, my, mz;

//pressure
/*int FSRpin = 0;
int Vo;
float Rfsr;
*/
int UpFSRpin = 0;
int DownFSRpin = 1;
int UpVo;
int DownVo;
float UpRfsr, DownRfsr;

int sendable = 0;

char datas[10];


char ssid[] = "MySSID" ;          //  your network SSID (name) 
char pass[] = "password" ;   // your network password

int status = WL_IDLE_STATUS;
IPAddress serverIP(192, 168, 43, 1);  // Server
//IPAddress serverIP(128, 199, 120, 239);  // Server
int serverPort = 12345;

// Initialize the client library
WiFiUDP client;



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
  
  Serial.println("Connected to AP.");
  sendable = 1;
}

void loop() {
  int i;
  
  //imu
  
  accelgyro.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
  //accelgyro.getMag(
  
  //pressure
  UpVo = analogRead(UpFSRpin);
  DownVo = analogRead(DownFSRpin);
  //UpRfsr = ((9.78 * UpVo)/(1-(UpVo/1023.0)));
  //DownRfsr = ((9.78 * DownVo)/(1-(DownVo/1023.0)));

  //sendable = 1;
  if(sendable) {
    //Serial.println(UpVo);
    Serial.println(mx);
   
    /*
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
    
    datas[18] = lowByte((int)UpVo);
    datas[19] = highByte((int)UpVo);
    datas[20] = lowByte((int)DownVo);
    datas[21] = highByte((int)DownVo);
   
    datas[22] = 0;
    */
 
    datas[0] = lowByte(mx);
    datas[1] = highByte(mx);
    datas[2] = lowByte(my);
    datas[3] = highByte(my);
    datas[4] = lowByte(mz);
    datas[5] = highByte(mz);
    
    datas[6] = lowByte((int)UpVo);
    datas[7] = highByte((int)UpVo);
    datas[8] = lowByte((int)DownVo);
    datas[9] = highByte((int)DownVo);
   
    //datas[10] = 0;
   //Serial.println(ax);
  
  
    for(i=0; i<10; i++) {
      if(datas[i]==0) datas[i] = 0x80;
      else if(datas[i]==0x80) datas[i] = 0x81;
    }
    
    
    client.beginPacket(serverIP, serverPort);
    client.write(datas);
    //client.write(lowByte((int)Vo));
    //client.write(highByte((int)Vo));
    client.endPacket();
 
    
    /**********
    client.write(65);
    client.write(66);
    client.write(67);
    client.write(68);
    client.write(69);
    client.write(70);
    client.write(71);
    client.write(72);
    *****************/
  }
  
  /*
  if (client.available()) {
  }
  */

  delay(100);
}
