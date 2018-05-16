package org.traccar.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Locale;

public class LanguageDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dalog_language,container,false);
        TextView title = v.findViewById(R.id.dialog_language_title);
        title.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/sans.ttf"));

        RadioButton fa = v.findViewById(R.id.dialog_language_fa);
        RadioButton en = v.findViewById(R.id.dialog_language_en);
        RadioButton ar = v.findViewById(R.id.dialog_language_ar);

        Typeface sansLight = Typeface.createFromAsset(getContext().getAssets(),"fonts/sanslight.ttf");
        fa.setTypeface(sansLight);
        en.setTypeface(sansLight);
        ar.setTypeface(sansLight);

        fa.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)updateLanguage(getActivity(),"fa");
                dismiss();
            }
        });

        en.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)updateLanguage(getActivity(),"en");
                dismiss();
            }
        });

        ar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)updateLanguage(getActivity(),"ar");
                dismiss();
            }
        });


        return v;
    }

    public static Context updateLanguage(Activity activity,String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Context c = activity.getApplicationContext();


        Resources res = c.getResources();
        Configuration config = new Configuration(res.getConfiguration());

        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
            c = activity.getApplicationContext().createConfigurationContext(config);
        } else {
            config.locale = locale;
            c.getResources().updateConfiguration(config,
                    res.getDisplayMetrics());
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        preferences.edit().putString("lang",lang).apply();

        return c;
    }
}
