package co.ar_smart.www.actions;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import co.ar_smart.www.adapters.FeedActionAdapter;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.FeedAction;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;

/**
 * This class polls the log of all the latest activities in the hub
 */
public class ActionActivity extends AppCompatActivity {

    /**
     * The backend auth token
     */
    private String API_TOKEN = "";
    /**
     * The ID of the hub the user wants to use in this session.
     */
    private int PREFERRED_HUB_ID = -1;

    private ListView listView;
    private TextView bigMessage;
    private TextView explanationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action);
        Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.label_actions_title));
        }
        listView = (ListView) findViewById(R.id.actions_list_view);
        bigMessage = (TextView) findViewById(R.id.no_actions_text_view);
        explanationMessage = (TextView) findViewById(R.id.no_actions_explanation_text_view);
        getFeedActions();
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
     * This method will try to obtain all the Living hubs the user owns/is invited.
     */
    private void getFeedActions() {
        ActionClient actionsClient = RetrofitServiceGenerator.createService(ActionClient.class, API_TOKEN);
        // Create a call instance for looking up Retrofit contributors.
        Call<List<FeedAction>> call = actionsClient.actions(PREFERRED_HUB_ID);
        //Log.d("OkHttp", String.format("Sending request %s ",call.request().toString()));
        call.enqueue(new Callback<List<FeedAction>>() {
            @Override
            public void onResponse(Call<List<FeedAction>> call, Response<List<FeedAction>> response) {
                if (response.isSuccessful()) {
                    // If the user got hubs he can select one to use. If he do not then send it to register one activity.
                    if (!response.body().isEmpty()) {
                        createUI(response.body());
                    } else {
                        listView.setVisibility(View.GONE);
                        bigMessage.setVisibility(View.VISIBLE);
                        explanationMessage.setVisibility(View.VISIBLE);
                    }
                } else {
                    AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToFeedActions", "The user do not have access to the Feed? token:" + API_TOKEN);
                }
            }

            @Override
            public void onFailure(Call<List<FeedAction>> call, Throwable t) {
                // something went completely south (like no internet connection)
                showNoInternetMessage();
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    private void createUI(List<FeedAction> actions) {
        listView.setVisibility(View.VISIBLE);
        bigMessage.setVisibility(View.GONE);
        explanationMessage.setVisibility(View.GONE);
        FeedActionAdapter customAdapter = new FeedActionAdapter(getApplicationContext(), actions);
        listView.setAdapter(customAdapter);
    }

    /**
     * This method will show a no internet error message to the user
     */
    private void showNoInternetMessage() {
        Toast.makeText(ActionActivity.this, getResources().getString(R.string.toast_missing_internet),
                Toast.LENGTH_SHORT).show();
    }

    private interface ActionClient {
        @GET("hubs/{hub_id}/actions/")
        Call<List<FeedAction>> actions(
                @Path("hub_id") int hub_id
        );
    }
}
