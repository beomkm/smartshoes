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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    static final int PORT = 12345;
    //static final int NUM_DATA = 11;
    static final int NUM_DATA = 5;

    private ServerSocket serversocket;
    private Socket socket;
    private DataInputStream is;


    private TextView text_msg;
    private Thread mThread;

    boolean isConnected = false;
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



        MyThread th = new MyThread(fv, img);
        th.start();

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {

                if(!mThread.isInterrupted()) {
                    // TODO Auto-generated method stub
                    try {
                        serversocket = new ServerSocket(PORT, 5, null);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {


                        socket = serversocket.accept();
                        isConnected = true;
                        loadingDlg.dismiss();

                        Log.d("ttt", "accepted");

                        is = new DataInputStream(socket.getInputStream());

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    int[] buf = new int[2*NUM_DATA];
                    short[] data = new short[NUM_DATA];
                    int count = 0;

                    int dir = 0;  //1:left , 2:right
                    try {
                        dir = is.readByte();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DataManager dm = DataManager.getInstance();

                    while (isConnected) {
                        try {

                            buf[count] = is.readByte();
                            if (buf[count] == -128) buf[count] = 0;
                            ++count;
                            if (count >= 2*NUM_DATA) {
                                msg = "";
                                for (int i = 0; i < NUM_DATA; i++) {
                                    data[i] = (short)((buf[i * 2] & 0xFF) | (buf[i * 2 + 1] & 0xFF) << 8);
                                    //msg += data[i] + " ";
                                }
                                //Log.d("ttt", msg);
                                dm.ahrsR.calcQuaternion(data);
                                //msg += dm.ahrs.q[0]+"  "+dm.ahrs.q[1]+"  "+dm.ahrs.q[2]+"  "+dm.ahrs.q[3];

                                if(!dm.ahrsR.tbFlag) {
                                    dm.ahrsR.tb = -(float)(180.0f/Math.PI*Math.atan2(dm.ahrsR.mmz, dm.ahrsR.mmx));
                                    dm.ahrsR.tbFlag = true;

                                }


                                dm.press1R = data[3];
                                dm.press2R = data[4];
                                msg += data[0]+" "+data[1]+" "+data[2]+" "+data[3]+" "+data[4];
                                Log.d("ttt", msg);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        //text_msg.setText(msg);

                                    }
                                });

                                //if press > threshold
                                //mmxFix = mmx;
                                //mmyFix = mmy;
                                //mmzFix = mmz;
                                if(dm.press1L+dm.press1R>-1 || !inited) {
                                    dm.ahrsR.mmxFix = dm.ahrsR.mmx;
                                    dm.ahrsR.mmyFix = dm.ahrsR.mmy;
                                    dm.ahrsR.mmzFix = dm.ahrsR.mmz;
                                    inited = true;
                                }

                                count = 0;
                            }

                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //text_msg.setText(msg);

                            }
                        });

                    }//end while
                }//end run()
            }//end run()
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
        switch(v.getId()) {
            case R.id.changeBtn :
                final DataManager dm = DataManager.getInstance();
                dm.ahrsR.tx = (float)Integer.parseInt(x.getText().toString());
                dm.ahrsR.tz = (float)Integer.parseInt(z.getText().toString());
                dm.ahrsR.tb = (float)Integer.parseInt(b.getText().toString());
                Log.d("ttt","rr");

                break;
            case R.id.stopBtn :
                try{
                    if(isConnected) {
                        serversocket.close();
                        Log.d("ttt", "server close");
                        socket.close();
                        Log.d("ttt", "socket close");
                        mThread.interrupt();
                        Log.d("ttt", "thread stop");
                        isConnected = false;
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




class MyThread extends Thread {
    private FootprintView bv;
    private ImageView iv;
    private int angle;
    private float power;

    MyThread(FootprintView bv, ImageView iv) {
        angle = 0;
        power = 0f;
        this.bv = bv;
        this.iv = iv;
    }

    @Override
    public void run() {
        final DataManager dm = DataManager.getInstance();
        while(true) {
            //Log.d("info", "MyThread running");
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

                    int at = (int)dm.ahrsR.tb + (int)(180.0f/Math.PI*Math.atan2(bz, bx));
                    bv.rotate(2, at);
                    bv.setPaint(FootProtocol.FOOT_RIGHT,(float)dm.press1R/300.0f, (float)dm.press2R/200.0f);
                    bv.invalidate();
                    iv.invalidate();

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