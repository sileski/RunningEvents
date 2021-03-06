package com.example.runningevents.Main.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.runningevents.Login.activities.LoginActivity;
import com.example.runningevents.Main.activities.MainActivity;
import com.example.runningevents.R;
import com.example.runningevents.Utils;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsPreferencesFragment extends PreferenceFragmentCompat {

    FirebaseAuth firebaseAuth;
    Preference login;
    Preference logout;
    ListPreference languageSelect;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        firebaseAuth = FirebaseAuth.getInstance();

        login = findPreference("login");
        logout = findPreference("logout");
        languageSelect = findPreference("select_language");

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser.isAnonymous())
        {
            logout.setVisible(false);
        }
        else {
            login.setVisible(false);
        }

        languageSelect.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Utils.setAppLocale(newValue.toString(), getResources());
                ((MainActivity) getActivity()).finish();
                return true;
            }
        });

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                signOut();
                return false;
            }
        });

        login.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                signIn();
                return false;
            }
        });
    }

    private void signIn(){
        Intent intent = new Intent(((MainActivity) getActivity()), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void signOut(){
        firebaseAuth.signOut();
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(((MainActivity) getActivity()), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
