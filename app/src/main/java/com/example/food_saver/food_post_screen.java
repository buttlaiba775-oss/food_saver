package com.example.food_saver;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import com.google.android.material.button.MaterialButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class food_post_screen extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1002;
    private static final int CAMERA_REQUEST_CODE = 1003;

    private EditText etFoodName, etQuantity, etDescription;
    private Spinner spinnerCategory;
    private AppCompatButton btnExpiresInPicker;
    private TextView tvLocationLabel;
    private ImageView ivCapturedPhoto;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String currentUserId;

    private FusedLocationProviderClient fusedLocationClient;

    private double latitude = 0.0;
    private double longitude = 0.0;
    private boolean isLocationFetched = false;
    private Bitmap imageBitmap = null;

    private long expiryTimestamp = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_post_screen);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ImageView btnBack = findViewById(R.id.btnBack);
        RelativeLayout btnCapturePhoto = findViewById(R.id.btnCapturePhoto);
        ivCapturedPhoto = findViewById(R.id.ivCapturedPhoto);
        etFoodName = findViewById(R.id.etFoodName);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etQuantity = findViewById(R.id.etQuantity);
        btnExpiresInPicker = findViewById(R.id.btnExpiresInPicker);
        LinearLayout btnSelectLocation = findViewById(R.id.btnSelectLocation);
        tvLocationLabel = findViewById(R.id.tvLocationLabel);
        etDescription = findViewById(R.id.etDescription);
        MaterialButton btnPostFood = findViewById(R.id.btnPostFood);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnCapturePhoto != null) {
            btnCapturePhoto.setOnClickListener(v -> checkCameraPermissionAndOpen());
        }

        if (btnExpiresInPicker != null) {
            btnExpiresInPicker.setOnClickListener(v -> showExpiryTimePicker());
        }

        if (btnSelectLocation != null) {
            btnSelectLocation.setOnClickListener(v -> checkLocationPermissionsAndFetch());
        }

        if (tvLocationLabel != null) {
            tvLocationLabel.setOnClickListener(v -> checkLocationPermissionsAndFetch());
        }

        if (btnPostFood != null) {
            btnPostFood.setOnClickListener(v -> uploadFoodPostToFirebase());
        }
    }

    private void showExpiryTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String timeFormat = hourOfDay + " hrs " + minuteOfHour + " mins";
            if (btnExpiresInPicker != null) {
                btnExpiresInPicker.setText(timeFormat);
            }

            long durationInMillis = (hourOfDay * 3600000L) + (minuteOfHour * 60000L);
            expiryTimestamp = System.currentTimeMillis() + durationInMillis;
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openNativeCameraIntent();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void openNativeCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No camera application found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLocationPermissionsAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentGPSLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @SuppressWarnings("MissingPermission")
    private void fetchCurrentGPSLocation() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Location Permission Not Granted", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tvLocationLabel != null) {
            tvLocationLabel.setText("Fetching...");
        }

        fusedLocationClient.getLastLocation().addOnCompleteListener(task -> {

            if (task.isSuccessful() && task.getResult() != null) {

                Location location = task.getResult();

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                isLocationFetched = true;

                tvLocationLabel.setText("Location Verified!");

                Toast.makeText(food_post_screen.this,
                        "Latitude: " + latitude + "\nLongitude: " + longitude,
                        Toast.LENGTH_LONG).show();

            } else {

                tvLocationLabel.setText("Click to retry");

                Toast.makeText(food_post_screen.this,
                        "Location not found. Please turn ON GPS.",
                        Toast.LENGTH_LONG).show();
            }

        }).addOnFailureListener(e -> {

            Toast.makeText(food_post_screen.this,
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                if (ivCapturedPhoto != null && imageBitmap != null) {
                    ivCapturedPhoto.setImageBitmap(imageBitmap);
                    ivCapturedPhoto.setImageTintList(null);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentGPSLocation();
            }
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openNativeCameraIntent();
            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadFoodPostToFirebase() {
        String foodName = etFoodName != null ? etFoodName.getText().toString().trim() : "";
        String category = spinnerCategory != null && spinnerCategory.getSelectedItem() != null ? spinnerCategory.getSelectedItem().toString() : "";
        String quantity = etQuantity != null ? etQuantity.getText().toString().trim() : "";
        String expiryTime = btnExpiresInPicker != null ? btnExpiresInPicker.getText().toString().trim() : "";
        String description = etDescription != null ? etDescription.getText().toString().trim() : "";

        if (TextUtils.isEmpty(foodName)) {
            if (etFoodName != null) etFoodName.setError("Food name is required");
            return;
        }
        if (TextUtils.isEmpty(quantity)) {
            if (etQuantity != null) etQuantity.setError("Quantity is required");
            return;
        }
        if (!isLocationFetched) {
            Toast.makeText(this, "Please verify your location first.", Toast.LENGTH_LONG).show();
            return;
        }

        String postId = mDatabase.child("FoodPosts").push().getKey();
        if (postId == null) return;

        Map<String, Object> postDetails = new HashMap<>();
        postDetails.put("postId", postId);

        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        }

        postDetails.put("donorId", currentUserId);
        postDetails.put("foodName", foodName);
        postDetails.put("category", category);
        postDetails.put("quantity", quantity);
        postDetails.put("expiresIn", expiryTime);
        postDetails.put("expiryTimestamp", expiryTimestamp);
        postDetails.put("description", description);
        postDetails.put("latitude", latitude);
        postDetails.put("longitude", longitude);
        postDetails.put("imageUrl", "default_placeholder");
        postDetails.put("timestamp", System.currentTimeMillis());

        mDatabase.child("FoodPosts").child(postId).setValue(postDetails)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(food_post_screen.this, "Food post posted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(food_post_screen.this, "Database error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}