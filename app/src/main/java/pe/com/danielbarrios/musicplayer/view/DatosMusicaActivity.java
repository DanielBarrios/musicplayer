package pe.com.danielbarrios.musicplayer.view;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import pe.com.danielbarrios.musicplayer.R;
import pe.com.danielbarrios.musicplayer.service.MusicService;

public class DatosMusicaActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String ACTION_AVANCE = "pe.com.danielbarrios.musicplayer.AVANCE";
    public static final String ACTION_DURATION = "pe.com.danielbarrios.musicplayer.DURATION";
    TextView textview_avance;
    TextView textview_nombre_cancion;
    Button button_play_pause;
    Button button_stop;
    boolean isPaused = true;
    ProgressBar progressBar_musica;
    NotificationManager notificationManager;
    String nombreCancionActual  = "";
    boolean nextScreen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_musica);
        setFilters();
        textview_avance = (TextView)findViewById(R.id.textview_avance);
        textview_nombre_cancion = (TextView)findViewById(R.id.textview_nombre_cancion);
        button_play_pause = (Button)findViewById(R.id.button_play_pause);
        button_stop = (Button)findViewById(R.id.button_stop);
        button_play_pause.setOnClickListener(this);
        button_stop.setOnClickListener(this);
        isPaused = getIntent().getExtras() != null ? getIntent().getExtras().getBoolean("pause",true) : true;
//        if(getIntent().getExtras()!=null)
//            nombreCancionActual = getIntent().getExtras().getString("nombreCancion")!= null ? getIntent().getExtras().getString("nombreCancion") : "";
//        textview_nombre_cancion.setText(nombreCancionActual);
        progressBar_musica = (ProgressBar)findViewById(R.id.progressBar);
//        progressBar_musica.setMax(100);
        progressBar_musica.setProgress(0);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nextScreen = false;
        solicitarDuracion();
        cambiarTextoBotonPausePlay();
    }

    private void solicitarDuracion() {
        Intent intent = new Intent(DatosMusicaActivity.this, MusicService.class);
        intent.setAction(MusicService.REQUEST_DATA);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_datos_musica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent!=null)
                System.out.println("ACTION: "+intent.getAction());
                if(intent.getAction().equals(ACTION_AVANCE)){
                    int seconds = (intent.getIntExtra("avance",0))/1000;
                    textview_avance.setText(seconds+"");
                    progressBar_musica.setProgress(seconds);
                }else if(intent.getAction().equals(ACTION_DURATION)){
                    System.out.println("Entro action duration");
                    progressBar_musica.setMax(intent.getIntExtra("duracion", 0) / 1000);
                    nombreCancionActual = intent.getStringExtra("nombreCancion") != null ? intent.getStringExtra("nombreCancion") : "NO SETEADO";
                    textview_nombre_cancion.setText(nombreCancionActual);
                    System.out.println("Recuperado .... Nombre: "+nombreCancionActual + " ; Max:"+progressBar_musica.getMax());
                }

        }
    };

    public void setFilters(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_AVANCE);
        filter.addAction(ACTION_DURATION);
        registerReceiver(mBroadcastReceiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("ON STOP");
        if(!nombreCancionActual.equalsIgnoreCase("") && !nextScreen && !isPaused){
            mostrarNotificacion(nombreCancionActual);
        }else
            System.out.println("NO IF ... nombre:"+nombreCancionActual + " ; bool:"+nextScreen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        desactivarNotificacion();
        nextScreen = false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        nextScreen = true;
        Intent intentRegresar = new Intent(ListaMusicaActivity.ACTION_RECIBE_NOMBRE_CANCION);
        intentRegresar.putExtra("nombreCancion", nombreCancionActual);
        intentRegresar.putExtra("pause",isPaused);
        sendBroadcast(intentRegresar);
    }

    public void mostrarNotificacion(String nombreCancion){
        PendingIntent mPendingIntent;
        Intent intent = new Intent(this, DatosMusicaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        |Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        intent.putExtra("valor_noti", "aa");
        mPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        //notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());

        mBuilder.setAutoCancel(false);
        mBuilder.setContentTitle("MusicPlayer");
        mBuilder.setTicker("Reproduciendo: "+nombreCancion);
        mBuilder.setContentText(nombreCancion);
        mBuilder.setSmallIcon(R.drawable.ico_update);
        mBuilder.setContentIntent(mPendingIntent);
        mBuilder.setOngoing(true);
        //API level 16
//        mBuilder.setSubText("This is short description of android app notification");
        mBuilder.setNumber(150);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ico_update));
//        mBuilder.build();
        Notification mNotification;
        mNotification = mBuilder.getNotification();
        notificationManager.notify(11, mNotification);
        //http://www.viralandroid.com/2016/05/show-and-clear-android-notification-example.html
        //click derecho en res, nuevo image asset, y en el combobox, elegir la opcion de icon bar.., tambien ver como se hace el icono de la app en launcher ( la grande)..


    }

    public void desactivarNotificacion(){
        if(notificationManager!=null)
            notificationManager.cancel(11);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_play_pause: funcionPlayPause();break;
            case R.id.button_stop: funcionStop();break;
        }
    }

    private void funcionStop() {

        Intent intent = new Intent(DatosMusicaActivity.this, MusicService.class);
        intent.setAction(MusicService.ACTION_STOP);
        startService(intent);
        nombreCancionActual="";
        isPaused = true;
        textview_nombre_cancion.setText(nombreCancionActual);
        desactivarNotificacion();
        cambiarTextoBotonPausePlay();

    }

    private void funcionPlayPause() {

        Intent intent = new Intent(DatosMusicaActivity.this, MusicService.class);
        intent.setAction(MusicService.ACTION_PAUSE);
        startService(intent);
        if(isPaused)
            isPaused = false;
        else
            isPaused = true;
        cambiarTextoBotonPausePlay();

    }

    public void cambiarTextoBotonPausePlay(){
        if(isPaused)
            button_play_pause.setText("PLAY");
        else
            button_play_pause.setText("PAUSE");
    }
}
