package com.example.hiddentreasures;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class NotificationService extends FirebaseMessagingService {

  /**
   * Called when receives notification and app is in the foreground. Shows a toast rather than
   * sending notification to notification tray. If app is in background/is completely closed, the
   * user receives notifications that go to the notification tray as normal.
   *
   * @param remoteMessage The notification that was received
   */
  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {

    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(() -> {
      Toast.makeText(
          getApplicationContext(),
          remoteMessage.getNotification().getBody(),
          Toast.LENGTH_LONG)
          .show();
    });
  }
  
}
