package com.example.faustino.delitapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class ReportDELIT extends AppCompatActivity implements View.OnClickListener {

    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST = 0x100;
    private static final int GALLERY_PERMISSION_REQUEST = 0x200;


    private Bitmap bitmap;
    private String selectedImagePath;

    private ImageView mImageView;
    private EditText mDescriptionEditText;
    private Button mSendButton;
    private String TAG = "ReportDELIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_delit);


        mImageView = (ImageView) findViewById(R.id.form_image_view);
        mDescriptionEditText = (EditText) findViewById(R.id.form_description_edit_text);
        mSendButton = (Button) findViewById(R.id.form_send_button);
        mImageView.setOnClickListener(this);
        mSendButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view == mImageView) {
            startDialog();
        } else if (view == mSendButton) {
            if (mDescriptionEditText.getText().length() > 1)
                sendForm();
        }
    }

    @SuppressWarnings("VisibleForTests")
    private void sendForm() {
        mSendButton.setEnabled(false);
        mImageView.setDrawingCacheEnabled(true);
        mImageView.buildDrawingCache();
        Bitmap bitmap = mImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Images");
        final String fileName = "IMG_" + new Date().getTime() + ".jpg";

        final StorageReference mountainsRef = storageRef.child(fileName);
        final UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("reports");
                Item item = new Item();
                item.image = fileName;
                item.date = new Date();
                item.email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                item.description = mDescriptionEditText.getText().toString();
                item.url = taskSnapshot.getDownloadUrl().toString();
                myRef.push().setValue(item);
                finish();
            }
        });
    }

    /**
     * String url =uploadTask.getSnapshot().getDownloadUrl().toString(); // taskSnapshot.getDownloadUrl();
     * <p>
     * Toast.makeText(getApplicationContext(),
     * "url: " + url,
     * Toast.LENGTH_LONG).show();
     */
    private void startDialog() {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle(R.string.select_pictures_title);
        myAlertDialog.setMessage(R.string.select_pictures_msg);
        myAlertDialog.setPositiveButton(R.string.gallery,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = ContextCompat.checkSelfPermission(ReportDELIT.this,
                                android.Manifest.permission_group.STORAGE);
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            launchGallery();
                        } else {
                            ActivityCompat.requestPermissions(ReportDELIT.this, new String[]{
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_PERMISSION_REQUEST);
                        }
                    }
                });
        myAlertDialog.setNegativeButton(R.string.camera,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        int result = ContextCompat.checkSelfPermission(ReportDELIT.this,
                                android.Manifest.permission.CAMERA);
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            launchCamera();
                        } else {
                            ActivityCompat.requestPermissions(ReportDELIT.this, new String[]{
                                    android.Manifest.permission.CAMERA,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST);
                        }
                    }
                });
        myAlertDialog.show();
    }

    private void launchGallery() {
        Intent pictureActionIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(
                pictureActionIntent,
                GALLERY_PICTURE);
    }

    private void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (CAMERA_PERMISSION_REQUEST == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(android.Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        launchCamera();
                    }
                }
            }
        } else if (GALLERY_PERMISSION_REQUEST == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        launchGallery();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(bitmap);
        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                cursor.moveToFirst();
                selectedImagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(selectedImagePath, options);
                // Do something with the bitmap
                mImageView.setImageBitmap(bitmap);
                // At the end remember to close the cursor or you will end with the RuntimeException!
                cursor.close();
            } else {
                Toast.makeText(getApplicationContext(), "Cancelled",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
