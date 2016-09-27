package pe.com.danielbarrios.musicplayer.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import pe.com.danielbarrios.musicplayer.R;
import pe.com.danielbarrios.musicplayer.bean.CancionBean;

/**
 * Created by alumno on 9/26/16.
 */
public class AdapterView extends ArrayAdapter<Object> {


    Context mContext;
    int layoutResourceId;
    Object data[] = null;


    public AdapterView(Context mContext, int layoutResourceId, Object[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

        }

        System.out.println("Tamanio: "+data.length);
        CancionBean cancionBean = (CancionBean)data[position];

        // get the TextView and then set the text (item name) and tag (item ID) values
        TextView textViewItem = (TextView) convertView.findViewById(R.id.text_view_nombre_cancion);
        textViewItem.setText(cancionBean.getNombreCancion());
        textViewItem.setTag("TAG");

        return convertView;

    }
}
