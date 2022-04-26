package ru.tmkstd.cardgamedurakonline;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.media.AudioManager;
import android.media.SoundPool;

import java.io.IOException;

public class ButtonMy {
    protected Bitmap normal;
    protected Bitmap pressed;
    protected Rect rectBtn;
    protected boolean pres;
    protected boolean tripped;
    private SoundPool mSoundPool;
    private int pressBtn;

    ButtonMy(Bitmap bmNorm, Bitmap bmPres) {

        normal = bmNorm;
        pressed = bmPres;
        rectBtn = new Rect(0,0,normal.getWidth(),normal.getHeight());
        pres = false;
        tripped = false;
    }
    ButtonMy(Bitmap bmNorm) {

        normal = bmNorm;
        pressed = normal;
        rectBtn = new Rect(0,0,normal.getWidth(),normal.getHeight());
        pres = false;
        tripped = false;
    }

    protected void setPosBtnX(int x){
        rectBtn.left = x;
        rectBtn.right = x + normal.getWidth();
    }
    protected void setPosBtnY(int y) {
        rectBtn.top = y;
        rectBtn.bottom = y + normal.getHeight();
    }
    protected int getPosX() {
        return rectBtn.left;
    }
    protected int getPosY() {
        return rectBtn.top;
    }
    protected Bitmap getBitmap() {
        if (pres) return pressed;
        return normal;
    }
    protected void touch(int x, int y) {
        if (rectBtn.contains(x, y)) {
            pres = true;
        }
        else pres = false;
    }
    protected void unTouch(int x, int y) {

        if (rectBtn.contains(x, y) && pres) {
            tripped = true;
        }
        else {
            tripped = false;
        }
        pres = false;
    }

    protected int getWidth() {
        return normal.getWidth();
    }
    protected int getHeight() {
        return normal.getHeight();
    }
}
