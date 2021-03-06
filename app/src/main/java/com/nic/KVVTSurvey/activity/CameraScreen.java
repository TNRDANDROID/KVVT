package com.nic.KVVTSurvey.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.KVVTSurvey.R;
import com.nic.KVVTSurvey.api.Api;
import com.nic.KVVTSurvey.api.ServerResponse;
import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.dataBase.DBHelper;
import com.nic.KVVTSurvey.dataBase.dbData;
import com.nic.KVVTSurvey.databinding.CameraScreenBinding;
import com.nic.KVVTSurvey.model.KVVTSurvey;
import com.nic.KVVTSurvey.session.PrefManager;
import com.nic.KVVTSurvey.support.MyLocationListener;
import com.nic.KVVTSurvey.utils.CameraUtils;
import com.nic.KVVTSurvey.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class CameraScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    public static final int MEDIA_TYPE_IMAGE = 1;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String imageStoragePath;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double offlatTextValue, offlongTextValue;
    private PrefManager prefManager;
    private CameraScreenBinding cameraScreenBinding;


    private List<View> viewArrayList = new ArrayList<>();


    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private dbData dbData = new dbData(this);
    String kvvt_id;
    String type_of_photo="";

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraScreenBinding = DataBindingUtil.setContentView(this, R.layout.camera_screen);
        cameraScreenBinding.setActivity(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }


        intializeUI();
    }

    public void intializeUI() {
        prefManager = new PrefManager(this);

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        kvvt_id = getIntent().getStringExtra("lastInsertedID");
        type_of_photo = getIntent().getStringExtra(AppConstant.TYPE_OF_PHOTO);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


    public void saveActivityImage() {
        dbData.open();

        long id = 0; String whereClause = "";String[] whereArgs = null;

        Log.d("type_of_photo",type_of_photo);


        byte[] imageInByte = new byte[0];
        String image_str = "";
        try {
            Bitmap bitmap = ((BitmapDrawable) cameraScreenBinding.imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            imageInByte = baos.toByteArray();
            image_str = Base64.encodeToString(imageInByte, Base64.DEFAULT);

            ContentValues values = new ContentValues();

            values.put(AppConstant.KVVT_ID, kvvt_id);
            values.put(AppConstant.TYPE_OF_PHOTO, getIntent().getStringExtra(AppConstant.TYPE_OF_PHOTO));
            values.put(AppConstant.KEY_LATITUDE, offlatTextValue.toString());
            values.put(AppConstant.KEY_LONGITUDE, offlongTextValue.toString());
            values.put(AppConstant.KEY_IMAGE, image_str.trim());

            if(type_of_photo.equals("2")){
                dbData.open();
                ArrayList<KVVTSurvey> imageOffline = dbData.getSavedKVVTImages(kvvt_id,"1");

                if (imageOffline.size() > 0){
                    for (int i= 0; i<imageOffline.size(); i++){
                        Double latitude = Double.valueOf(imageOffline.get(i).getLatitude());
                        Double longitude = Double.valueOf(imageOffline.get(i).getLongitude());
                        Log.d("latitude :"+latitude,"longitude :"+longitude);

                        float[] results = new float[1];
                        Location.distanceBetween(latitude, longitude, offlatTextValue, offlongTextValue, results);
                        Log.d("offlatitude :"+offlatTextValue,"offlongitude :"+offlongTextValue);
                        float distanceInMeters = results[0];
                        Log.d("isWithin10m", String.valueOf(distanceInMeters));
                        boolean isWithin10m = distanceInMeters < 15.0;
                        Log.d("isWithin10m", String.valueOf(isWithin10m));
                        //distFrom(latitude, longitude, offlatTextValue, offlongTextValue);
                        /*if(isInCampus(latitude, longitude, offlatTextValue, offlongTextValue)){
                            continue;
                        }
                        else {
                            Utils.showAlert(this, "Capturing must be within 10 metres");
                            return;
                        }*/
//                        if (isWithin10m) {
//                            continue;
//                        }
//                        else {
//                            Utils.showAlert(this, "Capturing must be within 15 metres");
//                            return;
//                        }
                    }
                }
            }

                whereClause = "kvvt_id = ? and type_of_photo = ?";
                whereArgs = new String[]{kvvt_id,type_of_photo};dbData.open();
                ArrayList<KVVTSurvey> imageOffline = dbData.getSavedKVVTImages(kvvt_id,type_of_photo);

                if(imageOffline.size() < 1) {
                    id = db.insert(DBHelper.SAVE_KVVT_IMAGES, null, values);
                }
                else {
                    id = db.update(DBHelper.SAVE_KVVT_IMAGES, values, whereClause, whereArgs);
                }


            if (id > 0) {
                Toasty.success(this, "Success!", Toast.LENGTH_SHORT, true).show();
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);

                if(type_of_photo.equals("2")){
                   /* Intent intent = new Intent(this,HomePage.class);
                    startActivity(intent);*/
                   homePage();
                }
            }
            Log.d("insIdsavesaveKVVTImages", String.valueOf(id));

        } catch (Exception e) {
//            Utils.showAlert(CameraScreen.this, "Atleast Capture one Photo");
            //e.printStackTrace();
        }
    }

    public static float distFrom(Double lat1, Double lng1, Double lat2, Double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);
        Log.d("DistMeter", "" + dist);
        return dist;
    }
    private void captureImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        if (MyLocationListener.latitude > 0) {
            offlatTextValue = MyLocationListener.latitude;
            offlongTextValue = MyLocationListener.longitude;
        }
    }

    public void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(CameraScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(CameraScreen.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CameraScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CameraScreen.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(CameraScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(CameraScreen.this, "Satellite communication not available to get GPS Co-ordination Please Capture Photo in Open Area..");
            }
        } else {
            Utils.showAlert(CameraScreen.this, "GPS is not turned on...");
        }
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions required!")
                .setMessage("Camera needs few permissions to work properly. Grant them in settings.")
                .setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(CameraScreen.this);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);
            cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
            cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            cameraScreenBinding.imageView.setImageBitmap(rotatedBitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    cameraScreenBinding.imageViewPreview.setVisibility(View.GONE);
                    cameraScreenBinding.imageView.setVisibility(View.VISIBLE);
                    cameraScreenBinding.imageView.setImageBitmap(photo);
                }else {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // successfully captured the image
                // display it in image view
                previewCapturedImage();}
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }



    @Override
    public void OnMyResponse(ServerResponse serverResponse) {

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(Math.abs(lat2 - lat1));
        double lonDistance = Math.toRadians(Math.abs(lon2 - lon1));

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c * 1000; // distance in meter

        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    private boolean isInCampus(double lat,double longtitude,double x,double y){

        // the lat and long of : Webel-IT Park
        final double previous_lat = lat;
        final double previous_long = longtitude;

        // radiusToCheck is defined as 200
        // radius up to 200 m is checked
        if(getDistance(previous_lat,previous_long,x,y) <= 50.0){
            Log.d("SuccessMeter",""+getDistance(previous_lat,previous_long,x,y));
            //Utils.showAlert(this,"This location is inside of circle!");
            return true;
        }
        else{
            Log.d("failedMeter",""+getDistance(previous_lat,previous_long,x,y));
           // Utils.showAlert(this,"This location is out of circle!");
            return false;
        }
    }

    public void save_and_delete_alert(){
        try {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(type_of_photo.equals("1")) {
                text.setText(getResources().getString(R.string.user_image_satisfaction));
            }
            else if(type_of_photo.equals("2")){
                text.setText(getResources().getString(R.string.user_house_satisfaction));
            }

            Button yesButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button noButton = (Button) dialog.findViewById(R.id.btn_cancel);
            noButton.setVisibility(View.VISIBLE);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        saveActivityImage();
                        dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getExactLocation() {

        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();


        //mlocManager.requestLocationUpdates(best, 50, 0, this);

        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(CameraScreen.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // check permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, CAMERA},
                        1000);
                // reuqest for permission

            }
            else {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                locationRequest = LocationRequest.create();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                //locationRequest.setInterval(0);

                mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        offlatTextValue = location.getLatitude();
                        offlongTextValue = location.getLongitude();
                        Log.d("LocationAccuracy", "" + location.getAccuracy());
                        Log.d("Locations", "" + offlatTextValue + "," + offlongTextValue);
                        if (CameraUtils.checkPermissions(CameraScreen.this)) {
                            captureImage();
                        } else {
                            requestCameraPermission(MEDIA_TYPE_IMAGE);
                        }
                    } else {
                        Utils.showAlert(CameraScreen.this, getResources().getString(R.string.satelite_communication_not_available));

                    }
                });
            }
        }
        else {
            Utils.showAlert(CameraScreen.this, getResources().getString(R.string.gps_is_not_turned_on));
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                    locationRequest = LocationRequest.create();
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    //locationRequest.setInterval(0);

                    mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    mlocListener = new MyLocationListener();


                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(CameraScreen.this,
                            ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

                    }

                    if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                            if (location != null) {
                                offlatTextValue = location.getLatitude();
                                offlongTextValue = location.getLongitude();
                                Log.d("LocationAccuracy", "" + location.getAccuracy());
                                Log.d("Locations", "" + offlatTextValue + "," + offlongTextValue);
                                if (CameraUtils.checkPermissions(CameraScreen.this)) {
                                    captureImage();
                                } else {
                                    requestCameraPermission(MEDIA_TYPE_IMAGE);
                                }
                            } else {
                                Utils.showAlert(CameraScreen.this, getResources().getString(R.string.satelite_communication_not_available));
                            }
                        });
                    }
                    else {
                        Utils.showAlert(CameraScreen.this, getResources().getString(R.string.gps_is_not_turned_on));
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }


                break;
            }
        }
    }

}
