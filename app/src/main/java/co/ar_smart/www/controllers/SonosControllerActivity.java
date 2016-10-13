package co.ar_smart.www.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import co.ar_smart.www.adapters.music_player.TrackAdapter;
import co.ar_smart.www.analytics.AnalyticsApplication;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.music_player.MusicTrack;
import co.ar_smart.www.pojos.sonos.SonosEndpoint;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE_PREF_HUB;
import static co.ar_smart.www.helpers.Constants.EXTRA_OBJECT;

/**
 * This class will be the controller of a Sonos Music Player
 * Created by Gabriel on 5/11/2016.
 */
public class SonosControllerActivity extends AppCompatActivity {

    private static final String PLAY_MODE_NORMAL = "NORMAL";
    private static final String PLAY_MODE_SHUFFLE_NOREPEAT = "SHUFFLE_NOREPEAT";
    private static final String PLAY_MODE_REPEAT_ALL = "REPEAT_ALL";
    private static final String PLAY_MODE_SHUFFLE = "SHUFFLE";
    boolean shuffleOn = false;
    boolean repeatOn = false;
    /**
     * The backend auth token
     */
    private String API_TOKEN = "";
    /**
     * The pojo containing all the commands to send/receive to/from the sonos
     */
    private SonosEndpoint sonosEndpoint;
    /**
     * the id of the hub where this device is in
     */
    private int PREFERRED_HUB_ID = -1;
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
    /**
     * a flag for toggling the play/pause button
     */
    private boolean isPlaying = false;
    /**
     * the adapter for the queue list view
     */
    private TrackAdapter adapter;
    /**
     * the volume bar controller
     */
    private SeekBar volControl;
    /**
     * the play/pause button
     */
    private Button playPauseButton;
    private Runnable runnable;
    private RelativeLayout spinner;
    private TextView current_track_text;
    private int track_number = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sonos_controller);
        final Intent intent = getIntent();
        API_TOKEN = intent.getStringExtra(EXTRA_MESSAGE);
        PREFERRED_HUB_ID = intent.getIntExtra(EXTRA_MESSAGE_PREF_HUB, -1);
        Endpoint endpoint = intent.getParcelableExtra(EXTRA_OBJECT);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(endpoint.getName());
        }
        current_track_text = (TextView) findViewById(R.id.sonos_current_track_name_text_view);
        sonosEndpoint = new SonosEndpoint(endpoint);
        getUI();
        volControl = (SeekBar) findViewById(R.id.sonos_sound_seek_bar);
        if (volControl != null) {
            volControl.setMax(100);
        }
        volControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                volumeCommand(progress);
            }
        });
        playPauseButton = (Button) findViewById(R.id.sonos_play_pause_button);
        if (playPauseButton != null) {
            playPauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        pauseCommand();
                        playPauseButton.setBackgroundResource(R.drawable.play_icon);
                    } else {
                        playCommand();
                        playPauseButton.setBackgroundResource(R.drawable.pause_icon);
                    }
                    isPlaying = !isPlaying;
                }
            });
        }
        Button backButton = (Button) findViewById(R.id.sonos_back_button);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backCommand();
                }
            });
        }
        Button nextButton = (Button) findViewById(R.id.sonos_next_button);
        if (nextButton != null) {
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextCommand();
                }
            });
        }
        spinner = (RelativeLayout) findViewById(R.id.sonos_ui_loader);
        final Button repeatButton = (Button) findViewById(R.id.sonos_repeat_button);
        final Button shuffleButton = (Button) findViewById(R.id.sonos_shuffle_button);
        if(repeatButton != null && shuffleButton != null)
        {
            repeatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (repeatOn && !shuffleOn) {
                        //NORMAL
                        shuffleRepeatCommand(PLAY_MODE_NORMAL);
                        repeatButton.setBackgroundResource(R.drawable.repeat_release);
                        repeatOn = false;
                    } else if (repeatOn) {
                        //SHUFFLE_NOREPEAT
                        shuffleRepeatCommand(PLAY_MODE_SHUFFLE_NOREPEAT);
                        repeatButton.setBackgroundResource(R.drawable.repeat_release);
                        repeatOn = false;
                    } else if (!shuffleOn) {
                        //REPEAT_ALL
                        shuffleRepeatCommand(PLAY_MODE_REPEAT_ALL);
                        repeatButton.setBackgroundResource(R.drawable.repeat_pressed);
                        repeatOn = true;
                    } else {
                        //!repeatOn && shuffleOn
                        //SHUFFLE
                        shuffleRepeatCommand(PLAY_MODE_SHUFFLE);
                        repeatButton.setBackgroundResource(R.drawable.repeat_pressed);
                        repeatOn = true;
                    }
                }
            });
            shuffleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (repeatOn && !shuffleOn) {
                        //SHUFFLE
                        shuffleRepeatCommand(PLAY_MODE_SHUFFLE);
                        shuffleButton.setBackgroundResource(R.drawable.shuffle_pressed);
                        shuffleOn = true;
                    } else if (repeatOn) {
                        //REPEAT_ALL
                        shuffleRepeatCommand(PLAY_MODE_REPEAT_ALL);
                        shuffleButton.setBackgroundResource(R.drawable.shuffle_released);
                        shuffleOn = false;
                    } else if (!shuffleOn) {
                        //SHUFFLE_NOREPEAT
                        shuffleRepeatCommand(PLAY_MODE_SHUFFLE_NOREPEAT);
                        shuffleButton.setBackgroundResource(R.drawable.shuffle_pressed);
                        shuffleOn = true;
                    } else {
                        //!repeatOn && shuffleOn
                        //NORMAL
                        shuffleRepeatCommand(PLAY_MODE_NORMAL);
                        shuffleButton.setBackgroundResource(R.drawable.shuffle_released);
                        shuffleOn = false;
                    }
                }
            });
        }

    }

    private void shuffleRepeatCommand(String mode) {
        Log.d("SHUFFLE/REPEAT COMMAND", sonosEndpoint.getShuffleRepeatCommand(mode));
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(sonosEndpoint.get_play()), new CommandManager.ResponseCallbackInterface() {
                    @Override
                    public void onFailureCallback() {
                        Constants.showNoInternetMessage(getApplicationContext());
                    }

                    @Override
                    public void onSuccessCallback(JSONObject jObject) {
                    }

                    @Override
                    public void onUnsuccessfulCallback() {
                        AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosShuffle", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
                    }
                });
    }

    private void setPlayState() {
        playPauseButton.setBackgroundResource(R.drawable.pause_icon);
        isPlaying = true;
    }

    /**
     * This method send a "play track" command to the sonos
     */
    private void playCommand() {
        Log.d("PLAY COMMAND", sonosEndpoint.get_play());
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(sonosEndpoint.get_play()), new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }
            @Override
            public void onSuccessCallback(JSONObject jObject) {
            }

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosPlay", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
            }
        });
    }

    /**
     * This method send a "pause track" command to the sonos
     */
    private void pauseCommand() {
        Log.d("PAUSE COMMAND", sonosEndpoint.get_pause());
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(sonosEndpoint.get_pause()), new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
            }

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosPause", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
            }
        });
    }

    /**
     * This method send a "play previous track" command to the sonos
     */
    private void backCommand() {
        track_number--;
        updateCurrentTrack();
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(SonosEndpoint.getBack()), new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
            }

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosPrev", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
            }
        });
    }

    /**
     * This method send a "play next track" command to the sonos
     */
    private void nextCommand() {
        track_number++;
        updateCurrentTrack();
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(SonosEndpoint.getNext()), new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
            }

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosNext", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
            }
        });
    }

    /**
     * This method sends a command to sets a new volume in the sonos
     *
     * @param volume the new volume to set in the sonos device
     */
    private void volumeCommand(int volume) {
        Log.d("VOLUME COMMAND", sonosEndpoint.getVolumeCommand(volume));
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(sonosEndpoint.getVolumeCommand(volume)), new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
            }

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosVolume", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
            }
        });
    }

    /**
     * This method sends a command to plays a track from the queue
     *
     * @param position the position of the song to play in the queue
     */
    private void playTrackFromQueue(int position) {
        setPlayState();
        track_number = position;
        updateCurrentTrack();
        Log.d("TRACK COMMAND", sonosEndpoint.getPlayTrackFromQueueCommand(position));
        CommandManager.sendCommandWithoutResult(API_TOKEN, PREFERRED_HUB_ID,
                CommandManager.getFormattedCommand(sonosEndpoint.getPlayTrackFromQueueCommand(position)), new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {}

            @Override
            public void onUnsuccessfulCallback() {
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosPlay", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
            }
        });
    }

    private void updateCurrentTrack() {
        int size = sonosEndpoint.getQueue().size();
        if (track_number < 0) {
            track_number = 0;
        }
        if (size > track_number) {
            current_track_text.setText(sonosEndpoint.getTrackFromQueue(track_number).getTitle());
        } else {
            track_number = size;
        }
    }

    /**
     * This method send a command to ask for the JSON needed to pain the UI.
     * This includes the queue with all their songs, the playing state of the sonos and more.
     */
    private void getUI() {
        stopHandlerFlag = false;
        CommandManager.sendCommandWithResult(API_TOKEN, PREFERRED_HUB_ID, sonosEndpoint.get_ui(), new CommandManager.CommandWithResultsCallbackInterface() {
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
                AnalyticsApplication.getInstance().trackEvent("Weird Event", "NoAccessToSonosUi", "Was a bad request?:" + API_TOKEN + " " + sonosEndpoint.toString());
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
                try {
                    Log.d("Response", jObject.toString());
                    if (jObject.has("status")) {
                        if (!jObject.getString("status").equalsIgnoreCase("processing")) {
                            stopHandlerFlag = true;
                            JSONObject ui = jObject.getJSONObject("response");
                            if (!ui.has("ERROR")) {
                                sonosEndpoint.setMute(ui.getBoolean("mute"));
                                sonosEndpoint.setVolume(ui.getInt("volume"));
                                sonosEndpoint.setState(ui.getString("state"));
                                sonosEndpoint.setPlay_mode(PLAY_MODE_NORMAL);//sonosEndpoint.setPlay_mode(ui.getString("play_mode"));
                                JSONArray tracks = ui.getJSONArray("queue");
                                for (int i = 0; i < tracks.length(); i++) {
                                    JSONObject t = tracks.getJSONObject(i);
                                    sonosEndpoint.addMusicTrack(new MusicTrack(t.getString("title"), t.getInt("queue_number"), t.getString("album_art_uri"), t.getString("item_id"), t.getString("artist"), t.getString("desc")));
                                }
                                JSONObject t = ui.getJSONObject("current_track");
                                MusicTrack current_track = new MusicTrack(t.getString("title"), t.getInt("playlist_position"), t.getString("album_art"), "", t.getString("artist"), t.getString("title"));
                                track_number = t.getInt("playlist_position");
                                sonosEndpoint.setCurrent_track(current_track);
                                addUIComponents();
                            } else {
                                manageNoSonosConnection();
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

    private void manageNoSonosConnection() {
        SonosControllerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.removeAllViews();
                TextView message = new TextView(getApplicationContext());
                message.setText(R.string.unreachable_sonos_message);
                message.setGravity(View.TEXT_ALIGNMENT_CENTER);
                spinner.addView(message);
            }
        });
    }

    /**
     * This method will add the corresponding tracks into the listvew defined in the UI
     */
    private void addUIComponents() {
        SonosControllerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
                TextView no_queue = (TextView) findViewById(R.id.message_no_songs_in_queue);
                if (!sonosEndpoint.getQueue().isEmpty()) {
                    no_queue.setVisibility(View.GONE);
                    adapter = new TrackAdapter(SonosControllerActivity.this, sonosEndpoint.getQueue());
                    // Attach the adapter to a ListView
                    ListView listView = (ListView) findViewById(R.id.music_tracks_list_view);
                    listView.setVisibility(View.VISIBLE);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            playTrackFromQueue(position);
                            Toast.makeText(getApplicationContext(), sonosEndpoint.getQueue().get(position).toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    no_queue.setVisibility(View.VISIBLE);
                }
                volControl.setProgress(sonosEndpoint.getVolume());
                if (sonosEndpoint.getState().equalsIgnoreCase("PLAYING")) {
                    isPlaying = true;
                    playPauseButton.setBackgroundResource(R.drawable.pause_icon);
                }
                current_track_text.setText(sonosEndpoint.getCurrent_track().getTitle());
            }
        });
    }

    /**
     * This method will poll the server response every 2 seconds until is stopped by the flag or the timeout expires
     */
    private void loadAsyncResponse() {
        runnable = new Runnable() {
            @Override
            public void run() {
                // while the handler is not stoped create a new request every time delta
                Date now = new Date();
                if (!stopHandlerFlag && timeoutDate.after(now)) {
                    processResponse();
                    pollingResponseHandler.postDelayed(this, 2000);
                }
            }
        };
        // start it with:
        pollingResponseHandler.post(runnable);
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

    private void stopPolling() {
        stopHandlerFlag = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        pollingResponseHandler.removeCallbacks(runnable);
        stopPolling();
    }
}
