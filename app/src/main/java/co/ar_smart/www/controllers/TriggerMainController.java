package co.ar_smart.www.controllers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.helpers.JWTManager;
import co.ar_smart.www.helpers.RetrofitServiceGenerator;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;
import co.ar_smart.www.pojos.Trigger;
import co.ar_smart.www.triggers.TriggerPropertiesActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

import static co.ar_smart.www.helpers.Constants.*;

public class TriggerMainController extends AppCompatActivity
{
    /**
     * The backend auth token
     */
    private String API_TOKEN = "";
    /**
     * the id of the hub where this device is in
     */
    private int PREFERRED_HUB_ID = -1;
    /**
     * The array of current modes for the user
     */
    private ArrayList<Mode> modes;
    /**
     * The array of current trigger for the given endpoint over the selected hub
     */
    private ArrayList<Trigger> triggers;

    /**
     * The selected endpoint for which a trigger want to be added o deleted
     */
    private Endpoint endpoint;
    /**
     * Represent if the endpoint is a binary or range sensor
     */
    private boolean binary_sensor;
    /**
     * Button for positive event
     */
    private Button btn_on_positive;
    /**
     * Button for positive event
     */
    private Button btn_on_negative;
    /**
     * Index of element to remove
     */
    private int indexToRemove;
    /**
     * Current context
     */
    private Context mContext;

