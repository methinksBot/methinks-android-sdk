package io.mtksdk.activitylogmanager;

import android.graphics.Bitmap;
import android.view.View;

public class ScreenShotManager {

    public static Bitmap getScreenShot(View v) {
        try {
            v.setDrawingCacheEnabled(true);
            v.buildDrawingCache(true);
            Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
            v.setDrawingCacheEnabled(false);
            return b;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
