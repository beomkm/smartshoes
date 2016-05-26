import processing.serial.*;
long now,pre;
Serial port;

int accX;
int accY;
int accZ;
int gyroX;
int gyroY;
int gyroZ;
int[] accX_arr;
int[] accY_arr;
int[] accZ_arr;
int[] gyroX_arr;
int[] gyroY_arr;
int[] gyroZ_arr;


void setup()
{
  size(1400, 600);
  port = new Serial(this, "COM4", 9600);
  accX_arr = new int[width];
  accY_arr = new int[width];
  accZ_arr = new int[width];
  gyroX_arr = new int[width];
  gyroY_arr = new int[width];
  gyroZ_arr = new int[width];
  smooth();
}
void draw()
{

  while (port.available() >= 13) {
    if (port.read() == 's') {
      accX = ((port.read())+(port.read()<<8));
      if(accX > 0x7FFF) accX |= 0xFFFF0000;
      accY = ((port.read())+(port.read()<<8));
      if(accY > 0x7FFF) accY |= 0xFFFF0000;
      accZ = ((port.read())+(port.read()<<8));
      if(accZ > 0x7FFF) accZ |= 0xFFFF0000;

      gyroX = ((port.read())+(port.read()<<8));
      if(gyroX > 0x7FFF) gyroX |= 0xFFFF0000;
      gyroY = ((port.read())+(port.read()<<8));
      if(gyroY > 0x7FFF) gyroY |= 0xFFFF0000;
      gyroZ = ((port.read())+(port.read()<<8));
      if(gyroZ > 0x7FFF) gyroZ |= 0xFFFF0000;
    }
  }


  now = millis();
  if(now-pre > 0){
    pre = now;
    background(255, 255, 255);

    fill(255, 0, 0);
    textSize(26);
    text("Accel X", 20, height/2+29);
    text(accX, 120, height/2+29);

    fill(0, 192, 0);
    text("Y", 220, height/2+29);
    text(accY, 240, height/2+29);

    fill(0, 0, 255);
    text("Z", 340, height/2+29);
    text(accZ, 360, height/2+29);

    fill(255, 0, 0);
    text("Gyro  X", 20, height/2-12);
    text(gyroX, 120, height/2-12);

    fill(0, 192, 0);
    text("Y", 220, height/2-12);
    text(gyroY, 240, height/2-12);

    fill(0, 0, 255);
    text("Z", 340, height/2-12);
    text(gyroZ, 360, height/2-12);

    //accX
    for (int i=0; i<width-1; i++)
      accX_arr[i] = accX_arr[i+1];
    accX_arr[width-1] = accX;
    stroke(255, 0, 0);
    for (int x=1; x<width; x++) {
      line(width-x, height-getAccY(accX_arr[x-1]), width-x, height-getAccY(accX_arr[x]));
    }

    //accY
    for (int i=0; i<width-1; i++)
      accY_arr[i] = accY_arr[i+1];
    accY_arr[width-1] = accY;
    stroke(0, 192, 0);
    for (int x=1; x<width; x++) {
      line(width-x,   height-getAccY(accY_arr[x-1]), width-x, height-getAccY(accY_arr[x]));
    }

    //accZ
    for (int i=0; i<width-1; i++)
      accZ_arr[i] = accZ_arr[i+1];
    accZ_arr[width-1] = accZ;
    stroke(0, 0, 255);
    for (int x=1; x<width; x++) {
      line(width-x,   height-getAccY(accZ_arr[x-1]), width-x, height-getAccY(accZ_arr[x]));
    }

    //gyroX
    for (int i=0; i<width-1; i++)
       gyroX_arr[i] = gyroX_arr[i+1];
    gyroX_arr[width-1] = gyroX;
    stroke(255, 0, 0);
    for (int x=1; x<width; x++) {
      line(width-x,   height-getGyroY(gyroX_arr[x-1]), width-x, height-getGyroY(gyroX_arr[x]));
    }

    //gyroY
    for (int i=0; i<width-1; i++)
      gyroY_arr[i] = gyroY_arr[i+1];
    gyroY_arr[width-1] = gyroY;
    stroke(0, 192, 0);
    for (int x=1; x<width; x++) {
      line(width-x,   height-getGyroY(gyroY_arr[x-1]), width-x, height-getGyroY(gyroY_arr[x]));
    }

    //gyroZ
    for (int i=0; i<width-1; i++)
      gyroZ_arr[i] = gyroZ_arr[i+1];
    gyroZ_arr[width-1] = gyroZ;
    stroke(0, 0, 255);
    for (int x=1; x<width; x++) {
      line(width-x,   height-getGyroY(gyroZ_arr[x-1]), width-x, height-getGyroY(gyroZ_arr[x]));
    }

    //base
    strokeWeight(1);
    stroke(0, 0, 0);
    line(0, height/4, width, height/4);
    strokeWeight(3);
    stroke(0, 0, 0);
    line(0, height/2, width, height/2);
    strokeWeight(1);
    stroke(0, 0, 0);
    line(0, height/4*3, width, height/4*3);
  }
}

int getAccY(int val) {
  return (int)((val+32768) / 65536.0f * height/2);
}

int getGyroY(int val) {
  return (int)((val+32768) / 65536.0f * height/2 + height/2);
}
