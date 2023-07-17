package com.example.alarmcenter.Views;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmcenter.Models.Room.NotificationEntity;
import com.example.alarmcenter.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationHolder> {
    private List<NotificationEntity> notificationEntities = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d("MVVM_TAG", "[Adapter] - onCreateViewHolder()");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);

        return new NotificationHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
//        Log.d("MVVM_TAG", "[Adapter] - onBindViewHolder() position : "+ String.valueOf(position));
        NotificationEntity currentNotification = notificationEntities.get(position);
        holder.item_tv_title.setText(currentNotification.getTitle());
        holder.item_tv_context.setText(currentNotification.getContext());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String postedDate = simpleDateFormat.format(currentNotification.getPosted_date());
        holder.item_tv_date.setText(postedDate);

    }

    @Override
    public int getItemCount() {
//        Log.d("MVVM_TAG", "[Adapter] - getItemCount() size : " + String.valueOf(notificationEntities.size()));
        return notificationEntities.size();
    }

    public void setNotificationEntities(List<NotificationEntity> notificationEntities) {
        Log.d("MVVM_TAG", "[Adapter] - setNotificationEntities() size : " + String.valueOf(notificationEntities.size()));
        this.notificationEntities = notificationEntities;
        notifyDataSetChanged();
    }

    public NotificationEntity getNotificationAt(int position){
        Log.d("MVVM_TAG", "[Adapter] - getNotificationAt() position : " + String.valueOf(position));
        return notificationEntities.get(position);
    }

    public class NotificationHolder extends RecyclerView.ViewHolder {
        private TextView item_tv_title;
        private TextView item_tv_context;
        private TextView item_tv_date;

        public NotificationHolder(@NonNull View itemView) {
            super(itemView);

//            Log.d("MVVM_TAG", "[Adapter] - NoteHolder() 생성자");

            item_tv_title = itemView.findViewById(R.id.item_tv_title);
            item_tv_context = itemView.findViewById(R.id.item_tv_context);
            item_tv_date = itemView.findViewById(R.id.item_tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("MVVM_TAG", "[Adapter] - NoteHolder() - itemview onClick() position : " + String.valueOf(position));
                    if(listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notificationEntities.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NotificationEntity notification);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        Log.d("MVVM_TAG", "[Adapter] - setOnItemClickListener() - listener method()");
        this.listener = listener;
    }
}
