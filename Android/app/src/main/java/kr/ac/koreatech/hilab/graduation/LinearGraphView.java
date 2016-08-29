package kr.ac.koreatech.hilab.graduation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Park on 2016-07-28.
 */
public class LinearGraphView extends View {

    private ImageView imgView;
    private Paint adjPaint;
    private Paint leftPaint;
    private Paint rightPaint;
    private int[] leftData;
    private int[] rightData;


    public LinearGraphView(Context context){
        super(context);
        setFocusable(true);
        setBackgroundColor(0x00000000);

        adjPaint = new Paint();
        leftPaint = new Paint();
        rightPaint = new Paint();

        adjPaint.setStrokeWidth(3);
        leftPaint.setStrokeWidth(4);
        rightPaint.setStrokeWidth(4);

        adjPaint.setStrokeCap(Paint.Cap.ROUND);
        leftPaint.setStrokeCap(Paint.Cap.ROUND);
        rightPaint.setStrokeCap(Paint.Cap.ROUND);

        adjPaint.setColor(0xFFAAAAAA);
        leftPaint.setColor(0xFFCC3333);
        rightPaint.setColor(0xFF3333CC);

    }

    public LinearGraphView(Context context, AttributeSet attr) {
        super(context,attr);
    }
    public LinearGraphView(Context context, AttributeSet attr, int defStyle){
        super(context, attr, defStyle);
    }

    public void setImageView(ImageView iv) {
        imgView = iv;
    }

    public void setDataArray(int[] left, int[] right)
    {
        leftData = left;
        rightData = right;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawLine(30, 10, 30, 210, adjPaint);
        canvas.drawLine(30, 210, 630, 210, adjPaint);
        if(leftData != null && rightData != null) {
            for(int i=0; i<rightData.length-1; i++) {
                canvas.drawLine(i+30, 210-rightData[i], i+31, 210-rightData[i+1], rightPaint);
            }
        }


        super.onDraw(canvas);
    }
}

