package kr.ac.koreatech.hilab.graduation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by byk on 2016-09-21.
 */
public class NetService extends Service
{

    static final int PORT = 12345;
    static final int NUM_DATA = 5;

    private Thread mThread;
    private ServerSocket serversocket;
    private ArrayList<ClientThread> thList;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("ttt", "Service On");
        thList = new ArrayList<ClientThread>();

        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                DataManager dm = DataManager.getInstance();
                try {
                    serversocket = new ServerSocket(PORT, 5, null);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(!mThread.isInterrupted()) {
                    try {
                        ClientThread cth = new ClientThread(serversocket.accept());
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        DataManager dm = DataManager.getInstance();
        try{

            if(dm.isConnectedLeft && dm.isConnectedRight) {
                serversocket.close();
                Log.d("ttt", "server close");

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
    }
}


class ClientThread extends Thread {

    private Socket sock;
    private int dir;
    private boolean isConnected;
    private boolean inited;

    public ClientThread(Socket sock) {
        this.sock = sock;
        inited = false;
        try {
            this.sock.setTcpNoDelay(true);
        } catch (SocketException e) {
            e.printStackTrace();
        }
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
            if(dm.isConnectedLeft && dm.isConnectedRight) {
                //loadingDlg.dismiss();
            }

            while (isConnected) {
                try {
                    if(dir == FootProtocol.FOOT_RIGHT) {
                        int d = is.available();
                        Log.d("ttt", "" + is.available());
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