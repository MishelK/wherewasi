package com.kdkvit.wherewasi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kdkvit.wherewasi.R;

import models.LocationsGroup;

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
        TextView locationsTV;
        TextView interactionsTV;
        LinearLayout statusCircleView;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);

            timeTV = itemView.findViewById(R.id.location_time_tv);
            locationsTV = itemView.findViewById(R.id.location_locations_tv);
            interactionsTV = itemView.findViewById(R.id.location_interactions_tv);
            statusCircleView = itemView.findViewById(R.id.status_circle_view);

            itemView.setOnClickListener(new View.OnClickListener() {
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
        LocationsGroup location = locations.get(position);
        String startTime = new SimpleDateFormat("HH:mm dd-MM").format(location.getStartTime());

        String endTime = new SimpleDateFormat("HH:mm dd-MM").format(location.getEndTime());
        holder.timeTV.setText(String.format("%s %s - %s", holder.itemView.getResources().getString(R.string.time), startTime, endTime));

        //holder.coordinatesTV.setText(String.format("%s %s,%s", holder.itemView.getResources().getString(R.string.coordinates), df2.format(location.getLatitude()), df2.format(location.getLongitude())));
        holder.locationsTV.setText(String.format("%s %s", holder.itemView.getResources().getString(R.string.locations), location.locationsSize()));

        holder.interactionsTV.setText(String.format("%s %s", holder.itemView.getResources().getString(R.string.interactions), location.interactionsSize()));

        if(location.getPositiveInteractions().size()>0){
            holder.statusCircleView.setBackgroundResource(R.drawable.circle_dra_orange);
        }else {
            holder.statusCircleView.setBackgroundResource(R.drawable.circle_dra_gren);
        }

        //todo: mark in red

    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
