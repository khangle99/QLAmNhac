package com.khangle.qlamnhac.songDetail;

import android.os.Build;
import android.util.Log;
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
import com.khangle.qlamnhac.model.Composer;
import com.khangle.qlamnhac.model.Song;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
@RequiresApi(api = Build.VERSION_CODES.O)
public class SongListAdapter extends ListAdapter<Song, SongListAdapter.SongViewHolder> {
    private SongClickListener listener;
    public SongListAdapter(SongClickListener listener) {
        super(SongCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songDetail;
        TextView releaseDateTextview;
        ImageView thumbImage;
        Song song;

        void bind(Song song) {
            this.song = song;
            LocalDate localDate = song.releaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            int year  = localDate.getYear();
            int month = localDate.getMonthValue();
            int day   = localDate.getDayOfMonth();
            songDetail.setText("" + song.songName);
            releaseDateTextview.setText("Ngày phát hành: " + day + "/" + month +"/"+ year);
        }
        public SongViewHolder(@NonNull View itemView, SongClickListener listener) {
            super(itemView);
            thumbImage =  itemView.findViewById(R.id.songThumb);
            songDetail = itemView.findViewById(R.id.songName);
            releaseDateTextview = itemView.findViewById(R.id.releaseDateTextview);
            itemView.setOnClickListener(v -> {
                listener.onSongClick(song);
            });
        }
    }

    public interface SongClickListener {
        void onSongClick(Song song);
    }
    public static final DiffUtil.ItemCallback<Song> SongCallback = new DiffUtil.ItemCallback<Song>() {
        @Override
        public boolean areItemsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return oldItem.songId == newItem.songId;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Song oldItem, @NonNull Song newItem) {
            return (newItem.songName.equals(oldItem.songName) && newItem.releaseDate.equals(oldItem.releaseDate));
        }
    };
}


