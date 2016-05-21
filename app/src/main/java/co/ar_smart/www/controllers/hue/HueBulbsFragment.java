package co.ar_smart.www.controllers.hue;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.adapters.hue.BulbsAdapter;
import co.ar_smart.www.living.R;
import co.ar_smart.www.pojos.hue.HueLight;

/**
 * A simple {@link Fragment} subclass.
 * to handle interaction events.
 * Use the {@link HueBulbsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HueBulbsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_HUE_LIGHTS = "huelightslist";

    private List<HueLight> bulbs = new java.util.ArrayList<>();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ColorPickerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HueBulbsFragment newInstance(ArrayList<HueLight> param1) {
        HueBulbsFragment fragment = new HueBulbsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_PARAM_HUE_LIGHTS, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public HueBulbsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bulbs = getArguments().getParcelableArrayList(ARG_PARAM_HUE_LIGHTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.hue_bulbs_fragment_list, container, false);
        // Inflate the layout for this fragment
        //TODO fill bulbs? or in the oncreate

        ListView sceneListView = (ListView) rootView.findViewById(R.id.hue_bulbs_list_view);
        BulbsAdapter customAdapter = new BulbsAdapter(getActivity().getApplicationContext(), bulbs);
        sceneListView.setAdapter(customAdapter);
        sceneListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity().getApplicationContext(), bulbs.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
