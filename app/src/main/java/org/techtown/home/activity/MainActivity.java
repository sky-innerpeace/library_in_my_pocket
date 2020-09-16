package org.techtown.home.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.techtown.home.Book;
import org.techtown.home.R;
import org.techtown.home.sns.SnsMainActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private View drawerView;

    //임시로 설정
    private String title = "당근 유치원";
    private int month = 70;
    private TextView p;
    private ProgressBar p1;

    //베스트셀러
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);
        ArrayList<Book> best_list = new ArrayList<Book>();
        ImageButton btn_bestseller_01 = (ImageButton)findViewById(R.id.recommendedBooks_1);
        ImageButton btn_bestseller_02 = (ImageButton)findViewById(R.id.recommendedBooks_2);
        ImageButton btn_bestseller_03 = (ImageButton)findViewById(R.id.recommendedBooks_3);
        ImageButton btn_bestseller_04 = (ImageButton)findViewById(R.id.recommendedBooks_4);
        ImageButton btn_bestseller_05 = (ImageButton)findViewById(R.id.recommendedBooks_5);

        ArrayList<String> title_list = new ArrayList<String>();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=%EB%B2%A0%EC%8A%A4%ED%8A%B8%EC%85%80%EB%9F%AC";
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Elements bestseller_parser = doc.select("ol > li > dl > dt");


                for(Element e : bestseller_parser){

                    title_list.add(e.select("a").get(0).attr("title"));

                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //초기 이미지 설정코드
        for(String title : title_list){
            Thread t2 = new Thread(new Runnable() {
                @Override
                public void run() {
                    String clientID = "dkF9FutHrLAvLGC3STcg"; //api 사용 신청시 제공되는 아이디
                    String clientSecret = "Phm7dLFwwC"; //패스워드

                    URL url;

                    try {
                        int display = 1;
                        int start = 1;
                        url = new URL("https://openapi.naver.com/v1/search/book.xml?query=" + URLEncoder.encode(title, "UTF-8")
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
                                                b.setTitle(parser.nextText().replace("<b>", "").replace("</b>", ""));
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
                                                b.setDescription(parser.nextText().replace("<b>", "").replace("</b>", "").replace("&#x0D;", ""));
                                            break;
                                    }
                                    break;
                                }

                                case XmlPullParser.END_TAG: {
                                    String tag = parser.getName();
                                    if (tag.equals("item")) {
                                        best_list.add(b);
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
                    try{


                        URL url_01 = new URL(best_list.get(0).getImage());
                        URL url_02 = new URL(best_list.get(1).getImage());
                        URL url_03 = new URL(best_list.get(2).getImage());
                        URL url_04 = new URL(best_list.get(3).getImage());
                        URL url_05 = new URL(best_list.get(4).getImage());

                        InputStream is_01 = url_01.openStream();
                        InputStream is_02 = url_02.openStream();
                        InputStream is_03 = url_03.openStream();
                        InputStream is_04 = url_04.openStream();
                        InputStream is_05 = url_05.openStream();

                        final Bitmap bm_01 = BitmapFactory.decodeStream(is_01);
                        final Bitmap bm_02 = BitmapFactory.decodeStream(is_02);
                        final Bitmap bm_03 = BitmapFactory.decodeStream(is_03);
                        final Bitmap bm_04 = BitmapFactory.decodeStream(is_04);
                        final Bitmap bm_05 = BitmapFactory.decodeStream(is_05);


                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                btn_bestseller_01.setImageBitmap(bm_01);
                                btn_bestseller_02.setImageBitmap(bm_02);
                                btn_bestseller_03.setImageBitmap(bm_03);
                                btn_bestseller_04.setImageBitmap(bm_04);
                                btn_bestseller_05.setImageBitmap(bm_05);

                            }
                        });
                        btn_bestseller_01.setImageBitmap(bm_01);
                        btn_bestseller_02.setImageBitmap(bm_02);
                        btn_bestseller_03.setImageBitmap(bm_03);
                        btn_bestseller_04.setImageBitmap(bm_04);
                        btn_bestseller_05.setImageBitmap(bm_05);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            t2.start();
        }

        btn_bestseller_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Title", best_list.get(0).getTitle());
                intent.putExtra("Author", best_list.get(0).getAuthor());
                intent.putExtra("Publisher", best_list.get(0).getPublisher());
                intent.putExtra("Description", best_list.get(0).getDescription());
                intent.putExtra("Image", best_list.get(0).getImage());
                intent.putExtra("Link", best_list.get(0).getLink());
                startActivity(intent);

            }
        });
        btn_bestseller_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Title", best_list.get(1).getTitle());
                intent.putExtra("Author", best_list.get(1).getAuthor());
                intent.putExtra("Publisher", best_list.get(1).getPublisher());
                intent.putExtra("Description", best_list.get(1).getDescription());
                intent.putExtra("Image", best_list.get(1).getImage());
                intent.putExtra("Link", best_list.get(1).getLink());
                startActivity(intent);
            }
        });

        btn_bestseller_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Title", best_list.get(2).getTitle());
                intent.putExtra("Author", best_list.get(2).getAuthor());
                intent.putExtra("Publisher", best_list.get(2).getPublisher());
                intent.putExtra("Description", best_list.get(2).getDescription());
                intent.putExtra("Image", best_list.get(2).getImage());
                intent.putExtra("Link", best_list.get(2).getLink());
                startActivity(intent);
            }
        });

        btn_bestseller_04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Title", best_list.get(3).getTitle());
                intent.putExtra("Author", best_list.get(3).getAuthor());
                intent.putExtra("Publisher", best_list.get(3).getPublisher());
                intent.putExtra("Description", best_list.get(3).getDescription());
                intent.putExtra("Image", best_list.get(3).getImage());
                intent.putExtra("Link", best_list.get(3).getLink());
                startActivity(intent);
            }
        });

        btn_bestseller_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Title", best_list.get(4).getTitle());
                intent.putExtra("Author", best_list.get(4).getAuthor());
                intent.putExtra("Publisher", best_list.get(4).getPublisher());
                intent.putExtra("Description", best_list.get(4).getDescription());
                intent.putExtra("Image", best_list.get(4).getImage());
                intent.putExtra("Link", best_list.get(4).getLink());
                startActivity(intent);
            }
        });

        //메뉴창 열기
        ImageButton menu_open = (ImageButton)findViewById(R.id.menu);
        menu_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(drawerView);
            }
        });

        //내정보 버튼 누르면 Profile로 이동
        final Button profile = (Button) findViewById(R.id.btn_myInfo);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MemberInfoActivity.class);
                startActivity(intent);
            }
        });

        //통계 버튼 누르면 Marathon으로 이동
        final Button statistics = (Button) findViewById(R.id.btn_statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MarathonActivity.class);
                startActivity(intent);
            }
        });

        //테스트 용
        final Button novel = (Button) findViewById(R.id.btn_novel);
        novel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SnsMainActivity.class);
                startActivity(intent);
            }
        });
        final Button humanity = (Button)findViewById(R.id.btn_humanities_philosophy);
        humanity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ReviewMainActivity.class);
                startActivity(intent);
            }
        });
        //로그아웃
        final Button logOut = (Button)findViewById(R.id.btn_logOut);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //로고 누르면 홈으로
        final ImageButton home = (ImageButton)findViewById(R.id.btn_mainLogo);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //책 등록 버튼
        final ImageButton bookRegistration = (ImageButton)findViewById(R.id.bookRegistration);
        bookRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MemberInfoActivity.class);
                startActivity(intent);  //액티비티 이동
            }
        });

        //검색창
        final ImageButton bookSearch = (ImageButton)findViewById(R.id.btn_search);
        bookSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });



        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progress(title,month);
    }

    DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
        @Override
        public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

        }

        @Override
        public void onDrawerOpened(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerClosed(@NonNull View drawerView) {

        }

        @Override
        public void onDrawerStateChanged(int newState) {

        }
    };

    //뒤로가기버튼 클릭시 어플 종료
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    @SuppressLint({"WrongViewCast", "SetTextI18n"})
    private void progress(String title, int month){
        p = findViewById(R.id.textView7);
        p.setText("현재 읽고 있는 책 "+title +", 이만큼 읽었습니다!");

        p1 = findViewById(R.id.progressBar6);
        p1.setProgress(month);
    }
}