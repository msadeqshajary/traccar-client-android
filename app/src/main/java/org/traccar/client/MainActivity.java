/*
 * Copyright 2017 Anton Tananaev (anton.tananaev@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.client;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.traccar.client.Profile.DbHelper;
import org.traccar.client.Profile.ProfileFragment;
import org.traccar.client.Profile.UserItem;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    TextView profileText;
    CircleImageView profileImg;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Init Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_toolbar);
        TextView toolbarTitle = toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/sans.ttf"));
        toolbarTitle.setText("ردیابی");

        //Init views
        drawerHelper();


        profileText = findViewById(R.id.drawer_profile_name);
        profileText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/sansmedium.ttf"));
        profileImg = findViewById(R.id.drawer_profile_img);

        updateDrawerProfile();
    }

    public void updateDrawerProfile(){
        DbHelper helper = new DbHelper(this);
        UserItem user = helper.getUser();
        if(user!=null){
            profileText.setText(user.getName()+" "+user.getLast());
            String imgPath = user.getImg();
            Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
            profileImg.setImageBitmap(bitmap);
        }
    }

    private void drawerHelper(){
        Typeface sansLight = Typeface.createFromAsset(getAssets(),"fonts/sanslight.ttf");

        TextView home = findViewById(R.id.drawer_home);
        TextView profile = findViewById(R.id.drawer_profile);
        TextView groups = findViewById(R.id.drawer_groups);
        TextView settings = findViewById(R.id.drawer_settings);
        TextView share = findViewById(R.id.drawer_share);
        TextView help = findViewById(R.id.drawer_help);
        TextView feedback = findViewById(R.id.drawer_feedback);

        home.setTypeface(sansLight, Typeface.BOLD);

        profile.setTypeface(sansLight, Typeface.BOLD);
        groups.setTypeface(sansLight, Typeface.BOLD);
        settings.setTypeface(sansLight, Typeface.BOLD);
        share.setTypeface(sansLight, Typeface.BOLD);
        help.setTypeface(sansLight, Typeface.BOLD);
        feedback.setTypeface(sansLight, Typeface.BOLD);

    }

    public void onClick(View v){
        Fragment fragment = null;
        switch (v.getId()){
            case R.id.drawer_home:fragment = new MainFragment();
                break;
                case R.id.drawer_profile:fragment = new ProfileFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_container,fragment).commit();
    }

}
