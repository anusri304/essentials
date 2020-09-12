package com.example.essentials.fragment;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
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
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.OrderCustomerPresentationBean;
import com.example.essentials.adapter.OrderCustomerRecyclerViewAdapter;
import com.example.essentials.domain.OrderCustomer;
import com.example.essentials.utils.APIUtils;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.viewmodel.OrderCustomerViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment  extends Fragment implements OrderCustomerRecyclerViewAdapter.ListItemClickListener{
    String TAG = "OrderFragment";
    View rootView;
    OrderCustomerViewModel orderCustomerViewModel;
    List<OrderCustomer> orderCustomers = new ArrayList<>();
    OrderCustomerRecyclerViewAdapter orderRecyclerViewAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order, container, false);
        ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
        orderCustomerViewModel = new ViewModelProvider(this, factory).get(OrderCustomerViewModel.class);

        observeOrderChanges();

        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
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
        return rootView;
    }

    private void observeOrderChanges() {
        orderCustomerViewModel.getAllOrdercustomer().observe(this, objOrderCustomer -> {
            orderCustomers = objOrderCustomer;
            setData(orderCustomers);

        });
    }

    private void setData(List<OrderCustomer> orderCustomers) {
        orderRecyclerViewAdapter = new OrderCustomerRecyclerViewAdapter(getActivity(), EssentialsUtils.getOrderCustomerPresentationBeans(orderCustomers), this);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_order);
        recyclerView.setAdapter(orderRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
        if(orderCustomers!=null && orderCustomers.size()>0) {
            logAnalyticsEvent(EssentialsUtils.getOrderCustomerPresentationBeans(orderCustomers));
        }
    }

    private void logAnalyticsEvent(List<OrderCustomerPresentationBean> orderCustomerPresentationBeans) {
        StringBuilder sb = new StringBuilder();
        for (OrderCustomerPresentationBean orderCustomerPresentationBean : orderCustomerPresentationBeans) {
            sb.append(String.valueOf(orderCustomerPresentationBean.getId()));
            sb.append(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString(ApplicationConstants.ORDERS, sb.toString());
        APIUtils.getFirebaseAnalytics(getActivity().getApplicationContext()).logEvent(ApplicationConstants.VIEW_ORDERS, bundle);
    }

    @Override
    public void onListItemClick(OrderCustomerPresentationBean orderCustomerPresentationBean) {
        OrderFragmentDirections.ActionNavTopOrderToNavTopOrderDetail action = OrderFragmentDirections.actionNavTopOrderToNavTopOrderDetail(orderCustomerPresentationBean);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}
