package com.kdkvit.wherewasi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.kdkvit.wherewasi.MainActivity.locations;

//import static com.kdkvit.wherewasi.MainActivity.locations;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationViewHolder> {


    public LocationsAdapter(){
    }

    public static class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView timeTV;
        TextView coordinatesTV;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.location_time_tv);
            coordinatesTV = itemView.findViewById(R.id.coordinates_tv);
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
        holder.timeTV.setText(String.format("%s %s", holder.itemView.getResources().getString(R.string.time), location.getTime().toString()));
        holder.coordinatesTV.setText(String.format("%s %s,%s", holder.itemView.getResources().getString(R.string.coordinates), location.getLatitude(), location.getLongitude()));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
