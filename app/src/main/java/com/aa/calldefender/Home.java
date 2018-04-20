package com.aa.calldefender;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment = null;
    int frag_id = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);

        if(savedInstanceState != null){
            int frag = savedInstanceState.getInt("Fragment", 1);

            if (frag == 1) {
                loadFragment(new Home_Fragment());
                frag_id = 1;
            }
            else if (frag == 2) {
                loadFragment(new Add_Fragment());
                frag_id = 2;
            }

            else if (frag == 3) {
                loadFragment(new View_Fragment());
                frag_id = 3;

            }
        } else {
            loadFragment(new Home_Fragment());
        }



    }

    private boolean loadFragment(Fragment fragment) {

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.navigation_home:
                fragment = new Home_Fragment();
                frag_id = 1;
                break;

            case R.id.navigation_add:
                fragment = new Add_Fragment();
                frag_id = 2;
                break;

            case R.id.navigation_view:
                fragment = new View_Fragment();
                frag_id = 3;
                break;
        }

        return loadFragment(fragment);

    }

    public void enableBroadcastReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, CallInterceptor.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }

    public void disableBroadcastReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, CallInterceptor.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("Fragment", frag_id);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);

    }
}
