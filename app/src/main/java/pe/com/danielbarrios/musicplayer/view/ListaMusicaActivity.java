package pe.com.danielbarrios.musicplayer.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import pe.com.danielbarrios.musicplayer.util.AdapterView;

public class ListaMusicaActivity extends AppCompatActivity {

    final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private String mp3Pattern = ".mp3";
    ArrayList<CancionBean> arrayListaCanciones = new ArrayList();
    MediaPlayer mediaPlayer;
    File traceFile;

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

        traceFile = new File(((Context)this).getExternalFilesDir(null),Constantes.CONFIG_FILE_MUSIC);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

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
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                System.out.println("REPRODUCIENDO : " + arrayListaCanciones.get(position).getRutaCancion());
                mediaPlayer.setDataSource(arrayListaCanciones.get(position).getRutaCancion());
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });



                //https://developer.android.com/guide/topics/media/mediaplayer.html


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


}
