package com.example.hiddentreasures;

import android.app.Application;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Typeface customFontTypeface = Typeface.createFromAsset(getAssets(), "source_code_pro.ttf");

        try {
            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField("MONOSPACE");
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
