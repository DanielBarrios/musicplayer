package pe.com.danielbarrios.musicplayer.view;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import pe.com.danielbarrios.musicplayer.R;
import pe.com.danielbarrios.musicplayer.bean.CancionBean;
import pe.com.danielbarrios.musicplayer.util.AdapterView;

public class ListaMusicaActivity extends AppCompatActivity {

    final String MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    private ArrayList<HashMap<String, String>> songsList = new ArrayList();
    private String mp3Pattern = ".mp3";
    private TextView textViewListaMusica;
    ArrayList<CancionBean> arrayListaCanciones = new ArrayList();
    String texto = "";
    CancionBean[] listaCanciones = new CancionBean[20];
    int indice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_musica);
        inicializarVariables();
    }

    private void inicializarVariables() {

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
                System.out.println("Aprete el boton");
                arrayListaCanciones.clear();
                updateMusicList();
                return true;
            default:
                return false;
        }
    }

    private void updateMusicList() {
        getPlayList();

        for (HashMap<String,String> musica : songsList){

            texto = texto + "\n" +"Titulo: "+musica.get("songTitle")+ " ; Path: "+musica.get("songPath");
            System.out.println(texto);
            //System.out.println("Titulo: " + musica.get("songTitle"));
            //System.out.println("Path: " + musica.get("songPath"));


        }

        inicializarAdapters();
    }

    public ArrayList<HashMap<String, String>> getPlayList() {
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
        // return songs list array
        return songsList;
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
            HashMap<String, String> songMap = new HashMap<String, String>();
            songMap.put("songTitle",song.getName().substring(0, (song.getName().length() - 4)));
            songMap.put("songPath", song.getPath());

            CancionBean cancionBean = new CancionBean();
            cancionBean.setNombreCancion(song.getName().substring(0, (song.getName().length() - 4)));
            cancionBean.setRutaCancion(song.getPath());
            //listaCanciones[indice] = cancionBean;
            arrayListaCanciones.add(cancionBean);
            System.out.println("ITEM : " +cancionBean.getNombreCancion());
            // Adding each song to SongList
            songsList.add(songMap);
            indice++;
        }
    }

    public void inicializarAdapters(){
        AdapterView adapter = new AdapterView(this, R.layout.list_item_song,arrayListaCanciones.toArray());
        ListView listViewItems = (ListView)findViewById(R.id.list_view_lista_canciones);
        listViewItems.setAdapter(adapter);
        listViewItems.setOnItemClickListener(mOnClickListener);
    }

    android.widget.AdapterView.OnItemClickListener mOnClickListener = new android.widget.AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(android.widget.AdapterView<?> parent, View view, int position, long id) {
            //play musica

            try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(arrayListaCanciones.get(position).getRutaCancion());
            mediaPlayer.prepare();
            mediaPlayer.start();

                //https://developer.android.com/guide/topics/media/mediaplayer.html


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


}
