package org.traccar.client.Groups;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.traccar.client.Constants;
import org.traccar.client.ProtocolFormatter;
import org.traccar.client.R;
import org.traccar.client.RequestManager;

import java.util.ArrayList;
import java.util.Random;

public class GroupsFragment extends Fragment {

    String filter,response;
    ProgressBar pb;
    RecyclerView list;
    GroupsAdapter adapter;
    SharedPreferences preferences;
    public static String deviceId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_groups,container,false);

        pb = v.findViewById(R.id.fragment_groups_pb);
        list = v.findViewById(R.id.fragment_groups_list);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        preferences = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        if(preferences.getString("DEVICEID","").equals("")) {
            try {
                getDeviceId();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else deviceId = preferences.getString("DEVICEID","");
        // URL for find in groups
        final String urlString = "http://185.142.158.195:10023/api/groups";

        // Filter groups by name when click
        filter = "{\"where\" : {\"name\" : {\"like\" : \".*$$\"}}}";
        ImageView search = v.findViewById(R.id.fragment_groups_search_icon);
        final EditText input = v.findViewById(R.id.fragment_groups_search_et);
        input.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/sanslight.ttf"));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(input.getText().toString().length()>0){
                    response = "";
                    pb.setVisibility(View.VISIBLE);
                    String replacedFilter = filter.replace("$$",input.getText().toString());
                    String request = ProtocolFormatter.formatRequest(urlString,replacedFilter);
                    Log.e("REQUEST", request);
                    getResponse(request);
                }
                else Toast.makeText(getContext(),"لطفا نام گروه را وارد کنید",Toast.LENGTH_LONG).show();
            }
        });

        return v;
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

            items.add(group);;
        }
        return items;
    }

    void getDeviceId() throws JSONException {
        final String urlString = "http://185.142.158.195:10023/api/devices";
        String deviceUniqueId = String.valueOf(new Random().nextInt(900000) + 100000);
        String brand = Build.MANUFACTURER;
        String model = Build.MODEL;
        String name = brand+" "+model;
        String userId = Constants.userId;
        String description = "";
        String icon = "icon";
        int baseLat = 0;
        int baseLng = 0;
        String baseTitle = "string";
        String accessToken = deviceUniqueId+"";
        String oneTimeCode = "string";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceUniqueId",deviceUniqueId);
        jsonObject.put("name",name);
        jsonObject.put("model",model);
        jsonObject.put("brand",brand);
        jsonObject.put("description",description);
        jsonObject.put("icon",icon);
        jsonObject.put("baseLat",baseLat);
        jsonObject.put("baseLng",baseLng);
        jsonObject.put("baseTitle",baseTitle);
        jsonObject.put("accessToken",accessToken);
        jsonObject.put("oneTimeCode",oneTimeCode);
        jsonObject.put("userId",userId);

        Log.e("JSON",jsonObject.toString());

        RequestManager requestManager = new RequestManager();
        requestManager.sendRequestAsync(urlString, new RequestManager.RequestHandler() {
            @Override
            public void onComplete(boolean success) {
                Log.e("SEND",success+" ");
            }
        });

        requestManager.setData(jsonObject.toString());
        requestManager.setListener(new RequestManager.RequestListener() {
            @Override
            public void onResultCompleted(String result) {
                SharedPreferences.Editor editor = preferences.edit();
                try {
                    JSONObject res = new JSONObject(result);
                    deviceId = res.getString("id");
                    Log.e("DEVICE ID",deviceId);
                    editor.putString("DEVICEID","5ae1c887d8f0981a31f139b4");
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