    /**
     * Tri
     * @param savedInstanceState
     */


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trigger_main_controller);
        setTitle(endpoint.getName());

        mContext = this;
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        endpoint = intent.getParcelableExtra(EXTRA_OBJECT);
        modes = intent.getParcelableArrayListExtra(EXTRA_ADDITIONAL_OBJECT);
        binary_sensor = intent.getBooleanExtra(EXTRA_BOOLEAN, true);
        btn_on_positive = (Button) findViewById(R.id.btn_on_positive);
        btn_on_negative = (Button) findViewById(R.id.btn_on_negative);
        if (btn_on_positive != null)
        {
            btn_on_positive.setEnabled(false);
            btn_on_positive.setText((binary_sensor)?R.string.btn_trigger_on_positive_binary:R.string.btn_trigger_on_positive_range);
        }
        if (btn_on_negative != null)
        {
            btn_on_negative.setEnabled(false);
            btn_on_negative.setText((binary_sensor)?R.string.btn_trigger_on_negative_binary:R.string.btn_trigger_on_negative_range);
        }
        getApiToken();
    }

    private void getApiToken()
    {
        if (!JWTManager.validateJWT(API_TOKEN))
        {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME,
                    Context.MODE_PRIVATE);
            // Get values using keys
            String EMAIL = settings.getString(PREF_EMAIL, DEFAULT_EMAIL);
            String PASSWORD = settings.getString(PREF_PASSWORD, DEFAULT_PASSWORD);
            JWTManager.getApiToken(EMAIL, PASSWORD, new JWTManager.JWTCallbackInterface()
            {
                @Override
                public void onFailureCallback()
                {
                    Constants.showCustomMessage(mContext,getResources().getString(R.string.toast_login_failure));
                }

                @Override
                public void onSuccessCallback(String nToken)
                {
                    API_TOKEN = nToken;
                    savePreferences();
                    getTriggers();
                }

                @Override
                public void onUnsuccessfulCallback()
                {
                    Constants.showCustomMessage(mContext,getResources().getString(R.string.toast_login_bad_credentials));
                }

                @Override
                public void onExceptionCallback()
                {
                    Constants.showCustomMessage(mContext,getResources().getString(R.string.toast_login_server_error));
                }
            });
        }
        else
        {
            getApiToken();
        }
    }

    /**
     * This method will save the user preferred hub for using until he change it or do logout.
     * Since every user can own multiple hubs, is necessary to pick at least one for this session.
     * The user can change the hub or this will be deleted on logout.
     */
    private void savePreferences(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("API_TOKEN", String.valueOf(API_TOKEN));
        editor.apply();
    }

    /**
     * Method that returns the days of the week in words
     */

    private void getTriggers()
    {
        TriggerMainControllerClient livingHomeClient = RetrofitServiceGenerator.createService(TriggerMainControllerClient.class, API_TOKEN);
        Call<List<Trigger>> call = livingHomeClient.triggers("" + PREFERRED_HUB_ID, "" + endpoint.getId());
        call.enqueue(new Callback<List<Trigger>>()
        {
            @Override
            public void onResponse(Call<List<Trigger>> call, Response<List<Trigger>> response)
            {
                if (response.isSuccessful())
                {
                    Log.d("DEBUG", response.body().toString());
                    triggers = new ArrayList<>();
                    //If user got no endpoints redirect to management activity. set grid layout otherwise.
                    if (!response.body().isEmpty())
                    {
                        for (Trigger trigger : response.body())
                        {
                            if (!triggers.contains(trigger))
                            {
                                trigger.setMode(getModefromTrigger(trigger.getPayload()));
                                triggers.add(trigger);
                                Log.d("DEVICE:", trigger.getMode() + "_" + trigger.getMinute_of_day() + "_" + trigger.getDays_of_the_week());
                            }
                        }
                    }
                    if (btn_on_positive != null)
                    {
                        initializeButton(btn_on_positive, true);
                    }
                    if (btn_on_negative != null)
                    {
                        initializeButton(btn_on_negative, false);
                    }
                }
                else
                {
                    // error response, no access to resource?
                    // if the user no longer has access to the endpoints (because he got uninvited) ask for select new hub.
//                    getHubs();
                }
            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call<List<Trigger>> call, Throwable t)
            {
                // something went completely south (like no internet connection)
                Constants.showNoInternetMessage(getApplicationContext());
                try
                {
                    Log.d("Error", call.request().body().toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                t.printStackTrace();
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    private Mode getModefromTrigger(List<Command> payload)
    {
        Mode response = null;
        boolean finished = false;
        for (int i = 0; i < modes.size() && !finished ; i++)
        {
            List<Command> currentMode = modes.get(i).getPayload();
            boolean endRevision = false;
            if(payload.size()==currentMode.size())
            {
                for (int j = 0; j < payload.size() && !endRevision; j++)
                {
                    if(!payload.get(i).equals(currentMode.get(i)))
                    {
                        endRevision=true;
                    }
                }
                if(!endRevision)
                {
                    finished = true;
                    response = modes.get(i);
                }
            }
        }
        return response;
    }

    public void initializeButton(Button button, final boolean action)
    {
        final String[] tempTriggers = getTriggersAsList(action);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (tempTriggers.length > 0)
                {
                    getOptionsDialog(tempTriggers,action).show();
                }
                else
                {
                    crearTrigger(action);
                }
            }
        });
        button.setEnabled(true);
    }

    /**
     * Method that returns the list of triggers for specific action
     *
     * @param action - true Positive action or
     *               false Negative action
     * @return list of triggers
     */
    private String[] getTriggersAsList(boolean action)
    {
        ArrayList<String> tempTriggers = new ArrayList<>();
        for (int i = 0; i < triggers.size(); i++)
        {
            Trigger tempTrig = triggers.get(i);
            if (binary_sensor)
            {
                if (action && (tempTrig.getPrimary_value() == 1))
                {
                    tempTriggers.add(tempTrig.getMode().getName() + "_" + tempTrig.getDaysAsString() + "_" + tempTrig.getHoursAsString());
                }
                else if (!action && (tempTrig.getPrimary_value() == 0))
                {
                    tempTriggers.add(tempTrig.getMode().getName() + "_" + tempTrig.getDaysAsString() + "_" + tempTrig.getHoursAsString());
                }
            }
            else
            {
                if (action && (tempTrig.getOperand().equals(Constants.Operand.between)))
                {
                    tempTriggers.add(tempTrig.getMode().getName() + "_" + tempTrig.getDaysAsString() + "_" + tempTrig.getHoursAsString());
                }
                else if (action && (tempTrig.getOperand().equals(Constants.Operand.not_between)))
                {
                    tempTriggers.add(tempTrig.getMode().getName() + "_" + tempTrig.getDaysAsString() + "_" + tempTrig.getHoursAsString());
                }
            }
        }
        return tempTriggers.toArray(new String[0]);
    }

    private AlertDialog getOptionsDialog(final String[] stateTriggers, final boolean action)
    {
        AlertDialog alertD = new AlertDialog.Builder(mContext)
                .setMessage(R.string.dialog_already_have_triggers)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_create_trigger, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        crearTrigger(action);
                    }
                })
                .setNegativeButton(R.string.dialog_remove_trigger, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle(R.string.dialog_remove_trigger_message)
                                .setItems(stateTriggers, new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        removeTrigger(triggers.get(which));
                                    }
                                });
                        AlertDialog app = builder.create();
                        app.show();
                    }
                })
                .create();

        return alertD;

    }

    private void crearTrigger(boolean action)
    {
        Trigger trigger = new Trigger(endpoint, PREFERRED_HUB_ID);
        trigger.setTrigger_type((binary_sensor) ? Constants.Trigger_type.BINARY : Constants.Trigger_type.RANGE);
        trigger.setOperand((binary_sensor)?Operand.equals:(action)?Operand.between:Operand.not_between);
        if(binary_sensor)
        {
            trigger.setPrimary_value((action)?1:0);
        }
        Intent i = new Intent(mContext, TriggerPropertiesActivity.class);
        i.putParcelableArrayListExtra("modes",modes);
        i.putExtra(EXTRA_MESSAGE, API_TOKEN);
        i.putExtra("trigger", trigger);
        i.putExtra("binary_sensor", binary_sensor);
        mContext.startActivity(i);
    }

    private void removeTrigger(Trigger trigger)
    {
        TriggerMainControllerClient livingHomeClient = RetrofitServiceGenerator.createService(TriggerMainControllerClient.class, API_TOKEN);
        Call call = livingHomeClient.deleteTriggers("" + PREFERRED_HUB_ID, "" + endpoint.getId(), "" + trigger.getIdTrigger());
        call.enqueue(new Callback()
        {
            @Override
            public void onResponse(Call call, Response response)
            {
                if (response.isSuccessful()) {
                    Toast.makeText(mContext,"Trigger removed", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("DEBUGGG", response.message() + " - " + response.code());
                    Constants.showNoInternetMessage(mContext);
                }
            }

            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             *
             * @param call
             * @param t
             */
            @Override
            public void onFailure(Call call, Throwable t)
            {
                // something went completely south (like no internet connection)
                Constants.showNoInternetMessage(getApplicationContext());
                try
                {
                    Log.d("Error", call.request().body().toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                t.printStackTrace();
                AnalyticsApplication.getInstance().trackException(new Exception(t));
            }
        });
    }

    /**
     * This interface implements a Retrofit interface for the Home Activity
     */
    private interface TriggerMainControllerClient
    {
        @GET("hubs/{id_hub}/endpoints/{id_endpoint}/triggers/")
        Call<List<Trigger>> triggers(
                @Path("id_hub") String id_hub, @Path("id_endpoint") String id_endpoint
        );

        @DELETE("hubs/{id_hub}/endpoints/{id_endpoint}/triggers/{id_trigger}")
        Call<List<Trigger>> deleteTriggers(
                @Path("id_hub") String id_hub, @Path("id_endpoint") String id_endpoint, @Path("id_trigger") String id_trigger
        );
    }
}
