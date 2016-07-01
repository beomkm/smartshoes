
#include <SoftwareSerial.h>
#include <stdlib.h>
//(TX, RX)
SoftwareSerial BTSerial(6, 7); 

char buf[12]; // "-2147483648\0"
byte buffer[1024];
int bufferPosition; 



int incomingByte = 10000;  
int rcvData;

void setup()
{
    Serial.begin(9600);
    BTSerial.begin(9600);      
}

void loop()
{


    if (Serial.available() > 2) {

        //incomingByte = Serial.read();
        rcvData = readInt();

        Serial.print("received : ");
        Serial.println(rcvData);
        
        //BTSerial.write(lowByte(rcvData));
        itoa(rcvData, buf, 10);
        BTSerial.write(buf, 12); 
    }
}

int readInt()
{
    int result = 0;
    result = (Serial.read()<<8) | Serial.read();

    return result;
}





