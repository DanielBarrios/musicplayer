package pe.com.danielbarrios.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

import pe.com.danielbarrios.musicplayer.bean.Constantes;
import pe.com.danielbarrios.musicplayer.view.DatosMusicaActivity;

/**
 * Created by DBarrios on 27/09/2016.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener {

    public static final String ACTION_PLAY = "pe.com.danielbarrios.musicplayer.PLAY";
    public static final String ACTION_PAUSE = "pe.com.danielbarrios.musicplayer.PAUSE";
    public static final String ACTION_STOP = "pe.com.danielbarrios.musicplayer.STOP";
    MediaPlayer mediaPlayer;
    final Handler mHandler = new Handler();
    Runnable mRunnable = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        System.out.println("Entre a onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Entre en onStartCommand");
        if(intent.getAction()!=null)
            procesarEstados(intent);
        return START_STICKY;
    }

    private void procesarEstados(Intent intent) {


        switch (intent.getAction()){

            case ACTION_PLAY:

                try {
                    if(mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                    mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    System.out.println("Ruta en intent extra: "+intent.getStringExtra("rutaCancion"));
                    mediaPlayer.setDataSource(intent.getStringExtra("rutaCancion"));
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(this);

                    enviarAvance();




                } catch (Exception e) {
                    e.printStackTrace();
                }


                break;
            case ACTION_STOP:

                mediaPlayer.stop();
                mediaPlayer.reset();

                if(mHandler!=null && mRunnable!=null)
                    mHandler.removeCallbacks(mRunnable);

                break;
            case ACTION_PAUSE:

                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    if(mHandler!=null && mRunnable!=null)
                        mHandler.removeCallbacks(mRunnable);
                }else {
                    mediaPlayer.start();
                    enviarAvance();
                }
                break;
            default:
                Log.i(Constantes.TAG_LOG,"Default case.");
                break;



        }


    }

    public void enviarAvance(){
        mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent1 = new Intent(DatosMusicaActivity.ACTION_AVANCE);
                intent1.putExtra("avance",mediaPlayer.getCurrentPosition());
                sendBroadcast(intent1);
                mHandler.post(this);
            }
        };

        mHandler.post(mRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        Log.e(Constantes.TAG_LOG, "Error en el MediaPlayer Service");
        return false;
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) mediaPlayer.release();
    }
}
