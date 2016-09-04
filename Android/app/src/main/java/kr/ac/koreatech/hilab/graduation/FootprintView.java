package kr.ac.koreatech.hilab.graduation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
public class FootprintView extends View {
    private final int COLOR_L = 0xFF0000FF;
    private final int COLOR_M = 0xFF0000FF;
    private final int COLOR_H = 0xFFFF0000;

    private final int COLOR_L_A = COLOR_L>>24&0xFF;
    private final int COLOR_L_R = COLOR_L>>16&0xFF;
    private final int COLOR_L_G = COLOR_L>>8&0xFF;
    private final int COLOR_L_B = COLOR_L&0xFF;

    private final int COLOR_M_A = COLOR_H>>24&0xFF;
    private final int COLOR_M_R = COLOR_H>>16&0xFF;
    private final int COLOR_M_G = COLOR_H>>8&0xFF;
    private final int COLOR_M_B = COLOR_H&0xFF;

    private final int COLOR_H_A = COLOR_H>>24&0xFF;
    private final int COLOR_H_R = COLOR_H>>16&0xFF;
    private final int COLOR_H_G = COLOR_H>>8&0xFF;
    private final int COLOR_H_B = COLOR_H&0xFF;

    private Bitmap ruBitmapOrg; //right upper original
    private Bitmap rlBitmapOrg; //right lower original
    private Bitmap luBitmapOrg; //left upper original
    private Bitmap llBitmapOrg; //left lower original

    private Bitmap canvasBitmap;
    private Bitmap ruBitmap; //right upper original
    private Bitmap rlBitmap; //right lower original
    private Bitmap luBitmap; //left upper original
    private Bitmap llBitmap; //left lower original

    private ImageView imgView;

    private Paint ruPaint;
    private Paint rlPaint;
    private Paint luPaint;
    private Paint llPaint;


    private int mWidth = 0;
    private int mHeight = 0;
    private int[] buffer;

    double[] sine = new double[360];
    double[] cosine = new double[360];
    int[] root = new int[160000];

