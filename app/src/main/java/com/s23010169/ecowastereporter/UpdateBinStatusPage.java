package com.s23010169.ecowastereporter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.s23010169.ecowastereporter.adapters.TaskSelectorAdapter;
import com.s23010169.ecowastereporter.models.Cleaner;
import com.s23010169.ecowastereporter.models.CleanerDatabaseHelper;
import com.s23010169.ecowastereporter.models.Report;
import com.s23010169.ecowastereporter.models.ReportDatabaseHelper;
import com.s23010169.ecowastereporter.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.s23010169.ecowastereporter.adapters.PhotoPreviewAdapter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.ImageView;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;

// This screen lets cleaners update the status of waste collection tasks and add notes/photos.
public class UpdateBinStatusPage extends AppCompatActivity implements TaskSelectorAdapter.TaskSelectionListener {
    private RadioButton radioFullyCleaned;
    private EditText editTextNotes;
    private Button btnSaveDraft, btnCompleteTask;
    private CardView photoUploadCard;
    private RecyclerView taskSelectorRecyclerView;
    private TaskSelectorAdapter taskAdapter;
    private TextView locationText, taskDetailsText;
    private ReportDatabaseHelper reportDatabaseHelper;
    private List<Task> availableTasks;
    private Task selectedTask;
    private Random random = new Random();
    private String userEmail;
    private List<Uri> photoUris = new ArrayList<>();
    private RecyclerView photoPreviewRecyclerView;
    private PhotoPreviewAdapter photoPreviewAdapter;
    private TextView photoCountText;
    private String currentPhotoPath;
    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private FusedLocationProviderClient fusedLocationClient;
    private Location userLocation;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_bin_status_page);

        // Get user email from intent
        userEmail = getIntent().getStringExtra("email");

        // Initialize database helper
        reportDatabaseHelper = new ReportDatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        requestUserLocation();

        // Initialize views
        initializeViews();
        initializePhotoFunction();
        setupToolbar();
        loadTasksFromDatabase();
        setupClickListeners();
    }

    private void initializeViews() {
        radioFullyCleaned = findViewById(R.id.radioFullyCleaned);
        editTextNotes = findViewById(R.id.editTextNotes);
        btnSaveDraft = findViewById(R.id.btnSaveDraft);
        btnCompleteTask = findViewById(R.id.btnCompleteTask);
        photoUploadCard = findViewById(R.id.photoUploadCard);
        taskSelectorRecyclerView = findViewById(R.id.taskSelectorRecyclerView);
        locationText = findViewById(R.id.locationText);
        taskDetailsText = findViewById(R.id.taskDetailsText);
        photoPreviewRecyclerView = findViewById(R.id.photoPreviewRecyclerView);
        photoCountText = findViewById(R.id.photoCountText);
    }

    private void initializePhotoFunction() {
        // Setup photo preview RecyclerView
        photoPreviewRecyclerView.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        photoPreviewAdapter = new PhotoPreviewAdapter(photoUris, position -> {
            photoUris.remove(position);
            photoPreviewAdapter.updatePhotos(photoUris);
            updatePhotoPreviewVisibility();
            updatePhotoCount();
        });
        photoPreviewAdapter.setOnPhotoClickListener((position, uri) -> showFullScreenPhoto(uri));
        photoPreviewRecyclerView.setAdapter(photoPreviewAdapter);
        updatePhotoCount();
        updatePhotoPreviewVisibility();
        // Activity result launchers
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
                        showToast("Photo captured successfully");
                    }
                }
            }
        );
        pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    if (selectedImage != null) {
                        photoUris.add(selectedImage);
                        photoPreviewAdapter.updatePhotos(photoUris);
                        updatePhotoCount();
                        showToast("Photo selected successfully");
                    }
                }
            }
        );
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
                    showToast("All permissions granted");
                } else {
                    showToast("Some permissions were denied");
                }
            }
        );
    }

    private void updatePhotoPreviewVisibility() {
        if (photoUris.isEmpty()) {
            photoPreviewRecyclerView.setVisibility(View.GONE);
        } else {
            photoPreviewRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void updatePhotoCount() {
        if (photoCountText != null) {
            if (photoUris.isEmpty()) {
                photoCountText.setText("No photos added");
                photoCountText.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            } else {
                photoCountText.setText(String.format("%d photo%s added", photoUris.size(), photoUris.size() == 1 ? "" : "s"));
                photoCountText.setTextColor(ContextCompat.getColor(this, R.color.green_500));
            }
            updatePhotoPreviewVisibility();
        }
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
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showToast("Error occurred while creating the file");
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                    "com.s23010169.ecowastereporter.fileprovider",
                    photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                try {
                    takePictureLauncher.launch(takePictureIntent);
                } catch (Exception e) {
                    showToast("Failed to launch camera");
                }
            }
        } else {
            showToast("No camera available on this device");
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        pickImageLauncher.launch(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Set toolbar title with cleaner name if available
        if (userEmail != null) {
            try {
                CleanerDatabaseHelper cleanerDb = new CleanerDatabaseHelper(this);
                Cleaner cleaner = cleanerDb.getCleanerByEmail(userEmail);
                if (cleaner != null) {
                    getSupportActionBar().setTitle("Cleaner: " + cleaner.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadTasksFromDatabase() {
        try {
            availableTasks = new ArrayList<>();
            List<Report> reports = reportDatabaseHelper.getAllReports();
            
            // If no reports exist, create sample tasks for demonstration
            if (reports.isEmpty()) {
                availableTasks = createSampleTasks();
            } else {
                // Convert reports to tasks
                for (Report report : reports) {
                    if (!"Resolved".equals(report.getStatus())) {
                        Task task = createTaskFromReport(report);
                        availableTasks.add(task);
                    }
                }
            }
            
            // If still no tasks, create some sample ones
            if (availableTasks.isEmpty()) {
                availableTasks = createSampleTasks();
            }
            
            setupTaskSelector();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error loading tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Fallback to sample tasks
            availableTasks = createSampleTasks();
            setupTaskSelector();
        }
    }

    private List<Task> createSampleTasks() {
        List<Task> sampleTasks = new ArrayList<>();
        
        String[] locations = {"Matara Fort Entrance", "Matara Bus Stand", "Matara Market Square", "Matara Beach Park", "Matara Clock Tower"};
        String[] descriptions = {"Bin cleaning and maintenance required", "Regular waste collection needed", "Bin overflow reported", "Weekly maintenance check", "Emergency cleanup needed"};
        String[] statuses = {"In Progress", "Pending", "In Progress", "Pending", "In Progress"};
        String[] distances = {"0.3 km away", "0.8 km away", "1.2 km away", "1.5 km away", "0.6 km away"};
        
        for (int i = 0; i < 5; i++) {
            String taskId = String.format("#T2024-%03d", 150 + i);
            Task task = new Task(taskId, locations[i], descriptions[i], statuses[i], distances[i]);
            task.setReportId("SAMPLE-" + (i + 1));
            sampleTasks.add(task);
        }
        
        return sampleTasks;
    }

    private Task createTaskFromReport(Report report) {
        String taskId = generateTaskId(report);
        String location = report.getLocation();
        String description = report.getDescription();
        String status = report.getStatus();
        String distance;
        if (userLocation != null && report.getLatitude() != 0 && report.getLongitude() != 0) {
            float[] results = new float[1];
            Location.distanceBetween(userLocation.getLatitude(), userLocation.getLongitude(), report.getLatitude(), report.getLongitude(), results);
            double km = results[0] / 1000.0;
            distance = String.format("%.1f km away", km);
        } else {
            distance = calculateDistance(report);
        }
        Task task = new Task(taskId, location, description, status, distance);
        task.setReportId(report.getReportId());
        return task;
    }

    private String generateTaskId(Report report) {
        if (report.getReportId() != null) {
            return "#T" + report.getReportId();
        } else {
            return "#T" + System.currentTimeMillis() % 10000;
        }
    }

    private String calculateDistance(Report report) {
        // Simulate distance calculation
        double distance = 0.3 + random.nextDouble() * 2.0; // 0.3-2.3 km
        return String.format("%.1f km away", distance);
    }

    private void setupTaskSelector() {
        // Setup RecyclerView
        taskAdapter = new TaskSelectorAdapter(this, availableTasks, this);
        taskSelectorRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        taskSelectorRecyclerView.setAdapter(taskAdapter);

        // Set initial task if available
        if (!availableTasks.isEmpty()) {
            onTaskSelected(availableTasks.get(0), 0);
        }
    }

    private void setupClickListeners() {
        btnSaveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDraft();
            }
        });
        btnCompleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTask();
            }
        });
        photoUploadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerOptions();
            }
        });
    }

    private void saveDraft() {
        if (selectedTask == null) {
            Toast.makeText(this, "Please select a task first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notes = editTextNotes.getText().toString().trim();
        boolean isFullyCleaned = radioFullyCleaned.isChecked();
        
        // Save draft logic here
        Toast.makeText(this, "Draft saved for task: " + selectedTask.getTaskId(), Toast.LENGTH_SHORT).show();
    }

    private void completeTask() {
        if (selectedTask == null) {
            Toast.makeText(this, "Please select a task first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notes = editTextNotes.getText().toString().trim();
        boolean isFullyCleaned = radioFullyCleaned.isChecked();
        
        try {
            // Update the report status in database if it's a real report
            if (selectedTask.getReportId() != null && !selectedTask.getReportId().startsWith("SAMPLE-")) {
                int updated = reportDatabaseHelper.updateReportStatus(selectedTask.getReportId(), "Resolved");
                if (updated > 0) {
                    Toast.makeText(this, "Task completed and report resolved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Task completed!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Task completed!", Toast.LENGTH_SHORT).show();
            }
            
            // Remove the completed task from the list
            availableTasks.remove(selectedTask);
            taskAdapter.notifyDataSetChanged();
            
            // Select next task if available
            if (!availableTasks.isEmpty()) {
                onTaskSelected(availableTasks.get(0), 0);
            } else {
                // No more tasks
                locationText.setText("No tasks available");
                taskDetailsText.setText("All tasks completed");
            }

            // Navigate to ViewTasksPage after completion
            Intent intent = new Intent(this, ViewTasksPage.class);
            intent.putExtra("email", userEmail);
            startActivity(intent);
            finish();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error completing task: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskSelected(Task task, int position) {
        selectedTask = task;
        
        // Update location card with selected task details
        locationText.setText(task.getLocation());
        taskDetailsText.setText(String.format("Task ID: %s • %s", task.getTaskId(), task.getDescription()));
        
        // Update UI based on task status
        updateUIForTaskStatus(task.getStatus());
    }

    private void updateUIForTaskStatus(String status) {
        // Update UI elements based on task status
        if ("In Progress".equals(status)) {
            btnCompleteTask.setEnabled(true);
            btnCompleteTask.setText("DONE TASK");
        } else if ("Pending".equals(status)) {
            btnCompleteTask.setEnabled(true);
            btnCompleteTask.setText("DONE TASK");
        } else {
            btnCompleteTask.setEnabled(false);
            btnCompleteTask.setText("Task Completed");
        }
    }

    private void showFullScreenPhoto(Uri uri) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_fullscreen_photo);
        ImageView imageView = dialog.findViewById(R.id.fullscreenImageView);
        imageView.setImageURI(uri);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        dialog.getWindow().setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        );
        imageView.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void requestUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                userLocation = location;
                // Reload tasks to update distances
                loadTasksFromDatabase();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestUserLocation();
            } else {
                Toast.makeText(this, "Location permission is required to show real distances.", Toast.LENGTH_LONG).show();
            }
        }
    }
} 