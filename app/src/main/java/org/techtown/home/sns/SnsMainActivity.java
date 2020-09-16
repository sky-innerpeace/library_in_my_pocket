package org.techtown.home.sns;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.home.R;
import org.techtown.home.activity.BasicActivity;
import org.techtown.home.activity.LoginActivity;
import org.techtown.home.activity.MemberInitActivity;
import org.techtown.home.sns.fragment.MyWriteFragment;
import org.techtown.home.sns.fragment.SnsHomeFragment;

public class SnsMainActivity extends BasicActivity {
    private static final String TAG = "SnsMainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sns_main);
        setToolbarTitle(getResources().getString(R.string.app_name));

        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                init();
                break;
        }
    }

    private void init(){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            myStartActivity(LoginActivity.class);//원래 singup
        } else {
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid());
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                myStartActivity(MemberInitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

            SnsHomeFragment snshomeFragment = new SnsHomeFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, snshomeFragment)
                    .commit();

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.home:
                            SnsHomeFragment snshomeFragment = new SnsHomeFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, snshomeFragment)
                                    .commit();
                            return true;
                        case R.id.myInfo:
                            MyWriteFragment myWriteFragment = new MyWriteFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container, myWriteFragment)
                                    .commit();
                            return true;
                    }
                    return false;
                }
            });
        }
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 1);
    }
}
