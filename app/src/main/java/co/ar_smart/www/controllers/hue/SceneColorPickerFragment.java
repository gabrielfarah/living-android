package co.ar_smart.www.controllers.hue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import co.ar_smart.www.adapters.hue.SceneColorPickerAdapter;
import co.ar_smart.www.living.R;

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
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SceneColorPickerFragment newInstance(String param1) {
        SceneColorPickerFragment fragment = new SceneColorPickerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public SceneColorPickerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.scene_color_picker_fragment_list, container, false);
        // Inflate the layout for this fragment
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
            }
        });

        return rootView;
    }
}
