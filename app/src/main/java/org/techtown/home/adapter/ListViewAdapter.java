package org.techtown.home.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.techtown.home.Book;
import org.techtown.home.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    Context context;
    private ArrayList<Book> bookList;

    class ViewHolder{
        ImageView ImageView;
        TextView titleTextView;
        TextView authorTextView;
        TextView pubTextView;
    }

    //생성자
    public ListViewAdapter(Context context, ArrayList<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return this.bookList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        ViewHolder viewholder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item,null);

            //if 문 속의 convertView 에서 계속 52번째 줄이 실행됨
            //layoutinflater를 통해 xml을 불러오는 작업이 매우 무겁기에
            //viewholder를 통해 Holder 패턴화
            viewholder = new ViewHolder();

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            viewholder.ImageView = (ImageView) convertView.findViewById(R.id.icon) ;
            viewholder.titleTextView = (TextView) convertView.findViewById(R.id.title) ;
            viewholder.authorTextView = (TextView) convertView.findViewById(R.id.author);
            viewholder.pubTextView = (TextView) convertView.findViewById(R.id.publisher) ;
            convertView.setTag(viewholder);
        }else{
            viewholder = (ViewHolder)convertView.getTag();
        }

        // Data Set(bookList)에서 position에 위치한 데이터 참조 획득
        Book book = bookList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        Glide.with(context).load(book.getImage()).fitCenter().into(viewholder.ImageView);
        viewholder.titleTextView.setText(book.getTitle());
        viewholder.authorTextView.setText(book.getAuthor());
        viewholder.pubTextView.setText(book.getPublisher());

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return bookList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem( String title, String author, String publisher) {
        //아이콘 뺌, 아직 어케 하는지 모르겠음
        Book item = new Book();
        item.setTitle(title);
        item.setAuthor(author);
        item.setPublisher(publisher);

        bookList.add(item);
    }



}
