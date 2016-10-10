package co.ar_smart.www.controllers.hue;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.ar_smart.www.adapters.hue.BulbsAdapter;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.hue.HueEndpoint;
import co.ar_smart.www.pojos.hue.HueLight;
import co.ar_smart.www.pojos.hue.HueLightGroup;
import co.ar_smart.www.pojos.hue.IHueObject;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

public class HueControllerActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String API_TOKEN;
    private int PREFERRED_HUB_ID;
    private ArrayList<IHueObject> bulbs = new java.util.ArrayList<>();
    private ArrayList<IHueObject> light_groups = new java.util.ArrayList<>();
    private HueEndpoint hueEndpoint;
    private BulbsAdapter adapter;
    /**
     * a boolean flag for stopping the polling process
     */
    private boolean stopHandlerFlag;
    /**
     * the polling handler
     */
    private Handler pollingResponseHandler = new Handler();
    /**
     * the timeout date for also stopping the handler polling
     */
    private Date timeoutDate;
    /**
     * the url to poll the response from
     */
    private String pollingURL;

    private Runnable runnableResponse;

    public String getAPI_TOKEN() {
        return API_TOKEN;
    }

    public int getPREFERRED_HUB_ID() {
        return PREFERRED_HUB_ID;
    }

    public HueEndpoint getHueEndpoint() {
        return hueEndpoint;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hue_controller);

        //TODO if respnse fail it could be a registration fail


        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        Endpoint endpoint = intent.getParcelableExtra(EXTRA_OBJECT);
        hueEndpoint = new HueEndpoint(endpoint);
        //setTitle(endpoint.getName());

        Toolbar toolbar = (Toolbar) findViewById(R.id.hue_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(endpoint.getName());
        }


        viewPager = (ViewPager) findViewById(R.id.hue_viewpager);
        tabLayout = (TabLayout) findViewById(R.id.hue_tabs);
        getUI();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(HueBulbsFragment.newInstance(bulbs), "Bulbs");
        adapter.addFragment(HueBulbsFragment.newInstance(light_groups), "Groups");
        viewPager.setAdapter(adapter);
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
     * This method send a command to ask for the JSON needed to pain the UI.
     * This includes the queue with all their songs, the playing state of the sonos and more.
     */
    private void getUI() {
        stopHandlerFlag = false;
        CommandManager.sendCommandWithResult(API_TOKEN, PREFERRED_HUB_ID, hueEndpoint.get_ui(), new CommandManager.CommandWithResultsCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(String pollingUrl, int timeout) {
                timeoutDate = Constants.calculateTimeout(timeout);
                pollingURL = pollingUrl;
                loadAsyncResponse();
            }

            @Override
            public void onUnsuccessfulCallback() {
            }
        });
    }

    /**
     * This method polls the response from the server
     */
    private void processResponse() {
        CommandManager.getCommandResult(API_TOKEN, pollingURL, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                stopHandlerFlag = true;
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
                Log.d("RESPONSe", jObject.toString());
                try {
                    if (jObject.has("status")) {
                        if (!jObject.getString("status").equalsIgnoreCase("processing")) {
                            stopHandlerFlag = true;
                            JSONObject ui = jObject.getJSONObject("response");
                            if (ui.has("ERROR")) {
                                showMessage(ui.getString("ERROR"));
                            } else {
                                JSONArray lights = ui.getJSONArray("lights");
                                JSONArray groups = ui.getJSONArray("groups");

                                Type listType = new TypeToken<ArrayList<HueLightGroup>>() {
                                }.getType();
                                light_groups = new Gson().fromJson(groups.toString(), listType);
                                Log.d("GROUPS", light_groups.toString());

                                Type listType2 = new TypeToken<ArrayList<HueLight>>() {
                                }.getType();
                                bulbs = new Gson().fromJson(lights.toString(), listType2);
                                Log.d("BULBS", bulbs.toString());

                                if (light_groups.get(0) instanceof HueLightGroup) {
                                    Log.d("=------====--------", "Es grupo");
                                    for (int i = 0; i < light_groups.size(); i++) {
                                        for (IHueObject temp : light_groups.get(i).getLights()) {
                                            Log.d("1", bulbs.toString());
                                            Log.d("2", temp.toString());
                                            if (!bulbs.contains(temp)) {
                                                bulbs.add(temp);
                                            }
                                        }
                                    }
                                } else if (light_groups.get(0) instanceof HueLight) {
                                    Log.d("=------====--------", "Es luz sola");
                                }

                                addUIComponents();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUnsuccessfulCallback() {
                stopHandlerFlag = true;
            }
        });
    }

    private void showMessage(final String message) {
        HueControllerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(HueControllerActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_device_added);

                TextView title = (TextView) dialog.findViewById(R.id.titleAddDevice);
                title.setText(message);

                TextView subtitle = (TextView) dialog.findViewById(R.id.subtitleAddDevice);
                subtitle.setText("Please press it and try again.");

                Button dialogButton = (Button) dialog.findViewById(R.id.btnDialogDevAdd);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        finish();
                    }
                });

                dialog.show();
            }
        });
    }

    /**
     * This method will add the corresponding tracks into the listvew defined in the UI
     */
    private void addUIComponents() {
        HueControllerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupViewPager(viewPager);
                if (tabLayout != null) {
                    tabLayout.setupWithViewPager(viewPager);
                }
            }
        });
    }

    /**
     * This method will poll the server response every 2 seconds until is stopped by the flag or the timeout expires
     */
    private void loadAsyncResponse() {
        runnableResponse = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stoped create a new request every time delta
                if (!stopHandlerFlag && timeoutDate.after(new Date())) {
                    processResponse();
                    pollingResponseHandler.postDelayed(this, 2000);
                }
            }
        };
        // start it with:
        pollingResponseHandler.post(runnableResponse);
    }

    private void stopPolling() {
        stopHandlerFlag = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        pollingResponseHandler.removeCallbacks(runnableResponse);
        stopPolling();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
