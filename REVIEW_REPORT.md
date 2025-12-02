# App Review Report: LeoConnect

## Overview
This report documents the findings from a code review of the LeoConnect application. The review focused on identifying broken features, non-functional components, and incomplete implementations.

## Broken/Non-Functional Features

### 1. Authentication
*   **Login Screen**:
    *   **Terms of Service & Privacy Policy**: The text "By continuing, you agree to our Terms of Service and Privacy Policy" is static and non-clickable. Users cannot access these documents from the login screen.

### 2. Home Tab
*   **Search**:
    *   The search functionality is currently mocked. `SearchScreenModel` uses hardcoded data for posts, clubs, and districts. It does not query the backend or filter real data.

### 3. Clubs Tab
*   **Follow Button**:
    *   In the club list (`ClubsTab.kt`), the "Follow" button code is commented out, making it impossible to follow clubs directly from the list.

### 4. Club Details
*   **Action Buttons**:
    *   **Follow Button**: The `onClick` handler is empty (`{ /* Follow */ }`).
    *   **Members Button**: The `onClick` handler is empty (`{ /* Message / View Members */ }`).
    *   **Admin Chip**: The `onClick` handler is empty (`{ /* Open admin menu */ }`).

### 5. Profile Tab
*   **Impact Tracker**:
    *   Displays hardcoded values ("1,200 Service Hours", "45 Projects", "8,000 Members") instead of real user data.
*   **Menu Items**:
    *   **Verify Leo ID**: `onClick` is empty.
    *   **My Leo Journey**: `onClick` is empty.
    *   **Following**: `onClick` is empty.
    *   **Invite Friends**: `onClick` is empty.
*   **Follow Logic**:
    *   `UserProfileScreenModel.toggleFollow()` is empty (`// TODO: Implement follow logic`).

### 6. User Profile (Other Users)
*   **Interactions**:
    *   **Message Button**: `onClick` is empty.
    *   **Follow Button**: Calls the empty `toggleFollow()` method.
    *   **Report/Block**: The top bar menu action is empty.
    *   **"Follows You" Chip**: `onClick` is empty.

### 7. Settings
*   **Edit Profile**:
    *   **Save Button**: Does nothing (`// TODO: Save profile changes`).
    *   **Change Photo**: Floating action button does nothing.
*   **Notifications**:
    *   Settings UI exists but changes are only stored in local state variables (`remember { mutableStateOf(...) }`). They are not persisted and do not affect app behavior.
*   **Privacy & Security**:
    *   Settings are local state only.
    *   **Clear All Local Data**: Button is empty.
    *   **Delete Account**: Button is empty.
*   **Appearance**:
    *   **Dark Mode / System Theme**: Local state only.
    *   **Button Preview**: Does nothing.
*   **Help & Support**:
    *   **Contact Support**: Button is empty.
    *   **Report a Bug**: Button is empty.
    *   **Send Feedback**: Button is empty.
    *   **User Guide**: Button is empty.
    *   **Video Tutorials**: Button is empty.
    *   **Visit Website**: Button is empty.
*   **Language**:
    *   Selection dialog works visually but does not persist the selection or change the app's locale.
*   **Data Usage**:
    *   **Clear Cache**: Button is empty.

### 8. District Details
*   **Data Fetching**:
    *   While the UI and ViewModel seem implemented, the data fetching relies on the `LeoRepository`. Verification is needed to ensure the backend endpoints (`/districts`, etc.) are operational and returning data in the expected format.

## Summary
The application has a well-structured UI and navigation flow, but many interactive features are currently placeholders. The Settings section is almost entirely cosmetic, and key social features like Following and Search are not fully implemented.
