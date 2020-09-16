package org.techtown.home.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.techtown.home.Book;
import org.techtown.home.R;
import org.techtown.home.adapter.ListViewAdapter;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity implements View.OnClickListener{

    ListViewAdapter adapter;
    ListView listview;
    ArrayList<Book> naverHtml;
    String keyword = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        naverHtml = new ArrayList<Book>();
        SearchView mSearchView = findViewById(R.id.btn_search);

        Intent intent = getIntent();
        keyword = intent.getExtras().getString("keyword");

        Thread t = new Thread() {
            public void run() {
                naverHtml = NaverBookService(keyword);
            }
        };
        t.start();
        //thread 실행이 끝나기 전에 다음 작업이 실행되려하니 자꾸 다음 작업이 먹힘
        //join() 사용하여 메인스레드와 시간 맞추기
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // 입력받은 문자열 처리
                //네이버 검색 api

                //https://jootc.com/p/201906042883
                keyword = String.valueOf(mSearchView.getQuery());

                Thread t = new Thread() {
                    public void run() {
                        naverHtml = NaverBookService(keyword);
                    }
                };
                t.start();
                //thread 실행이 끝나기 전에 다음 작업이 실행되려하니 자꾸 다음 작업이 먹힘
                //join() 사용하여 메인스레드와 시간 맞추기
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                adapter = new ListViewAdapter(SearchResultActivity.this, naverHtml);
                listview = (ListView) findViewById(R.id.listview);
                listview.setAdapter(adapter);

                //아이템 클릭시 작동
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView parent, View v, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                        intent.putExtra("Title", naverHtml.get(position).getTitle());
                        intent.putExtra("Author", naverHtml.get(position).getAuthor());
                        intent.putExtra("Image", naverHtml.get(position).getImage());
                        intent.putExtra("Publisher", naverHtml.get(position).getPublisher());
                        intent.putExtra("Description", naverHtml.get(position).getDescription());
                        intent.putExtra("Link", naverHtml.get(position).getLink());
                        intent.putExtra("Isbn",naverHtml.get(position).getIsbn());

                        startActivity(intent);

                        //intent.putExtra("Book",naverHtml);  //이렇게 보내려면 객체 직렬화 필요
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // 입력란의 문자열이 바뀔 때 처리
                // db 안에 같은, 비슷한 결과물만 필터링되어 아래에 표시되는 코드

                return false;
            }

        });  //setOnQueryTextListener

        adapter = new ListViewAdapter(SearchResultActivity.this, naverHtml);
        listview = (ListView) findViewById(R.id.listview);
        listview.setAdapter(adapter);

        //아이템 클릭시 작동
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Title", naverHtml.get(position).getTitle());
                intent.putExtra("Author", naverHtml.get(position).getAuthor());
                intent.putExtra("Image", naverHtml.get(position).getImage());
                intent.putExtra("Publisher", naverHtml.get(position).getPublisher());
                intent.putExtra("Description", naverHtml.get(position).getDescription());
                intent.putExtra("Link", naverHtml.get(position).getLink());

                startActivity(intent);

                //intent.putExtra("Book",naverHtml);  //이렇게 보내려면 객체 직렬화 필요
            }
        });
    }

    private ArrayList<Book> NaverBookService(String keyword) {
        String clientID = "dkF9FutHrLAvLGC3STcg"; //api 사용 신청시 제공되는 아이디
        String clientSecret = "Phm7dLFwwC"; //패스워드

        URL url;
        ArrayList<Book> list = new ArrayList<Book>();
        String tem_title = ""; //replace 사용하려고
        try {
            int display = 5;
            int start = 1;
            url = new URL("https://openapi.naver.com/v1/search/book.xml?query=" + URLEncoder.encode(keyword, "UTF-8")
                    + (display != 0 ? "&display=" + display : "") + (start != 0 ? "&start=" + start : ""));
            URLConnection urlConn;

            //url 연결
            urlConn = url.openConnection();
            urlConn.setRequestProperty("X-naver-Client-Id", clientID);
            urlConn.setRequestProperty("X-naver-Client-Secret", clientSecret);

            //파싱 - 팩토리 만들고 팩토리로 파서 생성 (풀 파서 사용)
            XmlPullParserFactory factory;

            factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput((new InputStreamReader(urlConn.getInputStream())));


            int eventType = parser.getEventType();
            Book b = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.END_DOCUMENT: // 문서의 끝
                        break;
                    case XmlPullParser.START_DOCUMENT:
                        //여기에 객체를 넣으면 Null
                        break;
                    case XmlPullParser.START_TAG: {
                        String tag = parser.getName();
                        switch (tag) {
                            case "item":
                                b = new Book();     //책 객체 생성
                                break;
                            case "title":
                                if (b != null)
                                    b.setTitle(parser.nextText().replace("<b>","").replace("</b>",""));
                                break;
                            case "link":
                                if (b != null)
                                    b.setLink(parser.nextText());
                                break;
                            case "image":
                                if (b != null)
                                    b.setImage(parser.nextText());
                                break;
                            case "author":
                                if (b != null)
                                    b.setAuthor(parser.nextText());
                                break;
                            case "publisher":
                                if (b != null)
                                    b.setPublisher(parser.nextText());
                                break;
                            case "isbn":
                                if (b != null)
                                    b.setIsbn(parser.nextText());
                                break;
                            case "description":
                                if (b != null)
                                    b.setDescription(parser.nextText().replace("<b>","").replace("</b>","").replace("&#x0D;",""));
                                break;
                        }
                        break;
                    }

                    case XmlPullParser.END_TAG: {
                        String tag = parser.getName();
                        if (tag.equals("item")) {
                            list.add(b);
                            b = null;
                        }

                    }

                }
                eventType = parser.next();
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public void onClick(View view) {

    }
}