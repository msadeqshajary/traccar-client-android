package org.traccar.client;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dalog_language,container,false);
        TextView title = v.findViewById(R.id.dialog_language_title);
        title.setTypeface(Typeface.createFromAsset(getContext().getAssets(),"fonts/sans.ttf"));

        RadioButton fa = v.findViewById(R.id.dialog_language_fa);
        RadioButton en = v.findViewById(R.id.dialog_language_en);
        RadioButton ar = v.findViewById(R.id.dialog_language_ar);

        Typeface sansMedium = Typeface.createFromAsset(getContext().getAssets(),"fonts/sansmedium.ttf");
        fa.setTypeface(sansMedium);
        en.setTypeface(sansMedium);
        ar.setTypeface(sansMedium);

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

    public static void updateLanguage(Activity activity,String lang){
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,
                activity.getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        preferences.edit().putString("lang",lang).apply();

    }
}
