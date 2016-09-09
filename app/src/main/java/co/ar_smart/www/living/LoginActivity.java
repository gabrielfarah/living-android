package co.ar_smart.www.living;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.text.style.UnderlineSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.register.NewAdminActivity;

import static co.ar_smart.www.helpers.Constants.DEFAULT_EMAIL;
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
    /**
     * Boolean used to know if the password is been show
     */
    private boolean showingPasswords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Hide the progressbar
        toggleProgress();
        Button loginButton = (Button) findViewById(R.id.loginButton);
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        emailText.setTypeface(Typeface.DEFAULT);
        //emailText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
        passwordText.setTypeface(Typeface.DEFAULT);
        //passwordText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
        setLastEmail(emailText);
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
        // Add underling to the existing account textView
        TextView txvExistingAccount = (TextView) findViewById(R.id.txvCreateAccount);
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
        emailText.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = emailText.getRight()
                            - emailText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()
                            - emailText.getPaddingEnd();
                    if (event.getRawX() >= leftEdgeOfRightDrawable)
                    {
                        emailText.setText("");
                    }
                }
                return false;
            }
        });
        passwordText.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_RIGHT = 2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = passwordText.getRight()
                            - passwordText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()
                            - passwordText.getPaddingEnd();
                    if (event.getRawX() >= leftEdgeOfRightDrawable)
                    {
                        TransformationMethod tempTransMeth = (showingPasswords)?null:new PasswordTransformationMethod();
                        int drawable = (showingPasswords)?R.drawable.hide_pass:R.drawable.show_pass;
                        passwordText.setTransformationMethod(tempTransMeth);
                        passwordText.setCompoundDrawablesWithIntrinsicBounds(0,0,drawable,0);
                        showingPasswords = !showingPasswords;
                    }
                }
                return false;
            }
        });
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

    public void signUp(View view) {
        Intent i = new Intent(this, NewAdminActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, LoginRegisterActivity.class);
        startActivity(i);
    }

    /**
     * Method that set the last sign in email in the edit text
     * @param lastEmail - Edit text where last sign in email is show
     */
    public void setLastEmail(EditText lastEmail)
    {
        //Get the information of the user from the SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        // Get values using keys
        String EMAIL = settings.getString(PREF_EMAIL, DEFAULT_EMAIL);
        if(!EMAIL.equals(DEFAULT_EMAIL))
        {
            lastEmail.setText(EMAIL);
        }
    }
}
