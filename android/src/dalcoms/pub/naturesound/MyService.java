package dalcoms.pub.naturesound;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyService extends Service {
    private static final String tag = "MyService";
    private static final String notiChId = "dalcoms.naturesound";
    private static final int notificationId = 8641;

    private final IBinder mBinder = new LocalBinder();
    MediaPlayer mpMusicBox;
    int[] musicBoxIds = {
            R.raw.musicbox_1, R.raw.musicbox_2, R.raw.musicbox_3, R.raw.musicbox_4,
            R.raw.musicbox_5, R.raw.musicbox_6, R.raw.musicbox_7, R.raw.musicbox_8,
            R.raw.musicbox_9, R.raw.musicbox_10, R.raw.musicbox_11, R.raw.musicbox_12,
            R.raw.musicbox_13, R.raw.musicbox_14, R.raw.musicbox_15, R.raw.musicbox_16,
            R.raw.musicbox_17, R.raw.musicbox_18, R.raw.musicbox_19
    };
    int musicBoxIndex;

    ArrayList<MediaPlayer> mpSounds;

    int[] soundIds = {
            R.raw.bg_wave, R.raw.bg_owl, R.raw.bg_fire,
            R.raw.bg_bell, R.raw.bg_stream, R.raw.bg_bird,
            R.raw.bg_rain, R.raw.bg_frog, R.raw.bg_meditation,
            R.raw.bg_study, R.raw.bg_sleep, R.raw.bg_white_noise
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaPlayer();
        createNotificationChannel();
        Log.d(tag, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(tag, "onStartCommand (intent,flags,startId)" + intent.toString() + "," + flags + "," +
                   startId);

        if (intent == null) {
            return Service.START_STICKY;
        } else {
            String cmd = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");
            Log.d(tag, intent.getAction() + "," + cmd + "," + name);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(tag, "onDestroy");
        if (mpMusicBox != null) {
            mpMusicBox.release();
        }
        super.onDestroy();

    }

    private int getNextMusicBoxIndex() {
        musicBoxIndex = musicBoxIndex < musicBoxIds.length - 1 ? musicBoxIndex + 1 : 0;
        Log.d(tag, "music box index = " + musicBoxIndex);
        return musicBoxIndex;
    }

    private void initMediaPlayer() {
        musicBoxIndex = new Random().nextInt(musicBoxIds.length - 1);
        Log.d(tag, "music box index = " + musicBoxIndex);
        mpMusicBox = MediaPlayer.create(getApplicationContext(), musicBoxIds[musicBoxIndex]);
        mpMusicBox.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mpMusicBox.release();
                playNextMusicBox();
            }
        });
        mpSounds = new ArrayList<>();
        for (int i = 0; i < soundIds.length; i++) {
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), soundIds[i]);
            mp.setLooping(true);
            mpSounds.add(mp);
        }
    }

    private void playNextMusicBox() {
        mpMusicBox = MediaPlayer
                .create(getApplicationContext(), musicBoxIds[getNextMusicBoxIndex()]);
        mpMusicBox.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mpMusicBox.release();
                playNextMusicBox();
            }
        });
        mpMusicBox.start();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Nature Meditation Sound";
            String description = "Nature Meditation Sound";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(notiChId, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void attachNotification() {
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, AndroidLauncher.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent,
                                          Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                                                  PendingIntent.FLAG_IMMUTABLE :
                                                  PendingIntent.FLAG_UPDATE_CURRENT);

        Intent actionStartPause = new Intent(CommandActions.NOTI_START_PAUSE);
        PendingIntent intentStartPause
                = PendingIntent.getBroadcast(this, 0, actionStartPause,
                                             Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ?
                                                     PendingIntent.FLAG_IMMUTABLE :
                                                     PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, notiChId)
                .setSmallIcon(R.drawable.notismallico)
                .setContentTitle("Nature Meditation Sound")
                .setContentText("Inner peace and calm")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent);
//                .addAction(R.drawable.outline_pause_circle_filled_black_24, "Pause",
//                           intentStartPause);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
        startForeground(notificationId, builder.build());

    }

    public void playMusic(int index, boolean isPlay, float volume) {

        if (index == 0) {//music box
            if (isPlay) {
                mpMusicBox.start();
                mpMusicBox.setVolume(volume, volume);
            } else {
                mpMusicBox.pause();
                //music box : pause -> start
                //sounds : pause -> seek to zeo -> start
            }
        } else {
            if (isPlay) {
                mpSounds.get(index - 1).start();
                mpSounds.get(index - 1).setVolume(volume, volume);
            } else {
                mpSounds.get(index - 1).pause();
                mpSounds.get(index - 1).seekTo(0);
            }
        }
    }

    public void stopAllMusic() {
        mpMusicBox.stop();
    }

    class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
}
