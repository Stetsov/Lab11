package ru.tpu.android.lab5.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.android.lab5.R;

public class RepoHolder extends RecyclerView.ViewHolder {

    public final TextView repo;

    public RepoHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab5_item_repo, parent, false));
        repo = itemView.findViewById(R.id.repo);
    }
}
