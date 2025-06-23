package com.s23010169.ecowastereporter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.s23010169.ecowastereporter.models.Report;
import com.s23010169.ecowastereporter.models.ReportDatabaseHelper;
import com.s23010169.ecowastereporter.adapters.PhotoPreviewAdapter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ReportWastePage extends AppCompatActivity {
    private CardView photoContainer;
    private Spinner wasteTypeSpinner;
    private EditText locationInput;
    private EditText descriptionInput;
    private MaterialButton submitButton;
    private ImageButton backButton;
    private MaterialButton getCurrentLocationButton;
    private FloatingActionButton cameraFab;
    private AppBarLayout appBarLayout;
    private String selectedWasteType;
    private boolean isSubmitting = false;
    private List<Uri> photoUris = new ArrayList<>(); // Store photo URIs
    private TextView photoCountText; // To show number of photos added
    private static final int MIN_DESCRIPTION_LENGTH = 20;
    private static final int MIN_PHOTOS_REQUIRED = 1;
    
    // Permission request codes
    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final int CAMERA_PERMISSION_REQUEST = 1002;
    private static final int STORAGE_PERMISSION_REQUEST = 1003;
    
    // Location related variables
    private LocationManager locationManager;
    private Location currentLocation;
    private String currentPhotoPath;
    
    // Activity result launchers
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    private RecyclerView photoPreviewRecyclerView;
    private PhotoPreviewAdapter photoPreviewAdapter;
    private View addPhotoContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_waste_page);

        // Initialize activity result launchers
        initializeActivityResultLaunchers();
        
        // Initialize location manager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        initializeViews();
        setupSpinner();
        setupClickListeners();
        setupAppBarBehavior();
    }

    private void initializeActivityResultLaunchers() {
        // Camera launcher
        takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (currentPhotoPath != null) {
                        File f = new File(currentPhotoPath);
                        Uri contentUri = FileProvider.getUriForFile(this,
                            "com.s23010169.ecowastereporter.fileprovider", f);
                        photoUris.add(contentUri);
                        photoPreviewAdapter.updatePhotos(photoUris);
                        updatePhotoCount();
                        showSnackbar("Photo captured successfully");
                    }
                }
            }
        );

        // Gallery launcher
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        photoUris.add(selectedImage);
                        photoPreviewAdapter.updatePhotos(photoUris);
                        updatePhotoCount();
                        showSnackbar("Photo selected successfully");
                    }
                }
            }
        );

        // Permission launcher
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean isGranted : permissions.values()) {
                    if (!isGranted) {
                        allGranted = false;
                        break;
                    }
                }
                if (allGranted) {
                    showSnackbar("All permissions granted");
                } else {
                    showSnackbar("Some permissions were denied");
                }
            }
        );
    }

    private void initializeViews() {
        photoContainer = findViewById(R.id.photoContainer);
        wasteTypeSpinner = findViewById(R.id.wasteTypeSpinner);
        locationInput = findViewById(R.id.locationInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
        getCurrentLocationButton = findViewById(R.id.getCurrentLocationButton);
        cameraFab = findViewById(R.id.cameraFab);
        appBarLayout = findViewById(R.id.appBarLayout);
        photoCountText = findViewById(R.id.photoCountText);
        photoPreviewRecyclerView = findViewById(R.id.photoPreviewRecyclerView);
        addPhotoContainer = findViewById(R.id.addPhotoContainer);
        
        // Setup photo preview RecyclerView
        setupPhotoPreviewRecyclerView();
        
        // Set initial photo count
        updatePhotoCount();
    }

    private void setupPhotoPreviewRecyclerView() {
        photoPreviewRecyclerView.setLayoutManager(
            new androidx.recyclerview.widget.LinearLayoutManager(
                this, 
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, 
                false
            )
        );
        
        photoPreviewAdapter = new PhotoPreviewAdapter(photoUris, position -> {
            // Handle photo deletion
            photoUris.remove(position);
            photoPreviewAdapter.updatePhotos(photoUris);
            updatePhotoPreviewVisibility();
            updatePhotoCount();
        });
        
        photoPreviewRecyclerView.setAdapter(photoPreviewAdapter);
    }

    private void updatePhotoPreviewVisibility() {
        if (photoUris.isEmpty()) {
            photoPreviewRecyclerView.setVisibility(View.GONE);
            addPhotoContainer.setVisibility(View.VISIBLE);
        } else {
            photoPreviewRecyclerView.setVisibility(View.VISIBLE);
            addPhotoContainer.setVisibility(View.GONE);
        }
    }

    private void updatePhotoCount() {
        if (photoCountText != null) {
            if (photoUris.isEmpty()) {
                photoCountText.setText("No photos added");
                photoCountText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            } else {
                photoCountText.setText(String.format("%d photo%s added", 
                    photoUris.size(), photoUris.size() == 1 ? "" : "s"));
                photoCountText.setTextColor(ContextCompat.getColor(this, R.color.green_500));
            }
            updatePhotoPreviewVisibility();
        }
    }

    private void setupSpinner() {
        // Create a custom adapter with styled items
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, 
                R.layout.spinner_item, 
                getResources().getStringArray(R.array.waste_types)) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                text.setTextSize(16);
                if (position == 0) {
                    text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setPadding(32, 16, 32, 16);
                if (position == 0) {
                    text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
                } else {
                    text.setTextColor(ContextCompat.getColor(getContext(), android.R.color.black));
                }
                return view;
            }
        };
        
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        wasteTypeSpinner.setAdapter(adapter);
        wasteTypeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedWasteType = null;
                } else {
                    selectedWasteType = parent.getItemAtPosition(position).toString();
                    animateSelection(view);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedWasteType = null;
            }
        });
    }

    private void setupClickListeners() {
        backButton.setOnClickListener(v -> onBackPressed());

        photoContainer.setOnClickListener(v -> showImagePickerOptions());
        cameraFab.setOnClickListener(v -> showImagePickerOptions());

        getCurrentLocationButton.setOnClickListener(v -> {
            if (checkLocationPermissions()) {
                fetchCurrentLocation();
            } else {
                requestLocationPermissions();
            }
        });

        submitButton.setOnClickListener(v -> {
            if (!isSubmitting && validateInputs()) {
                submitReport();
            }
        });

        // Add text change listener for description to show remaining characters
        descriptionInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                int currentLength = s.length();
                if (currentLength > 0 && currentLength < MIN_DESCRIPTION_LENGTH) {
                    descriptionInput.setError(String.format("Minimum %d characters required (%d more needed)", 
                        MIN_DESCRIPTION_LENGTH, MIN_DESCRIPTION_LENGTH - currentLength));
                } else {
                    descriptionInput.setError(null);
                }
            }
        });
    }

    private boolean checkLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        requestPermissionLauncher.launch(new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void fetchCurrentLocation() {
        if (!checkLocationPermissions()) {
            showSnackbar("Location permissions not granted");
            return;
        }

        getCurrentLocationButton.setEnabled(false);
        getCurrentLocationButton.setText("Fetching...");
        locationInput.setText("Fetching location...");
        locationInput.setError(null);

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        currentLocation = location;
                        updateLocationUI(location);
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onProviderDisabled(@NonNull String provider) {
                        showSnackbar("Please enable GPS");
                        getCurrentLocationButton.setEnabled(true);
                        getCurrentLocationButton.setText("Current");
                    }

                    @Override
                    public void onProviderEnabled(@NonNull String provider) {}

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {}
                }
            );
        } catch (SecurityException e) {
            showSnackbar("Error accessing location");
            getCurrentLocationButton.setEnabled(true);
            getCurrentLocationButton.setText("Current");
        }
    }

    private void updateLocationUI(Location location) {
        String locationText = String.format(Locale.getDefault(),
            "%.6f, %.6f",
            location.getLatitude(),
            location.getLongitude());
        locationInput.setText(locationText);
        getCurrentLocationButton.setEnabled(true);
        getCurrentLocationButton.setText("Current");
        showSnackbar("Location updated successfully");
    }

    private void setupAppBarBehavior() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percentage = (float) Math.abs(verticalOffset) / appBarLayout.getTotalScrollRange();
            cameraFab.setScaleX(1 - percentage);
            cameraFab.setScaleY(1 - percentage);
        });
    }

    private void showImagePickerOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        MaterialButton cameraButton = bottomSheetView.findViewById(R.id.cameraButton);
        MaterialButton galleryButton = bottomSheetView.findViewById(R.id.galleryButton);

        cameraButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            checkAndRequestCameraPermission();
        });

        galleryButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            checkAndRequestStoragePermission();
        });

        bottomSheetDialog.show();
    }

    private void checkAndRequestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        } else {
            dispatchTakePictureIntent();
        }
    }

    private void checkAndRequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_IMAGES});
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
            } else {
                openGallery();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showSnackbar("Error occurred while creating the file");
                return;
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                    "com.s23010169.ecowastereporter.fileprovider",
                    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                
                // Grant permissions to the camera app
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                
                try {
                    takePictureLauncher.launch(takePictureIntent);
                } catch (Exception e) {
                    showSnackbar("Failed to launch camera");
                }
            }
        } else {
            showSnackbar("No camera available on this device");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void animateSelection(View view) {
        view.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(100)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
                }
            })
            .start();
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String errorMessage = "";

        // Check waste type
        if (selectedWasteType == null || selectedWasteType.isEmpty()) {
            errorMessage = "Please select a waste type";
            isValid = false;
        }

        // Check location
        else if (locationInput.getText().toString().trim().isEmpty()) {
            errorMessage = "Please enter or fetch a location";
            isValid = false;
        }

        // Check description length
        else if (descriptionInput.getText().toString().length() < MIN_DESCRIPTION_LENGTH) {
            errorMessage = String.format("Description must be at least %d characters", MIN_DESCRIPTION_LENGTH);
            isValid = false;
        }

        // Check photos
        else if (photoUris.size() < MIN_PHOTOS_REQUIRED) {
            errorMessage = String.format("Please add at least %d photo%s", 
                MIN_PHOTOS_REQUIRED, MIN_PHOTOS_REQUIRED > 1 ? "s" : "");
            isValid = false;
        }

        if (!isValid) {
            showSnackbar(errorMessage);
        }

        return isValid;
    }

    private void submitReport() {
        if (!validateInputs()) {
            return;
        }

        if (isSubmitting) {
            return;
        }

        try {
            isSubmitting = true;
            submitButton.setEnabled(false);
            submitButton.setText("Submitting...");

            // Create a new report object
            Report report = new Report();
            report.setReportId(UUID.randomUUID().toString().substring(0, 8)); // Generate a unique ID
            report.setWasteType(selectedWasteType);
            report.setLocation(locationInput.getText().toString().trim());
            report.setDescription(descriptionInput.getText().toString().trim());
            report.setPhotoUris(new ArrayList<>(photoUris)); // Create a new ArrayList to avoid reference issues
            report.setTimestamp(System.currentTimeMillis());
            report.setStatus("Pending");

            if (currentLocation != null) {
                report.setLatitude(currentLocation.getLatitude());
                report.setLongitude(currentLocation.getLongitude());
            }

            // Save report to database in a background thread
            new Thread(() -> {
                try {
                    ReportDatabaseHelper dbHelper = new ReportDatabaseHelper(this);
                    long result = dbHelper.addReport(report);

                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        if (result != -1) {
                            showSnackbar("Report submitted successfully");
                            navigateToMyReports();
                        } else {
                            showSnackbar("Failed to submit report");
                            submitButton.setEnabled(true);
                            submitButton.setText("Submit Report");
                        }
                        isSubmitting = false;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    // Update UI on the main thread
                    runOnUiThread(() -> {
                        showSnackbar("Error: " + e.getMessage());
                        submitButton.setEnabled(true);
                        submitButton.setText("Submit Report");
                        isSubmitting = false;
                    });
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            showSnackbar("Error: " + e.getMessage());
            submitButton.setEnabled(true);
            submitButton.setText("Submit Report");
            isSubmitting = false;
        }
    }

    private void navigateToMyReports() {
        Intent intent = new Intent(this, MyReportPage.class);
        startActivity(intent);
        finish();
    }

    private void showSnackbar(String message) {
        // Make sure we show the snackbar on the main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } else {
            runOnUiThread(() -> {
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (isSubmitting) {
            showSnackbar("Please wait while submitting the report");
            return;
        }
        super.onBackPressed();
    }
} 