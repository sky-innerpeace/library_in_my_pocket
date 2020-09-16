package org.techtown.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.techtown.home.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends BasicActivity {

    private FirebaseAuth mAuth;
    private Button signupbtn;
    private EditText editname,editage,emailEdit,pwEdit,pwCheck,editGoal;
    private FirebaseFirestore fstore;
    private String userId;
    private static final String TAG = "activity_sign_up";
    private  RadioGroup sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        signupbtn = findViewById(R.id.signUpButton);
        emailEdit = findViewById(R.id.emailEditText);
        pwEdit = findViewById(R.id.passwordEditText);
        pwCheck = findViewById(R.id.passwordCheckEditText);
        editname = findViewById(R.id.editTextname);
        editage = findViewById(R.id.editTextAge);
        editGoal = findViewById(R.id.goalEditText);
        sex = findViewById(R.id.sex);

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editname.getText().toString().trim();
                String age = editage.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();
                String password = pwEdit.getText().toString().trim();
                String goal = editGoal.getText().toString();
                int finalGoal = Integer.parseInt(goal);
                int id = sex.getCheckedRadioButtonId();
                RadioButton gen = (RadioButton) findViewById(id);
                String gender = gen.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    editname.setError("Name is Required");
                    return;
                }
                if (TextUtils.isEmpty(age)) {
                    editage.setError("Age is Required");
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    emailEdit.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    pwEdit.setError("Password is Required");
                    return;
                }
                if (TextUtils.isEmpty(goal)) {
                    pwEdit.setError("Goal is Required");
                    return;
                }
                //회원정보 파이어베이스에 등록
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser fuser = mAuth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "User Created.", Toast.LENGTH_SHORT).show();
                            String userID = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("name", name);
                            user.put("email", email);
                            user.put("age", age);
                            user.put("gender",gender);
                            user.put("goal",goal);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            Toast.makeText(SignUpActivity.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

}