package edu.aku.hassannaqvi.tpvics_hhlisting_app.activities.other;

import static java.lang.Thread.sleep;
import static edu.aku.hassannaqvi.tpvics_hhlisting_app.CONSTANTS.LOGIN_SPLASH_FLAG;
import static edu.aku.hassannaqvi.tpvics_hhlisting_app.repository.SplashRepositoryKt.populatingSpinners;
import static edu.aku.hassannaqvi.tpvics_hhlisting_app.repository.UtilsKt.dbBackup;
import static edu.aku.hassannaqvi.tpvics_hhlisting_app.repository.UtilsKt.getPermissionsList;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.validatorcrawler.aliazaz.Validator;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.aku.hassannaqvi.tpvics_hhlisting_app.CONSTANTS;
import edu.aku.hassannaqvi.tpvics_hhlisting_app.R;
import edu.aku.hassannaqvi.tpvics_hhlisting_app.activities.sync.SyncActivity;
import edu.aku.hassannaqvi.tpvics_hhlisting_app.contracts.DistrictContract;
import edu.aku.hassannaqvi.tpvics_hhlisting_app.core.DatabaseHelper;
import edu.aku.hassannaqvi.tpvics_hhlisting_app.core.MainApp;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.coroutines.CoroutineContext;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "test1234:test1234", "testS12345:testS12345", "bar@example.com:world"
    };
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 3;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 4;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 5;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    protected static LocationManager locationManager;
    // UI references.
    @BindView(R.id.login_progress)
    ProgressBar mProgressView;
    @BindView(R.id.login_form)
    ScrollView mLoginFormView;
    @BindView(R.id.email)
    EditText mEmailView;
    @BindView(R.id.password)
    EditText mPasswordView;
    @BindView(R.id.txtinstalldate)
    TextView txtinstalldate;
    @BindView(R.id.email_sign_in_button)
    ImageButton mEmailSignInButton;
    @BindView(R.id.syncData)
    ImageButton syncData;
    @BindView(R.id.showPassword)
    ImageButton showPassword;
    @BindView(R.id.signup)
    Button signup;
    @BindView(R.id.testing)
    TextView testing;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    String DirectoryName;
    Location location;
    DatabaseHelper db;
    private UserLoginTask mAuthTask = null;
    private int clicks;
    ArrayAdapter<String> provinceAdapter;
    @BindView(R.id.spinnerProvince)
    Spinner spinnerProvince;
    @BindView(R.id.spinners)
    LinearLayout spinners;
    @BindView(R.id.spinnerDistrict)
    Spinner spinnerDistrict;

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        try {

            String packageName = getApplicationContext().getPackageName();

            long installedOn = this
                    .getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .lastUpdateTime;
            MainApp.versionCode = this
                    .getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .versionCode;
            MainApp.versionName = this
                    .getPackageManager()
                    .getPackageInfo(packageName, 0)
                    .versionName;
            txtinstalldate.setText("Ver. " + MainApp.versionName + "." + MainApp.versionCode + " \r\n( Last Updated: " + new SimpleDateFormat("dd MMM. yyyy").format(new Date(installedOn)) + " )");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Set up the login form.
        mEmailView = findViewById(R.id.email);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkAndRequestPermissions()) {
                //populateAutoComplete();
                loadIMEI();
            }
        } else {
            //    populateAutoComplete();
            loadIMEI();
        }

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                attemptLogin();

            }
        });

        setListeners();

//        DB backup
        dbBackup(this);

        db = new DatabaseHelper(this);

