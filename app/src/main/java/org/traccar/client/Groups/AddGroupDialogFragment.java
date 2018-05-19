package org.traccar.client.Groups;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.traccar.client.ProtocolFormatter;
import org.traccar.client.R;
import org.traccar.client.RequestManager;

import java.util.ArrayList;

public class AddGroupDialogFragment extends DialogFragment {

    String response,filter;
    // URL for find in groups
    final String urlString = "http://185.142.158.195:10023/api/groups";
    ProgressBar pb;
    RecyclerView list;
    GroupsAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_add_groups,container,false);

        final EditText search = v.findViewById(R.id.dialog_add_group_search_et);
        ImageView submit = v.findViewById(R.id.dialog_add_group_search_icon);
        pb = v.findViewById(R.id.dialog_add_group_pb);

        list = v.findViewById(R.id.dialog_add_group_list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));

        // Filter groups by name when click
        filter = "{\"where\" : {\"name\" : {\"like\" : \".*$$\"}}}";

        Typeface sansLight = Typeface.createFromAsset(getContext().getAssets(),"fonts/sanslight.ttf");
        Typeface sansMedium = Typeface.createFromAsset(getContext().getAssets(),"fonts/sansmedium.ttf");

        search.setTypeface(sansLight);

        TextView subject = v.findViewById(R.id.dialog_add_group_subject);
        subject.setTypeface(sansMedium);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = search.getText().toString();

                list.setVisibility(View.VISIBLE);
                if(query.length()>0){
                    response = "";
                    pb.setVisibility(View.VISIBLE);
                    String replacedFilter = filter.replace("$$",query);
                    String request = ProtocolFormatter.formatRequest(urlString,replacedFilter);
                    getResponse(request);
                }
                else Toast.makeText(getContext(),"لطفا نام گروه را وارد کنید",Toast.LENGTH_LONG).show();
            }
        });

        v.findViewById(R.id.dialog_add_group_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return v;
    }

    private ArrayList<GroupItem> getGroupsFromJson(String json) throws JSONException {
        ArrayList<GroupItem> items = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(json);

        JSONArray rows = jsonObject.getJSONArray("rows");
        if(rows.length() == 0){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(),"گروهی یافت نشد",Toast.LENGTH_LONG).show();
                }
            });
            return null;
        }
        for(int i =0;i<rows.length();i++){
            JSONObject row = rows.getJSONObject(i);
            GroupItem group = new GroupItem();
            group.setName(row.getString("name"));
            group.setCategory(row.getString("category"));
            group.setTitle(row.getString("title"));
            group.setId(row.getString("id"));

            items.add(group);
        }
        return items;
    }

    void getResponse(String request){
        RequestManager requestManager = new RequestManager();
        requestManager.sendRequestAsync(request,"GET", new RequestManager.RequestHandler() {
            @Override
            public void onComplete(boolean success) {
                if(!success){
                    response = "failure";
                    Toast.makeText(getContext(),"دریافت لیست گروه ها با مشکل مواجه شد",Toast.LENGTH_LONG).show();
                }
                Log.e("RESPONSE",success+" ");
                pb.setVisibility(View.GONE);
            }
        });

        requestManager.setListener(new RequestManager.RequestListener() {
            @Override
            public void onResultCompleted(String result) {
                response = result;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            adapter = new GroupsAdapter(getActivity(),getGroupsFromJson(response));
                            list.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
}
