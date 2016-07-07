package co.ar_smart.www.adapters;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.ar_smart.www.helpers.ModeManager;
import co.ar_smart.www.living.R;
import co.ar_smart.www.modes.EditModeActivity;
import co.ar_smart.www.modes.ListModesActivity;
import co.ar_smart.www.pojos.Endpoint;
import co.ar_smart.www.pojos.Mode;

import static co.ar_smart.www.helpers.Constants.EXTRA_MESSAGE;
import static co.ar_smart.www.helpers.Constants.EXTRA_MODE;

/**
 * Created by Gabriel on 5/13/2016.
 */
public class ModeAdapter extends BaseAdapter {

    private Context context;
    private List<Mode> modes;
    private LayoutInflater inflater;
    private int hubid;
    private String API_TOKEN;
    private Context mContext;
    private ArrayList<Endpoint> endpoints;

    public ModeAdapter(Context c, List<Mode> nModes, int hubid, String api_token, Context mContext, ArrayList<Endpoint> endpoints) {
        context = c;
        modes = nModes;
        inflater = (LayoutInflater.from(c));
        this.hubid = hubid;
        API_TOKEN = api_token;
        this.mContext = mContext;
        this.endpoints = endpoints;
    }

    @Override
    public int getCount() {
        return modes.size();
    }

    @Override
    public Object getItem(int position) {
        return modes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_mode_management_item, null);
            viewHolder.modeName = (TextView) convertView.findViewById(R.id.mode_text_view);
            convertView.setTag(viewHolder);
            viewHolder.modeName.setText(modes.get(position).getName());
            viewHolder.btn_edit = (ImageButton) convertView.findViewById(R.id.mode_edit);
            viewHolder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(mContext, EditModeActivity.class);
                    i.putExtra(EXTRA_MESSAGE, API_TOKEN);
                    Bundle b = new Bundle();
                    b.putParcelable(EXTRA_MODE, modes.get(position));
                    i.putExtras(b);
                    mContext.startActivity(i);
                }
            });
            viewHolder.btn_remove = (ImageButton) convertView.findViewById(R.id.mode_remove);
            viewHolder.btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_warning_delete);
                    TextView txtname=(TextView) dialog.findViewById(R.id.lbl_warning_del_device);
                    txtname.setText(mContext.getResources().getString(R.string.label_warning_delete_device)+" "+modes.get(position).getName()+"?");
                    Button dialogButton = (Button) dialog.findViewById(R.id.btnDel);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeMode(modes.get(position).getId(), position);
                            dialog.dismiss();
                        }
                    });

                    dialogButton = (Button) dialog.findViewById(R.id.btnCancelDel);
                    dialogButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
            ArrayList defaultModes = ModeManager.getDefaultModes(endpoints);
            if(defaultModes.contains(modes.get(position)))
            {
                viewHolder.btn_remove.setVisibility(View.GONE);
                viewHolder.modeName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.93f));
                viewHolder.btn_edit.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,0.07f));
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView modeName;
        ImageButton btn_edit;
        ImageButton btn_remove;
    }

    public void removeMode(int id, final int position) {
        ModeManager.removeMode(hubid, id, API_TOKEN, new ModeManager.ModeCallbackInterface() {
            @Override
            public void onFailureCallback() {

            }

            @Override
            public void onSuccessCallback(List<Mode> guest) {
                modes.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onSuccessCallback() {
                modes.remove(position);
                notifyDataSetChanged();
            }

            @Override
            public void onUnsuccessfulCallback() {
            }
        });
    }
}
