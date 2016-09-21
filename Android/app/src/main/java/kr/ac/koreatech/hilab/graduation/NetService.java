package kr.ac.koreatech.hilab.graduation;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by byk on 2016-09-21.
 */
public class NetService extends Service
{
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d("ttt", "Service On");

        DataManager dm = DataManager.getInstance();

        Log.d("ttt", ""+dm.DUMMY);
        Log.d("ttt", ""+dm.DUMMY);
        Log.d("ttt", ""+dm.DUMMY);
        Log.d("ttt", ""+dm.DUMMY);
        Log.d("ttt", ""+dm.DUMMY);
        Log.d("ttt", ""+dm.DUMMY);

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
    }
}