//        Testing visibility
        if (Integer.parseInt(MainApp.versionName.split("\\.")[0]) > 0) {
            testing.setVisibility(View.GONE);
        } else {
            testing.setVisibility(View.VISIBLE);
        }

    }

    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has not been granted.
                requestReadPhoneStatePermission();
            } else {
                doPermissionGrantedStuffs();
            }
        } else {
            doPermissionGrantedStuffs();
        }
    }

    private void setListeners() {
        provinceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, SplashscreenActivity.provinces);
        spinnerProvince.setAdapter(provinceAdapter);
        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                List<String> districts = new ArrayList<>(Collections.singletonList("...."));
                for (Map.Entry<String, Pair<String, DistrictContract>> entry : SplashscreenActivity.districtsMap.entrySet()) {
                    if (entry.getValue().getFirst().equals(spinnerProvince.getSelectedItem().toString()))
                        districts.add(entry.getKey());
                }
                spinnerDistrict.setAdapter(new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1
                        , districts));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                MainApp.DIST_ID = Objects.requireNonNull(SplashscreenActivity.districtsMap.get(spinnerDistrict.getSelectedItem().toString())).getSecond().getDist_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("permission read phone state rationale")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(LoginActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    @OnClick(R.id.syncData)
    void onSyncDataClick() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            startActivity(new Intent(this, SyncActivity.class));
        } else {
            Toast.makeText(this, "No network connection available.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkAndRequestPermissions() {
        if (!getPermissionsList(this).isEmpty()) {
            ActivityCompat.requestPermissions(this, getPermissionsList(this).toArray(new String[getPermissionsList(this).size()]),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            return false;
        }

        return true;
    }

    public void requestLocationUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            switch (permissions[i]) {
              /*  case Manifest.permission.READ_CONTACTS:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        populateAutoComplete();
                    }
                    break;*/
                case Manifest.permission.GET_ACCOUNTS:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                case Manifest.permission.ACCESS_COARSE_LOCATION:
                    break;
                case Manifest.permission.READ_PHONE_STATE:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        doPermissionGrantedStuffs();
                    }
                    break;
                case Manifest.permission.ACCESS_FINE_LOCATION:
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        requestLocationUpdate();
                    }
                    break;
            }

        }
    }

