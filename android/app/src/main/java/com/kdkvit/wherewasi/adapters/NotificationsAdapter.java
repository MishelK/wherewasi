package com.kdkvit.wherewasi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kdkvit.wherewasi.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import models.LocationsGroup;
import models.MyNotification;

import static com.kdkvit.wherewasi.fragments.ActivityFragment.locations;
import static com.kdkvit.wherewasi.fragments.MainFragment.notifications;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    Context context;

    public NotificationsAdapter(Context context) {
        this.context = context;
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView mainText;
        TextView dateTV;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTV = itemView.findViewById(R.id.notification_date);
            mainText = itemView.findViewById(R.id.notification_content);

        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        MyNotification notification = notifications.get(position);
        if(notification.getDays() == 0){
            holder.dateTV.setText(context.getString(R.string.today));
        }else if(notification.getDays() == 1){
                holder.dateTV.setText(context.getString(R.string.yestrday));
        }else{
            holder.dateTV.setText(String.format("%d %s", notification.getDays(), context.getString(R.string.days_ago)));
        }

        holder.mainText.setText(notification.getText());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }
}
