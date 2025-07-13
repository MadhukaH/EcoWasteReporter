# EcoWasteReporter

An Android application for reporting waste and managing environmental cleanup tasks.

## Features

### Citizen Features
- **User Registration & Login**: Secure authentication system for citizens
- **Waste Reporting**: Report waste locations with photos and descriptions
- **Profile Management**: Update profile information including profile photos
- **View Reports**: Track submitted waste reports
- **Recycling Tips**: Access educational content about recycling
- **Nearby Bins**: Find nearby waste bins and recycling centers

### Cleaner Features
- **Task Management**: View and complete assigned cleanup tasks
- **Performance Tracking**: Monitor cleanup performance and statistics
- **Route Optimization**: Efficient routing for cleanup tasks
- **Bin Status Updates**: Update bin status and maintenance information

## Profile Photo Functionality

The app now supports profile photo management for citizens:

### How to Update Profile Photo:
1. Navigate to the People Home Page
2. Tap on the profile icon (circular image)
3. In the Profile Page, tap on the profile image again
4. Select a photo from your device gallery
5. The photo will be automatically saved to the database and displayed

### Technical Implementation:
- **Database Storage**: Profile photo paths are stored in the SQLite database
- **Internal Storage**: Photos are saved to the app's internal storage
- **Automatic Updates**: Profile photos are automatically updated across the app
- **Persistence**: Photos persist between app sessions

### Database Schema:
The Citizen table includes a new `profile_photo` column that stores the file path to the saved profile image.

## Installation

1. Clone the repository
2. Open the project in Android Studio
3. Configure your Google Maps API key in `gradle.properties`
4. Build and run the application

## Permissions

The app requires the following permissions:
- Camera access for taking photos
- Location access for finding nearby bins
- Storage access for saving profile photos
- Internet access for maps and data synchronization

## Dependencies

- AndroidX Core and AppCompat
- Material Design Components
- Google Play Services (Maps & Location)
- CircleImageView for profile images
- MPAndroidChart for performance visualization