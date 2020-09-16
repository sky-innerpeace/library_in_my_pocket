package org.techtown.home.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;

import org.techtown.home.R;

public class MarathonActivity extends AppCompatActivity {

    private String title = "당근 유치원";
    private String user="박예진";
    private int now=60;
    private int month = 88;
    private ProgressBar progressBar,p2;
    private CircleProgressBar p1;
    private TextView edit1,edit2,edit3;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marathon);

        progress(title,now,month);

        edit3 = findViewById(R.id.textView5);
        edit3.setText(user+"님 목표를 향해 달려보세요!");
    }

    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    private void progress(String title, int now,int month){
        edit2 = findViewById(R.id.textView);
        edit2.findViewById(R.id.textView);

        p1 = findViewById(R.id.days_graph);
        p1.setProgress(now);
        edit2.setText("현재 읽고있는 책 :  [ "+title+" ] ");

        p2 = findViewById(R.id.progressBar3);
        p2.setProgress(month);
    }
}