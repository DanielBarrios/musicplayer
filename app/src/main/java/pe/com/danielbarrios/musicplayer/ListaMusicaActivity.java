package pe.com.danielbarrios.musicplayer;

import android.app.Activity;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ListaMusicaActivity extends AppCompatActivity {

    ArrayList<String> rutasMusica;
    ArrayList<String> nombresMusica;
    final String MEDIA_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/";
    private ArrayList<HashMap<String, String>> songsList = new ArrayList();
    private String mp3Pattern = ".mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_musica);
        inicializarVariables();
    }

    private void inicializarVariables() {
        rutasMusica = new ArrayList();
        nombresMusica = new ArrayList();
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
                updateMusicList();
                return true;
            default:
                return false;
        }
    }

    private void updateMusicList() {
        getPlayList();

        for (HashMap<String,String> musica : songsList){

            System.out.println("Titulo: " + musica.get("songTitle"));
            System.out.println("Path: " + musica.get("songPath"));


        }
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
            songMap.put("songTitle",
                    song.getName().substring(0, (song.getName().length() - 4)));
            songMap.put("songPath", song.getPath());

            // Adding each song to SongList
            songsList.add(songMap);
        }
    }
}
