package pe.com.danielbarrios.musicplayer.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import pe.com.danielbarrios.musicplayer.R;

public class DatosMusicaActivity extends ActionBarActivity {

    public static final String ACTION_AVANCE = "pe.com.danielbarrios.musicplayer.AVANCE";
    TextView textview_avance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_musica);
        setFilters();
        textview_avance = (TextView)findViewById(R.id.textview_avance);
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
                if(intent.getAction().equals(ACTION_AVANCE)){
                    textview_avance.setText(intent.getStringExtra("avance"));
                }

        }
    };

    public void setFilters(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_AVANCE);
        registerReceiver(mBroadcastReceiver, filter);
    }

}
