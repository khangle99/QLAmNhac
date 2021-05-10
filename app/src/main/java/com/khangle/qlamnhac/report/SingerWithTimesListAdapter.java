package com.khangle.qlamnhac.report;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.khangle.qlamnhac.R;
import com.khangle.qlamnhac.model.ReportTopSingerTuple;
import com.khangle.qlamnhac.model.Singer;

public class SingerWithTimesListAdapter extends ListAdapter<ReportTopSingerTuple, SingerWithTimesListAdapter.SingerTimesViewHolder> {
    public SingerWithTimesListAdapter() {
        super(singerCallback);
    }

    @NonNull
    @Override
    public SingerTimesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer_times, parent, false);
        return new SingerTimesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerTimesViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class SingerTimesViewHolder extends RecyclerView.ViewHolder {
        ImageView avartar;
        TextView nameTV;
        ReportTopSingerTuple tuple;
        TextView countTextview;
        public SingerTimesViewHolder(@NonNull View itemView) {
            super(itemView);
            avartar = itemView.findViewById(R.id.singerAvatar);
            nameTV =itemView.findViewById(R.id.singerName);
            countTextview = itemView.findViewById(R.id.times);
        }

        void bind(ReportTopSingerTuple tuple) {
            this.tuple = tuple;
            nameTV.setText(tuple.name);
            String uriString = tuple.uriString;
            if (uriString!=null&& !uriString.equals("")) {
                avartar.setImageDrawable(null);
                avartar.setImageURI(Uri.parse(uriString));
            }
            countTextview.setText(tuple.time+ " Lần");
        }
    }

    public static final DiffUtil.ItemCallback<ReportTopSingerTuple> singerCallback = new DiffUtil.ItemCallback<ReportTopSingerTuple>() {

        @Override
        public boolean areItemsTheSame(@NonNull ReportTopSingerTuple oldItem, @NonNull ReportTopSingerTuple newItem) {
            return oldItem.singerId == (newItem.singerId);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReportTopSingerTuple oldItem, @NonNull ReportTopSingerTuple newItem) {
            return oldItem.name.equals(newItem.name);
        }
    };
}
