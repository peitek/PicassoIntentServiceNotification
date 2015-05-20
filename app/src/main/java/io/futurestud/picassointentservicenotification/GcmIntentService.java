package io.futurestud.picassointentservicenotification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class GcmIntentService extends IntentService {

    public static String NOTIFICATION_OPTION = "NOTIFICATION_OPTION";

    private int notificationId = 666;
    private NotificationManager mNotificationManager;

    public GcmIntentService() {
        super(GcmIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.remoteview_notification);
        remoteViews.setImageViewResource(R.id.remoteview_notification_icon, R.mipmap.ic_launcher);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(GcmIntentService.this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContent(remoteViews)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setAutoCancel(true);

        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        Notification notification = mBuilder.build();

        int option = intent.getIntExtra(NOTIFICATION_OPTION, 0);

        switch (option) {
            case 0:
                picassoCrash(remoteViews, notification);
                break;

            case 1:
                doubleSound(remoteViews, notification);
                break;

            case 2:
                doubleSoundAndDoubleDisplay(remoteViews, notification);
                break;
        }
    }


    /**
     * this method crashes since the Picasso call happens on a background thread
     */
    private void picassoCrash(RemoteViews remoteViews, Notification notification) {
        mNotificationManager.notify(notificationId, notification);

        Picasso
                .with(GcmIntentService.this)
                .load("http://i.imgur.com/rFLNqWI.jpg")
                .into(remoteViews, R.id.remoteview_notification_icon, notificationId, notification);
    }

    /**
     * this method loads the image correctly, but causes the notification sound to play twice (only if the image is loaded from the network)
     */
    private void doubleSound(final RemoteViews remoteViews, final Notification notification) {
        mNotificationManager.notify(notificationId, notification);

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso
                        .with(GcmIntentService.this)
                        .load("http://i.imgur.com/rFLNqWI.jpg")
                        .memoryPolicy(MemoryPolicy.NO_CACHE) // force the reload from network
                        .networkPolicy(NetworkPolicy.NO_CACHE) // the bugs will appear without these two lines, too
                        .into(remoteViews, R.id.remoteview_notification_icon, notificationId, notification);
            }
        });
    }

    /**
     * worst behavior: when using the notify(tag, id, notification) call, the entire notification shows up twice. Once with the image loaded, once without.
     */
    private void doubleSoundAndDoubleDisplay(final RemoteViews remoteViews, final Notification notification) {
        mNotificationManager.notify("notifyByTag", notificationId, notification);

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                Picasso
                        .with(GcmIntentService.this)
                        .load("http://i.imgur.com/rFLNqWI.jpg")
                        .memoryPolicy(MemoryPolicy.NO_CACHE) // force the reload from network
                        .networkPolicy(NetworkPolicy.NO_CACHE) // the bugs will appear without these two lines, too
                        .into(remoteViews, R.id.remoteview_notification_icon, notificationId, notification);
            }
        });
    }
}
