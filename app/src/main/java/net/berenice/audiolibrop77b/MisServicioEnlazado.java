package net.berenice.audiolibrop77b;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import androidx.preference.PreferenceManager;

import java.io.IOException;


public class MisServicioEnlazado extends Service implements MediaPlayer.OnPreparedListener, MediaController.MediaPlayerControl {

    MediaPlayer mediaPlayer;
    public static String ARG_ID_LIBRO = "id_libro";
    String guardar = "";
    Activity activity;
    View view;
    public int libroID;

    MediaController mediaController;
    private final IBinder binder = new MiBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return binder;

    }

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public MisServicioEnlazado() {
    }


    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
    }

    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }


    public MediaController getMediaController(){
        return this.mediaController;
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }



    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }



    public class MiBinder extends Binder {

        public MisServicioEnlazado getService() {
            return MisServicioEnlazado.this;
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }


    public void onPrepared(MediaPlayer player) {

        Log.d("AudioLibros", "Entramos en onPrepared de MediaPlayer");
        SharedPreferences preferencias = PreferenceManager.getDefaultSharedPreferences(activity);
        mediaPlayer.start();

        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(view);
        mediaController.setPadding(0, 0, 0, 110);
        mediaController.setEnabled(true);
        mediaController.show();

        Intent intent = new Intent(activity.getApplicationContext(), MisServicioEnlazado.class);

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public void notificacion(){
        createNotificationChannel();
        // Create an explicit intent for an Activity in your app
        Intent intentN = new Intent(this, MainActivity.class);
        intentN.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentN, 0);

      /*  NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.books)
                .setContentTitle("Audiolibros")
                .setContentText("Se esta reproduciendo un audio")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
*/
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Mi Audio Libros P77B")
                .setContentText("Este es un servicio en primer plano")
                .setSmallIcon(R.drawable.preview)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

    }


    public IBinder getBinder() {
        return binder;
    }


    private static String TAG = "ForegroundService";


    public void setMediaPlayer(Activity activity, Libro libro, View view, int idLibro) {
        this.activity = activity;
        this.view = view;
        this.libroID = idLibro;

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(activity);
        Uri audio = Uri.parse(libro.urlAudio);
        try {
            mediaPlayer.setDataSource(activity, audio);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir " + audio, e);
        }
    }


    public void Stop() {
        mediaController.hide();
        try {
            mediaPlayer.stop();
            //mediaPlayer.release();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }

    }
    public void showMedia(){
        mediaController.show();

    }

}