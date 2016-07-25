#include <Wire.h>
#include <I2Cdev.h>
#include <MPU6050.h>

//imu
MPU6050 accelgyro;
int16_t ax, ay, az;
int16_t gx, gy, gz;
int16_t mx, my, mz;
//pressure
int FSRpin = 0;
int Vo;
float Rfsr;
//communication
int rcv;
char datas[20];
int temp = 0;

void setup()
{
    Wire.begin();
    Serial.begin(9600);
    accelgyro.initialize();
}
 
void loop()
{ 
    //imu
    accelgyro.getMotion9(&ax, &ay, &az, &gx, &gy, &gz, &mx, &my, &mz);
    //pressure
    Vo = analogRead(FSRpin);
    Rfsr = ((9.78 * Vo)/(1-(Vo/1023.0)));
    
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

    Serial.write(datas, 20);

}


