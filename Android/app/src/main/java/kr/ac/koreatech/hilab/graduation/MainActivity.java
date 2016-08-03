package kr.ac.koreatech.hilab.graduation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    static final int PORT = 12345;
    ServerSocket serversocket;
    Socket socket;
    DataInputStream is;

    TextView text_msg;
    Thread mThread;
    String msg="";

    boolean isConnected=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text_msg = (TextView)findViewById(R.id.massage);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
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
                       Log.d("ttt", "accepted");

                       is = new DataInputStream(socket.getInputStream());

                   } catch (IOException e) {
                       // TODO Auto-generated catch block
                       e.printStackTrace();
                   }
                   int[] buf = new int[20];
                   short[] data = new short[10];
                   int count = 0;
                   while (isConnected) {
                       try {

                           buf[count] = is.readByte();
                           if (buf[count] == -128) buf[count] = 0;
                           ++count;
                           if (count >= 20) {
                               msg = "";
                               for (int i = 0; i < 10; i++) {
                                   data[i] = (short)((buf[i * 2] & 0xFF) | (buf[i * 2 + 1] & 0xFF) << 8);
                                   msg += data[i] + " ";
                               }
                               Log.d("ttt",msg);
                               count = 0;
                           }

                                /*
                                if(is.available() >= 20) {
                                    msg = "";
                                    for (int i = 0; i < 10; i++) {
                                        data[i] = is.readByte() | is.readByte() << 8;
                                        msg += data[i] + " ";
                                    }
                                }
                                Log.d("ttt", msg);
                                */
                       } catch (IOException e) {
                           // TODO Auto-generated catch block
                           e.printStackTrace();
                       }

                       runOnUiThread(new Runnable() {

                           @Override
                           public void run() {
                               // TODO Auto-generated method stub
                               text_msg.setText(msg);

                           }
                       });
                   }//end while
               }//end run()
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
    public void onClick(View v) {
        switch(v.getId()) {
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
