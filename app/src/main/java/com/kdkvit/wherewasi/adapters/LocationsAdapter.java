package com.kdkvit.wherewasi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdkvit.wherewasi.R;

import models.MyLocation;

import static com.kdkvit.wherewasi.fragments.ActivityFragment.locations;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    public LocationsAdapter(){
    }

    public interface LocationListener{
        void onClick(int position);
    }

    private LocationListener listener;

    public void setListener(LocationListener listener) {
        this.listener = listener;
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView timeTV;
        TextView coordinatesTV;
        TextView addressTv;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.location_time_tv);
            coordinatesTV = itemView.findViewById(R.id.coordinates_tv);
            addressTv = itemView.findViewById(R.id.location_address_tv);
            Button btn = (Button)itemView.findViewById(R.id.show_in_map_btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onClick(getAdapterPosition());
                    }
                }
            });

        }

    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_card,parent,false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        MyLocation location = locations.get(position);
        holder.timeTV.setText(String.format("%s %s", holder.itemView.getResources().getString(R.string.time), location.getEndTime().toString()));
        holder.coordinatesTV.setText(String.format("%s %s,%s", holder.itemView.getResources().getString(R.string.coordinates), df2.format(location.getLatitude()), df2.format(location.getLongitude())));
        holder.addressTv.setText(String.format("%s %s",holder.itemView.getResources().getString(R.string.address),location.getAddressLine()));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
