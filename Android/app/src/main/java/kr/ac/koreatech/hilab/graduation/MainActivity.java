package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{

    static final int PORT = 12345;
    //static final int NUM_DATA = 11;
    static final int NUM_DATA = 5;

    private ServerSocket serversocket;
    private ArrayList<Socket> sockList;

    private Thread mThread;
    private ArrayList<ClientThread> thList;

    private ImageView img;
    private ViewGroup vg;
    private FootprintView fv;
    private LinearGraphView lgv;

    private EditText x;
    private EditText z;
    private EditText b;


    public Button changeBtn;

    public int cht;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        fv = new FootprintView(this);
        addContentView(fv, new LinearLayout.LayoutParams(0, 0));
        img = (ImageView) findViewById(R.id.img);
        fv.setImageView(img);

        lgv = new LinearGraphView(this);
        vg = (ViewGroup)findViewById(R.id.linearGraph);
        vg.addView(lgv);
        vg.invalidate();


        changeBtn = (Button)findViewById(R.id.changeBtn);
        changeBtn.setOnClickListener(this);

        x = (EditText)findViewById(R.id.editTextx);
        z = (EditText)findViewById(R.id.editTextz);
        b = (EditText)findViewById(R.id.editTextb);

        sockList = new ArrayList<Socket>();
        thList = new ArrayList<ClientThread>();

        cht = 0;

        //img.setImageBitmap();
        //text_msg= (TextView)findViewById(R.id.massage);


    }

    /*
    public void onClick2(View v)
    {
        final DataManager dm = DataManager.getInstance();
        dm.ahrs.tx = (float)Integer.getInteger(x.getText()+"");
        dm.ahrs.tz = (float)Integer.getInteger(z.getText()+"");
        dm.ahrs.tb = (float)Integer.getInteger(b.getText()+"");
    }*/



    @Override
    protected void onStart()
    {

        super.onStart();


        final Dialog loadingDlg = new Dialog(this, R.style.TransDialog);
        loadingDlg.setContentView(R.layout.layout_dialog);
        //ProgressBar pb = new ProgressBar(this);
        //TextView tv = new TextView(this);
        //tv.setText("연결 대기중...");

        //ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
        //ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //loadingDlg.addContentView(pb, lp1);
        //loadingDlg.addContentView(tv, lp2);

        loadingDlg.show();



        DisplayThread th = new DisplayThread(fv, img, lgv, vg);
        th.start();


        ClientThread cth1 = new ClientThread(12344, loadingDlg);
        ClientThread cth2 = new ClientThread(12345, loadingDlg);
        thList.add(cth1);
        thList.add(cth2);
        cth1.start();
        cth2.start();

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                new AlertDialog.Builder(this)
                        .setTitle("종료")
                        .setMessage("확인버튼을 누르면 종료됩니다.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        final DataManager dm = DataManager.getInstance();
        switch(v.getId()) {
            case R.id.changeBtn :
                dm.ahrsR.tx = (float)Integer.parseInt(x.getText().toString());
                dm.ahrsR.tz = (float)Integer.parseInt(z.getText().toString());
                dm.ahrsR.tb = (float)Integer.parseInt(b.getText().toString());
                Log.d("ttt","rr");

                break;
            case R.id.stopBtn :
                try{
                    if(dm.isConnectedLeft && dm.isConnectedRight) {
                        serversocket.close();
                        Log.d("ttt", "server close");
                        for(Socket s : sockList) {
                            s.close();
                        }
                        Log.d("ttt", "socket close");

                        mThread.interrupt();
                        for(ClientThread th : thList) {
                            th.interrupt();
                        }
                        Log.d("ttt", "thread stop");
                        dm.isConnectedLeft = false;
                        dm.isConnectedRight = false;
                    }
                }
                catch(IOException e){
                    e.printStackTrace();
                }

                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
                finish();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        DataManager dm = DataManager.getInstance();

        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            float x = event.getX();
            float y = event.getY();

            if(x>600 && y<100) {
                if((cht&1)==0)
                    ++cht;
            }
            else if(x>600 && y<200) {
                if((cht&1)==1)
                    ++cht;
            }

            if(cht==4) {
                Toast.makeText(MainActivity.this, "touch", Toast.LENGTH_SHORT).show();
                double bxl = dm.ahrsL.mmx;
                double bzl = dm.ahrsL.mmz;
                double bxr = dm.ahrsR.mmx;
                double bzr = dm.ahrsR.mmz;
                dm.ahrsL.tb = -(int)(180.0f/Math.PI*Math.atan2(bzl, -bxl));
                dm.ahrsR.tb = -(int)(180.0f/Math.PI*Math.atan2(bzr, -bxr));


                cht = 0;
            }

            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

}


class ClientThread extends Thread {
    private int dir;
    private Dialog loadingDlg;
    private boolean isConnected;
    private boolean inited;

    private int port;
    private DatagramSocket sock;
    private byte[] buf;
    private DatagramPacket packet;

    public ClientThread(int port, Dialog loadingDlg) {
        this.sock = sock;
        this.loadingDlg = loadingDlg;
        this.port = port;
        inited = false;

        try {
            sock = new DatagramSocket(this.port);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        buf = new byte[FootProtocol.NUM_DATA*2];
        packet = new DatagramPacket(buf, buf.length);

    }

    @Override
    public void run() {
        DataManager dm = DataManager.getInstance();
        String msg;

        if(!Thread.currentThread().isInterrupted()) {

            Log.d("ttt", "ready : " + port);

            DataInputStream is = null;

            short[] data = new short[FootProtocol.NUM_DATA];
            int count = 0;

            /*
            try {
                dir = is.readByte();
            } catch (IOException e) {
                e.printStackTrace();
            }

            isConnected = true;
            if(dir == FootProtocol.FOOT_LEFT) {
                dm.isConnectedLeft = true;
            }
            else if(dir == FootProtocol.FOOT_RIGHT) {
                dm.isConnectedRight = true;
            }
            if(dm.isConnectedLeft && dm.isConnectedRight) {
                loadingDlg.dismiss();
            }
            */

            isConnected = true;
            while (isConnected) {
                try {
                    //Log.d("ttt", "wating");
                    sock.receive(packet);

                    //Log.d("ttt", "received");
                    //String strdbg = "";
                    //for(int i=0; i<10; i++) {
                    //    strdbg += buf[i] + " ";
                    //}
                    //Log.d("ttt", strdbg);


                    for (int i = 0; i < FootProtocol.NUM_DATA; i++) {
                        if (buf[i*2] == -128) buf[i*2] = 0;
                        if (buf[i*2+1] == -128) buf[i*2+1] = 0;
                        data[i] = (short)((buf[i*2] & 0xFF) | (buf[i*2+1] & 0xFF) << 8);
                        //msg += data[i] + " ";
                    }

                    if(dm.isConnectedRight && dm.isConnectedLeft)
                        loadingDlg.dismiss();


                    if(port == 12345) {
                        dm.isConnectedRight = true;
                        ++dm.linearGraphCount;
                        dm.ahrsR.calcQuaternion(data);
                        if (!dm.ahrsR.tbFlag) {
                            //dm.ahrsR.tb = -(float) (180.0f / Math.PI * Math.atan2(dm.ahrsR.mmz, -dm.ahrsR.mmx));
                            dm.ahrsR.tb = -dm.ahrsR.q[1]*180;
                            dm.ahrsR.tbFlag = true;
                        }
                        dm.pressR1 = data[9];
                        dm.pressR2 = data[10];
                        dm.pressSumRU += dm.pressR1;
                        dm.pressSumRL += dm.pressR2;


                        for (int i = 0; i < FootProtocol.ARCHIVE_TIME-1; i++) {
                            dm.pressArrR[i] = dm.pressArrR[i + 1];
                        }
                        dm.pressArrR[FootProtocol.ARCHIVE_TIME-1] = (dm.pressR1 + dm.pressR2) / 2;

                        if (1==1 || !inited) {
                            dm.ahrsR.mmxFix = dm.ahrsR.mmx;
                            dm.ahrsR.mmyFix = dm.ahrsR.mmy;
                            dm.ahrsR.mmzFix = dm.ahrsR.mmz;
                            dm.pressR1Fix = dm.pressR1;
                            dm.pressR2Fix = dm.pressR2;
                            inited = true;
                        }
                        //count = 0;
                    }
                    else if(port == 12344) {
                        dm.isConnectedLeft = true;
                        ++dm.linearGraphCount;
                        dm.ahrsL.calcQuaternion(data);

                        if (!dm.ahrsL.tbFlag) {
                            dm.ahrsL.tb = -(float) (180.0f / Math.PI * Math.atan2(dm.ahrsL.mmz, -dm.ahrsL.mmx));
                            dm.ahrsL.tbFlag = true;
                        }
                        dm.pressL1 = data[9];
                        dm.pressL2 = data[10];
                        dm.pressSumLU += dm.pressL1;
                        dm.pressSumLL += dm.pressL2;

                        for (int i = 0; i < FootProtocol.ARCHIVE_TIME-1; i++) {
                            dm.pressArrL[i] = dm.pressArrL[i + 1];
                        }
                        dm.pressArrL[FootProtocol.ARCHIVE_TIME-1] = (dm.pressL1 + dm.pressL2) / 2;


                        if (1==1 || !inited) {
                            dm.ahrsL.mmxFix = dm.ahrsL.mmx;
                            dm.ahrsL.mmyFix = dm.ahrsL.mmy;
                            dm.ahrsL.mmzFix = dm.ahrsL.mmz;
                            dm.pressL1Fix = dm.pressL1;
                            dm.pressL2Fix = dm.pressL2;
                            inited = true;
                        }
                        count = 0;
                    }
                    /*
                    buf[count] = is.readByte();
                    if (buf[count] == -128) buf[count] = 0;
                    ++count;
                    if (count >= 2*FootProtocol.NUM_DATA) {
                        msg = "";
                        for (int i = 0; i < FootProtocol.NUM_DATA; i++) {
                            data[i] = (short)((buf[i * 2] & 0xFF) | (buf[i * 2 + 1] & 0xFF) << 8);
                            //msg += data[i] + " ";
                        }

                        if(dir == FootProtocol.FOOT_RIGHT) {
                            dm.ahrsR.calcQuaternion(data);
                            if (!dm.ahrsR.tbFlag) {
                                dm.ahrsR.tb = -(float) (180.0f / Math.PI * Math.atan2(dm.ahrsR.mmz, -dm.ahrsR.mmx));
                                dm.ahrsR.tbFlag = true;

                            }
                            dm.pressR1 = data[3];
                            dm.pressR2 = data[4];
                            dm.pressSumRU += dm.pressR1;
                            dm.pressSumRL += dm.pressR2;


                            for (int i = 0; i < FootProtocol.ARCHIVE_TIME-1; i++) {
                                dm.pressArrR[i] = dm.pressArrR[i + 1];
                            }
                            dm.pressArrR[FootProtocol.ARCHIVE_TIME-1] = (dm.pressR1 + dm.pressR2) / 2;

                            if (dm.pressR1 + dm.pressR2 > 10 || !inited) {
                                dm.ahrsR.mmxFix = dm.ahrsR.mmx;
                                dm.ahrsR.mmyFix = dm.ahrsR.mmy;
                                dm.ahrsR.mmzFix = dm.ahrsR.mmz;
                                dm.pressR1Fix = dm.pressR1;
                                dm.pressR2Fix = dm.pressR2;
                                inited = true;
                            }
                            count = 0;
                        }
                        else if(dir == FootProtocol.FOOT_LEFT) {
                            ++dm.linearGraphCount;
                            dm.ahrsL.calcQuaternion(data);
                            if (!dm.ahrsL.tbFlag) {
                                dm.ahrsL.tb = -(float) (180.0f / Math.PI * Math.atan2(dm.ahrsL.mmz, -dm.ahrsL.mmx));
                                dm.ahrsL.tbFlag = true;
                            }
                            dm.pressL1 = data[3];
                            dm.pressL2 = data[4];
                            dm.pressSumLU += dm.pressL1;
                            dm.pressSumLL += dm.pressL2;


                            for (int i = 0; i < FootProtocol.ARCHIVE_TIME-1; i++) {
                                dm.pressArrL[i] = dm.pressArrL[i + 1];
                            }
                            dm.pressArrL[FootProtocol.ARCHIVE_TIME-1] = (dm.pressL1 + dm.pressL2) / 2;


                            if (dm.pressL1 + dm.pressL2 > 10 || !inited) {
                                dm.ahrsL.mmxFix = dm.ahrsL.mmx;
                                dm.ahrsL.mmyFix = dm.ahrsL.mmy;
                                dm.ahrsL.mmzFix = dm.ahrsL.mmz;
                                dm.pressL1Fix = dm.pressL1;
                                dm.pressL2Fix = dm.pressL2;
                                inited = true;
                            }
                            count = 0;
                        }
                    }
                    */

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }//end while
        }//end run()
    }//end run()
}


class DisplayThread extends Thread {
    private FootprintView bv;
    private ImageView iv;
    private LinearGraphView lgv;
    private ViewGroup vg;

    private int angle;
    private float power;

    private int linearGraphCount;

    public DisplayThread(FootprintView bv, ImageView iv, LinearGraphView lgv, ViewGroup vg) {
        angle = 0;
        power = 0f;
        this.bv = bv;
        this.iv = iv;
        this.lgv = lgv;
        this.vg = vg;
        linearGraphCount = 0;
    }

    @Override
    public void run() {
        final DataManager dm = DataManager.getInstance();
        lgv.setDataArray(dm.pressArrL, dm.pressArrR);

        while(true) {
            if(dm.linearGraphCount == 0) continue;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //bv.rotate(angle += 5);
                    /*
                    bv.setPaint(FootProtocol.FOOT_LEFT, power, 1-power);
                    bv.invalidate();
                    iv.invalidate();
                    power += 0.1;
                    if(power>1) power = 0;
                    */
                    //bv.rotate(2, (int)(dm.ahrs.yaw));

                    //double bz = 180.0f;
                    //double bx = dm.ahrs.mmx/Math.cos(20.0f/180.0f*Math.PI)-bz*Math.tan(20.0f/180.0f*Math.PI);
                    double bxl = dm.ahrsL.mmx;
                    //double by = dm.ahrsR.mmy;
                    double bzl = dm.ahrsL.mmz;

                    double bxr = dm.ahrsR.mmx;
                    //double by = dm.ahrsR.mmy;
                    double bzr = dm.ahrsR.mmz;

                    //Log.d("ttt", ""+bxr+" "+bzr);

                    //int at = 0;
                    //int at = 180+(int)(180.0f/Math.PI*Math.atan2(by, bx));

                    int atl = (int)dm.ahrsL.tb + (int)(180.0f/Math.PI*Math.atan2(bzl, -bxl)); //200f 150f
                    atl = -(int)dm.ahrsL.roll;

                    if(atl>-20 && atl<20)
                        ++dm.normalCnt;
                    else
                        ++dm.abnormalCnt;
                    bv.rotateAndPaint(FootProtocol.FOOT_LEFT, atl, (float)dm.pressL1Fix/60.0f, (float)dm.pressL2Fix/40.0f);

                    int atr = (int)dm.ahrsR.tb + (int)(180.0f/Math.PI*Math.atan2(bzr, -bxr));
                    //atr = -(int)(dm.ahrsR.q[1]*180);
                    atr = -(int)dm.ahrsR.roll;

                    //Log.d("ttt", "atr : " + atr);
                    if(atr>-20 && atr<20)
                        ++dm.normalCnt;
                    else
                        ++dm.abnormalCnt;
                    bv.rotateAndPaint(FootProtocol.FOOT_RIGHT, atr, (float)dm.pressR1Fix/60.0f, (float)dm.pressR2Fix/40.0f);


                    /*
                    bv.rotateAndPaint(FootProtocol.FOOT_LEFT, 20, 50.0f/120.0f, 90.0f/120.0f);
                    bv.rotateAndPaint(FootProtocol.FOOT_RIGHT, -10, 20.0f/120.0f, 90.0f/120.0f);
                    int[] arr1 = {0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                    int[] arr2 = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,11,14,21,32,43,48,55,63,62,61,58,56,52,47,43,40,35,29,20,12,8,4,2,1};
                    for(int i=0; i<arr1.length; i++) {
                        arr1[i] += Math.random() * 8 - 4;
                        if(arr1[i]<0) arr1[i] = 0;
                    }
                    for(int i=0; i<arr2.length; i++) {
                        arr2[i] += Math.random() * 8 - 4;
                        if(arr2[i]<0) arr2[i] = 0;
                    }s
                    lgv.setDataArray(arr1, arr2);
                    */


                    //bv.setPaint(FootProtocol.FOOT_RIGHT,(float)dm.pressR1/300.0f, (float)dm.pressR2/200.0f);
                    //foot image
                    bv.invalidate();
                    //iv.invalidate();

                    //linear graph
                    if(linearGraphCount < dm.linearGraphCount) {
                        lgv.invalidate();
                        vg.invalidate();
                        linearGraphCount = dm.linearGraphCount;
                    }
                }
            });
            try {
                this.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}