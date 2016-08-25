package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Method;

public class InstructionActivity extends Activity implements View.OnClickListener{

    Button startBtn;
    TextView guideView;
    ImageView image = null;
    Bitmap bit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_instruction);

        // ****************************************************************************
        // 여기부터는 핫스팟
        // SSID, password바꾸는 코드.
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        WifiConfiguration wifiCon = new WifiConfiguration();
        wifiCon.SSID = "MySSID";
        wifiCon.preSharedKey = "password";
        wifiCon.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiCon.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiCon.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        try
        {
            Method setWifiApMethod = wm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            boolean apstatus=(Boolean) setWifiApMethod.invoke(wm, wifiCon,true);
        }
        catch (Exception e) {
            Log.e(this.getClass().toString(), "", e);
        }
        // ****************************************************************************



        // 자동으로 핫스팟 실행시킴
        ApManager.isApOn(this);
        ApManager.configApState(this);



        guideView = (TextView) findViewById(R.id.guideView);
        image = (ImageView) findViewById(R.id.guideImage);
        bit = BitmapFactory.decodeResource(getResources(), R.drawable.cc);
        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);

        image.setImageBitmap(bit);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.startBtn :
                Intent intent = new Intent(InstructionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
        }
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

}