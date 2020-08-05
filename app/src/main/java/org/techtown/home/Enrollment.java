package org.techtown.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Enrollment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        Button btn_save = (Button) findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText text_title = findViewById(R.id.text_title);
                String title = text_title.getText().toString();
                //db로 전달
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText text_author = findViewById(R.id.text_author);
                String author = text_author.getText().toString();
                //db로 전달
            }
        });
        //이런 식으로 진행

        //로고 누르면 홈으로
        final ImageButton home = (ImageButton) findViewById(R.id.btn_mainLogo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Enrollment.this, MainActivity.class);
                startActivity(intent);
            }
        });



    }
}