package pari.bet.paribet.ssport;

import android.app.Application;

import com.onesignal.OneSignal;

public class Myapp extends Application {

    private static final String ONESIGNAL_APP_ID = "a7ff1a19-de55-4465-8f12-5d725e1cb86f";

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
    }
}
