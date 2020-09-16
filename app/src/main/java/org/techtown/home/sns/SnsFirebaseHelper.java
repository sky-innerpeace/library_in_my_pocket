package org.techtown.home.sns;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.home.OnPostListener;
import org.techtown.home.PostInfo;
import org.techtown.home.sns.SnsPostInfo;

import java.util.ArrayList;

import static org.techtown.home.Util.isStorageUrl;
import static org.techtown.home.Util.showToast;
import static org.techtown.home.Util.storageUrlToName;

public class SnsFirebaseHelper {
    private Activity activity;
    private OnSnsPostListener onSnsPostListener;
    private int successCount;

    public SnsFirebaseHelper(Activity activity) {
        this.activity = activity;
    }

    public void setOnSnsPostListener(OnSnsPostListener onSnsPostListener){
        this.onSnsPostListener = onSnsPostListener;
    }

    public void storageDelete(final SnsPostInfo snspostInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        //id랑 contentsList도 바꿔야되나?
        final String id = snspostInfo.getId();
        ArrayList<String> contentsList = snspostInfo.getContents();
        for (int i = 0; i < contentsList.size(); i++) {
            String contents = contentsList.get(i);
            if (isStorageUrl(contents)) {
                successCount++;
                StorageReference desertRef = storageRef.child("snsposts/" + id + "/" + storageUrlToName(contents));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id, snspostInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity, "Error");
                    }
                });
            }
        }
        storeDelete(id, snspostInfo);
    }

    private void storeDelete(final String id, final SnsPostInfo snspostInfo) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if (successCount == 0) {
            firebaseFirestore.collection("snsposts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity, "게시글을 삭제하였습니다.");
                            onSnsPostListener.onDelete(snspostInfo);
                            //postsUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity, "게시글을 삭제하지 못하였습니다.");
                        }
                    });
        }
    }
}
