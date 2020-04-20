package ru.tpu.android.lab5.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.tpu.android.lab5.Lab5Activity;
import ru.tpu.android.lab5.Repo;
import ru.tpu.android.lab5.RepoActivity;

public class ReposAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_REPO = 0;
    public static final int TYPE_DESCRIPTION = 1;

    private final Context context;

    //список выводимых репозиториев
    private List<Repo> repos = new ArrayList<>();

    public ReposAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_REPO:
                return new RepoHolder(parent);
            case TYPE_DESCRIPTION:
                return new DescriptionHolder(parent);
        }
        throw new IllegalArgumentException("unknown viewType = " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_REPO:
                RepoHolder repoHolder = (RepoHolder) holder;
                Repo repo = repos.get((position+1) / 2);
                repoHolder.repo.setText(
                        repo.fullName
                );
                repoHolder.repo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RepoActivity.class);
                        intent.putExtra("name", repos.get((position+1)/2).fullName);
                        intent.putExtra("url", repos.get((position+1) / 2).url);
                        intent.putExtra("description", repos.get((position+1)/2).description);
                        context.startActivity(intent);
                    }
                });
                break;
            case TYPE_DESCRIPTION:
                DescriptionHolder descriptionHolder = (DescriptionHolder) holder;
                Repo repo1 = repos.get( position / 2);
                descriptionHolder.description.setText(
                        repo1.description
                );
                descriptionHolder.description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, RepoActivity.class);
                        intent.putExtra("name", repos.get(position/2).fullName);
                        intent.putExtra("url", repos.get(position / 2).url);
                        intent.putExtra("description", repos.get(position/2).description);
                        context.startActivity(intent);
                    }
                });
                break;
        }
    }

    public void clear()
    {
        List<Repo> emptyRepos = new ArrayList<Repo>();
        this.repos = emptyRepos;
    }

    @Override
    public int getItemCount() {
        return repos.size() * 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_REPO : TYPE_DESCRIPTION;
    }

    public void setRepos(List<Repo> repos) {
        this.repos = repos;
    }
}