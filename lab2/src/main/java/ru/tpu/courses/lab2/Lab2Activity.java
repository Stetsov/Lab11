package ru.tpu.courses.lab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Lab2Activity extends AppCompatActivity {
    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, Lab2Activity.class);
    }

    private static final String STATE_VIEWS_VALUES = "viewsValues";
    private Lab2ViewsContainer lab2ViewsContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab2_activity);

        setTitle(getString(R.string.lab2_title, getClass().getSimpleName()));

        lab2ViewsContainer = findViewById(R.id.container);

        findViewById(R.id.btn_add_view).setOnClickListener(view -> lab2ViewsContainer.addValue());
        findViewById(R.id.btn_reset).setOnClickListener(view -> lab2ViewsContainer.initializeValues());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            lab2ViewsContainer.setViewsValues(savedInstanceState.getDoubleArray(STATE_VIEWS_VALUES));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDoubleArray(STATE_VIEWS_VALUES, lab2ViewsContainer.getViewsValues());
    }
}
