package com.example.pigeon.FirebaseManagers;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class ImageHandler {

    public static UploadTask uploadImagePath(String src, String storagePath) {
        Uri file = Uri.fromFile(new File(src));
        StorageReference storageRef = FirebaseHelper.mainStorage.getReference().child(storagePath);

        UploadTask uploadTask = storageRef.putFile(file);
        // Register observers to listen for when the download is done or if it fails
        return uploadTask;
    }

    public static UploadTask uploadImagePath(String src, String storagePath, StorageMetadata metadata) {
        Uri file = Uri.fromFile(new File(src));
        StorageReference storageRef = FirebaseHelper.mainStorage.getReference().child(storagePath);

        UploadTask uploadTask = storageRef.putFile(file, metadata);
        // Register observers to listen for when the download is done or if it fails
        return uploadTask;
    }


    public static UploadTask uploadImageBytes(ImageView image, String storagePath){
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        byte[] data = os.toByteArray();

        StorageReference storageRef = FirebaseHelper.mainStorage.getReference().child(storagePath);
        UploadTask uploadTask = storageRef.putBytes(data);
        return uploadTask;
    }


}
