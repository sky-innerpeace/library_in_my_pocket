package org.techtown.home.sns;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.techtown.home.PostInfo;
import org.techtown.home.R;
import org.techtown.home.activity.BasicActivity;
import org.techtown.home.activity.GalleryActivity;
import org.techtown.home.sns.SnsWritePostActivity;
import org.techtown.home.view.ContentsItemView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static org.techtown.home.Util.GALLERY_IMAGE;
import static org.techtown.home.Util.GALLERY_VIDEO;
import static org.techtown.home.Util.INTENT_MEDIA;
import static org.techtown.home.Util.INTENT_PATH;
import static org.techtown.home.Util.isStorageUrl;
import static org.techtown.home.Util.isVideoFile;
import static org.techtown.home.Util.showToast;
import static org.techtown.home.Util.isImageFile;
import static org.techtown.home.Util.storageUrlToName;

public class SnsWritePostActivity extends BasicActivity {
    private static final String TAG = "SnsWritePostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;
    private ImageView selectedImageVIew;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    private SnsPostInfo snspostInfo;
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns_write_post);
        setToolbarTitle("게시글 작성");

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLyaout);
        contentsEditText = findViewById(R.id.contentsEditText);
        titleEditText = findViewById(R.id.titleEditText);

        findViewById(R.id.check).setOnClickListener(onClickListener);//확인버튼, 글 등록
        findViewById(R.id.image).setOnClickListener(onClickListener);//이미지 추가 버튼
        findViewById(R.id.video).setOnClickListener(onClickListener);//동영상 추가 버튼
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectedEditText = null;
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        snspostInfo = (SnsPostInfo) getIntent().getSerializableExtra("snspostInfo");
        postInit();
    }

    @Override//결과?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if (selectedEditText == null) {
                        parent.addView(contentsItemView);
                    } else {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            if (parent.getChildAt(i) == selectedEditText.getParent()) {
                                parent.addView(contentsItemView, i + 1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);//경로
                    pathList.set(parent.indexOfChild((View) selectedImageVIew.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageVIew);//이미지 넣어줌
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.check://작성글 업로드
                    storageUpload();
                    break;
                case R.id.image://갤러리(사진)으로 이동
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 0);
                    break;
                case R.id.video://갤러리(동영상)으로 이동
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if (buttonsBackgroundLayout.getVisibility() == View.VISIBLE) {
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class, GALLERY_IMAGE, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartActivity(GalleryActivity.class, GALLERY_VIDEO, 1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    final View selectedView = (View) selectedImageVIew.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                    if(isStorageUrl(path)){
                        StorageReference desertRef = storageRef.child("snsposts/" + snspostInfo.getId() + "/" + storageUrlToName(path));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(SnsWritePostActivity.this, "파일을 삭제하였습니다.");
                                pathList.remove(parent.indexOfChild(selectedView) - 1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(SnsWritePostActivity.this, "파일을 삭제하는데 실패하였습니다.");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView) - 1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                selectedEditText = (EditText) v;
            }
        }
    };

    private void storageUpload() {//확인버튼(check)누르면 파이어베이스에 업로드 되는 함수
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();
        if (title.length() > 0) {
            loaderLayout.setVisibility(View.VISIBLE);
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = snspostInfo == null ? firebaseFirestore.collection("snsposts").document() : firebaseFirestore.collection("snsposts").document(snspostInfo.getId());
            final Date date = snspostInfo == null ? new Date() : snspostInfo.getCreatedAt();
            for (int i = 0; i < parent.getChildCount(); i++) {
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for (int ii = 0; ii < linearLayout.getChildCount(); ii++) {
                    View view = linearLayout.getChildAt(ii);
                    if (view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))) {
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);
                        if(isImageFile(path)){
                            formatList.add("image");
                        }else if (isVideoFile(path)){
                            formatList.add("video");
                        }else{
                            formatList.add("text");
                        }
                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index", "" + (contentsList.size() - 1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index, uri.toString());
                                            if (successCount == 0) {
                                                SnsPostInfo snspostInfo = new SnsPostInfo(title, contentsList, formatList, user.getUid(), date);
                                                storeUpload(documentReference, snspostInfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e) {
                            Log.e("로그", "에러: " + e.toString());
                        }
                        pathCount++;
                    }
                }
            }
            if (successCount == 0) {
                storeUpload(documentReference, new SnsPostInfo(title, contentsList, formatList, user.getUid(), date));
            }
        } else {
            showToast(SnsWritePostActivity.this, "제목을 입력해주세요.");
        }
    }

    private void storeUpload(DocumentReference documentReference, final SnsPostInfo snspostInfo) {
        documentReference.set(snspostInfo.getSnsPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        loaderLayout.setVisibility(View.GONE);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("snspostInfo", snspostInfo);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

    private void postInit() {
        if (snspostInfo != null) {
            titleEditText.setText(snspostInfo.getTitle());
            ArrayList<String> contentsList = snspostInfo.getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {
                    pathList.add(contents);
                    ContentsItemView contentsItemView = new ContentsItemView(this);
                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageVIew = (ImageView) v;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                    if (i < contentsList.size() - 1) {
                        String nextContents = contentsList.get(i + 1);
                        if (!isStorageUrl(nextContents)) {
                            contentsItemView.setText(nextContents);
                        }
                    }
                } else if (i == 0) {
                    contentsEditText.setText(contents);
                }
            }
        }
    }

    private void myStartActivity(Class c, int media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra(INTENT_MEDIA, media);//갤러리 액티비티에서 이미지인지 동영상인지를 나타냄??
        startActivityForResult(intent, requestCode);
    }

}
