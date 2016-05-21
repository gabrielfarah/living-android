package co.ar_smart.www.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

import co.ar_smart.www.adapters.GuestAdapter;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.GuestManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Guest;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;

public class GuestManagementActivity extends AppCompatActivity {

    /**
     * The user api token to perform requets
     */
    private String API_TOKEN;
    /**
     * The id of the hub to perform requets with
     */
    private int PREFERRED_HUB_ID;
    /**
     * The list of guests the hub contains
     */
    private List<Guest> guests;
    /**
     * This field is the ui adapter for displaying the guests list
     */
    private GuestAdapter adapter;
    /**
     * This var will be filled with a user promted email
     */
    private String new_guest_email_str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_management);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.label_manage_guests));
        }
        Button createNewGuestButton = (Button) findViewById(R.id.add_new_guest_button);
        if (createNewGuestButton != null) {
            createNewGuestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addNewGuest();
                }
            });
        }
        setUI();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method will ask the user to input an email address and will try to add a new guest using this email.
     * The user must be registered for the request to succeed.
     */
    private void addNewGuest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.label_add_guest_by_email));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getResources().getString(R.string.label_add_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_guest_email_str = input.getText().toString();
                if (!new_guest_email_str.isEmpty())
                    addNewGuestFromEmail();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.label_cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * This method will try to add a new guest from the email the user input
     */
    private void addNewGuestFromEmail() {
        final Guest nGuest = new Guest();
        nGuest.setEmail(new_guest_email_str);
        GuestManager.addGuest(PREFERRED_HUB_ID, nGuest, API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(List<Guest> guest) {

            }

            @Override
            public void onSuccessCallback() {
                setUI();
            }

            @Override
            public void onUnsuccessfulCallback() {
                failedToAddGuest();
            }
        });
    }

    /**
     * >This method sets the UI of the activity. It will try to load the guests for this hub
     * The method will fail if the user doing the request is not the admin, because only him can manage guests.
     */
    private void setUI() {
        GuestManager.getGuests(PREFERRED_HUB_ID, API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(final List<Guest> guest) {
                guests = guest;
                adapter = new GuestAdapter(GuestManagementActivity.this, guests);
                // Attach the adapter to a ListView
                ListView listView = (ListView) findViewById(R.id.guest_list_view);
                if (listView != null) {
                    listView.setAdapter(adapter);
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.guest_list_container);
                    if (linearLayout != null) {
                        linearLayout.setVisibility(View.VISIBLE);
                    }
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            createAndShowAlertDialog(guests.get(position));
                        }
                    });
                }
            }

            @Override
            public void onSuccessCallback() {

            }

            @Override
            public void onUnsuccessfulCallback() {
                RelativeLayout onlyAdmins = (RelativeLayout) findViewById(R.id.only_admins_can_edit_guests_layout);
                if (onlyAdmins != null) {
                    onlyAdmins.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * This method creates a dialog for the user to validate or cancel the elimination of a guest
     *
     * @param guest the user to be confirmed if eliminated or not
     */
    private void createAndShowAlertDialog(final Guest guest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.label_confirmation_remove_guest) + guest.getEmail() + "?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeUser(guest);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This method removes a guest from the guest list of this hub
     *
     * @param guest the guest to be removed from the hub
     */
    private void removeUser(final Guest guest) {
        GuestManager.removeGuest(PREFERRED_HUB_ID, guest.getId(), API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {
                failedToRemoveGuest();
            }

            @Override
            public void onSuccessCallback(List<Guest> guest) {
            }

            @Override
            public void onSuccessCallback() {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_guest_removed_message), Toast.LENGTH_SHORT).show();
                guests.remove(guest);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUnsuccessfulCallback() {
                failedToRemoveGuest();
            }
        });
    }

    /**
     * This method displays a message when the the removing a guest fails
     */
    private void failedToRemoveGuest() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_failed_remove_guest), Toast.LENGTH_LONG).show();
    }

    /**
     * This method shows a message to the user when the email entered is not yet registered
     */
    private void failedToAddGuest() {
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.label_only_failed_add_guest), Toast.LENGTH_LONG).show();
    }
}
