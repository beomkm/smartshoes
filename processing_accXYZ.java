
import processing.serial.*;
long now,pre;
Serial port;

int accX;
int[] accX_arr;
int accY;
int[] accY_arr;
int accZ;
int[] accZ_arr;


void setup()
{
  size(1000, 480);
  port = new Serial(this, "COM4", 9600);
  accX_arr = new int[width];
  accY_arr = new int[width];
  accZ_arr = new int[width];
  smooth();
}
void draw()
{

  while (port.available() >= 7) {
    if (port.read() == 's') {
      accX = ((port.read())+(port.read()<<8));
      if(accX > 0x7FFF) accX |= 0xFFFF0000;
      accY = ((port.read())+(port.read()<<8));
      if(accY > 0x7FFF) accY |= 0xFFFF0000;
      accZ = ((port.read())+(port.read()<<8));
      if(accZ > 0x7FFF) accZ |= 0xFFFF0000;
    }
  }


  now = millis();
  if(now-pre > 0){
    pre = now;
    background(255, 255, 255);

    fill(255, 0, 0);
    text("accX", 50, 50);
    text(accX, 100, 50);

    fill(0, 192, 0);
    text("accY", 50, 100);
    text(accY, 100, 100);

    fill(0, 0, 255);
    text("accZ", 50, 150);
    text(accZ, 100, 150);

    //accX
    for (int i=0; i<width-1; i++)
      accX_arr[i] = accX_arr[i+1];
    accX_arr[width-1] = accX;
    stroke(255, 0, 0);
    for (int x=1; x<width; x++) {
      line(width-x,   height-1-getY(accX_arr[x-1]), width-1-x, height-1-getY(accX_arr[x]));
    }

    //accY
    for (int i=0; i<width-1; i++)
      accY_arr[i] = accY_arr[i+1];
    accY_arr[width-1] = accY;
    stroke(0, 192, 0);
    for (int x=1; x<width; x++) {
      line(width-x,   height-1-getY(accY_arr[x-1]), width-1-x, height-1-getY(accY_arr[x]));
    }

    //accZ
    for (int i=0; i<width-1; i++)
      accZ_arr[i] = accZ_arr[i+1];
    accZ_arr[width-1] = accZ;
    stroke(0, 000, 255);
    for (int x=1; x<width; x++) {
      line(width-x,   height-1-getY(accZ_arr[x-1]), width-1-x, height-1-getY(accZ_arr[x]));
    }

    //base
    stroke(0, 0, 0);
    line(0, height/2, width, height/2);
  }
}

int getY(int val) {
  return (int)((val+32768) / 65536.0f * height);
}
