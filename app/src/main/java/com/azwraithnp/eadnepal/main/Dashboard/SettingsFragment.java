package com.azwraithnp.eadnepal.main.Dashboard;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.azwraithnp.eadnepal.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private Switch autoLogin;


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final SharedPreferences mPrefs = getActivity().getSharedPreferences("userPref", MODE_PRIVATE);

        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        autoLogin = v.findViewById(R.id.switchButton);

        autoLogin.setChecked(mPrefs.getBoolean("autologin", true));

        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean("autologin", isChecked);
                editor.apply();
            }
        });

        return v;
    }

}
