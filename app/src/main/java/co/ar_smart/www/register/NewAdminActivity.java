package co.ar_smart.www.register;

import android.content.Intent;
import android.graphics.PorterDuff;
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
import co.ar_smart.www.living.LoginActivity;
import co.ar_smart.www.living.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static co.ar_smart.www.helpers.Constants.*;

public class NewAdminActivity extends AppCompatActivity {


    /**
     * The user email
     */
    private String EMAIL = "";
    /**
     * The user password
     */
    private String PASSWORD = "";

    /*
     * TextView used for users that already have an account
     */
    private TextView txvExistingAccount;
    private EditText edtName;
    private EditText edtEmail;
    private EditText edtConfEmail;
    private EditText edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_admin);
        setTitle(R.string.nav_bar_new_user_title);

        // Add underling to the textView
        txvExistingAccount = (TextView) findViewById(R.id.txvExistingAccount);
        String udata = null;
        if (txvExistingAccount != null) {
            udata = (String) txvExistingAccount.getText();
        }
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata != null ? udata.length() : 0, 0);
        txvExistingAccount.setText(content);

        //Change hint color text and editText line color
        edtName = (EditText) findViewById(R.id.edtName);
        if (edtName != null) {
            edtName.getBackground().mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        }
        edtName.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        if (edtEmail != null) {
            edtEmail.getBackground().mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        }
        edtEmail.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));

        edtConfEmail = (EditText) findViewById(R.id.edtConfEmail);
        if (edtConfEmail != null) {
            edtConfEmail.getBackground().mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        }
        edtConfEmail.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));

        edtPassword = (EditText) findViewById(R.id.edtPassword);
        if (edtPassword != null) {
            edtPassword.getBackground().mutate().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.blanco), PorterDuff.Mode.SRC_ATOP);
        }
        edtPassword.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blanco));
    }

    public void existingAccount(View v)
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

    public void createAccount(View v) {
        boolean camposLlenos = (!edtName.getText().toString().trim().equals(""))
                && (!edtEmail.getText().toString().trim().equals(""))
                && (!edtConfEmail.getText().toString().trim().equals(""))
                && (!edtPassword.getText().toString().trim().equals(""));
        boolean correspondenCorreos = edtEmail.getText().toString().trim().equals(edtConfEmail.getText().toString().trim());
        if (!camposLlenos)
        {
            displayMessage(getResources().getString(R.string.toast_incomplete_form));
        }
        else if (!correspondenCorreos)
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
            EMAIL = edtEmail.getText().toString();
            PASSWORD = edtPassword.getText().toString();
            //TODO VER LA NUEVA IMPLEMENTACION DEL METODO USANDO LOS CALLBACKS!
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
                        String jsonData = response.body().string();
                        response.body().close();
                        if (!response.isSuccessful()) {
                            displayMessage(getResources().getString(R.string.toast_login_bad_credentials));
                        }
                        else
                        {
                            createdUser();
                        }
                    }
                });
            } catch (Exception e) {
                displayMessage(getResources().getString(R.string.create_user_error));
            }
        }
    }

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
