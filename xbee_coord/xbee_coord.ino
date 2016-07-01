int incomingByte = 10000;  
int rcvData;

void setup()
{
    Serial.begin(9600);     
}

void loop()
{


    if (Serial.available() > 2) {

        //incomingByte = Serial.read();
        rcvData = readInt();

        Serial.print("received : ");
        Serial.println(rcvData);
    }
}

int readInt()
{
    int result = 0;
    result = (Serial.read()<<8) | Serial.read();

    return result;
}