    public FootprintView(Context context){
        super(context);
        Log.d("ttt", "Con1");
        setFocusable(true);
        setBackgroundColor(0x00000000);


        for(int deg = 0 ; deg < 360; deg++) {
            double rad = deg * Math.PI / 180;
            sine[deg] = Math.sin(rad);
            cosine[deg] = Math.cos(rad);
        }
        for(int i=0; i<160000; i++) {
            root[i] = (int)Math.sqrt(i);
        }

        Log.d("ttt", "Con2");

        ruPaint = new Paint();
        rlPaint = new Paint();
        luPaint = new Paint();
        llPaint = new Paint();

        ruBitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.foot_ru);
        rlBitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.foot_rl);
        luBitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.foot_lu);
        llBitmapOrg = BitmapFactory.decodeResource(getResources(), R.drawable.foot_ll);


        mWidth = ruBitmapOrg.getWidth();
        mHeight = ruBitmapOrg.getHeight();

        //buffer = new int[mWidth * mHeight];
        //ruBitmap.getPixels(buffer, 0, mWidth, 0, 0, mWidth, mHeight);

        canvasBitmap = Bitmap.createBitmap(400*2, 400, Bitmap.Config.ARGB_8888);

        ruBitmap = Bitmap.createScaledBitmap(ruBitmapOrg, 400, 400, false);
        rlBitmap = Bitmap.createScaledBitmap(rlBitmapOrg, 400, 400, false);
        luBitmap = Bitmap.createScaledBitmap(luBitmapOrg, 400, 400, false);
        llBitmap = Bitmap.createScaledBitmap(llBitmapOrg, 400, 400, false);
        Log.d("ttt", "Con3");
        //mViewBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        //mViewBitmap.setPixels(buffer, 0, mWidth, 0, 0, mWidth, mHeight);


    }

    public FootprintView (Context context,AttributeSet attr) {
        super(context,attr);
    }
    public FootprintView (Context context, AttributeSet attr, int defStyle){
        super(context, attr, defStyle);
    }


    public int rotateAndPaint(int dir, int degree, float u, float l)
    {

        Bitmap uBitmap;
        Bitmap lBitmap;
        if(dir == FootProtocol.FOOT_LEFT) {
            uBitmap = Bitmap.createScaledBitmap(luBitmapOrg, 400, 400, false);
            lBitmap = Bitmap.createScaledBitmap(llBitmapOrg, 400, 400, false);
        }
        else if(dir == FootProtocol.FOOT_RIGHT) {
            uBitmap = Bitmap.createScaledBitmap(ruBitmapOrg, 400, 400, false);
            lBitmap = Bitmap.createScaledBitmap(rlBitmapOrg, 400, 400, false);
        }
        else
            return -1;


        int[] oldData, newData;
        int width, height;
        int cx, cy;

        oldData = new int[uBitmap.getWidth() * uBitmap.getHeight()];
        newData = new int[uBitmap.getWidth() * uBitmap.getHeight()];


        width = uBitmap.getWidth();
        height = uBitmap.getHeight();
        cx = width/2;
        cy = height/2;  //centre : middle


        uBitmap.getPixels(oldData, 0, 400, 0, 0, uBitmap.getWidth(), uBitmap.getHeight());
        rotate2DAndPaint(newData, oldData, width, height, degree, cx, cy, dir, u, l);
        uBitmap.setPixels(newData, 0, 400, 0, 0, uBitmap.getWidth(), uBitmap.getHeight());



        lBitmap.getPixels(oldData, 0, 400, 0, 0, lBitmap.getWidth(), lBitmap.getHeight());
        rotate2DAndPaint(newData, oldData, width, height, degree, cx, cy, dir, u, l);
        lBitmap.setPixels(newData, 0, 400, 0, 0, lBitmap.getWidth(), lBitmap.getHeight());


        if(dir == FootProtocol.FOOT_LEFT) {
            luBitmap = uBitmap;
            llBitmap = lBitmap;
        }
        else if(dir == FootProtocol.FOOT_RIGHT) {
            ruBitmap = uBitmap;
            rlBitmap = lBitmap;
        }
        else
            return -1;

        return 0;
    }


    private void rotate2DAndPaint(int[] dest, int[] src, int width, int height, int deg, int cx, int cy, int dir, float u, float l) {
        int px, py;
        deg %= 360;
        if(deg<0) deg += 360;
        for(int i=0; i<height; i++) { //y
            for(int j=0; j<width; j++) { //x
                px = (int)((j-cx)*cosine[deg] - (i-cy)*sine[deg] + cx);
                py = (int)((j-cx)*sine[deg] + (i-cy)*cosine[deg] + cy);
                if(px<0 || px>width-1 || py<0 || py>height-1) {
                    dest[i*width+j] = 0x00000000; //transparent
                }
                else {
                    int dux, duy, dlx, dly; //delta upper/lower x/y
                    if(dir == FootProtocol.FOOT_LEFT) {
                        dux = px - FootProtocol.SENSOR_LUX;
                        duy = py - FootProtocol.SENSOR_LUY;
                        dlx = px - FootProtocol.SENSOR_LLX;
                        dly = py - FootProtocol.SENSOR_LLY;
                    }
                    else {  //FootProtocol.FOOT_RIGHT
                        dux = px - FootProtocol.SENSOR_RUX;
                        duy = py - FootProtocol.SENSOR_RUY;
                        dlx = px - FootProtocol.SENSOR_RLX;
                        dly = py - FootProtocol.SENSOR_RLY;
                    }
                    float colorU = (float)u-(float)root[dux*dux+duy*duy]/500;
                    float colorL = (float)l-(float)root[dlx*dlx+dly*dly]/500;

                    if(colorU<0) colorU = 0;
                    if(colorL<0) colorL = 0;

                    float color = colorU + colorL;
                    if(color>1) color = 1;

                    //int a = (int)(COLOR_L_A*(1-color) + COLOR_H_A*color);
                    int r = (int)(COLOR_L_R*(1-color) + COLOR_H_R*color) & 0xF0;
                    int g = (int)(COLOR_L_G*(1-color) + COLOR_H_G*color) & 0xF0;
                    int b = (int)(COLOR_L_B*(1-color) + COLOR_H_B*color) & 0xF0;

                    dest[i*width+j] = src[py*width+px] | r<<16 | g<<8 | b;
                }
            }
        }
    }

    public int setPaint(int dir, float u, float l)
    {

        if(u>1) u=1;
        else if(u<0) u=0;
        if(l>1) l=1;
        else if(l<0) l=0;

        int ua = (int)(COLOR_L_A*(1-u) + COLOR_H_A*u);
        int ur = (int)(COLOR_L_R*(1-u) + COLOR_H_R*u);
        int ug = (int)(COLOR_L_G*(1-u) + COLOR_H_G*u);
        int ub = (int)(COLOR_L_B*(1-u) + COLOR_H_B*u);

        int la = (int)(COLOR_L_A*(1-l) + COLOR_H_A*l);
        int lr = (int)(COLOR_L_R*(1-l) + COLOR_H_R*l);
        int lg = (int)(COLOR_L_G*(1-l) + COLOR_H_G*l);
        int lb = (int)(COLOR_L_B*(1-l) + COLOR_H_B*l);


        if(dir == FootProtocol.FOOT_LEFT) {
            luPaint.setColorFilter(new PorterDuffColorFilter(ua<<24|ur<<16|ug<<8|ub, PorterDuff.Mode.SRC_ATOP));
            llPaint.setColorFilter(new PorterDuffColorFilter(la<<24|lr<<16|lg<<8|lb, PorterDuff.Mode.SRC_ATOP));
        }
        else if(dir == FootProtocol.FOOT_RIGHT) {
            ruPaint.setColorFilter(new PorterDuffColorFilter(ua<<24|ur<<16|ug<<8|ub, PorterDuff.Mode.SRC_ATOP));
            rlPaint.setColorFilter(new PorterDuffColorFilter(la<<24|lr<<16|lg<<8|lb, PorterDuff.Mode.SRC_ATOP));
        }
        else
            return -1;

        return 0;
    }

    public void setImageView(ImageView iv) {
        imgView = iv;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d("ttt", "oD1");
        //Log.d("ttt", "onDraw : " + ruBitmap + "Paint" + ruPaint);

        canvasBitmap = Bitmap.createBitmap(400*2, 400, Bitmap.Config.ARGB_8888);
        Canvas can = new Canvas(canvasBitmap);

        can.drawBitmap(ruBitmap, 400, 0, ruPaint);
        can.drawBitmap(rlBitmap, 400, 0, rlPaint);
        can.drawBitmap(luBitmap, 0, 0, luPaint);
        can.drawBitmap(llBitmap, 0, 0, llPaint);

        //MainActivity.img.setImageBitmap(uBitmap);

        imgView.setImageBitmap(canvasBitmap);
        //canvas.translate(0, mViewBitmap.getHeight());
        //Log.d("ttt", "oD2");
        super.onDraw(canvas);
    }
}