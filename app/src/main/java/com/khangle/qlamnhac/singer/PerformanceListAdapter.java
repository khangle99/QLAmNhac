package com.khangle.qlamnhac.singer;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.model.PerformanceInfo;
import com.khangle.qlamnhac.model.PerformanceSongTuple;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
@RequiresApi(api = Build.VERSION_CODES.O)
public class PerformanceListAdapter extends ListAdapter<PerformanceSongTuple, PerformanceListAdapter.PerformanceViewHolder> {
    private PerformanceClickListener listener;
    public PerformanceListAdapter(PerformanceClickListener listener) {
        super(PerformanceCallback);
        this.listener = listener;
    }
    @NonNull
    @Override
    public PerformanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_performance, parent, false);
        return new PerformanceViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PerformanceViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class PerformanceViewHolder extends RecyclerView.ViewHolder {
        TextView songDetail;
        TextView performaceDetail;
        ImageView thumbImage;
        PerformanceSongTuple info;

        void bind(PerformanceSongTuple info) {
            this.info = info;
            LocalDate localDate = info.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year  = localDate.getYear();
            int month = localDate.getMonthValue();
            int day   = localDate.getDayOfMonth();
            songDetail.setText("Bài hát: " + info.songName);
            performaceDetail.setText( " ⏱ Ngày " +day + "/" + month +"/"+ year + ",\n 📍 tại "  + info.location + ".");
        }
        public PerformanceViewHolder(@NonNull View itemView, PerformanceClickListener listener) {
            super(itemView);
            thumbImage =  itemView.findViewById(R.id.songThumb);
            songDetail = itemView.findViewById(R.id.songName);
            performaceDetail = itemView.findViewById(R.id.timelocation);
            itemView.setOnClickListener(v -> {
                listener.onPerformanceClick(info);
            });
        }
    }

    public interface PerformanceClickListener {
        void onPerformanceClick(PerformanceSongTuple info);
    }
    public static final DiffUtil.ItemCallback<PerformanceSongTuple> PerformanceCallback = new DiffUtil.ItemCallback<PerformanceSongTuple>() {
        @Override
        public boolean areItemsTheSame(@NonNull PerformanceSongTuple oldItem, @NonNull PerformanceSongTuple newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull PerformanceSongTuple oldItem, @NonNull PerformanceSongTuple newItem) {
            return (newItem.date.equals(oldItem.date) && newItem.date.equals(oldItem.date) && newItem.location.equals(oldItem.location));
        }
    };
}
