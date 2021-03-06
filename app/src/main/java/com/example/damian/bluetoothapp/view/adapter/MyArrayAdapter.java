package com.example.damian.bluetoothapp.view.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.damian.bluetoothapp.R;

/**
 * Created by Admin on 12.11.2015.
 */
public class MyArrayAdapter extends ArrayAdapter<BluetoothDevice> {
    ListListener listListener;
    public MyArrayAdapter(Context context, int resource, ListListener listListener) {
        super(context, resource);
        this.listListener = listListener;
    }

    static class Holder{
        TextView tvName;
        TextView tvDetails;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Holder holder = new Holder();
        if(v==null){
            v = LayoutInflater.from(getContext()).inflate(R.layout.list_item,null);
            holder.tvName = (TextView) v.findViewById(R.id.tvName);
            holder.tvDetails = (TextView) v.findViewById(R.id.tvDetails);
            v.setTag(holder);
        }else{
            holder = (Holder) v.getTag();
        }
        BluetoothDevice item = getItem(position);
        holder.tvName.setText(item.getName());
        holder.tvDetails.setText(item.getAddress());
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listListener.onItemSelected(getItem(position));
            }
        });
        return v;
    }

    public interface ListListener{
        void onItemSelected(BluetoothDevice device);
    }
}
