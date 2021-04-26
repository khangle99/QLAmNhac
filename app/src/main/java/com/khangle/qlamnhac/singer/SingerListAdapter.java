package com.khangle.qlamnhac.singer;

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
import com.khangle.qlamnhac.model.Singer;

public class SingerListAdapter extends ListAdapter<Singer, SingerListAdapter.SingerViewHolder> {
   SingerClickListener listener;
    public SingerListAdapter(SingerClickListener listener) {
        super(singerCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_singer, parent, false);
        return new SingerViewHolder(view , listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class SingerViewHolder extends RecyclerView.ViewHolder {
        ImageView avartar;
        TextView nameTV;
        Singer singer;
        public SingerViewHolder(@NonNull View itemView, SingerClickListener listener) {
            super(itemView);
            avartar = itemView.findViewById(R.id.singerAvatar);
            nameTV =itemView.findViewById(R.id.singerName);
            itemView.setOnClickListener(v -> {
                listener.onClick(singer);
            });
        }

        void bind(Singer singer) {
            this.singer = singer;
            nameTV.setText(singer.name);
            String uriString = singer.uriString;
            if (uriString!=null&& !uriString.equals("")) {
                avartar.setImageDrawable(null);
                avartar.setImageURI(Uri.parse(uriString));
            }
        }
    }
    public interface SingerClickListener {
        void onClick(Singer composer);
    }
    public static final DiffUtil.ItemCallback<Singer> singerCallback = new DiffUtil.ItemCallback<Singer>() {

        @Override
        public boolean areItemsTheSame(@NonNull Singer oldItem, @NonNull Singer newItem) {
            return oldItem.id == (newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Singer oldItem, @NonNull Singer newItem) {
            return oldItem.name.equals(newItem.name);
        }
    };
}
