package org.techtown.home.sns;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.techtown.home.R;
import org.techtown.home.activity.BasicActivity;
import org.techtown.home.sns.view.SnsReadContentsView;


public class SnsPostActivity extends BasicActivity {
    private SnsPostInfo snspostInfo;
    private SnsFirebaseHelper snsfirebaseHelper;
    private SnsReadContentsView snsreadContentsVIew;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns_post);

        snspostInfo = (SnsPostInfo) getIntent().getSerializableExtra("snspostInfo");
        contentsLayout = findViewById(R.id.contentsLayout);
        snsreadContentsVIew = findViewById(R.id.readContentsView);

        snsfirebaseHelper = new SnsFirebaseHelper(this);
        snsfirebaseHelper.setOnSnsPostListener(onSnsPostListener);
        uiUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    snspostInfo = (SnsPostInfo)data.getSerializableExtra("snspostinfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                snsfirebaseHelper.storageDelete(snspostInfo);
                return true;
            case R.id.modify:
                myStartActivity(SnsWritePostActivity.class, snspostInfo);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    OnSnsPostListener onSnsPostListener = new OnSnsPostListener() {
        @Override
        public void onDelete(SnsPostInfo snspostInfo) {
            Log.e("로그 ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 ","수정 성공");
        }
    };

    private void uiUpdate(){
        setToolbarTitle(snspostInfo.getTitle());
        snsreadContentsVIew.setSnsPostInfo(snspostInfo);
    }

    private void myStartActivity(Class c, SnsPostInfo snspostInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("snspostInfo", snspostInfo);
        startActivityForResult(intent, 0);
    }
}
