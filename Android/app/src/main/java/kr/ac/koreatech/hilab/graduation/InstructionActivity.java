package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
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
}