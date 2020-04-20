package ru.tpu.android.lab5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.tpu.android.lab5.adapter.ReposAdapter;

public class Lab5Activity extends AppCompatActivity {

    private static final String TAG = Lab5Activity.class.getSimpleName();

    private final int DIALOG = 1;

    public static String Query;

    //пул потоков
    private static Executor threadExecutor = Executors.newCachedThreadPool();

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab5Activity.class);
    }

    //получение инстанса кэша
    private final ReposCache reposCache = ReposCache.getInstance();

    private RecyclerView list;
    private ReposAdapter reposAdapter;

    //задача для выполнения поиска с помощью API
    private SearchTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(getString(R.string.lab5_title, getClass().getSimpleName()));

        setContentView(R.layout.lab5_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list = findViewById(android.R.id.list);

        //инициализация RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        //подключение адаптера
        list.setAdapter(reposAdapter = new ReposAdapter(this));

        //запись полученных репозиториев
        reposAdapter.setRepos(reposCache.getRepos());

        //создание задачи поиска, выполнение которой будет происходить в обсервере
        task = new SearchTask(searchObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab5_search_repo, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void search(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length()>2)
                {
                    //изменение переменной, отвечающей за строку поиска
                    Query = query;
                    //выполнение через пул потоков
                    threadExecutor.execute(task);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //очистка списка, если поисковой запрос пустой
                if (newText.equals(""))
                {
                    reposAdapter.clear();
                    reposAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //отписка обсервера
        task.unregisterObserver();
    }

    private Observer<List<Repo>> searchObserver = new Observer<List<Repo>>() {
        @Override
        public void onLoading(@NonNull Task<List<Repo>> task) {
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        public void onSuccess(@NonNull Task<List<Repo>> task, @Nullable List<Repo> data) {
            //загрузка в список найденных репозиториев
            reposAdapter.setRepos(data);
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            reposAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(@NonNull Task<List<Repo>> task, @NonNull Exception e) {
            ProgressBar progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            showDialog(DIALOG);
        }
    };

    //диалог на случай неудавшегося запроса
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG)
        {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            //заголовок
            adb.setTitle("Произошла ошибка");
            adb.setMessage("Повторить запрос?");
            adb.setIcon(android.R.drawable.ic_dialog_alert);
            adb.setPositiveButton("Да", repeatSearchListener);
            adb.setNegativeButton("Нет", repeatSearchListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    //листенер на обработку выбора пользователя, повторить запрос или нет
    DialogInterface.OnClickListener repeatSearchListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int answer) {
            switch (answer)
            {
                case Dialog.BUTTON_POSITIVE:
                    //создание задачи поиска, выполнение которой будет происходить в обсервере
                    task = new SearchTask(searchObserver);
                    //выполнение через пул потоков
                    threadExecutor.execute(task);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}