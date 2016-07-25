
#include <SoftwareSerial.h>
#include <stdlib.h>
//(TX, RX)
SoftwareSerial BTSerial(6, 7); 

String str;
char buf[20];
byte buffer[1024];
int bufferPosition; 



int incomingByte = 10000;  
int rcvData;
int temp = 0;


void setup()
{
    Serial.begin(9600);
    BTSerial.begin(9600);      
}

void loop()
{

    if (Serial.available() >= 20) {
        Serial.readBytes(buf, 20);
        //incomingByte = Serial.read();
        //rcvData = readInt();
        Serial.println(Serial.available());
        //BTSerial.write(lowByte(rcvData));
        //itoa(rcvData, buf, 10);
        BTSerial.write(buf, 20);
    }
}


/*
int readInt()
{
    int result = 0;
    result = (Serial.read()<<8) | Serial.read();

    return result;
}
*/






