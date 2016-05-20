package co.ar_smart.www.register;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.IOException;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.living.LoginActivity;
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

public class NewAdminActivity extends AppCompatActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_admin);
        setTitle(R.string.nav_bar_new_user_title);

        // Add underling to the existing account textView
        TextView txvExistingAccount = (TextView) findViewById(R.id.txvExistingAccount);
        String udata = null;
        if (txvExistingAccount != null) {
            udata = (String) txvExistingAccount.getText();
        }
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata != null ? udata.length() : 0, 0);
        if (txvExistingAccount != null)
        {
            txvExistingAccount.setText(content);
        }

        //Change hint color text
        edtName = (EditText) findViewById(R.id.edtName);
        edtName.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtEmail.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));

        edtConfEmail = (EditText) findViewById(R.id.edtConfEmail);
        edtConfEmail.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtPassword.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
    }

    /**
     * Method that guides to login Activity
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
     *              2. Email and Confirmation Email Match
     *              3. Email and Confirmation Email have email format
     * @param v - View required for OnClick property
     */
    public void createAccount(View v) {
        boolean filledFields = (!edtName.getText().toString().trim().equals(""))
                && (!edtEmail.getText().toString().trim().equals(""))
                && (!edtConfEmail.getText().toString().trim().equals(""))
                && (!edtPassword.getText().toString().trim().equals(""));
        boolean emailsEqual = edtEmail.getText().toString().trim().equals(edtConfEmail.getText().toString().trim());
        if (!filledFields)
        {
            displayMessage(getResources().getString(R.string.toast_incomplete_form));
        }
        else if (!emailsEqual)
        {
            displayMessage(getResources().getString(R.string.toast_not_matching_email));
        }
        else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText().toString()).matches()
                || !android.util.Patterns.EMAIL_ADDRESS.matcher(edtConfEmail.getText().toString()).matches())
        {
            displayMessage(getResources().getString(R.string.toast_email_format_error));
        }
        else
        {
            email = edtEmail.getText().toString();
            password = edtPassword.getText().toString();
            JSONObject json = new JSONObject();
            try {
                json.put("password", edtPassword.getText());
                json.put("email", edtEmail.getText());
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBody.create(JSON, json.toString());
                Request request = new Request.Builder()
                        .url(REGISTER_URL)
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        AnalyticsApplication.getInstance().trackException(e);
                        displayMessage(getResources().getString(R.string.toast_login_failure));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        response.body().close();
                        if (!response.isSuccessful()) {
                            displayMessage(getResources().getString(R.string.toast_login_bad_credentials));
                        }
                        else
                        {
                            JWTManager.getApiToken(email, password, new JWTManager.JWTCallbackInterface() {
                                @Override
                                public void onFailureCallback() {
                                    displayMessage(getResources().getString(R.string.toast_login_failure));
                                }

                                @Override
                                public void onSuccessCallback(String nToken) {
                                    api_token = nToken;
                                    savePreferences();
                                }

                                @Override
                                public void onUnsuccessfulCallback() {
                                    displayMessage(getResources().getString(R.string.toast_login_bad_credentials));
                                }

                                @Override
                                public void onExceptionCallback() {
                                    displayMessage(getResources().getString(R.string.toast_login_server_error));
                                }
                            });
                            createdUser();
                        }
                    }
                });
            } catch (Exception e) {
                displayMessage(getResources().getString(R.string.create_user_error));
            }
        }
    }

    /**
     * This method will store the user credentials and api token in the shared preferences
     * This will run after the server api has successfully authenticated the user.
     */
    private void savePreferences() {
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

        Intent i = new Intent(this, CreatedUserActivity.class);
        startActivity(i);
    }

    /**
     * This method display a dialog message in the UI thread given a message.
     * @param message The message sent to be displayed in the main UI
     */
    private void displayMessage(final String message){
        NewAdminActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(NewAdminActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}

