package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;

import androidx.core.content.res.ResourcesCompat;


public class TextViewGradient extends androidx.appcompat.widget.AppCompatTextView {

    Paint myPaint;

    public TextViewGradient(Context context) {
        super(context);
//        setTextColor(Color.rgb(193,92,32));
//        Typeface fase = Typeface.createFromAsset(context.getAssets(),"font/balsamiq_sans_regular.ttf");
        this.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        init();
    }
    public TextViewGradient(Context context, AttributeSet attrs) {
        super(context,attrs);
//        setTextColor(Color.rgb(193,92,32));
//        Typeface fase = Typeface.createFromAsset(context.getAssets(),"font/balsamiq_sans_regular.ttf");
        this.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        init();
    }
    public TextViewGradient(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
//        setTextColor(Color.rgb(193,92,32));
//        Typeface fase = Typeface.createFromAsset(context.getAssets(),"font/balsamiq_sans_regular.ttf");
        this.setTypeface(ResourcesCompat.getFont(context, R.font.balsamiq_sans_regular));
        init();
    }
    private void init() {
        myPaint = new Paint();
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeWidth(5);
    }
    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawLine(0,getPaint().getFontMetrics().ascent,500,getPaint().getFontMetrics().ascent,myPaint);
        getPaint().setShader(null);
        getPaint().setShadowLayer(4f,getTextSize()/25,
                getTextSize()/25,
//                0f,
                Color.rgb(204,97,24));
        super.onDraw(canvas);

        getPaint().clearShadowLayer();
//        getPaint().setShader(new LinearGradient(0,2.3f*(-getPaint().getFontMetrics().top+getPaint().getFontMetrics().ascent),0,getPaint().getFontMetrics().bottom/2-getPaint().getFontMetrics().top, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

//        getPaint().setShader(new LinearGradient(0,2.7f*(-getPaint().getFontMetrics().top+getPaint().getFontMetrics().ascent),0,2.7f*(-getPaint().getFontMetrics().top+getPaint().getFontMetrics().ascent)+1, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

//        getPaint().setShader(new LinearGradient(0,getPaint().getFontMetrics().bottom/3-getPaint().getFontMetrics().top-1,0,getPaint().getFontMetrics().bottom/3-getPaint().getFontMetrics().top, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

        getPaint().setShader(new LinearGradient(0,2.7f*(-getPaint().getFontMetrics().top+getPaint().getFontMetrics().ascent),0,getPaint().getFontMetrics().bottom/3-getPaint().getFontMetrics().top, Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

//        getPaint().setShader(new LinearGradient(0,getTextSize()/4,0,getTextSize()-getTextSize()/10,  Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));

//        getPaint().setShader(new LinearGradient(0,0,0,getHeight(), Color.rgb(236,240,45),Color.rgb(204,97,24), Shader.TileMode.CLAMP));
Log.d("PaintHeight:", getHeight()+ " " + getTextSize());
        super.onDraw(canvas);
    }
}
