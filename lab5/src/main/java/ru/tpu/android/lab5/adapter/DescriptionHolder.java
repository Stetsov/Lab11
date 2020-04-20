package ru.tpu.android.lab5.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ru.tpu.android.lab5.R;

public class DescriptionHolder extends RecyclerView.ViewHolder {

    public final TextView description;

    public DescriptionHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.lab5_item_description, parent, false));
        description = itemView.findViewById(R.id.description);
    }
}
