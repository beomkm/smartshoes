package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class StartActivity extends Activity implements View.OnClickListener{

    Button start_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);

        start_bt = (Button)findViewById(R.id.start_bt);
        start_bt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_bt :
                Intent intent = new Intent(StartActivity.this, InstructionActivity.class);
                startActivity(intent);
                finish();
        }
    }
}
