int num = 10000;

void setup()
{
    Serial.begin(9600);
}
 
void loop()
{
    writeInt(num++);
    delay(1000);
}

void writeInt(int val)
{
    Serial.write(highByte(val));
    Serial.write(lowByte(val));
}

