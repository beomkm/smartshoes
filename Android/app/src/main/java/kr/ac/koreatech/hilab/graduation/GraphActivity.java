package kr.ac.koreatech.hilab.graduation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CircleGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraph;
import com.handstudio.android.hzgrapherlib.vo.circlegraph.CircleGraphVO;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    private ViewGroup layoutGraphView;

    private TextView leftUp_tv;
    private TextView leftLow_tv;
    private TextView rightUp_tv;
    private TextView rightLow_tv;
    private TextView up;
    private TextView low;
    private TextView left;
    private TextView right;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graph);

        layoutGraphView = (ViewGroup) findViewById(R.id.layoutGraphView);
        leftUp_tv = (TextView) findViewById(R.id.leftUp_tv);
        leftLow_tv = (TextView) findViewById(R.id.leftLow_tv);
        rightUp_tv = (TextView) findViewById(R.id.rightUp_tv);
        rightLow_tv = (TextView) findViewById(R.id.rightLow_tv);
        left = (TextView) findViewById(R.id.left);
        right = (TextView) findViewById(R.id.textView4);
        low = (TextView) findViewById(R.id.textView3);
        up = (TextView) findViewById(R.id.textView2);

        /**
         * Intent로 값을 넘겨 받으면 Text를 설정하세요.
         */
        leftUp_tv.setText(Integer.toString(50));
        leftLow_tv.setText(Integer.toString(50));
        rightUp_tv.setText(Integer.toString(50));
        rightLow_tv.setText(Integer.toString(50));
        up.setText(Integer.toString(50));
        low.setText(Integer.toString(50));
        left.setText(Integer.toString(50));
        right.setText(Integer.toString(50));

        // 그래프 그림
        setCircleGraph();
    }
    private void setCircleGraph() {

        CircleGraphVO vo = makeLineGraphAllSetting();

        layoutGraphView.addView(new CircleGraphView(this,vo));
    }

    /**
     * make line graph using options
     * @return
     */
    private CircleGraphVO makeLineGraphAllSetting() {
        //BASIC LAYOUT SETTING
        //padding
        int paddingBottom 	= CircleGraphVO.DEFAULT_PADDING;
        int paddingTop 		= CircleGraphVO.DEFAULT_PADDING;
        int paddingLeft 	= CircleGraphVO.DEFAULT_PADDING;
        int paddingRight 	= CircleGraphVO.DEFAULT_PADDING;

        //graph margin
        int marginTop 		= CircleGraphVO.DEFAULT_MARGIN_TOP;
        int marginRight 	= CircleGraphVO.DEFAULT_MARGIN_RIGHT;

        // radius setting
        int radius = 130;

        List<CircleGraph> arrGraph 	= new ArrayList<CircleGraph>();

        arrGraph.add(new CircleGraph("정상", Color.parseColor("#3366CC"), 1));
        arrGraph.add(new CircleGraph("비정상", Color.parseColor("#DC3912"), 1));


        CircleGraphVO vo = new CircleGraphVO(paddingBottom, paddingTop, paddingLeft, paddingRight,marginTop, marginRight,radius, arrGraph);

        // circle Line
        vo.setLineColor(Color.WHITE);

        // set text setting
        vo.setTextColor(Color.WHITE);
        vo.setTextSize(20);

        // set circle center move X ,Y
        vo.setCenterX(0);
        vo.setCenterY(0);

        //set animation
        vo.setAnimation(new GraphAnimation(GraphAnimation.LINEAR_ANIMATION, 2000));
        //set graph name box

        GraphNameBox graphNameBox = new GraphNameBox();

        // nameBox
        graphNameBox.setNameboxMarginTop(25);
        graphNameBox.setNameboxMarginRight(25);

        vo.setGraphNameBox(graphNameBox);

        return vo;
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