package kr.ac.koreatech.hilab.graduation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


        guideView = (TextView) findViewById(R.id.guideView);
        image = (ImageView) findViewById(R.id.guideImage);
        bit = BitmapFactory.decodeResource(getResources(), R.drawable.cc);
        startBtn = (Button)findViewById(R.id.startBtn);
        startBtn.setOnClickListener(this);

        guideView.setText(
                "1. 측정 장치에 전원을 연결해주세요\n\n"+
                        "2. 양 발을 사진과 같이 일자로 만들어주세요\n\n"+
                        "3. 자연스럽게 보행해주세요\n\n"+
                        "4. 시작버튼을 터치하면 측정이 시작됩니다\n\n"+
                        "5. 측정이 시작되면 기존의 기록은 사라지고 새로운 기록이 저장됩니다\n\n\n"+
                        "주의 : 가이드라인을 따르지 않을시 결과가 정확하지 않을 수 있습니다"
        );
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