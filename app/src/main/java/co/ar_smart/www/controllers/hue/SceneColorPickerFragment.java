package co.ar_smart.www.controllers.hue;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import co.ar_smart.www.adapters.hue.SceneColorPickerAdapter;
import co.ar_smart.www.helpers.CommandManager;
import co.ar_smart.www.helpers.Constants;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.hue.HueEndpoint;
import co.ar_smart.www.pojos.hue.HueLight;
import co.ar_smart.www.pojos.hue.HueLightGroup;
import co.ar_smart.www.pojos.hue.IHueObject;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link SceneColorPickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SceneColorPickerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private List<Map.Entry<String, String>> scenes = new java.util.ArrayList<>();
    private IHueObject hueLight;
    private HueEndpoint hueEndpoint;
    private HueColorControllerActivity parentActivity;

    public SceneColorPickerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param hueLight Parameter 1.
     * @param hueEndpoint the actual endpoint
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SceneColorPickerFragment newInstance(IHueObject hueLight, HueEndpoint hueEndpoint) {
        SceneColorPickerFragment fragment = new SceneColorPickerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, hueLight);
        args.putParcelable(ARG_PARAM2, hueEndpoint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hueLight = getArguments().getParcelable(ARG_PARAM1);
            hueEndpoint = getArguments().getParcelable(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.scene_color_picker_fragment_list, container, false);
        // Inflate the layout for this fragment
        parentActivity = (HueColorControllerActivity) getActivity();
        scenes.add(new java.util.AbstractMap.SimpleEntry<>("Claridad", "connect_btn"));
        scenes.add(new java.util.AbstractMap.SimpleEntry<>("Test 1", "hue_icon1"));
        scenes.add(new java.util.AbstractMap.SimpleEntry<>("Test 3", "right_arrow_icon"));

        ListView sceneListView = (ListView) rootView.findViewById(R.id.scene_color_picker_list_view);
        SceneColorPickerAdapter customAdapter = new SceneColorPickerAdapter(getActivity().getApplicationContext(), scenes);
        sceneListView.setAdapter(customAdapter);
        sceneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), scenes.get(position).getKey(), Toast.LENGTH_SHORT).show();
                changeColor(position);
            }
        });

        return rootView;
    }

    private int getColorFromPosition(int position) {
        /*
        'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan',
        'magenta', 'yellow', 'lightgray', 'darkgray', 'grey',
        'lightgrey', 'darkgrey', 'aqua', 'fuchsia',
        'lime', 'maroon', 'navy', 'olive', 'purple', 'silver', 'teal'
         */
        switch (position) {
            case 0:
                return Color.parseColor("lime");
            case 1:
                return Color.parseColor("darkgray");
            case 2:
                return Color.parseColor("yellow");
        }
        return Color.parseColor("black");
    }

    private void changeColor(int value) {
        int color = getColorFromPosition(value);
        int lid = hueLight.getId();
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        String command = "{}";
        if (hueLight instanceof HueLight) {
            command = hueEndpoint.getSetRGBColorCommand(lid, r, g, b).toString();
        } else if (hueLight instanceof HueLightGroup) {
            command = hueEndpoint.getSetRGBColorGroupCommand(lid, r, g, b).toString();
        }
        CommandManager.sendCommandWithoutResult(parentActivity.getAPI_TOKEN(), parentActivity.getPREFERRED_HUB_ID(), command, new CommandManager.ResponseCallbackInterface() {
            @Override
            public void onFailureCallback() {
                Constants.showNoInternetMessage(getActivity().getApplicationContext());
            }

            @Override
            public void onSuccessCallback(JSONObject jObject) {
            }

            @Override
            public void onUnsuccessfulCallback() {
                //TODO if respnse fail it could be a registration fail
            }
        });
    }


}
