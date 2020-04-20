package ru.tpu.android.lab5;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class SearchIssuesTask extends Task<List<Issue>> {
    //переменная класса для выполнения HTTP-запросов
    private static OkHttpClient httpClient;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (SearchIssuesTask.class) {
                if (httpClient == null) {
                    // Логирование запросов в logcat
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BASIC);
                    httpClient = new OkHttpClient.Builder()
                            .addInterceptor(loggingInterceptor)
                            .build();
                }
            }
        }
        return httpClient;
    }

    public SearchIssuesTask(@Nullable Observer<List<Issue>> observer) {
        super(observer);
    }

    @Override
    @WorkerThread
    protected List<Issue> executeInBackground() throws Exception {
        String response = search(RepoActivity.repoUrl);
        return parseSearch(response);
    }

    //запрос к API с возвращением тела ответа
    private String search(String query) throws Exception {
        query = query.substring(19, query.length());
        query = "https://api.github.com/repos/" + query;
        Request request = new Request.Builder()
                .url(query + "/issues")
                .build();
        Response response = getHttpClient().newCall(request).execute();
        if (response.code() != 200) {
            throw new Exception("api returned unexpected http code: " + response.code());
        }

        return response.body().string();
    }

    //парсинг ответа
    private List<Issue> parseSearch(String response) throws JSONException {
        List<Issue> issues = new ArrayList<>();

        JSONArray responseJson = new JSONArray(response);
        for (int i = 0; i < responseJson.length(); i++)
        {
            JSONObject issue = responseJson.getJSONObject(i);
            Issue newIssue = new Issue();
            newIssue.date = issue.getString("created_at");
            newIssue.title = issue.getString("title");
            newIssue.message = issue.getString("body");
            issues.add(newIssue);
        }
        return issues;
    }
}
