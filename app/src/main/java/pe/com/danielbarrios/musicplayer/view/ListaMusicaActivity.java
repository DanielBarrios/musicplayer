package pe.com.danielbarrios.musicplayer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pe.com.danielbarrios.musicplayer.R;
import pe.com.danielbarrios.musicplayer.bean.CancionBean;
import pe.com.danielbarrios.musicplayer.bean.Constantes;
import pe.com.danielbarrios.musicplayer.service.MusicService;
import pe.com.danielbarrios.musicplayer.util.AdapterView;

public class ListaMusicaActivity extends AppCompatActivity implements View.OnClickListener {

    final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private String mp3Pattern = ".mp3";
    ArrayList<CancionBean> arrayListaCanciones = new ArrayList();
    MediaPlayer mediaPlayer;
    File traceFile;
    Button boton_pause;
    Button boton_stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_musica);
        inicializarVariables();
        validarDataInicial();
    }

    private void validarDataInicial() {

        if (traceFile.exists()){
            try {
                BufferedReader reader = new BufferedReader(new FileReader(traceFile));
                String jsonData = reader.readLine();
                arrayListaCanciones = new Gson().fromJson(jsonData, new TypeToken<ArrayList<CancionBean>>() {}.getType());
                setearAdapters();
            } catch (Exception e) {
                Log.e(Constantes.TAG_LOG, "Error en el grabado de archivo conf. Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void crearDatosStorage() {

        try
        {
            if (!traceFile.exists())
                traceFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(traceFile, false /*append*/));
            writer.write(new Gson().toJson(arrayListaCanciones));
            writer.close();
            MediaScannerConnection.scanFile((Context) (this),new String[]{traceFile.toString()},null,null);

        }
        catch (Exception e){
            Log.e(Constantes.TAG_LOG,"Error en el grabado de archivo conf. Error: "+e.getMessage());
        }
    }


    private void inicializarVariables() {

        boton_pause = (Button)findViewById(R.id.boton_pause);
        boton_stop = (Button)findViewById(R.id.boton_stop);
        boton_pause.setOnClickListener(this);
        boton_stop.setOnClickListener(this);
        traceFile = new File(((Context)this).getExternalFilesDir(null),Constantes.CONFIG_FILE_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        inicializarServicio();
        setFilters();
    }

    public void inicializarServicio(){
        Intent intent = new Intent(ListaMusicaActivity.this, MusicService.class);
        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_lista_musica, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_update_item:
                arrayListaCanciones.clear();
                updateMusicList();
                return true;
            default:
                return false;
        }
    }

    private void updateMusicList() {
        getPlayList();
        setearAdapters();
    }

    public void getPlayList() {
        System.out.println(MEDIA_PATH);
        if (MEDIA_PATH != null) {
            File home = new File(MEDIA_PATH);
            File[] listFiles = home.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    System.out.println(file.getAbsolutePath());
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }
                }
            }
        }
        crearDatosStorage();
    }

    private void scanDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File file : listFiles) {
                    if (file.isDirectory()) {
                        scanDirectory(file);
                    } else {
                        addSongToList(file);
                    }

                }
            }
        }
    }

    private void addSongToList(File song) {
        if (song.getName().endsWith(mp3Pattern)) {
            CancionBean cancionBean = new CancionBean();
            cancionBean.setNombreCancion(song.getName().substring(0, (song.getName().length() - 4)));
            cancionBean.setRutaCancion(song.getPath());
            arrayListaCanciones.add(cancionBean);

        }
    }

    public void setearAdapters(){
        AdapterView adapter = new AdapterView(this, R.layout.list_item_song,arrayListaCanciones.toArray());
        ListView listViewItems = (ListView)findViewById(R.id.list_view_lista_canciones);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(mOnClickListener);
    }

    android.widget.AdapterView.OnItemClickListener mOnClickListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
            try {

                //https://www.tutorialspoint.com/android/android_mediaplayer.htm
//                if(mediaPlayer.isPlaying()) {
//                    mediaPlayer.stop();
//                    mediaPlayer.reset();
//                }
//                mediaPlayer = new MediaPlayer();
//                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                //mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
//                System.out.println("REPRODUCIENDO : " + arrayListaCanciones.get(position).getRutaCancion());
//                mediaPlayer.setDataSource(arrayListaCanciones.get(position).getRutaCancion());
//                mediaPlayer.prepare();
//                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mediaPlayer) {
//                        mediaPlayer.start();
//                    }
//                });



                //https://developer.android.com/guide/topics/media/mediaplayer.html

                Intent intent = new Intent(ListaMusicaActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_PLAY);
                intent.putExtra("rutaCancion",arrayListaCanciones.get(position).getRutaCancion());
                startService(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.boton_pause:

                Intent intent = new Intent(ListaMusicaActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_PAUSE);
                startService(intent);

            break;

            case R.id.boton_stop:

                intent = new Intent(ListaMusicaActivity.this, MusicService.class);
                intent.setAction(MusicService.ACTION_STOP);
                startService(intent);

            break;
        }

    }

    private BroadcastReceiver mAudioBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) { //Cuando el headset se desconecta
                Intent intentBR = new Intent(ListaMusicaActivity.this, MusicService.class);
                intentBR.setAction(MusicService.ACTION_STOP);
                startService(intentBR);
                Toast.makeText(getApplicationContext(), "Broadcast Noisy Reconocido ....", Toast.LENGTH_SHORT).show();
            }else if(intent.getAction().equals(android.media.AudioManager.ACTION_HEADSET_PLUG)){
                int state = intent.getIntExtra("state", -1);
                if(state==0)
                    Toast.makeText(getApplicationContext(), "DESCONECTADO HEADSET ....", Toast.LENGTH_SHORT).show();
                else
                    if(state==1)
                        Toast.makeText(getApplicationContext(), "CONECTADO HEADSET ....", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void setFilters(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        filter.addAction(android.media.AudioManager.ACTION_HEADSET_PLUG);
        registerReceiver(mAudioBroadcastReceiver, filter);
    }

    //TODO: ver donde hacer el stopService y unregister receiver

}
