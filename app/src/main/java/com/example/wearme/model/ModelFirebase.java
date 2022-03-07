package com.example.wearme.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {

    public static void getItem(String id, Model.GetItemListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Item item = null;
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null) {
                        item = new Item();
                        item.fromMap(task.getResult().getData());
                    }
                }
                listener.onComplete(item);
            }
        });
    }

    interface GetAllItemsListener{
        void onComplete(List<Item> list);
    }
    public void getAllItems(Long lastUpdated, final GetAllItemsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(lastUpdated, 0);
//        db.collection("items").whereGreaterThanOrEqualTo("lastUpdated",ts).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
        db.collection("items").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<Item> items = new LinkedList<Item>();
                if (task.isSuccessful()) {
                    for ( DocumentSnapshot doc:task.getResult()) {
                        Item item = new Item();
                        item.fromMap(doc.getData());
//                        Item item = doc.toObject(Item.class);
                        items.add(item);
                    }
                }
                listener.onComplete(items);
            }
        });
    }

    public void addItem(Item item, Model.AddItemListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(item.getId())
                .set(item.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void documentReference) {
                        Log.d("TAG", "item added successfully");
                        listener.onComplete();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding item", e);
                        listener.onComplete();
                    }
                });
    }

    public void updateItem(Item item, Model.AddItemListener listener) {
        addItem(item, listener);
    }

    public void delete(Item item, Model.DeleteItemListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").document(item.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("falied to delete");
            }
        });
    }

    public void uploadImage(Bitmap imageBmp, String name, final Model.UploadImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child("images").child(name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    }
                });
            }
        });
    }
}
