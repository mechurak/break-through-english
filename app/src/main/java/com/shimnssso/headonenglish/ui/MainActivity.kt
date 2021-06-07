package com.shimnssso.headonenglish.ui

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.googlesheet.SheetHelper
import com.shimnssso.headonenglish.room.DatabaseLecture
import com.shimnssso.headonenglish.ui.daylist.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private val viewmodel: HomeViewModel by lazy {
        val activity = requireNotNull(this) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(activity).get(HomeViewModel::class.java)
    }

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            requestSignIn()
        } else {
            viewmodel.setLogIn(false)
        }

        setContent {
            HeadOnEnglishApp()
        }
    }

    private var currentLecture: DatabaseLecture? = null
    fun launchAudioChooser(lecture: DatabaseLecture) {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {

                    // Here after all the permission are granted launch the gallery to select and image.
                    if (report!!.areAllPermissionsGranted()) {
                        currentLecture = lecture
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
                        audioChooserLauncher.launch(intent)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }
            }).onSameThread()
            .check()
    }

    private val audioChooserLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data // Handle the Intent //do stuff here
                Timber.i("intent: %s", intent)
                val uri = intent!!.data
                Timber.i("uri: %s", uri)

                currentLecture?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        val newLecture = it.copy(localUrl = uri.toString())
                        Timber.i("newLecture: %s", newLecture)
                        Graph.database.lectureDao.updateLecture(newLecture)
                    }
                }
            }
        }

    private val signInResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultData = result.data // Handle the Intent //do stuff here
                Timber.i("resultData: %s", resultData)
                resultData?.let {
                    handleSignInResult(resultData)
                }
            }
        }

    private val selectDocLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data // Handle the Intent //do stuff here
                Timber.i("intent: %s", intent)
                val uri = intent!!.data
                Timber.i("uri: %s", uri)
                uri?.let {
                    viewmodel.getSheetIdAndShowDialog(contentResolver, uri)
                }
            }
        }

    /**
     * A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
     */
    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    /**
     * Opens the Storage Access Framework file picker using [.REQUEST_CODE_OPEN_DOCUMENT].
     */
    fun openFilePicker() {
        Timber.d("Opening file picker.")
        val pickerIntent = SheetHelper.getFilePickerIntent()
        selectDocLauncher.launch(pickerIntent)
    }

    fun requestSignIn() {
        Timber.i("Requesting sign-in")
        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_METADATA_READONLY), Scope(SheetsScopes.SPREADSHEETS_READONLY))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)
        GoogleSignIn.getLastSignedInAccount(this)
        signInResultLauncher.launch(client.signInIntent)
    }

    fun requestSignOut() {
        Timber.i("Requesting sign-out")

        // val client = GoogleSignIn.getLastSignedInAccount(this)

        val signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_METADATA_READONLY), Scope(SheetsScopes.SPREADSHEETS_READONLY))
            .build()
        val client = GoogleSignIn.getClient(this, signInOptions)
        client.signOut().addOnSuccessListener {
            Timber.i("signOut(). succeeded")
            viewmodel.setLogIn(false)
        }.addOnFailureListener {
            Timber.i("signOut(). failed")
        }
    }

    private fun handleSignInResult(result: Intent) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
            .addOnSuccessListener { googleAccount: GoogleSignInAccount ->
                Timber.i("Signed in as %s", googleAccount.email)
                viewmodel.setLogIn(true)

                // Use the authenticated account to sign in to the Drive service.
                val credential = GoogleAccountCredential.usingOAuth2(
                    this, listOf(DriveScopes.DRIVE_METADATA_READONLY, SheetsScopes.SPREADSHEETS_READONLY)
                )
                credential.selectedAccount = googleAccount.account
                val googleDriveService = Drive.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential
                )
                    .setApplicationName("Drive API Migration")
                    .build()

                val googleSheetService = Sheets.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    GsonFactory(),
                    credential
                )
                    .setApplicationName("Drive API Migration")
                    .build()

                SheetHelper.init(googleDriveService, googleSheetService)

                viewmodel.refresh(true)
            }
            .addOnFailureListener { exception: Exception? ->
                Timber.e(exception, "Unable to sign in.")
                viewmodel.setLogIn(false)
            }
    }
}