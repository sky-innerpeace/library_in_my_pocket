package org.techtown.home.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SearchView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.techtown.home.R;

public class SearchActivity extends AppCompatActivity {

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView mSearchView = findViewById(R.id.btn_search);
        TextView recentTextView = findViewById(R.id.btn_recent);

        //검색창 아래에 급상승 검색어 노출
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //네이버 검색어 - 베스트셀러
                    String url = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=%EB%B2%A0%EC%8A%A4%ED%8A%B8%EC%85%80%EB%9F%AC";
                    Document doc = Jsoup.connect(url).get();

                    Elements bestseller_parser = doc.select("ol > li > dl > dt");
                    String bestseller = "";

                    for(Element e : bestseller_parser){

                        bestseller += e.select("a").get(0).attr("title");
                        bestseller += "\n";

                    }

                    recentTextView.setText(bestseller);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });  //Thread
        t.start();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // 입력받은 문자열 처리
                //네이버 검색 api

                //https://jootc.com/p/201906042883
                String query = String.valueOf(mSearchView.getQuery());

                Intent intent = new Intent(getApplicationContext(), SearchResultActivity.class);
                intent.putExtra("keyword",query);
                startActivity(intent);
                overridePendingTransition(0, 0);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 입력란의 문자열이 바뀔 때 처리
                // db 안에 같은, 비슷한 결과물만 필터링되어 아래에 표시되는 코드

                return false;
            }

        });  //setOnQueryTextListener
    }   //OnCreate
}   //SearchActivity.java