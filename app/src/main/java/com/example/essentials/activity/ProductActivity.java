package com.example.essentials.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.essentials.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class ProductActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navView;
    BottomNavigationView bottomNavigationView;
    NavController navController;

    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        navView = findViewById(R.id.nav_view);
        configureToolbar();
        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_item_home, R.id.nav_item_blog, R.id.nav_item_app).setDrawerLayout(drawerLayout).build();
//        configureNavigationDrawer();
        //TODO: elevation for navigation drawer
        NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);
        NavigationUI.setupWithNavController(navView,navController);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        configureNavigationDrawer();

    }

//    @Override
//    public boolean onSupportNavigateUp() {
////        Log.d("onSupportNavigateUp()", "pressed");
////        finish();
////        return super.onSupportNavigateUp();
//        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
//        return navController.navigateUp() || super.onSupportNavigateUp();
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    Log.d("Product Activity", "Inside home");
                    Navigation.findNavController(ProductActivity.this,R.id.nav_host_fragment).navigate(R.id.nav_homeFragment);
                    return true;
                case R.id.bottom_category :
                    Log.d("Product Activity", "Inside category ");
                    Navigation.findNavController(ProductActivity.this,R.id.nav_host_fragment).navigate(R.id.nav_directionFragment);
                    return true;
                case R.id.bottom_cart :
                    Log.d("Product Activity", "Inside cart ");
                    Navigation.findNavController(ProductActivity.this,R.id.nav_host_fragment).navigate(R.id.nav_directionFragment);
                    return true;
                case R.id.bottom_wishlist :
                    Log.d("Product Activity", "Inside cart ");
                    Navigation.findNavController(ProductActivity.this,R.id.nav_host_fragment).navigate(R.id.nav_directionFragment);
                    return true;
            }
            return false;
        }
    };

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        // actionbar.setHomeAsUpIndicator(R.drawable.ic_action_menu_white);
       // actionbar.setDisplayHomeAsUpEnabled(true);
    }
//
    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment f = null;
                int itemId = menuItem.getItemId();
                if (itemId == R.id.homeIcon) {
                    //  f = new RefreshFragment();
                    Log.d("Product Activity","Inside home");

                    Navigation.findNavController(ProductActivity.this,R.id.nav_host_fragment).navigate(R.id.nav_item_home);
                    drawerLayout.closeDrawers();
                    return true;
                } else if (itemId == R.id.login) {
                    Log.d("Product Activity","Inside login");
                    Toast.makeText(ProductActivity.this, "Hello login", Toast.LENGTH_LONG).show();
                }
//                if (f != null) {
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.frame, f);
//                    transaction.commit();
//                    drawerLayout.closeDrawers();
//                    return true;
//                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            // Android home
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            // manage other entries if you have it ...
        }
        return true;
    }
}
