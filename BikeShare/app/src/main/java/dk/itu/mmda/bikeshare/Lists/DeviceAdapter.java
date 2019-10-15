package dk.itu.mmda.bikeshare.Lists;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dk.itu.mmda.bikeshare.R;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceHolder> {

    private List<String> mDevices;

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new DeviceHolder(inflater, viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder deviceHolder, int i) {
        if (mDevices != null && mDevices.size() > i) {
            String device = mDevices.get(i);
            deviceHolder.bind(device);
        } else
            deviceHolder.bind("No device available");
    }

    @Override
    public int getItemCount() {
        if (mDevices != null)
            return mDevices.size();

        return 0;
    }

    public void setDevices(List<String> devices) {
        mDevices = devices;
        notifyDataSetChanged();
    }

}

class DeviceHolder extends RecyclerView.ViewHolder {

    private TextView mDevice;

    DeviceHolder(@NonNull LayoutInflater inflater, ViewGroup parent) {
        super(inflater.inflate(R.layout.list_paired_device, parent, false));
        mDevice = itemView.findViewById(R.id.device);
    }

    void bind(String device) {
        mDevice.setText(device);
    }

}

