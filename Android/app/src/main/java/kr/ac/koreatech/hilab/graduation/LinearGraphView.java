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
    Paint paint = new Paint();

    public LinearGraphView(Context context){
        super(context);
        setFocusable(true);
        setBackgroundColor(0x00000000);

        paint.setColor(Color.BLACK);

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

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawLine(0, 0, 20, 20, paint);
        canvas.drawLine(20, 0, 0, 20, paint);

        super.onDraw(canvas);
    }
}

