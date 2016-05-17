package co.ar_smart.www.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import co.ar_smart.www.adapters.GuestAdapter;
import co.ar_smart.www.helpers.GuestManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Guest;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;

public class GuestManagementActivity extends AppCompatActivity {

    private String API_TOKEN;
    private int PREFERRED_HUB_ID;
    private List<Guest> guests;
    private GuestAdapter adapter;
    private String new_guest_email_str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_management);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
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

    private void addNewGuest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add a new");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new_guest_email_str = input.getText().toString();
                if (new_guest_email_str != null && !new_guest_email_str.isEmpty())
                    addNewGuestFromEmail();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addNewGuestFromEmail() {
        final Guest nGuest = new Guest();
        nGuest.setEmail(new_guest_email_str);
        GuestManager.addGuest(PREFERRED_HUB_ID, nGuest, API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {

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

            }
        });
    }

    private void setUI() {
        GuestManager.getGuests(PREFERRED_HUB_ID, API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onSuccessCallback(final List<Guest> guest) {
                guests = guest;
                adapter = new GuestAdapter(GuestManagementActivity.this, guests);
                // Attach the adapter to a ListView
                ListView listView = (ListView) findViewById(R.id.guest_list_view);
                listView.setAdapter(adapter);
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.guest_list_container);
                linearLayout.setVisibility(View.VISIBLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createAndShowAlertDialog(guests.get(position));
                    }
                });
            }

            @Override
            public void onSuccessCallback() {

            }

            @Override
            public void onUnsuccessfulCallback() {

            }
        });
    }

    private void createAndShowAlertDialog(final Guest guest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to remove: " + guest.getEmail() + "?");
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

    private void removeUser(final Guest guest) {
        GuestManager.removeGuest(PREFERRED_HUB_ID, guest.getId(), API_TOKEN, new GuestManager.GuestCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Toast.makeText(getApplicationContext(), "Unable to remove, try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccessCallback(List<Guest> guest) {
            }

            @Override
            public void onSuccessCallback() {
                Toast.makeText(getApplicationContext(), "User removed!", Toast.LENGTH_SHORT).show();
                guests.remove(guest);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onUnsuccessfulCallback() {

            }
        });
    }
}
