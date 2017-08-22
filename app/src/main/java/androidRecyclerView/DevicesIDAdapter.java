package androidRecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.javacodegeeks.Bluetooth.BluetoothChat;
import com.javacodegeeks.R;
import java.util.List;


/**
 * Created by eruvaka on 15-07-2017.
 */

public class DevicesIDAdapter extends RecyclerView.Adapter<DevicesIDAdapter.ViewHolder> {
    private List<DeviceIds> hexidList;
    public static final int SENDER = 0;
    public static final int RECIPIENT = 1;
    public Activity context1;

    public DevicesIDAdapter(Activity context, List<DeviceIds> hexids) {
        hexidList = hexids;
        context1=context;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView,mTextview2,medit;
        public LinearLayout liner_deviceId;

        public ViewHolder(LinearLayout v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.text1);
            mTextview2=(TextView)v.findViewById(R.id.text2);
            medit=(TextView)v.findViewById(R.id.edit);
            liner_deviceId=(LinearLayout)v.findViewById(R.id.liner_deviceId);

        }
    }

    @Override
    public DevicesIDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deviceids_layout, parent, false);
            DevicesIDAdapter.ViewHolder vh = new DevicesIDAdapter.ViewHolder((LinearLayout) v);
            return vh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deviceids_layout, parent, false);
            DevicesIDAdapter.ViewHolder vh = new DevicesIDAdapter.ViewHolder((LinearLayout) v);
            return vh;
        }
    }

    public void remove(int pos) {
        int position = pos;
        hexidList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, hexidList.size());

    }

    @Override
    public void onBindViewHolder(DevicesIDAdapter.ViewHolder holder, final int position) {
        holder.mTextView.setText(hexidList.get(position).gethex_id());
        holder.mTextview2.setText(hexidList.get(position).getstatus());
        holder.liner_deviceId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove(position);
               /* Intent i=new Intent(context1,BluetoothChat.class);
                i.putExtra("hex_id",hexidList.get(position).gethex_id());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                context1.startActivity(i);*/
               Intent intent = new Intent(context1, BluetoothChat.class);// New activity
                intent.putExtra("hex_id",hexidList.get(position).gethex_id());
                context1.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return hexidList.size();
    }

    @Override
    public int getItemViewType(int position) {
        DeviceIds deviceid = hexidList.get(position);
        if (deviceid.getstatus().equals("ERUVAKA")) {
            return SENDER;
        } else {
            return RECIPIENT;
        }

    }


}
