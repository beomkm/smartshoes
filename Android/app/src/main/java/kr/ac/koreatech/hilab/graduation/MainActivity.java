package kr.ac.koreatech.hilab.graduation;

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
import android.view.View;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final int PORT = 12345;
    //static final int NUM_DATA = 11;
    static final int NUM_DATA = 5;

    private ServerSocket serversocket;
    private Socket socket;
    private ArrayList<Socket> sockList;
    private DataInputStream is;


    private TextView text_msg;
    private Thread mThread;
    private ArrayList<ClientThread> thList;

    boolean isConnectedLeft = false;
    boolean isConnectedRight = false;
    private String msg = "";

    private ImageView img;
    private ViewGroup vg;
    private FootprintView fv;
    private LinearGraphView lgv;




    private EditText x;
    private EditText z;
    private EditText b;

    private boolean inited = false;

    public Button changeBtn;

    int angle = 0;
    int power = 0;
    int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serversocket = new ServerSocket(PORT, 5, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(!mThread.isInterrupted()) {
                    try {
                        ClientThread cth = new ClientThread(serversocket.accept(), loadingDlg);
                        thList.add(cth);
                        cth.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();

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
                    if(dm.isConnectedLeft=true && dm.isConnectedRight) {
                        serversocket.close();
                        Log.d("ttt", "server close");
                        socket.close();
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
}


class ClientThread extends Thread {

    private Socket sock;
    private int dir;
    private Dialog loadingDlg;
    private boolean isConnected;
    private boolean inited;

    public ClientThread(Socket sock, Dialog loadingDlg) {
        this.sock = sock;
        this.loadingDlg = loadingDlg;
        inited = false;
    }

    @Override
    public void run() {
        DataManager dm = DataManager.getInstance();
        String msg;

        if(!Thread.currentThread().isInterrupted()) {



            Log.d("ttt", "accepted");

            DataInputStream is = null;
            try {
                is = new DataInputStream(sock.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }


            int[] buf = new int[2*FootProtocol.NUM_DATA];
            short[] data = new short[FootProtocol.NUM_DATA];
            int count = 0;

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
            if(dm.isConnectedLeft=true && dm.isConnectedRight) {
                loadingDlg.dismiss();
            }

            while (isConnected) {
                try {
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
                                Log.d("ttt","@@@\n@@@@\n@@@@\n@@@@\n@@@@@@@@@@@@");
                                dm.ahrsR.tb = -(float) (180.0f / Math.PI * Math.atan2(dm.ahrsR.mmz, -dm.ahrsR.mmx));
                                dm.ahrsR.tbFlag = true;

                            }
                            dm.pressR1 = data[3];
                            dm.pressR2 = data[4];

                            for (int i = 0; i < 599; i++) {
                                dm.pressArrR[i] = dm.pressArrR[i + 1];
                            }
                            dm.pressArrR[599] = (dm.pressR1 + dm.pressR2) / 2;

                            if (dm.pressR1 + dm.pressR2 > -1 || !inited) {
                                dm.ahrsR.mmxFix = dm.ahrsR.mmx;
                                dm.ahrsR.mmyFix = dm.ahrsR.mmy;
                                dm.ahrsR.mmzFix = dm.ahrsR.mmz;
                                inited = true;
                            }
                            count = 0;
                        }
                        else if(dir == FootProtocol.FOOT_LEFT) {
                            dm.ahrsL.calcQuaternion(data);
                            if (!dm.ahrsL.tbFlag) {
                                dm.ahrsL.tb = -(float) (180.0f / Math.PI * Math.atan2(dm.ahrsL.mmz, dm.ahrsL.mmx));
                                dm.ahrsL.tbFlag = true;

                            }
                            dm.pressL1 = data[3];
                            dm.pressL2 = data[4];

                            for (int i = 0; i < 599; i++) {
                                dm.pressArrL[i] = dm.pressArrL[i + 1];
                            }
                            dm.pressArrL[599] = (dm.pressL1 + dm.pressL2) / 2;


                            if (dm.pressL1 + dm.pressL2 > -1 || !inited) {
                                dm.ahrsL.mmxFix = dm.ahrsL.mmx;
                                dm.ahrsL.mmyFix = dm.ahrsL.mmy;
                                dm.ahrsL.mmzFix = dm.ahrsL.mmz;
                                inited = true;
                            }
                            count = 0;
                        }
                    }

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

    public DisplayThread(FootprintView bv, ImageView iv, LinearGraphView lgv, ViewGroup vg) {
        angle = 0;
        power = 0f;
        this.bv = bv;
        this.iv = iv;
        this.lgv = lgv;
        this.vg = vg;
    }

    @Override
    public void run() {
        final DataManager dm = DataManager.getInstance();
        lgv.setDataArray(dm.pressArrL, dm.pressArrR);

        while(true) {

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
                    double bx = dm.ahrsR.mmx;
                    double by = dm.ahrsR.mmy;
                    double bz = dm.ahrsR.mmz;

                    //int at = 0;
                    //int at = 180+(int)(180.0f/Math.PI*Math.atan2(by, bx));

                    int at = (int)dm.ahrsR.tb + (int)(180.0f/Math.PI*Math.atan2(bz, -bx));
                    bv.rotateAndPaint(2, at, (float)dm.pressR1/300.0f, (float)dm.pressR2/200.0f);
                    //bv.setPaint(FootProtocol.FOOT_RIGHT,(float)dm.pressR1/300.0f, (float)dm.pressR2/200.0f);
                    bv.invalidate();
                    iv.invalidate();
                    lgv.invalidate();
                    vg.invalidate();

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