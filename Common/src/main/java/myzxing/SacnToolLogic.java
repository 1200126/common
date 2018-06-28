package myzxing;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.Result;

import myzxing.camera.CameraManager;
import myzxing.view.ViewfinderView;

public abstract class SacnToolLogic {

    public abstract ViewfinderView getViewfinderView();

    public abstract Handler getHandler();

    public abstract CameraManager getCameraManager();

    public abstract void drawViewfinder();

    public abstract Activity getActivity();

    public abstract void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor);

    public abstract void setResult(Message message);

    public abstract PackageManager getPackageManager();

    public abstract void startActivity(Intent intent);





}
