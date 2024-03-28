package com.example.agromate;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMAGE_PICK = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;

    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pg);

        Button plusButton = findViewById(R.id.plusButton);


        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSensorDialog();
            }
        });
        imageView1=findViewById(R.id.imageView1);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Home.class);
                startActivity(intent);
            }
        });
    }

    private void checkPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(MainActivity.this, "Permission denied. Cannot open gallery.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView1.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private AlertDialog dialog;
    private void showAddSensorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_sensor, null);

        EditText sensorIdEditText = dialogView.findViewById(R.id.sensorIdEditText);
        Button addButton = dialogView.findViewById(R.id.addButton);
        addButton.setText("Connect");
        addButton.setTextColor(Color.WHITE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sensorId = sensorIdEditText.getText().toString().trim();
                if (!sensorId.isEmpty()) {
                     imageView1=findViewById(R.id.imageView1);
                    imageView1.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } else {
                    Toast.makeText(MainActivity.this, "Please enter Sensor ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Build and show dialog
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveSensorDataLocally(String sensorId) {
        // Placeholder for saving sensor data locally
        Toast.makeText(this, "Sensor ID: " + sensorId + " saved locally", Toast.LENGTH_SHORT).show();

        // Create a new circular ImageView
        ImageView sensorImageView = new ImageView(this);
        sensorImageView.setImageResource(R.drawable.circular_button); // Set circular shape background
        sensorImageView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Add sensor name as text to ImageView
        TextView sensorNameTextView = new TextView(this);
        sensorNameTextView.setText(sensorId);
        sensorNameTextView.setTextColor(Color.BLACK);
        sensorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        sensorNameTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Create a LinearLayout to hold the ImageView and TextView
        LinearLayout sensorLayout = new LinearLayout(this);
        sensorLayout.setOrientation(LinearLayout.VERTICAL);
        sensorLayout.addView(sensorImageView);
        sensorLayout.addView(sensorNameTextView);

        // Add the LinearLayout to your main layout
        LinearLayout mainLayout = findViewById(R.id.main); // Replace mainLayout with your actual main layout ID
        mainLayout.addView(sensorLayout);
    }

}
