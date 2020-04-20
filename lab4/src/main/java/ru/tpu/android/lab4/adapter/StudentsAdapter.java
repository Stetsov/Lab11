package ru.tpu.android.lab4.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.tpu.android.lab4.db.Lab4Database;
import ru.tpu.android.lab4.db.Student;
import ru.tpu.android.lab4.db.StudentDao;

public class StudentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_NUMBER = 0;
    public static final int TYPE_STUDENT = 1;

    private List<Student> students = new ArrayList<>();
    private List<Student> filteredStudents = new ArrayList<>();

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NUMBER:
                return new NumberHolder(parent);
            case TYPE_STUDENT:
                return new StudentHolder(parent);
        }
        throw new IllegalArgumentException("unknown viewType = " + viewType);
    }

    //отвечает за вывод номеров и ФИО в айтемы
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_NUMBER:
                NumberHolder numberHolder = (NumberHolder) holder;
                // Высчитыванием номер студента начиная с 1
                numberHolder.bind((position + 1) / 2);
                break;
            case TYPE_STUDENT:
                StudentHolder studentHolder = (StudentHolder) holder;
                Student student = filteredStudents.get(position / 2);
                if(student.spans.isEmpty())
                    student.spans = " ";
                if ((!student.spans.equals(" ")))
                {
                    Spannable coloredString = new SpannableString(student.shownName);

                    //разбиение spans по запятым, чтобы найти позиции
                    List<String> spns = Arrays.asList(student.spans.split(","));

                    for (int i = 0; i < spns.size() - 1; i=i+2)
                    {
                        int position1 = Integer.parseInt(spns.get(i));
                        int position2 = Integer.parseInt(spns.get(i+1)) + 1;

                        coloredString.setSpan(new ForegroundColorSpan(Color.RED), position1, position2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    studentHolder.student.setText(
                            coloredString
                    );
                }
                else
                    studentHolder.student.setText(student.shownName);

                break;
        }
    }

    @Override
    public int getItemCount() {
        return filteredStudents.size() * 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_NUMBER : TYPE_STUDENT;
    }

    //передача списка студентов
    public void setStudents(List<Student> students) {
        this.students = students;
        this.filteredStudents = students;
    }

    public void setLists()
    {
        filteredStudents = students;
    }

    //вспомогательная, находит начало и конец соответствия
    public int[] findFromPosition (String word, String string, int pos)
    {
        boolean flag = true;
        for (int i = 0; i < word.length(); i++)
        {
            if (word.charAt(i) != string.charAt(pos + i))
            {
                flag = false;
                break;
            }
        }

        int[] result = new int[2];
        if (flag == true)
        {
            result[0] = pos;
            result[1] = pos + word.length() - 1;
        }
        else
        {
            result[0] = -1;
            result[1] = -1;
        }
        return result;
    }

    //находит слово в заданной строке
    //word - искомое слово, string - shownName
    public List<Integer> findWord (String word, String string)
    {
        List<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < string.length(); i++)
        {
            if ((word.charAt(0) == string.charAt(i))&&( (i+word.length()) < string.length()+1 ))
            {
                int[] positions = findFromPosition(word, string, i);
                list.add(positions[0]);
                list.add(positions[1]);
            }
        }
        return list;
    }

    //стереть оформление цветом для выбранного студента
    public void clearColor (Student student)
    {
        student.spans = " ";
    }

    public void changeColors (List<Student> foundStudents, CharSequence charSequence)
    {
        //получение слова-запроса в виде string
        String charString = charSequence.toString();

        //проверка строки на пустоту
        if (charString.isEmpty())
        {
            //убрать выделение цветом у всех студентов
            for (Student student : students) {
                clearColor(student);
            }
            //вернуть для отображения весь список
            filteredStudents = students;
        } else {
            //список студентов, прошедших фильтрацию по запросу
            ArrayList<Student> filteredList = new ArrayList<>();

            //передача значений
            for (int i = 0; i < foundStudents.size(); i++)
                filteredList.add(foundStudents.get(i));

            //перебор всех найденных студентов
            for (Student student : filteredList) {
                //стереть предыдущие выделения
                clearColor(student);
                //найти позиции для выделения цветом
                List<Integer> positions = findWord(charString.toLowerCase(), student.shownName.toString().toLowerCase());
                for (int i = 0; i<positions.size(); i++)
                {
                    if (positions.get(i) != -1)
                        student.spans = student.spans + positions.get(i).toString() + ",";
                }
                student.spans = student.spans.substring(1, student.spans.length() - 1);
            }
            filteredStudents = filteredList;
        }
        notifyDataSetChanged();
    }
}
