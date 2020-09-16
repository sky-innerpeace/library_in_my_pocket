package org.techtown.home.sns.view;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;

import org.techtown.home.R;
import org.techtown.home.activity.BookPostActivity;
import org.techtown.home.sns.OnSnsPostListener;
import org.techtown.home.sns.SnsFirebaseHelper;
import org.techtown.home.sns.SnsMainActivity;
import org.techtown.home.sns.SnsPostActivity;
import org.techtown.home.sns.SnsPostInfo;
import org.techtown.home.sns.SnsWritePostActivity;
import org.techtown.home.sns.fragment.MyWriteFragment;

import java.util.ArrayList;

import static org.techtown.home.Util.showToast;


public class MyWriteAdapter  extends RecyclerView.Adapter<MyWriteAdapter.MainViewHolder>{
    private ArrayList<SnsPostInfo> mDataset;
    private Activity activity;
    private SnsFirebaseHelper snsfirebaseHelper;
    private ArrayList<ArrayList<SimpleExoPlayer>> playerArrayListArrayList = new ArrayList<>();
    private final int MORE_INDEX = 2;

    static class MainViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        MainViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public MyWriteAdapter(Activity activity, ArrayList<SnsPostInfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;

        snsfirebaseHelper = new SnsFirebaseHelper(activity);
    }

    public void setOnSnsPostListener(OnSnsPostListener onSnsPostListener){
        snsfirebaseHelper.setOnSnsPostListener(onSnsPostListener);
    }

    @Override
    public int getItemViewType(int position){
        return position;
    }

    @NonNull
    @Override
    public MyWriteAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sns_post, parent, false);
        final MyWriteAdapter.MainViewHolder mainViewHolder = new MyWriteAdapter.MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SnsPostActivity.class);
                intent.putExtra("snspostInfo", mDataset.get(mainViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyWriteAdapter.MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);

        SnsPostInfo snspostInfo = mDataset.get(position);
        titleTextView.setText(snspostInfo.getTitle());

        SnsReadContentsView snsreadContentsVIew = cardView.findViewById(R.id.readContentsView);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(snspostInfo)) {
            contentsLayout.setTag(snspostInfo);
            contentsLayout.removeAllViews();

            snsreadContentsVIew.setMoreIndex(MORE_INDEX);
            snsreadContentsVIew.setSnsPostInfo(snspostInfo);

            ArrayList<SimpleExoPlayer> playerArrayList = snsreadContentsVIew.getPlayerArrayList();
            if(playerArrayList != null){
                playerArrayListArrayList.add(playerArrayList);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void showPopup(View v, final int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.modify:
                        myStartActivity(SnsWritePostActivity.class, mDataset.get(position));
                        return true;
                    case R.id.delete:
                        snsfirebaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

    private void myStartActivity(Class c, SnsPostInfo snspostInfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("snspostInfo", snspostInfo);
        activity.startActivity(intent);
    }

    public void playerStop(){
        for(int i = 0; i < playerArrayListArrayList.size(); i++){
            ArrayList<SimpleExoPlayer> playerArrayList = playerArrayListArrayList.get(i);
            for(int ii = 0; ii < playerArrayList.size(); ii++){
                SimpleExoPlayer player = playerArrayList.get(ii);
                if(player.getPlayWhenReady()){
                    player.setPlayWhenReady(false);
                }
            }
        }
    }


}
