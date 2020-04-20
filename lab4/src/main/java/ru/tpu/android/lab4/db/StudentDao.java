package ru.tpu.android.lab4.db;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT rowid, first_name, second_name, last_name, shown_name, string_spans FROM student")
    List<Student> getAll();

    @Insert
    void insert(@NonNull Student student);

    @Query(
            "SELECT COUNT(*) FROM student WHERE " +
                    "first_name = :firstName AND " +
                    "second_name = :secondName AND " +
                    "last_name = :lastName"
    )
    int count(@NonNull String firstName, @NonNull String secondName, @NonNull String lastName);

    //запрос, ищущий совпадения с введенным словом
    @Query(
            "SELECT rowid, first_name, second_name, last_name, shown_name, string_spans FROM student WHERE " +
                    "shown_name MATCH :search"
    )
    List<Student> searchByWord(@NonNull String search);
}