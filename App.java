package ru.tmkstd.cardgamedurakonline;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

public class App extends Application {
    private DownloadAllRes downloadAllRes;
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }
    public void loadingRes(int maxX, int maxY) {
        downloadAllRes = new DownloadAllRes(this,maxX,maxY);
    }

    public DownloadAllRes getDownloadAllRes() {
        return downloadAllRes;
    }

    public void setDownloadAllRes(DownloadAllRes res) {
        downloadAllRes = res;
    }

    //без этого была ошибка на планшете Android 4.4. говорят из-за мультидекса. После оптимизации может быть уберу мультидекс и это
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
