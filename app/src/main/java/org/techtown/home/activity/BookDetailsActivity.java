package org.techtown.home.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.techtown.home.Book;
import org.techtown.home.R;
import org.w3c.dom.Text;

public class BookDetailsActivity extends AppCompatActivity {

    private String bookimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        Intent intent = getIntent();

        ImageView image = (ImageView) findViewById(R.id.image);
        TextView title = (TextView)findViewById(R.id.title);
        TextView author = (TextView)findViewById(R.id.author);
        TextView publisher = (TextView)findViewById(R.id.publisher);
        TextView description = (TextView)findViewById(R.id.description);
        Button wishButton = (Button)findViewById(R.id.wishButton);
        Button bookPost = (Button)findViewById(R.id.bookStartButton);

        bookimg = intent.getStringExtra("Image");
        Glide.with(this).load(bookimg).fitCenter().into(image);
        title.setText(intent.getStringExtra("Title"));
        author.setText(intent.getStringExtra("Author"));
        publisher.setText(intent.getStringExtra("Publisher"));
        description.setText(intent.getStringExtra("Description"));

        //ISBN 정보
        String isbn = intent.getStringExtra("Isbn");

        wishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(wishButton.getText().equals("♡")) wishButton.setText("❤");
                else wishButton.setText("♡");
                //위시리스트 db에 넣는 코드 넣어주세요 ^❤^
            }
        });

        bookPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookPostActivity.class);
                intent.putExtra("Title",title.getText().toString());        //title.toString()은 되나?
                intent.putExtra("Author",author.getText().toString());
                intent.putExtra("Image",bookimg);
                intent.putExtra("Isbn",isbn);


                startActivity(intent);
            }
        });
    }
}