package ru.tpu.android.lab5;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.tpu.android.lab5.adapter.ReposAdapter;

public class RepoActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    static public String nameOfRepo;
    static public String repoUrl;
    static public String repoDescription;

    static public List<Commit> commits = new ArrayList<>();
    static public List<Issue> issues = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;

    private final int DIALOG = 1;

    //пул потоков
    private static Executor threadExecutor = Executors.newCachedThreadPool();

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, RepoActivity.class);
    }

    //задача для выполнения поиска с помощью API
    private SearchCommitsTask taskCommits;
    private SearchIssuesTask taskIssues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getIntent().getExtras();
        nameOfRepo = (String) arguments.getSerializable("name");
        repoUrl = (String) arguments.getSerializable("url");
        repoDescription = (String) arguments.getSerializable("description");

        setTitle(nameOfRepo);

        setContentView(R.layout.lab5_activity_repo);

        //задание заголовка и описания
        NestedScrollView scrollView = this.findViewById(R.id.lab5_repo_scroll);
        LinearLayout linearLayout = scrollView.findViewById(R.id.lab5_repo_information_list);
        TextView descriptionView = linearLayout.findViewById(R.id.lab5_repo_description);
        if (!repoDescription.equals("null"))
            descriptionView.setText(repoDescription);
        else descriptionView.setText("Нет описания");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        taskCommits = new SearchCommitsTask(searchObserver);
        taskIssues = new SearchIssuesTask(searchIssuesObserver);
        //выполнение через пул потоков
        threadExecutor.execute(taskCommits);
        threadExecutor.execute(taskIssues);

        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Отменяем анимацию обновления
                swipeRefreshLayout.setRefreshing(false);
                taskCommits = new SearchCommitsTask(searchObserver);
                taskIssues = new SearchIssuesTask(searchIssuesObserver);
                //выполнение через пул потоков
                threadExecutor.execute(taskCommits);
                threadExecutor.execute(taskIssues);
            }
        }, 4000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //отписка обсервера
        taskCommits.unregisterObserver();
        taskIssues.unregisterObserver();
    }

    //отображение всех найденных коммитов
    public void showCommitsList()
    {
        NestedScrollView scrollView = this.findViewById(R.id.lab5_repo_scroll);
        LinearLayout linearLayout = scrollView.findViewById(R.id.lab5_repo_information_list);
        NestedScrollView commitsScroll = linearLayout.findViewById(R.id.commits);
        LinearLayout commitsList = commitsScroll.findViewById(R.id.commits_list);

        LayoutInflater inflater = getLayoutInflater();

        //убрать уже выведенные коммиты
        commitsList.removeAllViews();

        //вывести все коммиты
        if (commits.size()>0)
        {
            for (int i = 0; i < commits.size(); i++)
            {
                View newCommit = inflater.inflate(R.layout.lab5_commit_item, commitsList, false);
                TextView commitDate = newCommit.findViewById(R.id.commit_date);
                commitDate.setText(commits.get(i).date);
                TextView commitMessage = newCommit.findViewById(R.id.commit_message);
                commitMessage.setText(commits.get(i).message);
                commitsList.addView(newCommit);
            }
        } else {
            LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(this);
            textView.setText("Не найдено");
            textView.setLayoutParams(lpView);
            commitsList.addView(textView);
        }
    }

    //отображение всех найденных issues
    public void showIssuesList()
    {
        NestedScrollView scrollView = this.findViewById(R.id.lab5_repo_scroll);
        LinearLayout linearLayout = scrollView.findViewById(R.id.lab5_repo_information_list);
        NestedScrollView issuesScroll = linearLayout.findViewById(R.id.issues);
        LinearLayout issuesList = issuesScroll.findViewById(R.id.issues_list);

        LayoutInflater inflater = getLayoutInflater();

        //очистить текущий выведенный список
        issuesList.removeAllViews();
        //вывести найденные issues
        if (issues.size()>0)
        {
            for (int i = 0; i < issues.size(); i++)
            {
                View newIssue = inflater.inflate(R.layout.lab5_issue_item, issuesList, false);
                TextView issueDate = newIssue.findViewById(R.id.issue_date);
                issueDate.setText(issues.get(i).date);
                TextView issueTitle = newIssue.findViewById(R.id.issue_title);
                issueTitle.setText(issues.get(i).title);
                TextView issueMessage = newIssue.findViewById(R.id.issue_message);
                issueMessage.setText(issues.get(i).message);
                issuesList.addView(newIssue);
            }
        } else {
            LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            TextView textView = new TextView(this);
            textView.setText("Не найдено");
            textView.setLayoutParams(lpView);
            issuesList.addView(textView);
        }
    }

    //обсервер для коммитов
    private Observer<List<Commit>> searchObserver = new Observer<List<Commit>>() {
        @Override
        public void onLoading(@NonNull Task<List<Commit>> task) { }

        @Override
        public void onSuccess(@NonNull Task<List<Commit>> task, @Nullable List<Commit> data) {
            //загрузка в список найденных коммитов
            commits = data;
            showCommitsList();
        }

        @Override
        public void onError(@NonNull Task<List<Commit>> task, @NonNull Exception e) {
            showDialog(DIALOG);
        }
    };

    //обсервер для issues
    private Observer<List<Issue>> searchIssuesObserver = new Observer<List<Issue>>() {
        @Override
        public void onLoading(@NonNull Task<List<Issue>> task) { }

        @Override
        public void onSuccess(@NonNull Task<List<Issue>> task, @Nullable List<Issue> data) {
            //загрузка в список найденных коммитов
            issues = data;
            showIssuesList();
        }

        @Override
        public void onError(@NonNull Task<List<Issue>> task, @NonNull Exception e) {
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
                    taskCommits = new SearchCommitsTask(searchObserver);
                    //выполнение через пул потоков
                    threadExecutor.execute(taskCommits);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };
}