package ru.tmkstd.cardgamedurakonline;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Card {
    protected Bitmap normal;
    protected Rect rect;
    char nickName;
    String emojiName;
    int needPosX,maxX;
    int needPosY,maxY;
    int width,height;
    float coefX,coefY;

    Card(Bitmap bmNorm,int x, int y) {
        normal = bmNorm;
        rect = new Rect(x,(y-normal.getHeight())/2,x+normal.getWidth(),(y-normal.getHeight())/2+normal.getHeight());
        width = normal.getWidth();
        height = normal.getHeight();
        maxX=x;
        maxY = y;
        coefX = 1f;
        coefY = 1f;
    }
    Card(int w, int h,int x, int y) {
//        normal = bmNorm;
        rect = new Rect(x,(y-h)/2,x+w,(y-h)/2+h);
        width = w;
        height = h;
        maxX=x;
        maxY = y;
        coefX = 1f;
        coefY = 1f;
    }
    protected void setNickName(char nick) {
        nickName = nick;
    }

//    protected boolean cardIsStop() {
//        if(Math.abs(needPosX-rect.left)<width/4 && Math.abs(needPosY-rect.top)<width/4) return true;
//        return false;
//
//    }
    protected void setNeedPosX(int x) {
        needPosX = x;
    }
    protected void setNeedPosY(int y) {
//        Log.d("NeedPosY2",y+" "+rect.top);
//        Log.d("NeedPosY21",needPosX+" "+rect.left);

        needPosY = y;

        if(Math.abs(needPosX-rect.left)>Math.abs(needPosY-rect.top)){
            coefX = 1f;
            coefY = (float)Math.abs(needPosY-rect.top)/(float)Math.abs(needPosX-rect.left);
        }
        if(Math.abs(needPosX-rect.left)<Math.abs(needPosY-rect.top)){
            coefX = (float)Math.abs(needPosX-rect.left)/(float)Math.abs(needPosY-rect.top);
            coefY = 1f;
        }
        if(Math.abs(needPosX-rect.left)==Math.abs(needPosY-rect.top)) {
            coefX = 1f;
            coefY = 1f;
        }


    }
    protected void setPosBtnX(int x){
        needPosX = x;
        rect.left = x;
        rect.right = x + width;
    }
    protected void setPosBtnY(int y) {
        needPosY=y;
        rect.top = y;
        rect.bottom = y + height;
    }
    protected int getPosX() { return rect.left;}
    protected int getPosY() { return rect.top; }
    protected int getCenterX(){
        return rect.left + width/2;
    }
    protected int getCenterY() {
        return rect.top + height/2;
    }
    protected void update(int fps) {
        if (fps!=0) {
            if (1 + needPosX < rect.left) {
                if(rect.left-needPosX>maxX/4)rect.left = rect.left - (int)Math.ceil(6*coefX*(maxX/4)/fps);
                else rect.left = rect.left - (int)Math.ceil(6*coefX*(rect.left-needPosX)/fps) - 1;
            }
            if (needPosX > rect.left + 1) {
                if(needPosX-rect.left>maxX/4)rect.left = rect.left + (int)Math.ceil(6*coefX*(maxX/4)/fps);
                else rect.left = rect.left + (int)Math.ceil(6*coefX*(needPosX-rect.left)/fps) + 1;
            }
            if (1+needPosY < rect.top) {
                if(rect.top - needPosY>maxX/4) rect.top = rect.top - (int)Math.ceil(6*coefY*(maxX/4)/fps);
                else rect.top = rect.top - (int)Math.ceil(6*coefY*(rect.top-needPosY)/fps)-1;
            }
            if (needPosY > rect.top+1) {
                if(needPosY - rect.top>maxX/4) rect.top = rect.top + (int)Math.ceil(6*coefY*(maxX/4)/fps);
                else rect.top = rect.top + (int)Math.ceil(6*coefY*(needPosY-rect.top)/fps) + 1;

            }

            rect.right = rect.left + width;
            rect.bottom = rect.top + height;
        }

    }
    protected Bitmap getBitmap() {
        return normal;
    }
    protected int getWidth() {
        return width;
    }
    protected int getHeight() {
        return height;
    }

    protected boolean touch(int x, int y) {
        if (rect.contains(x, y)) {
            return true;
        }
        return false;
    }
}
