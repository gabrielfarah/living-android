package co.ar_smart.www.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.Command;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;

/**
 * Created by Gabriel on 5/25/2016.
 */
public class SceneListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Endpoint> endpoints; //we only need the commands here to reconstruct the list
    private LayoutInflater inflater;
    private Mode mode;

    public SceneListAdapter(Context c, Mode nMode, ArrayList<Endpoint> nEndpoints) {
        context = c;
        endpoints = nEndpoints;
        inflater = (LayoutInflater.from(c));
        mode = nMode;
        if (mode == null) {
            mode = new Mode();
        }
    }

    private View getConvertView(String ui_class_command) {
        if (ui_class_command.equalsIgnoreCase(Constants.UI_CLASS_ZWAVE_LEVEL_SWITCH) ||
                ui_class_command.equalsIgnoreCase(Constants.UI_CLASS_ZWAVE_LEVEL_LIGHT) ||
                ui_class_command.equalsIgnoreCase(Constants.UI_CLASS_ZWAVE_SHADES)) {
            return inflater.inflate(R.layout.mode_list_slider_item, null);
        } else {
            return inflater.inflate(R.layout.mode_list_on_off_item, null);
        }
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public int getCount() {
        return endpoints.size();
    }

    @Override
    public Object getItem(int position) {
        return endpoints.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder; // view lookup cache stored in tag
        String ui_class_command = endpoints.get(position).getUi_class_command();
        /*if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = getConvertView(ui_class_command);
            viewHolder.endpoint_in_scene_check_box = (CheckBox) convertView.findViewById(R.id.endpoint_in_scene_check_box);
            viewHolder.icon_endpoint_in_scene = (ImageView) convertView.findViewById(R.id.icon_endpoint_in_scene);
            viewHolder.icon_endpoint_in_scene.setImageResource(HomeGridDevicesAdapter.getDrawableFromString(endpoints.get(position).getImage()));
            viewHolder.name_endpoint_in_scene = (TextView) convertView.findViewById(R.id.name_endpoint_in_scene);
            viewHolder.name_endpoint_in_scene.setText(endpoints.get(position).getName());
            viewHolder.state_endpoint_in_scene_text_view = (TextView) convertView.findViewById(R.id.state_endpoint_in_scene_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }*/
        viewHolder = new ViewHolder();
        convertView = getConvertView(ui_class_command);
        viewHolder.endpoint_in_scene_check_box = (CheckBox) convertView.findViewById(R.id.endpoint_in_scene_check_box);
        viewHolder.icon_endpoint_in_scene = (ImageView) convertView.findViewById(R.id.icon_endpoint_in_scene);
        viewHolder.icon_endpoint_in_scene.setImageResource(HomeGridDevicesAdapter.getDrawableFromString(endpoints.get(position).getImage()));
        viewHolder.name_endpoint_in_scene = (TextView) convertView.findViewById(R.id.name_endpoint_in_scene);
        viewHolder.name_endpoint_in_scene.setText(endpoints.get(position).getName());
        viewHolder.state_endpoint_in_scene_text_view = (TextView) convertView.findViewById(R.id.state_endpoint_in_scene_text_view);
        // Validate if there are previous states for this endpoint given the mode in the constructor
        Command temp;
        int index = -1;
        boolean havePreviousData = false;
        if (!mode.getPayload().isEmpty()) {
            temp = new Command(endpoints.get(position));
            index = mode.getPayload().indexOf(temp);
            if (index != -1) {
                havePreviousData = true;
            }
        }
        // Set the behavior for all the "Level" type devices
        if (Constants.UI_CLASS_ZWAVE_LEVEL_SWITCH.equalsIgnoreCase(ui_class_command) ||
                Constants.UI_CLASS_ZWAVE_SHADES.equalsIgnoreCase(ui_class_command) ||
                Constants.UI_CLASS_ZWAVE_LEVEL_LIGHT.equalsIgnoreCase(ui_class_command)) {
            final Command c = new Command(endpoints.get(position));
            c.setFunction(Constants.ZWAVE_FUNCTION_LEVEL_SET);
            final int maxRange = 99;
            viewHolder.state_endpoint_in_scene_seekbar = (SeekBar) convertView.findViewById(R.id.state_endpoint_in_scene_seekbar);
            viewHolder.state_endpoint_in_scene_seekbar.setMax(maxRange);
            viewHolder.state_endpoint_in_scene_seekbar.setKeyProgressIncrement(1);
            viewHolder.state_endpoint_in_scene_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    viewHolder.state_endpoint_in_scene_text_view.setText(String.format(context.getResources().getString(R.string.label_level_percentage),
                            seekBar.getProgress() == maxRange ? maxRange + 1 : seekBar.getProgress()));
                    // If the values are modified while the user does not check/unckeck the checkbox, then we update the value from the controller.
                    int exist = mode.getPayload().indexOf(c);
                    if (exist != -1) {
                        mode.getPayload().get(exist).setV(seekBar.getProgress());
                    }
                }
            });
            if (havePreviousData) {
                int v = mode.getPayload().get(index).getV();
                viewHolder.endpoint_in_scene_check_box.setChecked(true);
                viewHolder.state_endpoint_in_scene_seekbar.setProgress(v);
                viewHolder.state_endpoint_in_scene_text_view.setText(String.format(context.getResources().getString(R.string.label_level_percentage),
                        v == maxRange ? maxRange + 1 : v));
            }
            viewHolder.endpoint_in_scene_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        c.setV(viewHolder.state_endpoint_in_scene_seekbar.getProgress());
                        mode.addCommandToPayload(c);
                    } else {
                        mode.removeCommandFromPayload(c);
                    }
                }
            });
        } else if (Constants.UI_CLASS_ZWAVE_LOCK.equalsIgnoreCase(ui_class_command)) {
            final Command c = new Command(endpoints.get(position));
            c.setFunction(Constants.ZWAVE_FUNCTION_DOOR_SET);
            viewHolder.state_endpoint_in_scene_toggle = (ToggleButton) convertView.findViewById(R.id.state_endpoint_in_scene_toggle);
            viewHolder.state_endpoint_in_scene_toggle.setTextOff(context.getResources().getString(R.string.label_unlock));
            viewHolder.state_endpoint_in_scene_toggle.setTextOn(context.getResources().getString(R.string.label_lock));
            final int[] state = {0};
            viewHolder.state_endpoint_in_scene_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                    if (!isChecked) {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_unlocked));
                        state[0] = 0;
                    } else {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_locked));
                        state[0] = 255;
                    }
                    // If the values are modified while the user does not check/unckeck the checkbox, then we update the value from the controller.
                    int exist = mode.getPayload().indexOf(c);
                    if (exist != -1) {
                        mode.getPayload().get(exist).setV(state[0]);
                    }
                }
            });
            if (havePreviousData) {
                int v = mode.getPayload().get(index).getV();
                if (v > 0) {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(true);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_locked));
                } else {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(false);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_unlocked));
                }
                viewHolder.endpoint_in_scene_check_box.setChecked(true);
            }
            viewHolder.endpoint_in_scene_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        c.setV(state[0]);
                        mode.addCommandToPayload(c);
                    } else {
                        mode.removeCommandFromPayload(c);
                    }
                }
            });
        } else if (Constants.UI_CLASS_SONOS.equalsIgnoreCase(ui_class_command)) {
            final Command c = new Command(endpoints.get(position));
            c.setTarget("sonos");
            viewHolder.state_endpoint_in_scene_toggle = (ToggleButton) convertView.findViewById(R.id.state_endpoint_in_scene_toggle);
            viewHolder.state_endpoint_in_scene_toggle.setTextOff(context.getResources().getString(R.string.label_play));
            viewHolder.state_endpoint_in_scene_toggle.setTextOn(context.getResources().getString(R.string.label_pause));
            final String[] state = {Constants.SONOS_FUNCTION_STOP};
            viewHolder.state_endpoint_in_scene_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                    if (!isChecked) {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_play));
                        state[0] = Constants.SONOS_FUNCTION_PLAY;
                    } else {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_pause));
                        state[0] = Constants.SONOS_FUNCTION_STOP;
                    }
                    // If the values are modified while the user does not check/unckeck the checkbox, then we update the value from the controller.
                    int exist = mode.getPayload().indexOf(c);
                    if (exist != -1) {
                        mode.getPayload().get(exist).setFunction(state[0]);
                    }
                }
            });
            if (havePreviousData) {
                String function = mode.getPayload().get(index).getFunction();
                if (function.equalsIgnoreCase(Constants.SONOS_FUNCTION_STOP)) {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(false);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_pause));
                } else {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(true);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_play));
                }
                viewHolder.endpoint_in_scene_check_box.setChecked(true);
            }
            viewHolder.endpoint_in_scene_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        c.setFunction(state[0]);
                        mode.addCommandToPayload(c);
                    } else {
                        mode.removeCommandFromPayload(c);
                    }
                }
            });
        } else if (Constants.UI_CLASS_HUE.equalsIgnoreCase(ui_class_command)) {
            final Command c = new Command(endpoints.get(position));
            c.setTarget("hue");
            viewHolder.state_endpoint_in_scene_toggle = (ToggleButton) convertView.findViewById(R.id.state_endpoint_in_scene_toggle);
            viewHolder.state_endpoint_in_scene_toggle.setTextOff(context.getResources().getString(R.string.label_off));
            viewHolder.state_endpoint_in_scene_toggle.setTextOn(context.getResources().getString(R.string.label_on));
            final String[] state = {Constants.HUE_FUNCTION_OFF};
            viewHolder.state_endpoint_in_scene_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                    if (isChecked) {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_on));
                        state[0] = Constants.HUE_FUNCTION_ON;
                    } else {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_off));
                        state[0] = Constants.HUE_FUNCTION_OFF;
                    }
                    // If the values are modified while the user does not check/unckeck the checkbox, then we update the value from the controller.
                    int exist = mode.getPayload().indexOf(c);
                    if (exist != -1) {
                        mode.getPayload().get(exist).setFunction(state[0]);
                    }
                }
            });
            if (havePreviousData) {
                if (mode.getPayload().get(index).getFunction().equalsIgnoreCase(Constants.HUE_FUNCTION_OFF)) {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(false);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_off));
                } else {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(true);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_on));
                }
                viewHolder.endpoint_in_scene_check_box.setChecked(true);
            }
            viewHolder.endpoint_in_scene_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        c.setFunction(state[0]);
                        mode.addCommandToPayload(c);
                    } else {
                        mode.removeCommandFromPayload(c);
                    }
                }
            });
        } else { //Default On/Off for all the devices
            final Command c = new Command(endpoints.get(position));
            c.setFunction(Constants.ZWAVE_FUNCTION_BASIC_SET);
            viewHolder.state_endpoint_in_scene_toggle = (ToggleButton) convertView.findViewById(R.id.state_endpoint_in_scene_toggle);
            viewHolder.state_endpoint_in_scene_toggle.setTextOff(context.getResources().getString(R.string.label_off));
            viewHolder.state_endpoint_in_scene_toggle.setTextOn(context.getResources().getString(R.string.label_on));
            final int[] state = {0};
            viewHolder.state_endpoint_in_scene_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {
                    // We update the values from the toggle
                    if (!isChecked) {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_off));
                        state[0] = 0;
                    } else {
                        viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_on));
                        state[0] = 255;
                    }
                    // If the values are modified while the user does not check/unckeck the checkbox, then we update the value from the controller.
                    int exist = mode.getPayload().indexOf(c);
                    if (exist != -1) {
                        mode.getPayload().get(exist).setV(state[0]);
                    }
                }
            });
            // Since we will reuse this adapter for editing modes, we check for existing data
            if (havePreviousData) {
                if (mode.getPayload().get(index).getV() == 0) {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(false);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_off));
                } else {
                    viewHolder.state_endpoint_in_scene_toggle.setChecked(true);
                    viewHolder.state_endpoint_in_scene_text_view.setText(context.getResources().getString(R.string.label_on));
                }
                viewHolder.endpoint_in_scene_check_box.setChecked(true);
            }
            // We add or remove the mode from the array if the user check/uncheck the checkbox
            viewHolder.endpoint_in_scene_check_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        c.setV(state[0]);
                        mode.addCommandToPayload(c);
                    } else {
                        mode.removeCommandFromPayload(c);
                    }
                }
            });
        }
        return convertView;
    }

    private static class ViewHolder {
        CheckBox endpoint_in_scene_check_box;
        ImageView icon_endpoint_in_scene;
        TextView name_endpoint_in_scene;
        TextView state_endpoint_in_scene_text_view;
        ToggleButton state_endpoint_in_scene_toggle;
        SeekBar state_endpoint_in_scene_seekbar;
    }
}
