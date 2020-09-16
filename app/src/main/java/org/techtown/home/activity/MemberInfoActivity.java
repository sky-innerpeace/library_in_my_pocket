package org.techtown.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.techtown.home.R;

import javax.annotation.Nullable;

public class MemberInfoActivity extends BasicActivity {
    private static final String TAG = "MemberInfo";
    private TextView profilename,profileemail,profileage,profilegender,goalTextView;
    ImageView profileImageView;
    FirebaseFirestore db;
    FirebaseUser user;
    StorageReference storageReference;
    Button editprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        profileImageView = findViewById(R.id.profileImageView);
        profileemail=findViewById(R.id.profileemail);
        profilename=(TextView) findViewById(R.id.profilename);
        profileage=(TextView) findViewById(R.id.profileage);
        profilegender=(TextView) findViewById(R.id.profilegender);
        editprofile=(Button)findViewById(R.id.editprofile);
        goalTextView=findViewById(R.id.goalTextView);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        String userId=user.getUid();


        StorageReference profileRef = storageReference.child("users/"+user.getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImageView);
            }
        });

        //회원정보 불러오기
        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    profilename.setText(documentSnapshot.getString("name"));
                    profileemail.setText(documentSnapshot.getString("email"));
                    profileage.setText(documentSnapshot.getString("age"));
                    profilegender.setText(documentSnapshot.getString("gender"));
                    goalTextView.setText(documentSnapshot.getString("goal"));
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open gallery
                Intent i = new Intent(v.getContext(), EditProfileActivity.class);
                i.putExtra("name",profilename.getText().toString());
                i.putExtra("goal",goalTextView.getText().toString());
                startActivity(i);

            }
        });
    }


    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}
