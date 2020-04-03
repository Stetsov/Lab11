package ru.tpu.courses.lab2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.ProgressBar;

import androidx.constraintlayout.widget.ConstraintLayout;

public class Lab2View extends ConstraintLayout
{
    private TextView titleTextView;
    private ProgressBar progressBar;
    private TextView valueTextView;
    
    public Lab2View(Context context) {
        this(context, null);
    }

    public Lab2View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Lab2View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View mainView = inflate(context, R.layout.lab2_view, null);
        titleTextView = mainView.findViewById(R.id.lab2_titletextview);
        valueTextView = mainView.findViewById(R.id.lab2_valuetextview);
        progressBar = mainView.findViewById(R.id.lab2_progressbar);
        addView(mainView);
    }

    public void setTitle(String title){
        titleTextView.setText(String.valueOf(title));
    }

    public void setValue(double value){
        valueTextView.setText(String.valueOf(value));
        progressBar.setProgress(((int) Math.round(value)));

    }
}