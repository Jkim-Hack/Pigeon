package com.example.pigeon.FirebaseManagers.Messaging;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.pigeon.FirebaseManagers.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.Callable;

public class ImageDownloadService implements Callable {

    private final String link;

    public ImageDownloadService(String link) {
        this.link = link;
    }

    @Override
    public Bitmap call() {
        StorageReference islandRef = FirebaseHelper.mainStorage.getReference().child(link);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                System.out.println(exception.getMessage());
            }
        });
        return null;
    }
}
