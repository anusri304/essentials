package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.OrderCustomerPresentationBean;
import com.example.essentials.adapter.OrderCustomerRecyclerViewAdapter;
import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.OrderCustomerViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderFragment  extends Fragment implements OrderCustomerRecyclerViewAdapter.ListItemClickListener{
    View rootView;
    OrderCustomerViewModel orderCustomerViewModel;
    List<OrderCustomer> orderCustomers = new ArrayList<>();
    OrderCustomerRecyclerViewAdapter orderRecyclerViewAdapter;
    RecyclerView recyclerView;
    List<OrderCustomerPresentationBean> orderCustomerPresentationBeans = new ArrayList<OrderCustomerPresentationBean>();
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            EssentialsUtils.showAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
        } else {
            ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
            orderCustomerViewModel = new ViewModelProvider(this, factory).get(OrderCustomerViewModel.class);

            observeOrderChanges();

            final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

            LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

            TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
            titleView.setText(getResources().getString(R.string.order_history));

            if (actionBar != null) {
                // enable the customized view and disable title
                actionBar.setDisplayShowCustomEnabled(true);
                actionBar.setCustomView(actionBarView);
                //  actionBar.setTitle(getResources().getString(R.string.categories));
                actionBar.setDisplayShowTitleEnabled(false);


                // remove Burger Icon
                toolbar.setNavigationIcon(null);
            }
            actionBarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionBar.setDisplayShowCustomEnabled(false);
                    actionBar.setDisplayShowTitleEnabled(true);
                    DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            getActivity(), drawer, toolbar, R.string.drawer_open,
                            R.string.drawer_close);
                    // All that to re-synchronize the Drawer State
                    toggle.syncState();
                    getActivity().onBackPressed();
                }
            });
        }
        return rootView;
    }

    private void observeOrderChanges() {
        orderCustomerViewModel.getAllOrdercustomer().observe(getViewLifecycleOwner(), objOrderCustomer -> {
            orderCustomers = objOrderCustomer;
            orderCustomerPresentationBeans= EssentialsUtils.getOrderCustomerPresentationBeans(orderCustomers);
            setData(orderCustomerPresentationBeans);

        });
    }

    private void setData(List<OrderCustomerPresentationBean> orderCustomers) {

        orderRecyclerViewAdapter = new OrderCustomerRecyclerViewAdapter(getActivity(), orderCustomers, this);
        recyclerView = rootView.findViewById(R.id.rv_order);
        recyclerView.setAdapter(orderRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);

    }

    private void logAnalyticsEvent(OrderCustomerPresentationBean orderCustomerPresentationBeans) {
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationConstants.ORDER_ID, String.valueOf(orderCustomerPresentationBeans.getId()));
        bundle.putString(ApplicationConstants.ORDER_DATE, orderCustomerPresentationBeans.getDateAdded());
        APIUtils.getFirebaseAnalytics(getActivity().getApplicationContext()).logEvent(ApplicationConstants.SELECT_ORDER, bundle);
        APIUtils.getFirebaseCrashlytics().setCustomKey(ApplicationConstants.ORDER_DATE, orderCustomerPresentationBeans.getDateAdded());
    }

    @Override
    public void onListItemClick(OrderCustomerPresentationBean orderCustomerPresentationBean) {
        OrderFragmentDirections.ActionNavTopOrderToNavTopOrderDetail action = OrderFragmentDirections.actionNavTopOrderToNavTopOrderDetail(orderCustomerPresentationBean);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
        if(orderCustomerPresentationBean!=null) {
            logAnalyticsEvent(orderCustomerPresentationBean);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        if (recyclerView != null && orderCustomerPresentationBeans != null) {
            Parcelable listState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            // putting recyclerview position
            savedInstanceState.putParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID, listState);
            // putting recyclerview items
            savedInstanceState.putParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID, new ArrayList<>(orderCustomerPresentationBeans));
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable listState = savedInstanceState.getParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID);
        // getting recyclerview items
        if (recyclerView != null && orderCustomerPresentationBeans != null) {
            orderCustomerPresentationBeans = savedInstanceState.getParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID);
            // Restoring adapter items
            setData(orderCustomerPresentationBeans);
            // Restoring recycler view position
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }
}
