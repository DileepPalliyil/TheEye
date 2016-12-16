package in.thefleet.cropme;

import android.animation.Animator;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


import in.thefleet.croperino.CropImage;
import in.thefleet.croperino.CropUtil;
import in.thefleet.croperino.Croperino;
import in.thefleet.croperino.CroperinoConfig;
import in.thefleet.croperino.CroperinoFileUtil;
import in.thefleet.croperino.RotateBitmap;
import in.thefleet.cropme.model.Fleet;
import in.thefleet.cropme.online.isOnline;
import in.thefleet.cropme.phone.TelephonyInfo;

import static android.R.attr.radius;
import static android.R.id.list;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ImageButton btnSummon;
    private ImageView ivMain;
    private ImageButton btnUpload;
    private ImageButton btnRefresh;
    Uri fileUri =null;
    Uri photoUri = null;
    public static final String TAG = "MainActivity";
    private String UPLOAD_URL ="https://thefleet.in/fleetMasterService.svc/UploadImage";
    public static final String FLEETS_BASE_URL =
            "https://thefleet.in/Fleetmasterservice.svc/getFleets/";
    public String fleetUrl = null;
    public String headerUrl = null;
    String imsiSIM;
    List<Fleet> fleetList;
    FleetAdapter adapter;
    private CursorAdapter cursorAdapter;
    private static final int LOADER1 = 1;
    Spinner spinner;
    Spinner spinner2;
    ProgressBar pb;
    TextView uploadResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        btnSummon = (ImageButton) findViewById(R.id.btn_click);
        ivMain = (ImageView) findViewById(R.id.iv_main);
        btnUpload = (ImageButton) findViewById(R.id.upload);
        btnRefresh = (ImageButton) findViewById(R.id.refresh);
        pb = (ProgressBar) findViewById(R.id.progressBar1);
        uploadResult = (TextView) findViewById(R.id.tv_result);
        uploadResult.setText(null);
        pb.setVisibility(View.INVISIBLE);

        new CroperinoConfig("FLT_" + System.currentTimeMillis() + ".jpg", "/TheFleet/Pictures", "/sdcard/TheFleet/Pictures");
        CroperinoFileUtil.verifyStoragePermissions(MainActivity.this);
        CroperinoFileUtil.verifyCameraPermissions(MainActivity.this);
        CroperinoFileUtil.verifyInternetPermissions(MainActivity.this);
        CroperinoFileUtil.verifyPhoneStatePermissions(MainActivity.this);
        CroperinoFileUtil.setupDirectory(MainActivity.this);

        if (savedInstanceState != null){
             fileUri = savedInstanceState.getParcelable("uri");
            if (fileUri!=null) {
             ivMain.setImageURI(fileUri);
             btnUpload.setEnabled(true);
            }
        }

        btnSummon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadResult.setText(null);
                ivMain.setImageURI(null);
                if (btnUpload.isEnabled()) {
                    btnUpload.setEnabled(false);
                }
                Croperino.prepareChooser(MainActivity.this, "Capture photo...", ContextCompat.getColor(MainActivity.this, android.R.color.background_dark));
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Globals g = Globals.getInstance();
                Log.d(TAG, "In btn upload");
                if (g.getfleetSelected()!=null && g.getImageTyp() != null && fileUri!=null) {
                    if (g.getImageTyp().equals("M")) {
                        Bitmap bitmap = decodeSampledBitmapFromFile(fileUri.getPath(), 150, 150);
                        String encodedImage = getBase64ImageString(bitmap);
                        Log.d(TAG, encodedImage);
                        uploadImageToServer(encodedImage);
                    }else if (g.getImageTyp().equals("I")) {
                        Bitmap bitmap = decodeSampledBitmapFromFile(fileUri.getPath(), 300, 400);
                        String encodedImage = getBase64ImageString(bitmap);
                        Log.d(TAG, encodedImage);
                        uploadImageToServer(encodedImage);
                    }else if (g.getImageTyp().equals("F")) {
                        Bitmap bitmap = decodeSampledBitmapFromFile(fileUri.getPath(), 300, 400);
                        String encodedImage = getBase64ImageString(bitmap);
                        Log.d(TAG, encodedImage);
                        uploadImageToServer(encodedImage);
                    }else if (g.getImageTyp().equals("P")) {
                        Bitmap bitmap = decodeSampledBitmapFromFile(fileUri.getPath(), 300, 400);
                        String encodedImage = getBase64ImageString(bitmap);
                        Log.d(TAG, encodedImage);
                        uploadImageToServer(encodedImage);
                    }

                }else {
                    Toast.makeText(getApplicationContext(),
                            "Please select Vehicle/Image Type.", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG,"Refresh button pressed");
                getFleetData();
            }
        });

        getLoaderManager().initLoader(LOADER1, null, this);

        cursorAdapter = new FleetAdapter(this,null,0);
        spinner = (Spinner) findViewById(R.id.spinner1);

        spinner.setAdapter(cursorAdapter);

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.Image_Type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Globals g = Globals.getInstance();
                TextView selectedFleet = (TextView)findViewById(R.id.tvFleetId);
                g.setfleetSelected(selectedFleet.getText().toString());
                Log.d(TAG,"Selected fleet :"+selectedFleet.getText().toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Globals g = Globals.getInstance();

                if ( spinner2.getSelectedItem().toString().equals("Insurance")){
                    g.setImageTyp("I");
                }else if (spinner2.getSelectedItem().toString().equals("Vehicle")){
                    g.setImageTyp("M");
                }else if (spinner2.getSelectedItem().toString().equals("Fitness")){
                    g.setImageTyp("F");
                }else if (spinner2.getSelectedItem().toString().equals("Permit")) {
                    g.setImageTyp("P");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Globals g = Globals.getInstance();

        switch (requestCode) {
            case CroperinoConfig.REQUEST_TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    if (g.getImageTyp().equals("M")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 1, 1, 0, 0);
                    }else if (g.getImageTyp().equals("I")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 3, 4, 0, 0);
                    }else if (g.getImageTyp().equals("F")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 1, 1, 0, 0);
                    }else if (g.getImageTyp().equals("P")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 3, 4, 0, 0);
                    }
                }
                break;
            case CroperinoConfig.REQUEST_PICK_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    CroperinoFileUtil.newGalleryFile(data, MainActivity.this);
                    if (g.getImageTyp().equals("M")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 1, 1, 0, 0);
                    }else if (g.getImageTyp().equals("I")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 3, 4, 0, 0);
                    }else if (g.getImageTyp().equals("F")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 1, 1, 0, 0);
                    }else if (g.getImageTyp().equals("P")) {
                        Croperino.runCropImage(CroperinoFileUtil.getmFileTemp(), MainActivity.this, false, 3, 4, 0, 0);
                    }
                }
                break;
            case CroperinoConfig.REQUEST_CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    ivMain.setImageURI(null);
                    fileUri = Uri.fromFile(CroperinoFileUtil.getmFileTemp());
                    ivMain.setImageURI(fileUri);
                    btnUpload.setEnabled(true);
                    btnUpload.setImageResource(R.drawable.ic_file_export_white_48dp);
                    //Do saving / uploading of photo method here.
                }
                break;
            default:
                break;
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public String getBase64ImageString(Bitmap photo) {
        String imgString;
        if(photo != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            byte[] profileImage = outputStream.toByteArray();

            imgString = Base64.encodeToString(profileImage,
                    Base64.NO_WRAP);
        }else{
            imgString = "";
        }

        return imgString;
    }

    private void uploadImageToServer(final String encodedImage) {
      if (isOnline.isNetworkConnected(this)) {
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            boolean isSIMReady = telephonyInfo.isSIM1Ready();

       if (isSIMReady) {
           SharedPreferences sv = PreferenceManager.getDefaultSharedPreferences(this);
           String simValue = sv.getString("simValue", "1");

        if (simValue.equals("1")) {
            imsiSIM = telephonyInfo.getImsiSIM1();
        } else if (simValue.equals("2")) {
            imsiSIM = telephonyInfo.getImsiSIM2();
        } else {
            imsiSIM = "357327070825555";
        }

        Globals g = Globals.getInstance();

        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        String fillingJson = "{'IMEI_No':" + "'" + imsiSIM + "'" + ","
                + "'Fleet_ID':" + "'" + g.getfleetSelected() + "'" + ","
                + "'ImageBytes':" + "'" + encodedImage + "'" + ","
                + "'FileType':" + "'" + g.getImageTyp() + "'"
                + "}";

        PostImage post = new PostImage(this);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            String response = null;
            try {
                response = post.post(UPLOAD_URL, fillingJson);
                loading.show();
                btnUpload.setImageResource(R.drawable.ic_file_export_grey600_48dp);
                btnUpload.setEnabled(false);
                Log.d(TAG, "Upload url is: " + UPLOAD_URL);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Exception " + e.getMessage());
            }


            if (response != null ) {

                if (response.equals("2")) {
                    btnUpload.setEnabled(true);
                    btnUpload.setImageResource(R.drawable.ic_file_export_white_48dp);

                    loading.dismiss();
                    uploadResult.setText("Vehicle data not available in server.");
                //    Log.d(TAG, "Vehicle data not available in server.");
                } else if (response.equals("1")) {
                    btnUpload.setEnabled(true);
                    loading.dismiss();
                    btnUpload.setImageResource(R.drawable.ic_file_export_white_48dp);

                    //   Log.d(TAG, "Server response error on post 1.");
                } else if (response.equals("0")) {
                    loading.dismiss();
                    btnUpload.setImageResource(R.drawable.ic_file_export_grey600_48dp);
                    btnUpload.setEnabled(false);
                  //  Log.d(TAG, "Successfully posted image");
                    uploadResult.setText("Successfully posted image");
                } else if (response.equals("3")) {
                    btnUpload.setEnabled(true);
                    loading.dismiss();
                    btnUpload.setImageResource(R.drawable.ic_file_export_white_48dp);
                    Toast.makeText(getApplicationContext(),
                            "Upload format error...", Toast.LENGTH_SHORT)
                            .show();
                    uploadResult.setText("Upload format error");

                }  else {
                    btnUpload.setEnabled(true);
                    loading.dismiss();
                  //  Log.d(TAG, "Upload format error."+response.toString());
                    uploadResult.setText("Posting Error");
                }
            } else {

                Log.d(TAG, "Server response error on post.");
                loading.dismiss();
            }
         }
        } else {
            Toast.makeText(this, "Sim is not ready or no access.Try with different sim", Toast.LENGTH_LONG).show();
        }

        } else {
            Toast.makeText(getApplicationContext(),
                    "Network isn't available", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void getFleetData() {

        if (isOnline.isNetworkConnected(this)) {

            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
            boolean isSIMReady = telephonyInfo.isSIM1Ready();

            //Comment if for running in emulator
            if (isSIMReady) {
                SharedPreferences sv = PreferenceManager.getDefaultSharedPreferences(this);
                String simValue = sv.getString("simValue", "1");
                String fleetTypValue = sv.getString("fleetTypValue", "all");

                if (simValue.equals("1")) {
                    imsiSIM = telephonyInfo.getImsiSIM1();
                    fleetUrl = MainActivity.FLEETS_BASE_URL + imsiSIM + "/" + fleetTypValue + "/" + "all";
                    requestData(fleetUrl);

                } else if (simValue.equals("2")) {
                    imsiSIM = telephonyInfo.getImsiSIM2();
                    fleetUrl = MainActivity.FLEETS_BASE_URL + imsiSIM + "/" + fleetTypValue + "/" + "all";
                    requestData(fleetUrl);
                } else {
                    imsiSIM = "357327070825555";
                    fleetUrl = MainActivity.FLEETS_BASE_URL + "357327070825555" + "/" + fleetTypValue + "/" + "all";
                    requestData(fleetUrl);

                }
                //Comment below else for running the app on simulator
            } else {
                Toast.makeText(this, "Sim is not ready or no access.Try with different sim", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Network isn't available", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    //Volley request
    private void requestData(String uri) {
        Log.d(TAG,"Fleeturl:"+uri);
        StringRequest request = new StringRequest(uri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        fleetList = FleetJSONParser.parseFeed(response);
                        insertFleetDataToDb(fleetList);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getApplicationContext(),
                                    "Network time out error in request data", Toast.LENGTH_SHORT)
                                    .show();
                            pb.setVisibility(View.INVISIBLE);
                        } else if (error instanceof AuthFailureError) {
                            Toast.makeText(getApplicationContext(),
                                    "Authentication failure in request data", Toast.LENGTH_SHORT)
                                    .show();
                            pb.setVisibility(View.INVISIBLE);
                        } else if (error instanceof ServerError) {
                            Toast.makeText(getApplicationContext(),
                                    "Server error", Toast.LENGTH_SHORT)
                                    .show();
                            pb.setVisibility(View.INVISIBLE);
                        } else if (error instanceof NetworkError) {
                            Toast.makeText(getApplicationContext(),
                                    "Network error in request data", Toast.LENGTH_SHORT)
                                    .show();
                            pb.setVisibility(View.INVISIBLE);
                        } else if (error instanceof ParseError) {
                            Toast.makeText(getApplicationContext(),
                                    "Parse error in request data", Toast.LENGTH_SHORT)
                                    .show();
                            pb.setVisibility(View.INVISIBLE);
                        }  else   {
                            Toast.makeText(getApplicationContext(),
                                    "Error in parsing", Toast.LENGTH_SHORT)
                                    .show();
                            pb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        int socketTimeout = 600;// seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        request.setRetryPolicy(policy);
        queue.add(request);
        pb.setVisibility(View.VISIBLE);

    }

    private void insertFleetDataToDb(List<Fleet> fleetList) {

        if (this.fleetList.size()>0) {

            getContentResolver().delete(
                    FleetsDataSource.CONTENT_URI, null, null);

            ContentValues[] fvaluesArr = new ContentValues[fleetList.size()];
            int i = 0;
            for (Fleet fleet : this.fleetList) {

                ContentValues fvalues = new ContentValues();
                fvalues.put(FleetsDBOpenHelper.FLEETS_KEY, fleet.getFleetID());
                fvalues.put(FleetsDBOpenHelper.FLEETS_REGNO, fleet.getRegNo());
               // fvalues.put(FleetsDBOpenHelper.FLEETS_MODEL, fleet.getModelName());
                fvalues.put(FleetsDBOpenHelper.FLEETS_CREATED, getTodaysDate());

                fvaluesArr[i++] = fvalues;
            }
            getContentResolver().bulkInsert(FleetsDataSource.CONTENT_URI, fvaluesArr);
            getLoaderManager().restartLoader(LOADER1, null, this);

            pb.setVisibility(View.INVISIBLE);
            }
        }

    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, FleetPreferenceActivity.class);
                startActivity(i);
                return true;
            default: return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG,"In on Create Loader");
        return new CursorLoader(this,FleetsDataSource.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"In on Load Finished");
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (fileUri !=null) {
            outState.putParcelable("uri", fileUri);
        }
    }
}
