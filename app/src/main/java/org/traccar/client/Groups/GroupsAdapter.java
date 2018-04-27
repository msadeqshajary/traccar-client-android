package org.traccar.client.Groups;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.traccar.client.R;
import org.traccar.client.RequestManager;

import java.util.ArrayList;

import static android.graphics.Typeface.BOLD;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.Holder> {

    private ArrayList<GroupItem> items;
    private Activity context;

    GroupsAdapter(Activity context, ArrayList<GroupItem> items) {
        this.items = items;
        this.context = context;
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView titleTv,title,catTv,cat,idTv,id;
        ImageView add;
        LinearLayout container;
        Holder(View itemView) {
            super(itemView);
            Typeface sansMedium = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/sansmedium.ttf");
            Typeface sansLight = Typeface.createFromAsset(itemView.getContext().getAssets(),"fonts/sanslight.ttf");

            container = itemView.findViewById(R.id.item_container);
            titleTv = itemView.findViewById(R.id.item_gp_gp_title_tv);
            title  = itemView.findViewById(R.id.item_gp_gp_title);
            catTv = itemView.findViewById(R.id.item_gp_gp_cat_tv);
            cat = itemView.findViewById(R.id.item_gp_gp_cat);
            idTv = itemView.findViewById(R.id.item_gp_id_tv);
            id = itemView.findViewById(R.id.item_gp_id);

            title.setTypeface(sansLight,BOLD);
            cat.setTypeface(sansLight, BOLD);
            id.setTypeface(sansLight);

            titleTv.setTypeface(sansMedium);
            catTv.setTypeface(sansMedium);
            idTv.setTypeface(sansMedium);

            add = itemView.findViewById(R.id.item_gp_add);
        }
    }

    @NonNull
    @Override
    public GroupsAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupsAdapter.Holder holder, int position) {
        final GroupItem group = items.get(position);

        holder.cat.setText(group.getCategory());
        holder.title.setText(group.getTitle());
        holder.id.setText(group.getName());

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name","string");
                    jsonObject.put("deviceId",GroupsFragment.deviceId);
                    jsonObject.put("groupId",group.getId());

                    String url = "http://185.142.158.195:10023/api/device_groups";

                    RequestManager requestManager = new RequestManager();
                    requestManager.sendRequestAsync(url, new RequestManager.RequestHandler() {
                        @Override
                        public void onComplete(boolean success) {
                            Log.e("SEND",success+" ");
                        }
                    });

                    requestManager.setData(jsonObject.toString());
                    requestManager.setListener(new RequestManager.RequestListener() {
                        @Override
                        public void onResultCompleted(String result) {
                            Log.e("DEVICE RESULT",result);
                            if(result.contains("modifiedAt")){
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context.getApplicationContext(),"با موفقیت به گروه افزوده شدید",Toast.LENGTH_LONG).show();
                                        holder.container.setBackgroundColor(Color.parseColor("#8BC34A"));
                                    }
                                });

                            }else context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context.getApplicationContext(),"مشکل در افزوده شدن به گروه",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (items!=null)?items.size():0;
    }
}
