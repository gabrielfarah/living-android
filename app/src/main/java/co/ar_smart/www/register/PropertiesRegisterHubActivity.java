package co.ar_smart.www.register;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;

import static co.ar_smart.www.helpers.Constants.DEFAULT_EMAIL;
import static co.ar_smart.www.helpers.Constants.DEFLT_BACKGRND;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;

public class PropertiesRegisterHubActivity extends AppCompatActivity
{

    /**
     * Constant for capture image event
     */
    public final static int CAPTURE_IMAGE_RC = 1888;
    /**
     * Contant for select from gallery event
     */
    public final static int SELECT_IMAGE = 1889;
    /**
     * Constant for click on static map event
     */
    public final static int MAP_RC = 1890;
    /**
     * Constant used when the application verifies the permissions given by the user
     */
    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    /**
     * Listener to capture a photo
     */
    View.OnClickListener listenerCapture = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_RC);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * Listener for select image
     */
    View.OnClickListener listenerSelection = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_IMAGE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * Longitude given by last location registered
     */
    private double lastLong;
    /**
     * Longitude set by user in map activity
     */
    private double finalLong;
    /**
     * Latitude given by last location registered
     */
    private double lastLat;
    /**
     * Latitude set by user in map activity
     */
    private double finalLat;
    /**
     * Redius set by user in map activity
     */
    private double radius;
    /**
     * Path of the image selected for application background
     */
    private String imagePath;
    /**
     * Name assigned by user to hub
     */
    private String hubName;
    /**
     * ImageView that shows selected image
     */
    private ImageView selectedImage;
    /**
     * Current context
     */
    private Context mContext;
    /**
     * Listener for clic on static map
     */
    View.OnClickListener listenerMap = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Intent i = new Intent(mContext, MapRegisterHubActivity.class);
                i.putExtra("lat", lastLat);
                i.putExtra("long", lastLong);
                i.putExtra("radius", radius);
                startActivityForResult(i, MAP_RC);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * Listener for end of properties assign process
     */
    View.OnClickListener listenerProperties = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                EditText hub_name = (EditText) findViewById(R.id.edit_text_hub_name);
                if (hub_name != null) {
                    hubName = hub_name.getText().toString();
                    if (hubName.equals("")) {
                        displayMessage(getString(R.string.empty_hub_name));
                    } else if ((finalLong == 0) || (finalLat == 0) || (radius == 0)) {
                        displayMessage(getString(R.string.not_selected_location));
                    } else if (imagePath.equals(Constants.DEFAULT_BACKGROUND_PATH)) {
                        new AlertDialog.Builder(mContext)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle(R.string.default_background_title)
                                .setMessage(R.string.default_backgroung_message)
                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        registerHub();
                                    }

                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    } else {
                        registerHub();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    /**
     * ImageView that shows static map
     */
    private ImageView staticMap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties_register_hub);
        setTitle(R.string.nav_bar_prop_reg_hub_title);

        //Gets the bitmap of last register location
        Intent i = getIntent();
        Bitmap bitmap = i.getParcelableExtra("bitmap");
        staticMap = (ImageView) findViewById(R.id.staticMap);

        //Gets the coordinates of last registered location
        lastLong = i.getDoubleExtra("long", -74);
        lastLat = i.getDoubleExtra("lat", 4);

        //Initializes final values
        mContext = this;
        finalLong = 0.0;
        finalLat = 0.0;
        radius = 0.0;
        hubName = "";
        imagePath = Constants.DEFAULT_BACKGROUND_PATH;
        selectedImage = (ImageView) findViewById(R.id.selectedImage);
        if (selectedImage != null)
        {
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.default_background);
            selectedImage.setImageBitmap(icon);
        }

        //Set action for buttons
        Button capture = (Button) findViewById(R.id.btnCapture);
        if (capture != null)
        {
            capture.setOnClickListener(listenerCapture);
        }
        Button select = (Button) findViewById(R.id.btnGallery);
        if (select != null)
        {
            select.setOnClickListener(listenerSelection);
        }
        if (staticMap != null)
        {
            staticMap.setImageBitmap(bitmap);
            staticMap.setOnClickListener(listenerMap);
        }
        Button continueMap = (Button) findViewById(R.id.continueMap);
        if (continueMap != null)
        {
            continueMap.setOnClickListener(listenerProperties);
        }
        showMessage();

    }

    private void showMessage()
    {
        new android.app.AlertDialog.Builder(mContext)
                .setMessage(R.string.permission_images_message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        askAndroidPermissions();
                    }
                })
                .create().show();
    }

    /**
     * This method ask the user the required permissions for the application to work well
     */
    private void askAndroidPermissions()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        {
            for (int i = 0; i < permissions.length; i++)
            {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
                {
                    if (grantResult != PackageManager.PERMISSION_GRANTED)
                    {
                        showMessage();
                    }
                }
            }
        }
    }

    /**
     * Get the path for the latest capture photo
     *
     * @return Path of the last captured photo
     */
    public String getOriginalImagePath()
    {
        //verifies if the user have given the required permissions
        /*
      Boolean that represent if the user give the required permissions
     */
        boolean permissionCheck = (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != -1);
        // in the positive case creates and initialize the atributes for getting the static map image
        if (permissionCheck)
        {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            int column_index_data;
            String path = "";
            if (cursor != null)
            {
                column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToLast();
                path = cursor.getString(column_index_data);
                cursor.close();
            }

            return path;
        }
        else
        {
            // in the negative case shows again the permission ask so the user can accept them.
            askAndroidPermissions();
            return getOriginalImagePath();
        }

    }

    /**
     * Method executed when an Activity returns a result
     *
     * @param requestCode - Code of the request
     * @param resultCode  - Code of the response
     * @param data        - data atached to result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null)
        {
            //Case for Capture image event
            if (requestCode == CAPTURE_IMAGE_RC)
            {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                if (selectedImage != null)
                {
                    selectedImage.setImageBitmap(imageBitmap);
                }
                imagePath = getOriginalImagePath();
            }
            //Case for select image event
            else if (requestCode == SELECT_IMAGE)
            {
                // Let's read picked image data - its URI
                Uri pickedImage = data.getData();
                // Let's read picked image path using content resolver
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
                if (cursor != null)
                {
                    cursor.moveToFirst();
                    imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
                    cursor.close();
                }
                try
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), pickedImage);
                    if (selectedImage != null)
                    {
                        selectedImage.setImageBitmap(bitmap);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
            //Case for map location result
            else if (requestCode == MAP_RC)
            {
                finalLat = data.getDoubleExtra("latitude", 0.0);
                lastLat = finalLat;
                finalLong = data.getDoubleExtra("longitude", 0.0);
                lastLong = finalLong;
                radius = data.getDoubleExtra("radius", 0.0);
                Bitmap bitmap = data.getParcelableExtra("bitmap");
                if (staticMap != null)
                {
                    staticMap.setImageBitmap(bitmap);
                    staticMap.invalidate();
                }
            }
        }

    }

    /**
     * Method for advance to next activity
     * send as extras the necessary information for hub register
     */
    private void registerHub()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String email = settings.getString(PREF_EMAIL,DEFAULT_EMAIL);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(email+"-"+DEFLT_BACKGRND, imagePath);
        editor.apply();
        Intent i = new Intent(mContext, RegisterHubActivity.class);
        i.putExtra("hubName", hubName);
        i.putExtra("backgroundPath", imagePath);
        i.putExtra("hubLatitude", finalLat);
        i.putExtra("hubLongitude", finalLong);
        i.putExtra("hubRadius", radius);
        startActivity(i);
    }

    /**
     * This method display a dialog message in the UI thread given a message.
     *
     * @param message The message sent to be displayed in the main UI
     */
    private void displayMessage(final String message)
    {
        PropertiesRegisterHubActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(PropertiesRegisterHubActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
