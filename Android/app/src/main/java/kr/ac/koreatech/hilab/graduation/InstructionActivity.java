package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class InstructionActivity extends Activity implements View.OnClickListener{

    Button next_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_instruction);

        next_bt = (Button)findViewById(R.id.next_bt);
        next_bt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.next_bt :
                Intent intent = new Intent(InstructionActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
        }
    }
}