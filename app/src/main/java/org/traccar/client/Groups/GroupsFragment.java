package org.traccar.client.Groups;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.traccar.client.MainActivity;
import org.traccar.client.R;

import java.util.ArrayList;

public class GroupsFragment extends Fragment {

    public static String deviceId;
    ProgressBar pb;
    TextView noGp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_groups,container,false);

        pb = v.findViewById(R.id.fragment_groups_pb);
        pb.setVisibility(View.VISIBLE);
        noGp = v.findViewById(R.id.fragment_groups_nogp);

        noGp.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/sansmedium.ttf"));

        FloatingActionButton addFab = v.findViewById(R.id.groups_add_fab);
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupDialogFragment dialogFragment = new AddGroupDialogFragment();
                dialogFragment.show(MainActivity.fragmentManager,"TAG");

                dialogFragment.onDismiss(new DialogInterface() {
                    @Override
                    public void cancel() {

                    }

                    @Override
                    public void dismiss() {
                        //refreshList();
                    }
                });
            }
        });

        return v;
    }

    public ArrayList<GroupItem> getDeviceGroups(String deviceId){
        return null;
    }

}
