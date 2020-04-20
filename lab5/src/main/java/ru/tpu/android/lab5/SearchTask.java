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

//задача поиска репозиториев
public class SearchTask extends Task<List<Repo>> {

    //переменная класса для выполнения HTTP-запросов
    private static OkHttpClient httpClient;

    public static OkHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (SearchTask.class) {
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

    public SearchTask(@Nullable Observer<List<Repo>> observer) {
        super(observer);
    }

    @Override
    @WorkerThread
    protected List<Repo> executeInBackground() throws Exception {
        String response = search(Lab5Activity.Query);
        return parseSearch(response);
    }

    //запрос к API с возвращением тела ответа
    private String search(String query) throws Exception {
        Request request = new Request.Builder()
                .url("https://api.github.com/search/repositories?q=" + query)
                .build();
        Response response = getHttpClient().newCall(request).execute();
        if (response.code() != 200) {
            throw new Exception("api returned unexpected http code: " + response.code());
        }

        return response.body().string();
    }

    //парсинг ответа
    private List<Repo> parseSearch(String response) throws JSONException {
        JSONObject responseJson = new JSONObject(response);
        List<Repo> repos = new ArrayList<>();
        JSONArray items = responseJson.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject repoJson = items.getJSONObject(i);
            Repo repo = new Repo();
            repo.fullName = repoJson.getString("full_name");
            repo.url = repoJson.getString("html_url");
            repo.description = repoJson.getString("description");
            repos.add(repo);
        }
        return repos;
    }
}