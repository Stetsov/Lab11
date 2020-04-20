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

//задача поиска коммитов
public class SearchCommitsTask extends Task<List<Commit>> {

    //переменная класса для выполнения HTTP-запросов
    private static OkHttpClient httpClient;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (SearchCommitsTask.class) {
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

    public SearchCommitsTask(@Nullable Observer<List<Commit>> observer) {
        super(observer);
    }

    @Override
    @WorkerThread
    protected List<Commit> executeInBackground() throws Exception {
        String response = search(RepoActivity.repoUrl);
        return parseSearch(response);
    }

    //запрос к API с возвращением тела ответа
    private String search(String query) throws Exception {
        query = query.substring(19, query.length());
        query = "https://api.github.com/repos/" + query;
        Request request = new Request.Builder()
                .url(query + "/commits")
                .build();
        Response response = getHttpClient().newCall(request).execute();
        if (response.code() != 200) {
            throw new Exception("api returned unexpected http code: " + response.code());
        }

        return response.body().string();
    }

    //парсинг ответа
    private List<Commit> parseSearch(String response) throws JSONException {
        List<Commit> commits = new ArrayList<>();

        JSONArray responseJson = new JSONArray(response);
        for (int i = 0; i < responseJson.length(); i++)
        {
            JSONObject commit = responseJson.getJSONObject(i);
            Commit newCommit = new Commit();
            JSONObject commitInformation = commit.getJSONObject("commit");
            newCommit.message = commitInformation.getString("message");
            JSONObject committer = commitInformation.getJSONObject("committer");
            newCommit.date = committer.getString("date");
            commits.add(newCommit);
        }
        return commits;
    }
}
