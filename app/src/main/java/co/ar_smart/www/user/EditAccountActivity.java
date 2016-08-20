package co.ar_smart.www.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.UserManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.User;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class EditAccountActivity extends AppCompatActivity {

    private String API_TOKEN;
    private User USER;
    private EditText first_name;
    private EditText last_name;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        USER = intent.getParcelableExtra(EXTRA_OBJECT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_user_management_title));
        }
        first_name = (EditText) findViewById(R.id.edit_user_first_name);
        last_name = (EditText) findViewById(R.id.edit_user_last_name);
        email = (EditText) findViewById(R.id.edit_user_email);
        first_name.setText(USER.getFirst_name());
        last_name.setText(USER.getLast_name());
        email.setText(USER.getEmail());
        Button updateButton = (Button) findViewById(R.id.edit_user_button);
        if (updateButton != null) {
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateUser();
                }
            });
        }
    }

    private void updateUser() {
        String nFirst_name = first_name.getText().toString();
        String nLast_name = last_name.getText().toString();
        String nEmail = email.getText().toString();
        if ((nFirst_name.isEmpty()) || (nLast_name.isEmpty()) || (nEmail.isEmpty())) {
            Constants.showCustomMessage(getApplicationContext(), getResources().getString(R.string.label_error_message_fill_fields));
        } else {
            USER.setEmail(nEmail);
            USER.setFirst_name(nFirst_name);
            USER.setLast_name(nLast_name);
            UserManager.updateUser(USER, API_TOKEN, new UserManager.UserCallbackInterface() {
                @Override
                public void onFailureCallback() {
                    Constants.showNoInternetMessage(getApplicationContext());
                }

                @Override
                public void onSuccessCallback(User user) {
                    showUpdatedMessage();
                    setResultUser();
                    EditAccountActivity.this.finish();
                }

                @Override
                public void onUnsuccessfulCallback() {
                    showProblemMessage();
                }
            });
        }
    }

    private void showUpdatedMessage() {
        Constants.showCustomMessage(getApplicationContext(), getResources().getString(R.string.label_success_updating_user));
    }

    private void showProblemMessage() {
        Constants.showCustomMessage(getApplicationContext(), getResources().getString(R.string.label_error_updating_user));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go back
                setResultUser();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        setResultUser();
        super.onBackPressed();
    }

    private void setResultUser() {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, USER);
        data.putExtras(bundle);
        if (getParent() == null) {
            setResult(Activity.RESULT_OK, data);
        } else {
            getParent().setResult(Activity.RESULT_OK, data);
        }
    }

}
