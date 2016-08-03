package kr.tibyte.footprint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        BitmapView bv = new BitmapView(this);
        setContentView(bv);


        MyThread th = new MyThread(bv);
        th.start();
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



class MyThread extends Thread {
    private BitmapView bv;
    private int angle;
    private float power;

    MyThread(BitmapView bv) {
        angle = 0;
        power = 0f;
        this.bv = bv;
    }

    @Override
    public void run() {
        while(true) {
            Log.d("info", "MyThread running");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //bv.rotate(angle += 5);
                    bv.setPaint(power+=0.02,0);
                    bv.invalidate();
                    if(power>=1.0) power = 0;
                }
            });

            try {
                this.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}

class BitmapView extends View{

    private final int COLOR_L = 0xFF2222DD;
    private final int COLOR_H = 0xFFDD2222;
    private final int COLOR_L_A = COLOR_L>>24&0xFF;
    private final int COLOR_L_R = COLOR_L>>16&0xFF;
    private final int COLOR_L_G = COLOR_L>>8&0xFF;
    private final int COLOR_L_B = COLOR_L&0xFF;
    private final int COLOR_H_A = COLOR_H>>24&0xFF;
    private final int COLOR_H_R = COLOR_H>>16&0xFF;
    private final int COLOR_H_G = COLOR_H>>8&0xFF;
    private final int COLOR_H_B = COLOR_H&0xFF;
    private Bitmap luBitmap;
    private Bitmap llBitmap;
    private Bitmap ruBitmap; //right upper
    private Bitmap rlBitmap; //right lower
    private Bitmap uBitmap;
    private Bitmap lBitmap;
    private Bitmap tempBitmap;
    private Paint uPaint;
    private Paint lPaint;

    private int mWidth = 0;
    private int mHeight = 0;
    private int[] buffer;

    double[] sine = new double[360];
    double[] cosine = new double[360];

    public BitmapView(Context context){
        super(context);
        setFocusable(true);
        setBackgroundColor(0x00000000);

        for(int deg = 0 ; deg < 360; deg++)
        {
            double rad = deg * Math.PI / 180;
            sine[deg] = Math.sin(rad);
            cosine[deg] = Math.cos(rad);
        }



        uPaint = new Paint();

        ruBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.foot_ru);
        rlBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.foot_rl);

        mWidth = ruBitmap.getWidth();
        mHeight = ruBitmap.getHeight();

        //buffer = new int[mWidth * mHeight];
        //ruBitmap.getPixels(buffer, 0, mWidth, 0, 0, mWidth, mHeight);


        uBitmap = Bitmap.createScaledBitmap(ruBitmap, 400, 400, false);
        lBitmap = Bitmap.createScaledBitmap(rlBitmap, 400, 400, false);

        //mViewBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        //mViewBitmap.setPixels(buffer, 0, mWidth, 0, 0, mWidth, mHeight);

    }

    public void rotate(int degree)
    {
        uBitmap = Bitmap.createScaledBitmap(ruBitmap, 400, 400, false);
        lBitmap = Bitmap.createScaledBitmap(rlBitmap, 400, 400, false);

        int[] oldData, newData;
        oldData = new int[uBitmap.getWidth() * uBitmap.getHeight()];
        newData = new int[uBitmap.getWidth() * uBitmap.getHeight()];
        int width, height;
        int cx, cy;

        width = uBitmap.getWidth();
        height = uBitmap.getHeight();
        cx = width/2;
        cy = height/2;  //centre : middle

        uBitmap.getPixels(oldData, 0, 400, 0, 0, uBitmap.getWidth(), uBitmap.getHeight());
        rotate2D(newData, oldData, width, height, degree, cx, cy);
        uBitmap.setPixels(newData, 0, 400, 0, 0, uBitmap.getWidth(), uBitmap.getHeight());



        lBitmap.getPixels(oldData, 0, 400, 0, 0, lBitmap.getWidth(), lBitmap.getHeight());
        rotate2D(newData, oldData, width, height, degree, cx, cy);
        lBitmap.setPixels(newData, 0, 400, 0, 0, lBitmap.getWidth(), lBitmap.getHeight());
        //tempBitmap = Bitmap.createScaledBitmap(ruBitmap, 400, 400, true);
        //uBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        //scaledBitmap = Bitmap.createScaledBitmap(rlBitmap, 400, 400, true);
        //lBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
    }


    private void rotate2D(int[] dest, int[] src, int width, int height, int deg, int cx, int cy) {
        int px, py;
        deg %= 360;
        for(int i=0; i<height; i++) { //y
            for(int j=0; j<width; j++) { //x
                px = (int)((j-cx)*cosine[deg] - (i-cy)*sine[deg] + cx);
                py = (int)((j-cx)*sine[deg] + (i-cy)*cosine[deg] + cy);
                if(px<0 || px>width-1 || py<0 || py>height-1) {
                    dest[i*width+j] = 0x00000000; //transparent
                }
                else {
                    dest[i*width+j] = src[py*width+px];
                }
            }
        }
    }

    public void setPaint(float u, float l)
    {
        /*
        float[] colorTransform = {
                0x01, 0x00, 0x00, 0x00, 0x99,
                0x00, 0x01, 0x00, 0x00, 0x66,
                0x00, 0x00, 0x01, 0x00, 0xCC,
                0x00, 0x00, 0x00, 0x01, 0x00};

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0f);
        colorMatrix.set(colorTransform);
        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(colorFilter);
        */



        int ua = (int)(COLOR_L_A*(1-u) + COLOR_H_A*u);
        int ur = (int)(COLOR_L_R*(1-u) + COLOR_H_R*u);
        int ug = (int)(COLOR_L_G*(1-u) + COLOR_H_G*u);
        int ub = (int)(COLOR_L_B*(1-u) + COLOR_H_B*u);

        int la = (int)(COLOR_L_A*(1-u) + COLOR_H_A*u);
        int lr = (int)(COLOR_L_R*(1-u) + COLOR_H_R*u);
        int lg = (int)(COLOR_L_G*(1-u) + COLOR_H_G*u);
        int lb = (int)(COLOR_L_B*(1-u) + COLOR_H_B* u);

        uPaint.setColorFilter(new PorterDuffColorFilter(ua<<24|ur<<16|ug<<8|ub, PorterDuff.Mode.SRC_ATOP));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(uBitmap, 0, 0, uPaint);
        canvas.drawBitmap(lBitmap, 0, 0, lPaint);
        //canvas.translate(0, mViewBitmap.getHeight());
        super.onDraw(canvas);
    }


}