/*    private void populateAutoComplete() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                checkAndRequestPermissions();
            } else {
                //Permission already granted
                getLoaderManager().initLoader(0, null, this);
            }
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }*/


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } /*else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }*/

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if (!Validator.emptyCheckingContainer(this, spinners))
                return;
            showProgress(true);
            mAuthTask = new UserLoginTask(this, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 7;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

/*    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }*/

    @OnClick(R.id.showPassword)
    void onShowPasswordClick() {
        //TODO implement
        AnimatedVectorDrawable drawable;

        if (mPasswordView.getTransformationMethod() == null) {

            drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_anim_close);
            showPassword.setImageDrawable(drawable);
            drawable.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
                }
            }, 1000);


        } else {

            drawable = (AnimatedVectorDrawable) getResources().getDrawable(R.drawable.avd_anim);
            showPassword.setImageDrawable(drawable);
            drawable.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPasswordView.setTransformationMethod(null);
                }
            }, 1000);

        }
    }

    public void showCredits(View view) {
        if (clicks < 5) {
            clicks++;
            Toast.makeText(this, String.valueOf(clicks), Toast.LENGTH_SHORT).show();
        } else {
            clicks = 0;
            Toast.makeText(this, "TEAM CREDITS: " +
                            "\r\nHassan Naqvi, " +
                            "Ali Azaz, " +
                            "Abdul Sajid, " +
                            "Farooqui",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    protected void showCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            //Toast.makeText(getApplicationContext(), message,
            //Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else return isNewer && !isSignificantlyLessAccurate && isFromSameProvider;
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().getBooleanExtra(LOGIN_SPLASH_FLAG, false))
            callingCoroutine();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSTANTS.LOGIN_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                callingCoroutine();
            }
        }
    }

    private void callingCoroutine() {
        //To call coroutine here
        populatingSpinners(getApplicationContext(), provinceAdapter, new SplashscreenActivity.Continuation<Unit>() {
            @Override
            public void resume(Unit value) {

            }

            @Override
            public void resumeWithException(@NotNull Throwable exception) {

            }

            @NotNull
            @Override
            public CoroutineContext getContext() {
                return null;
            }
        });
    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {

            SharedPreferences sharedPref = getSharedPreferences("GPSCoordinates", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            String dt = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(sharedPref.getString("Time", "0"))).toString();

            Location bestLocation = new Location("storedProvider");
            bestLocation.setAccuracy(Float.parseFloat(sharedPref.getString("Accuracy", "0")));
            bestLocation.setTime(Long.parseLong(sharedPref.getString(dt, "0")));
//                bestLocation.setTime(Long.parseLong(dt));
            bestLocation.setLatitude(Float.parseFloat(sharedPref.getString("Latitude", "0")));
            bestLocation.setLongitude(Float.parseFloat(sharedPref.getString("Longitude", "0")));

            if (isBetterLocation(location, bestLocation)) {
                editor.putString("Longitude", String.valueOf(location.getLongitude()));
                editor.putString("Latitude", String.valueOf(location.getLatitude()));
                editor.putString("Accuracy", String.valueOf(location.getAccuracy()));
                editor.putString("Time", String.valueOf(location.getTime()));
                editor.putString("Altitude", String.valueOf(location.getAltitude()));
//                    editor.putString("Time", DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(String.valueOf(location.getTime()))).toString());

//                String date = DateFormat.format("dd-MM-yyyy HH:mm", Long.parseLong(String.valueOf(location.getTime()))).toString();
//                Toast.makeText(getApplicationContext(),
//                        "GPS Commit! LAT: " + String.valueOf(location.getLongitude()) +
//                                " LNG: " + String.valueOf(location.getLatitude()) +
//                                " Accuracy: " + String.valueOf(location.getAccuracy()) +
//                                " Time: " + date,
//                        Toast.LENGTH_SHORT).show();

                editor.apply();
            }


            Map<String, ?> allEntries = sharedPref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                Log.d("Map", entry.getKey() + ": " + entry.getValue().toString());
            }
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            showCurrentLocation();
        }

        public void onProviderDisabled(String s) {

        }

        public void onProviderEnabled(String s) {

        }

    }

    // New Function in LoginActivity
    public String computeHash(String passwordInput) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.reset();
        byte[] byteData = digest.digest(passwordInput.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte byteDatum : byteData) {
            sb.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context mContext;

        UserLoginTask(Context context, String email, String password) {
            mEmail = email;
            mPassword = password;
            mContext = context;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            String[] DUMMY_CREDENTIALS = new String[]{
                    "test1234:test1234", "testS12345:testS12345", "bar@example.com:world"
            };

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (!success) return;
            LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            assert mlocManager != null;
            if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                DatabaseHelper db = new DatabaseHelper(LoginActivity.this);
                try {
                    if ((mEmail.equals("dmu@aku") && mPassword.equals("aku?dmu")) ||
                            (mEmail.equals("guest@aku") && mPassword.equals("aku1234")) || db.Login(mEmail, computeHash(mPassword))
                            || (mEmail.equals("test1234") && mPassword.equals("test1234"))) {
                        MainApp.userEmail = mEmail;
                        MainApp.admin = mEmail.contains("@");
                        Intent iLogin = new Intent(LoginActivity.this, MainActivity.class);
                        finish();
                        startActivity(iLogin);

                    } else {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                        Toast.makeText(LoginActivity.this, mEmail + " " + mPassword, Toast.LENGTH_SHORT).show();
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }


            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        LoginActivity.this);
                alertDialogBuilder
                        .setMessage("GPS is disabled in your device. Enable it?")
                        .setCancelable(false)
                        .setPositiveButton("Enable GPS",
                                (dialog, id) -> {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                });
                alertDialogBuilder.setNegativeButton("Cancel",
                        (dialog, id) -> dialog.cancel());
                AlertDialog alert = alertDialogBuilder.create();
                alert.show();

            }

        }


        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void doPermissionGrantedStuffs() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        MainApp.IMEI = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        MainApp.IMEI = getDeviceId(this);
    }

}

