package com.example.essentials.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
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
import com.example.essentials.domain.Wishlist;
import com.example.essentials.fragment.ProductFragment;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.example.essentials.viewmodel.WishlistViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    NavigationView navView;
    BottomNavigationView bottomNavigationView;
    NavController navController;

    AppBarConfiguration appBarConfiguration;

    androidx.appcompat.widget.SearchView searchView;

    WishlistViewModel wishlistViewModel;

    List<Wishlist> wishlist = new ArrayList<>();

    Toolbar toolbar;


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
                new AppBarConfiguration.Builder(R.id.nav_top_home, R.id.nav_top_login, R.id.nav_top_customer_details, R.id.nav_top_logout, R.id.nav_top_register, R.id.nav_top_order, R.id.nav_top_cart, R.id.nav_bottom_home, R.id.nav_bottom_category, R.id.nav_bottom_cart, R.id.nav_bottom_wishlist).setDrawerLayout(drawerLayout).build();
//        configureNavigationDrawer();
        //TODO: elevation for navigation drawer
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        configureNavigationDrawer();
        if (getIntent() != null) {
            if (getIntent().getBooleanExtra(ApplicationConstants.LAUNCH_WISH_LIST, false)) {
                Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_wishlist);
            } else if (getIntent().getBooleanExtra(ApplicationConstants.LAUNCH_CART, false)) {
                Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_cart);
            }
        }
        ViewModelFactory factory = new ViewModelFactory((Application) getApplicationContext());
        wishlistViewModel = new ViewModelProvider(this, factory).get(WishlistViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_app_bar, menu);

        // observeWishlistChanges();
        BottomNavigationView navBar = findViewById(R.id.navigationView);
        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                // navBar.setVisibility(View.INVISIBLE);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                //  navBar.setVisibility(View.VISIBLE);
                return true;
            }
        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.search).getActionView();

        // TODO: Add code so that search view is visible in only certain fragments
        if (navController.getCurrentDestination().getDisplayName().contains("nav_top_home") || navController.getCurrentDestination().getDisplayName().contains("nav_bottom_home") ) {
            menu.findItem(R.id.search).setVisible(true);

            searchView.setQueryHint(getString(R.string.search_hint));

            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
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
        } else {
            menu.findItem(R.id.search).setVisible(false);
        }

        return true;
    }

    private void drawBadge(int number) {
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.nav_bottom_wishlist);
        if (number > 0) {
            badgeDrawable.setVisible(true);
            badgeDrawable.setNumber(number);
        } else {
            badgeDrawable.setVisible(false);
        }
    }


    private void observeWishlistChanges() {
        wishlistViewModel.getAllWishlist().observe(this, objWishlist -> {
            wishlist = objWishlist;

            if (!wishlist.isEmpty() && APIUtils.isUserLogged(ProductActivity.this)) {
                drawBadge(wishlist.size());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
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
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_home);
                    return true;
                case R.id.nav_bottom_category:
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_category);
                    return true;
                case R.id.nav_bottom_cart:
                    if (APIUtils.isUserLogged(ProductActivity.this)) {
                        Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_cart);
                    } else {
                        EssentialsUtils.showMessageAlertDialog(ProductActivity.this, ApplicationConstants.NO_LOGIN, ApplicationConstants.NO_LOGIN_MESSAGE_CART);
                    }
                    return true;
                case R.id.nav_bottom_wishlist:
                    if (APIUtils.isUserLogged(ProductActivity.this)) {
                        Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_bottom_wishlist);
                    } else {
                        EssentialsUtils.showMessageAlertDialog(ProductActivity.this, ApplicationConstants.NO_LOGIN, ApplicationConstants.NO_LOGIN_MESSAGE_WISHLIST);
                    }
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
            bottomNavigationView.setVisibility(View.VISIBLE);
            super.onBackPressed();
        }
    }

    private void configureToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();


        // actionbar.setDisplayHomeAsUpEnabled(true);

    }

    //
    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        configureHeaderView();
        if (!APIUtils.isUserLogged(ProductActivity.this)) {
            navView.getMenu().findItem(R.id.nav_top_logout).setVisible(false);
            navView.getMenu().findItem(R.id.nav_top_login).setVisible(true);
            navView.getMenu().findItem(R.id.nav_top_register).setVisible(true);
        } else {
            navView.getMenu().findItem(R.id.nav_top_login).setVisible(false);
            navView.getMenu().findItem(R.id.nav_top_register).setVisible(false);
            navView.getMenu().findItem(R.id.nav_top_logout).setVisible(true);
        }

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                invalidateOptionsMenu();
                Fragment f = null;
                int itemId = menuItem.getItemId();
                // Highlight the item selected in the navigation drawer
                navView.setCheckedItem(itemId);

                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                if (itemId == R.id.nav_top_home) {

                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_top_home);
                    return true;
                } else if (itemId == R.id.nav_top_register) {
                    bottomNavigationView.setVisibility(View.INVISIBLE);

                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_top_register);
                    return true;
                } else if (itemId == R.id.nav_top_order) {
                    if (APIUtils.isUserLogged(ProductActivity.this)) {
                        Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_top_order);
                        return true;
                    } else {
                        EssentialsUtils.showMessageAlertDialog(ProductActivity.this, ApplicationConstants.NO_LOGIN, ApplicationConstants.NO_LOGIN_MESSAGE_ORDERS);
                    }
                    return true;
                } else if (itemId == R.id.nav_top_login) {
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                    Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_top_login);
                    return true;
                } else if (itemId == R.id.nav_top_logout) {
                    showLogoutMessageAlertDialog(ProductActivity.this, ApplicationConstants.LOG_OUT_TITLE, ApplicationConstants.LOG_OUT_MESSAGE);
                    return true;
                } else if (itemId == R.id.nav_top_customer_details) {
                    bottomNavigationView.setVisibility(View.INVISIBLE);
                    if (APIUtils.isUserLogged(ProductActivity.this)) {
                        Navigation.findNavController(ProductActivity.this, R.id.nav_host_fragment).navigate(R.id.nav_top_customer_details);
                        return true;
                    } else {
                        EssentialsUtils.showMessageAlertDialog(ProductActivity.this, ApplicationConstants.NO_LOGIN, ApplicationConstants.NO_LOGIN_MESSAGE_CUSTOMER_DETAILS);
                    }
                }
                return false;
            }
        });
    }

    private void configureHeaderView() {
        View headerView = navView.getHeaderView(0);
        AppCompatTextView navUsername = (AppCompatTextView) headerView.findViewById(R.id.username);
        if (!APIUtils.isUserLogged(ProductActivity.this)) {
            navUsername.setVisibility(View.INVISIBLE);
        } else {
            navUsername.setText(TextUtils.concat(ApplicationConstants.HELLO, " ", APIUtils.getLoggedInUserName(getApplicationContext())));
        }
    }

    public void showLogoutMessageAlertDialog(Context context, String title, String message) {
        if (context instanceof Activity) {
            Activity activity = ((Activity) context);
            if (!activity.isFinishing()) {
                new MaterialAlertDialogBuilder(context, R.style.RoundShapeTheme).setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences pref = context.getSharedPreferences(ApplicationConstants.SHARED_PREF_NAME, 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(ApplicationConstants.API_TOKEN, "");
                                editor.apply();
                                configureNavigationDrawer();
                                // clear wishlist
                                drawBadge(0);
                                Fragment navHostFragment = getSupportFragmentManager().getPrimaryNavigationFragment();
                                Fragment fragment = navHostFragment.getChildFragmentManager().getFragments().get(0);
                                // clear cart
                                ((ProductFragment) fragment).drawBadge(0);
                            }
                        })
                        .show();
            }
        }
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
