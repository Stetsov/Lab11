package ru.tpu.android.lab4.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.tpu.android.lab4.R;
import ru.tpu.android.lab4.db.Lab4Database;
import ru.tpu.android.lab4.db.Student;
import ru.tpu.android.lab4.db.StudentDao;

public class AddStudentActivity extends AppCompatActivity {

    private static final String EXTRA_STUDENT = "student";

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AddStudentActivity.class);
    }

    public static Student getResultStudent(@NonNull Intent intent) {
        return intent.getParcelableExtra(EXTRA_STUDENT);
    }

    private StudentDao studentDao;

    private TempStudentPref studentPref;

    private boolean skipSaveToPrefs;

    private EditText firstName;
    private EditText secondName;
    private EditText lastName;

    private Spannable shownName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lab4_activity_add_student);

        studentPref = new TempStudentPref(this);
        studentDao = Lab4Database.getInstance(this).studentDao();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //получение вьюшек для вывода ФИО
        firstName = findViewById(R.id.first_name);
        secondName = findViewById(R.id.second_name);
        lastName = findViewById(R.id.last_name);

        firstName.setText(studentPref.getFirstName());
        secondName.setText(studentPref.getSecondName());
        lastName.setText(studentPref.getLastName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!skipSaveToPrefs) {
            studentPref.set(
                    firstName.getText().toString(),
                    secondName.getText().toString(),
                    lastName.getText().toString(),
                    shownName
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lab4_add_student, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            saveStudent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveStudent() {
        Student student = new Student(
                firstName.getText().toString(),
                secondName.getText().toString(),
                lastName.getText().toString()
        );

        // Проверяем, что все поля были указаны
        if (TextUtils.isEmpty(student.firstName) ||
                TextUtils.isEmpty(student.secondName) ||
                TextUtils.isEmpty(student.lastName)) {
            // Класс Toast позволяет показать системное уведомление поверх всего UI
            Toast.makeText(this, R.string.lab4_error_empty_fields, Toast.LENGTH_LONG).show();
            return;
        }

        if (studentDao.count(student.firstName, student.secondName, student.lastName) > 0) {
            Toast.makeText(
                    this,
                    R.string.lab4_error_already_exists,
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        skipSaveToPrefs = true;

        studentPref.clear();

        Intent data = new Intent();
        data.putExtra(EXTRA_STUDENT, student);
        setResult(RESULT_OK, data);
        finish();
    }
}