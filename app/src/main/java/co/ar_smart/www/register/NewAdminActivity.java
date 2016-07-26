package co.ar_smart.www.register;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.living.LoginActivity;
import co.ar_smart.www.living.LoginRegisterActivity;
import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.JSON;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;
import static co.ar_smart.www.helpers.Constants.PREF_JWT;
import static co.ar_smart.www.helpers.Constants.PREF_PASSWORD;
import static co.ar_smart.www.helpers.Constants.REGISTER_URL;

public class NewAdminActivity extends AppCompatActivity
{


    /**
     * The user email
     */
    private String email = "";
    /**
     * The user password
     */
    private String password = "";
    /**
     * the user api_token
     */
    private String api_token;
    /**
     * EditText for user name
     */
    private EditText edtName;
    /**
     * EditText for user name
     */
    private EditText edtLastname;
    /**
     * EditText for user email
     */
    private EditText edtEmail;
    /**
     * EditText for user confirmation email
     */
    private EditText edtConfEmail;
    /**
     * EditText for user password
     */
    private EditText edtPassword;
    /**
     * EditText for user confirmation password
     */
    private EditText edtConfPassword;
    /**
     * Constant used when the application verifies the permissions given by the user
     */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    /**
     * Actual context of application
     */
    private Context mContext;
    /**
     * Boolean used to know if the password is been show
     */
    private boolean showingPasswords;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_admin);
        setTitle(R.string.nav_bar_new_user_title);
        mContext = this;
        showingPasswords = false;

        // Add underling to the existing account textView
        TextView txvExistingAccount = (TextView) findViewById(R.id.txvExistingAccount);
        String udata = null;
        if (txvExistingAccount != null)
        {
            udata = (String) txvExistingAccount.getText();
        }
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata != null ? udata.length() : 0, 0);
        if (txvExistingAccount != null)
        {
            txvExistingAccount.setText(content);
        }
        //Initialize the variables
        edtName = (EditText) findViewById(R.id.edtName);
        edtName.setTypeface(Typeface.SERIF);
        edtLastname = (EditText) findViewById(R.id.edtLastname);
        edtLastname.setTypeface(Typeface.SERIF);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtEmail.setTypeface(Typeface.SERIF);
        edtConfEmail = (EditText) findViewById(R.id.edtConfEmail);
        edtConfEmail.setTypeface(Typeface.SERIF);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setTypeface(Typeface.SERIF);
        edtConfPassword = (EditText) findViewById(R.id.edtConfPassword);
        edtConfPassword.setTypeface(Typeface.SERIF);
        edtConfPassword.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = edtConfPassword.getRight()
                            - edtConfPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()
                            - edtConfPassword.getPaddingEnd();
                    if (event.getRawX() >= leftEdgeOfRightDrawable)
                    {
                        TransformationMethod tempTransMeth = (showingPasswords)?null:new PasswordTransformationMethod();
                        int drawable = (showingPasswords)?R.drawable.hide_pass:R.drawable.show_pass;
                        edtConfPassword.setTransformationMethod(tempTransMeth);
                        edtPassword.setTransformationMethod(tempTransMeth);
                        edtConfPassword.setCompoundDrawablesWithIntrinsicBounds(0,0,drawable,0);
                        showingPasswords = !showingPasswords;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Method that guides to login Activity
     *
     * @param v - View required for OnClick property
     */
    public void existingAccount(View v)
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    /**
     * Method that validate information and creates de user
     * Validations: 1. All fields are filled
     * 2. Email and Confirmation Email Match
     * 3. Email and Confirmation Email have email format
     *
     * @param v - View required for OnClick property
     */
    public void createAccount(View v)
    {
        String name = edtName.getText().toString().trim();
        String lastName = edtLastname.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        String confEmail = edtConfEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        String confPassword = edtConfPassword.getText().toString().trim();
        boolean filledFields = (!name.equals(""))
                && (!lastName.equals(""))
                && (!email.equals(""))
                && (!confEmail.equals(""))
                && (!password.equals(""))
                && (!confPassword.equals(""));
        boolean emailsEqual = email.equals(confEmail);
        boolean passwordsEqual = password.equals(confPassword);
        Pattern pattern = Pattern.compile(Constants.PASSWORD_REGEX);
        Matcher matcher = pattern.matcher(password);
        if (!filledFields)
        {
            displayMessage(getResources().getString(R.string.toast_incomplete_form));
        }
        else if (!emailsEqual)
        {
            displayMessage(getResources().getString(R.string.toast_not_matching_email));
        }
        else if (!passwordsEqual)
        {
            displayMessage(getString(R.string.toast_not_matching_password));
        }
        else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            displayMessage(getResources().getString(R.string.toast_email_format_error));
        }
        else if(!matcher.matches())
        {
            displayMessage(getResources().getString(R.string.not_regex_password));
        }
        else
        {
            JSONObject json = new JSONObject();
            try
            {
                json.put("password", password);
                json.put("email", email);
                json.put("first_name",name);
                json.put("last_name",lastName);
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(REGISTER_URL)
                        .header("Accept", "application/json")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback()
                {
                    @Override
                    public void onFailure(Call call, IOException e)
                    {
                        AnalyticsApplication.getInstance().trackException(e);
                        displayMessage(getResources().getString(R.string.toast_login_failure));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException
                    {
                        String body = response.body().string();
                        response.body().close();
                        if (!response.isSuccessful())
                        {
                            String error = body.substring(body.lastIndexOf("[")+2,body.lastIndexOf("]")-1);
                            displayMessage(error);
                        }
                        else
                        {
                            //Gets API_TOKEN
                            JWTManager.getApiToken(email, password, new JWTManager.JWTCallbackInterface()
                            {
                                @Override
                                public void onFailureCallback()
                                {
                                    displayMessage(getResources().getString(R.string.toast_login_failure));
                                }

                                @Override
                                public void onSuccessCallback(String nToken)
                                {
                                    api_token = nToken;
                                    savePreferences();
                                }

                                @Override
                                public void onUnsuccessfulCallback()
                                {
                                    displayMessage(getResources().getString(R.string.toast_login_bad_credentials));
                                }

                                @Override
                                public void onExceptionCallback()
                                {
                                    displayMessage(getResources().getString(R.string.toast_login_server_error));
                                }
                            });
                            //Change activity
                            createdUser();
                        }
                    }
                });
            }
            catch (Exception e)
            {
                displayMessage(getResources().getString(R.string.create_user_error));
            }
        }
    }

    /**
     * This method will store the user credentials and api token in the shared preferences
     * This will run after the server api has successfully authenticated the user.
     */
    private void savePreferences()
    {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        // Edit and commit the values
        editor.putString(PREF_EMAIL, email);
        editor.putString(PREF_PASSWORD, password);
        editor.putString(PREF_JWT, api_token);
        editor.apply();
    }

    /**
     * Method in charge of getting api_token and move to next activity
     */
    private void createdUser()
    {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            runOnUiThread(new Runnable() {
                public void run() {
                    new AlertDialog.Builder(mContext)
                            .setMessage(R.string.permission_location_message)
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    ActivityCompat.requestPermissions((Activity) mContext,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                                }
                            })
                            .create().show();
                }
            });
        }
        else
        {
            Intent intent = new Intent(this, CreatedUserActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(this, CreatedUserActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    }


    /**
     * This method display a dialog message in the UI thread given a message.
     *
     * @param message The message sent to be displayed in the main UI
     */
    private void displayMessage(final String message)
    {
        NewAdminActivity.this.runOnUiThread(new Runnable()
        {
            public void run()
            {
                Toast.makeText(NewAdminActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, LoginRegisterActivity.class);
        startActivity(i);
    }
}

