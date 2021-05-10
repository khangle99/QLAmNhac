package com.khangle.qlamnhac.adapter;

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
import com.khangle.qlamnhac.model.Composer;

public class ComposerAdapter extends ListAdapter<Composer, ComposerAdapter.ComposerViewHolder> {
    ComposerClickListener listener;
    public ComposerAdapter(ComposerClickListener listener) {
        super(composerCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ComposerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_composer, parent, false);
        return new ComposerViewHolder(view , listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ComposerViewHolder holder, int position) {
            holder.bind(getItem(position));
    }

    static class ComposerViewHolder extends RecyclerView.ViewHolder {
        ImageView avartar;
        TextView nameTV;
        Composer composer;
        public ComposerViewHolder(@NonNull View itemView, ComposerClickListener listener) {
            super(itemView);
//            avartar = itemView.findViewById(R.id.composerAvatar);
            nameTV =itemView.findViewById(R.id.composerName);
            itemView.setOnClickListener(v -> {
                listener.onClick(composer);
            });
        }

        void bind(Composer composer) {
            this.composer = composer;
            nameTV.setText(composer.name);
        }
    }
    public interface ComposerClickListener {
        void onClick(Composer composer);
    }
    public static final DiffUtil.ItemCallback<Composer> composerCallback = new DiffUtil.ItemCallback<Composer>() {

        @Override
        public boolean areItemsTheSame(@NonNull Composer oldItem, @NonNull Composer newItem) {
            return oldItem.id == (newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Composer oldItem, @NonNull Composer newItem) {
            return oldItem.name.equals(newItem.name);
        }
    };
}



