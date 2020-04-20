package ru.tpu.android.lab5;

public class Repo {
    public String fullName;
    public String description;
    public String url;

    @Override
    public String toString() {
        return "Repo{" +
                "fullName='" + fullName + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}