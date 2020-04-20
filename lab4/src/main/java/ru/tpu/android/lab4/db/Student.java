package ru.tpu.android.lab4.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableString;

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Fts4;
import androidx.room.PrimaryKey;

import java.util.List;

@Fts4
@Entity
public class Student implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    public int id;
    @NonNull
    @ColumnInfo(name = "first_name")
    public String firstName;
    @NonNull
    @ColumnInfo(name = "second_name")
    public String secondName;
    @NonNull
    @ColumnInfo(name = "last_name")
    public String lastName;
    //отображаемое имя, представляющее собой соединение ФИО
    @NonNull
    @ColumnInfo(name = "shown_name")
    public String shownName;
    //хранит информацию об окрашиваемых отрезках
    @NonNull
    @ColumnInfo(name = "string_spans")
    public String spans;

    public Student(@NonNull String firstName,
                   @NonNull String secondName,
                   @NonNull String lastName
    ) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.secondName = secondName;
        this.shownName = lastName + " " + firstName + " " + secondName;
        this.spans = " ";
    }

    protected Student(Parcel in) {
        id = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        secondName = in.readString();
        shownName = in.readString();
        spans = in.readString();
    }

    public String getName() {
        return (firstName + " " + lastName + " " + secondName);
    }

    public static final Creator<Student> CREATOR = new Creator<Student>() {
        @Override
        public Student createFromParcel(Parcel in) {
            return new Student(in);
        }

        @Override
        public Student[] newArray(int size) {
            return new Student[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(secondName);
        dest.writeString(shownName);
        dest.writeString(spans);
    }
}
