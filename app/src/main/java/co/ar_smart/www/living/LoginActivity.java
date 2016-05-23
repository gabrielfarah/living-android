package co.ar_smart.www.living;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.ar_smart.www.helpers.JWTManager;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.PREFS_NAME;
import static co.ar_smart.www.helpers.Constants.PREF_EMAIL;
import static co.ar_smart.www.helpers.Constants.PREF_JWT;
import static co.ar_smart.www.helpers.Constants.PREF_PASSWORD;

/**
 * This activity is responsable to authenticate a user given the input of an email and password fields.
 * If successful, it will open the home activity and pass to it the api token while saving the user credentials into the shared preferences.
 * Created by Gabriel on 4/27/2016.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * The user email
     */
    private String EMAIL = "";
    /**
     * The user password
     */
    private String PASSWORD = "";
    /**
     * The backend auth token
     */
    private String API_TOKEN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Hide the progressbar
        toggleProgress();
        Button loginButton = (Button) findViewById(R.id.loginButton);
        final TextView emailText = (TextView) findViewById(R.id.emailText);
        final TextView passwordText = (TextView) findViewById(R.id.passwordText);
        if (loginButton != null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EMAIL =  emailText.getText().toString();
                    PASSWORD = passwordText.getText().toString();
                    // Show progressbar and do network request
                    toggleProgress();
                    JWTManager.getApiToken(EMAIL, PASSWORD, new JWTManager.JWTCallbackInterface() {
                        @Override
                        public void onFailureCallback() {
                            displayMessage(getResources().getString(R.string.toast_login_failure));
                            toggleProgress();
                        }

                        @Override
                        public void onSuccessCallback(String nToken) {
                            API_TOKEN = nToken;
                            successfulLogin();
                        }

                        @Override
                        public void onUnsuccessfulCallback() {
                            displayMessage(getResources().getString(R.string.toast_login_bad_credentials));
                            // Hide progressbar
                            toggleProgress();
                        }

                        @Override
                        public void onExceptionCallback() {
                            displayMessage(getResources().getString(R.string.toast_login_server_error));
                            toggleProgress();
                        }
                    });
                }
            });
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
        editor.putString(PREF_EMAIL, EMAIL);
        editor.putString(PREF_PASSWORD, PASSWORD);
        editor.putString(PREF_JWT, API_TOKEN);
        editor.apply();
    }

    /**
     * This method display a dialog message in the UI thread given a message.
     * @param message The message sent to be displayed in the main UI
     */
    private void displayMessage(final String message){
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void successfulLogin(){
        savePreferences();
        toggleProgress();
        openHomeActivity();
    }

    /**
     * This method opens the home activity and sends to it the valid api token obtained from user login
     */
    public void openHomeActivity() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(EXTRA_MESSAGE, API_TOKEN);
        startActivity(intent);
    }

    /**
     * This method toggles visible/invisible the circular progress bar.
     */
    private void toggleProgress() {
        LoginActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progress);
                if(mProgressBar.getVisibility() == View.INVISIBLE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                else{
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
