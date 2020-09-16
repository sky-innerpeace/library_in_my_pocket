package org.techtown.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.googlecode.tesseract.android.TessBaseAPI;

import org.techtown.home.PostInfo;
import org.techtown.home.R;
import org.techtown.home.view.ContentsItemView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import static org.techtown.home.Util.GALLERY_IMAGE;
import static org.techtown.home.Util.GALLERY_VIDEO;
import static org.techtown.home.Util.INTENT_MEDIA;
import static org.techtown.home.Util.INTENT_PATH;
import static org.techtown.home.Util.isImageFile;
import static org.techtown.home.Util.isStorageUrl;
import static org.techtown.home.Util.isVideoFile;
import static org.techtown.home.Util.showToast;
import static org.techtown.home.Util.storageUrlToName;

public class BookPostActivity extends AppCompatActivity {
    private static final String TAG = "BookPostActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout buttonsBackgroundLayout;

    private RelativeLayout loaderLayout;
    private EditText selectedEditText;
    private EditText titleEditText;
    private EditText selectedImageVIew;
    private EditText contentsEditText;
    private PostInfo postInfo;
    private int pathCount, successCount;

    String isbn = "";
    String text_title;
    String text_author;
    String text_startDay;
    String text_finishDay;
    String text_totalPage;
    String text_readPage;
    String text_wiseOcr;
    String image_book = "";

    TessBaseAPI tess;
    String dataPath = "";
    private static final int REQUEST_CODE = 0;
    private Button wiseSaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_post);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLyaout);
        contentsEditText = findViewById(R.id.contentsEditText);

        Intent intent = getIntent();


        EditText title = (EditText) findViewById(R.id.title);
        EditText author = (EditText) findViewById(R.id.author);
        EditText startDay = (EditText)findViewById(R.id.startDay);
        EditText finishDay = (EditText)findViewById(R.id.finishDay);
        EditText totalPage = (EditText)findViewById(R.id.totalPage);
        EditText readPage = (EditText)findViewById(R.id.readPage);
        EditText ocrWise = (EditText)findViewById(R.id.wise_saying);
        Button postSave = (Button)findViewById(R.id.btn_post_save);
        ImageView image = (ImageView)findViewById(R.id.image);

        text_title = intent.getStringExtra("Title");
        text_author = intent.getStringExtra("Author");
        text_startDay = intent.getStringExtra("StartDay");
        text_finishDay = intent.getStringExtra("FinishDay");
        text_totalPage = intent.getStringExtra("TotalPage");
        text_readPage = intent.getStringExtra("ReadPage");
        image_book = intent.getStringExtra("Image");
        isbn = intent.getStringExtra("Isbn");

        if(text_title != null) title.setText(text_title);
        if(text_author != null) author.setText(text_author);
        if(text_startDay != null) startDay.setText(text_startDay);
        if(text_finishDay != null) finishDay.setText(text_finishDay);
        if(text_totalPage != null) totalPage.setText(text_totalPage);
        if(text_readPage != null) readPage.setText(text_readPage);
        if(image_book != null) Glide.with(this).load(image_book).fitCenter().into(image);

        wiseSaying = findViewById(R.id.btn_wise_saying);

        wiseSaying.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        //데이터 경로
        dataPath = getFilesDir()+"/tesseract/";

        //한글&영어 데이터 체크
        checkFile(new File(dataPath + "tessdata/"),"kor");
        checkFile(new File(dataPath+"tessdata/"),"eng");

        //문자 인식을 수행할 tess 객체 생성
        String lang = "kor+eng";
        tess = new TessBaseAPI();
        tess.init(dataPath,lang);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);


        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        postInit();

        postSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//입력한 책 리뷰 저장
                storageUpload();
            }
        });

    }
    @Override//결과?
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in); //갤러리에서 가져온 이미지 저장
                    in.close();
                    processImage(img); //갤러리에서 가져온 이미지 인식하기
                    //processImage(BitmapFactory.decodeResource(getResources(),R.drawable.test3)); //저장한 test3 이미지 인식하기
                    //imageView.setImageBitmap(img); //가져온 이미지 출력하기
                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //위시리스트에 포함
            Button b = (Button)v;
            if(b.getText().equals("♡")) b.setText("♥");
            else if(b.getText().equals("♥")) b.setText("♡");

            switch (v.getId()) {
                case R.id.check://작성글 업로드
                    storageUpload();
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
        String title = ((TextView) findViewById(R.id.title)).getText().toString();
        String author = ((TextView) findViewById(R.id.author)).getText().toString();
        String startDay = ((EditText)findViewById(R.id.startDay)).getText().toString();
        String finishDay = ((EditText)findViewById(R.id.finishDay)).getText().toString();
        String totalPage = ((EditText)findViewById(R.id.totalPage)).getText().toString();
        String readPage = ((EditText)findViewById(R.id.readPage)).getText().toString();
        String ocrWise = ((EditText)findViewById(R.id.wise_saying)).getText().toString();
        //isbn, image_book

        if (title.length() > 0) {
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = postInfo == null ? firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();
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
                                                PostInfo postInfo = new PostInfo(title, author, startDay, finishDay, totalPage, readPage, ocrWise, contentsList, formatList, user.getUid(), date);
                                                storeUpload(documentReference, postInfo);
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
                storeUpload(documentReference, new PostInfo(title, author, startDay, finishDay, totalPage, readPage, ocrWise, contentsList, formatList, user.getUid(), date));
               }
        } else {
            showToast(BookPostActivity.this, "제목을 입력해주세요.");
        }
    }

    private void storeUpload(DocumentReference documentReference, final PostInfo postInfo) {
        documentReference.set(postInfo.getPostInfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("postInfo", postInfo);
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
        if (postInfo != null) {
            titleEditText.setText(postInfo.getTitle());
            ArrayList<String> contentsList = postInfo.getContents();
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
                            selectedImageVIew = (EditText) v;
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

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }

    //문자 인식 및 결과 출력
    public void processImage(Bitmap bitmap){
        Toast.makeText(getApplicationContext(),"이미지가 복잡할 경우 해석시 많은 시간이 소요될수도 있습니다.",Toast.LENGTH_LONG).show();
        String OCRresult = null;
        tess.setImage(bitmap);
        OCRresult = tess.getUTF8Text();
        EditText OCRTextView = (EditText) findViewById(R.id.wise_saying);

        OCRTextView.setText(OCRresult);
    }

    //파일 복제
    private void copyFiles(String lang){
        try{
            String filepath = dataPath + "/tessdata/" + lang + ".traineddata";

            AssetManager assetManager = getAssets();

            InputStream inStream = assetManager.open("tessdata/"+lang+".traineddata");
            OutputStream outStream = new FileOutputStream(filepath);

            byte[] buffer = new byte[1024];
            int read;
            while((read = inStream.read(buffer))!=-1){
                outStream.write(buffer,0,read);
            }
            outStream.flush();
            outStream.close();
            inStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir, String lang) {
        if(!dir.exists()&&dir.mkdirs()){
            copyFiles(lang);
        }
        if(dir.exists()){
            String datafilePath = dataPath +"/tessdata/" + lang +".traineddata";
            File datafile = new File(datafilePath);
            if(!datafile.exists()){
                copyFiles(lang);
            }
        }
    }
}

