package com.shimnssso.headonenglish.drive;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.sheets.v4.Sheets;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import timber.log.Timber;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;
    private final Sheets mSheetService;

    public DriveServiceHelper(Drive driveService, Sheets sheetService) {
        mDriveService = driveService;
        mSheetService = sheetService;
    }

    public Task<String> check(String title) {
        return Tasks.call(mExecutor, () -> {
            Drive.Files.List request = mDriveService.files().list()
                    .setPageSize(10)
                    // Available Query parameters here:
                    //https://developers.google.com/drive/v3/web/search-parameters
                    .setQ("mimeType = 'application/vnd.google-apps.spreadsheet' and name contains '" + title + "' and trashed = false")
                    .setFields("nextPageToken, files(id, name)");

            FileList result = request.execute();

            List<File> files = result.getFiles();
            String spreadsheetId = null;
            if (files != null) {
                for (File file : files) {
                    Timber.e(file.toString());
                    // More code here to discriminate best result, if you want
                    spreadsheetId = file.getId();
                    Timber.e("spreadsheetId: %s", spreadsheetId);
                }
            }
            return spreadsheetId;
        });
    }

    /**
     * Returns an {@link Intent} for opening the Storage Access Framework file picker.
     */
    public Intent createFilePickerIntent() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.google-apps.spreadsheet");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        return intent;
    }

    /**
     * Opens the file at the {@code uri} returned by a Storage Access Framework {@link Intent}
     * created by {@link #createFilePickerIntent()} using the given {@code contentResolver}.
     */
    public Task<String> openFileUsingStorageAccessFramework(
            ContentResolver contentResolver, Uri uri) {
        return Tasks.call(mExecutor, () -> {
            // Retrieve the document's display name from its metadata.
            String name;
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    name = cursor.getString(nameIndex);
                } else {
                    throw new IOException("Empty cursor returned for file.");
                }
            }
            return name;
        });
    }
}
