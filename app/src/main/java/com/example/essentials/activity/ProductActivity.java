package com.example.essentials.activity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.essentials.R;
import com.example.essentials.fragment.ProductFragment;
import com.example.essentials.viewmodel.ProductViewModel;
import com.example.essentials.viewmodel.UserViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class ProductActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navView;
    BottomNavigationView bottomNavigationView;
    NavController navController;

    AppBarConfiguration appBarConfiguration;

    androidx.appcompat.widget.SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        drawerLayout = findViewById(R.id.drawer_layout);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navView = findViewById(R.id.nav_view);
        configureToolbar();
        appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.nav_top_home, R.id.nav_top_login, R.id.nav_top_register, R.id.nav_top_promotion, R.id.nav_top_category, R.id.nav_top_order, R.id.nav_top_cart, R.id.nav_bottom_home, R.id.nav_bottom_category, R.id.nav_bottom_cart, R.id.nav_bottom_wishlist).setDrawerLayout(drawerLayout).build();
//        configureNavigationDrawer();
        //TODO: elevation for navigation drawer
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        configureNavigationDrawer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_app_bar, menu);

        BadgeDrawable badgeDrawable= bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_wishlist);
        badgeDrawable.setVisible(true);
        badgeDrawable.setNumber(1);
        BottomNavigationView navBar = findViewById(R.id.navigationView);
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(ProductActivity.this, "Expanded", Toast.LENGTH_SHORT).show();
                // navBar.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Toast.makeText(ProductActivity.this, "Collapsed", Toast.LENGTH_SHORT).show();
                //  navBar.setVisibility(View.VISIBLE);
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.search).getActionView();

        // TODO: Add code so that search view is visible in only certain fragments
        if (!navController.getCurrentDestination().getDisplayName().contains("nav_top_home")) {
            menu.findItem(R.id.search).setVisible(false);
        } else {
            menu.findItem(R.id.search).setVisible(true);

            searchView.setQueryHint(getString(R.string.search_hint));

            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.d("Anandhi ", navController.getCurrentDestination().getDisplayName());
                    //com.example.essentials:id/nav_top_home
                    Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                    ((ProductFragment) fragment).filter(s);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    // fastItemAdapter.filter(s);
                    // touchCallback.setIsDragEnabled(TextUtils.isEmpty(s));

                    Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                    ((ProductFragment) fragment).filter(s);
                    return true;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                    Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                    ((ProductFragment) fragment).filter("");
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
//        Log.d("onSupportNavigateUp()", "pressed");
//        finish();
//        return super.onSupportNavigateUp();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            invalidateOptionsMenu();
            switch (item.getItemId()) {
                case R.id.nav_bottom_home:
                    Log.d("Product Activity", "Inside home");
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_home);
                    return true;
                case R.id.nav_bottom_category:
                    Log.d("Product Activity", "Inside category ");
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_category);
                    return true;
                case R.id.nav_bottom_cart:
                    Log.d("Product Activity", "Inside cart ");
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_cart);
                    return true;
                case R.id.nav_bottom_wishlist:
                    Log.d("Product Activity", "Inside cart ");
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_wishlist);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void configureToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();

        // actionbar.setDisplayHomeAsUpEnabled(true);

    }

    //
    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                invalidateOptionsMenu();
                Fragment f = null;
                int itemId = menuItem.getItemId();

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (itemId == R.id.nav_top_home) {
                    //  f = new RefreshFragment();
                    Log.d("Product Activity", "Inside home");

                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_top_home);
                    return true;
                } else if (itemId == R.id.nav_top_login) {
                    Log.d("Product Activity", "Inside login");
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
