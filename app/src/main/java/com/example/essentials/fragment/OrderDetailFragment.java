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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentials.R;
import com.example.essentials.activity.bean.CartPresentationBean;
import com.example.essentials.activity.bean.OrderCustomerPresentationBean;
import com.example.essentials.adapter.DeliveryRecyclerViewAdapter;
import com.example.essentials.domain.OrderProduct;
import com.example.essentials.utils.ApplicationConstants;
import com.example.essentials.utils.EssentialsUtils;
import com.example.essentials.utils.NetworkUtils;
import com.example.essentials.viewmodel.OrderProductViewModel;
import com.example.essentials.viewmodel.ViewModelFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.essentials.utils.ApplicationConstants.SAVED_PRODUCT;

public class OrderDetailFragment extends Fragment {
    View rootView;
    OrderProductViewModel orderProductViewModel;
    List<OrderProduct> orderProducts = new ArrayList<>();
    DeliveryRecyclerViewAdapter deliveryRecyclerViewAdapter;
    OrderCustomerPresentationBean orderCustomerPresentationBean;
    List<CartPresentationBean> cartPresentationBeans = new ArrayList<CartPresentationBean>();
    RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_order_detail, container, false);
        if (!NetworkUtils.isNetworkConnected(getActivity())) {
            EssentialsUtils.showAlertDialog(getActivity(), ApplicationConstants.NO_INTERNET_TITLE, ApplicationConstants.NO_INTERNET_MESSAGE);
        } else {
            ViewModelFactory factory = new ViewModelFactory((Application) getActivity().getApplicationContext());
            orderProductViewModel = new ViewModelProvider(this, factory).get(OrderProductViewModel.class);

            if (getArguments() != null) {
                orderCustomerPresentationBean = OrderDetailFragmentArgs.fromBundle(getArguments()).getOrderCustomerPresentationBean();

                observeOrderDetailChanges();

                initTextView(orderCustomerPresentationBean);

                final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                final Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

                LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View actionBarView = layoutInflater.inflate(R.layout.fragment_actionbar, null);

                TextView titleView = actionBarView.findViewById(R.id.actionbar_view);
                titleView.setText(getResources().getString(R.string.order_detail));

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
        }
        return rootView;
    }

    private void initTextView(OrderCustomerPresentationBean orderCustomerPresentationBean) {

        TextView orderIdTextView = rootView.findViewById(R.id.order_id_text_view);
        TextView orderStatusTextView = rootView.findViewById(R.id.order_status_text_view);
        TextView orderTotalTextView = rootView.findViewById(R.id.order_id_total_view);

        orderIdTextView.setText(String.valueOf(orderCustomerPresentationBean.getId()));
        orderStatusTextView.setText(orderCustomerPresentationBean.getStatus());
        orderTotalTextView.setText(ApplicationConstants.CURRENCY_SYMBOL.concat(String.valueOf(orderCustomerPresentationBean.getTotal())));
    }

    private void observeOrderDetailChanges() {
        orderProductViewModel.getAllOrderProducts().observe(getViewLifecycleOwner(), objOrderProduct -> {
            orderProducts = objOrderProduct;
            setData(orderProducts);

        });
    }

    private void setData(List<OrderProduct> orderProducts) {

        orderProducts = orderProducts.stream().filter(orderProduct -> orderProduct.getOrderId() == orderCustomerPresentationBean.getId()).collect(Collectors.toList());
        for (OrderProduct orderProduct : orderProducts) {
            CartPresentationBean cartPresentationBean = new CartPresentationBean();
            cartPresentationBean.setId(orderProduct.getId());
            cartPresentationBean.setImage(orderProduct.getProductImage());
            cartPresentationBean.setQuantity(orderProduct.getQuantity());
            cartPresentationBean.setName(orderProduct.getProductName());
            cartPresentationBean.setPrice(ApplicationConstants.CURRENCY_SYMBOL.concat(String.valueOf(orderProduct.getPrice())));
            cartPresentationBeans.add(cartPresentationBean);
        }
        setOrderData(cartPresentationBeans);
    }

    private void setOrderData(List<CartPresentationBean> cartPresentationBeans) {

        deliveryRecyclerViewAdapter = new DeliveryRecyclerViewAdapter(getActivity(), cartPresentationBeans);
        recyclerView = rootView.findViewById(R.id.rv_item_detail);
        recyclerView.setAdapter(deliveryRecyclerViewAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), EssentialsUtils.getSpan(getActivity()));
        recyclerView.setLayoutManager(manager);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        if (recyclerView != null && orderCustomerPresentationBean != null && cartPresentationBeans!=null) {
            Parcelable listState = Objects.requireNonNull(recyclerView.getLayoutManager()).onSaveInstanceState();
            // putting recyclerview position
            savedInstanceState.putParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID, listState);
            savedInstanceState.putParcelable(ApplicationConstants.SAVED_ORDER, orderCustomerPresentationBean);
            // putting recyclerview items
            savedInstanceState.putParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID, new ArrayList<>(cartPresentationBeans));
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    public void restorePreviousState(Bundle savedInstanceState) {
        // getting recyclerview position
        Parcelable listState = savedInstanceState.getParcelable(ApplicationConstants.SAVED_RECYCLER_VIEW_STATUS_ID);

        orderCustomerPresentationBean = savedInstanceState.getParcelable(SAVED_PRODUCT);
        if (recyclerView != null && orderCustomerPresentationBean != null && cartPresentationBeans!=null) {
            initTextView(orderCustomerPresentationBean);
            // getting recyclerview items
            cartPresentationBeans = savedInstanceState.getParcelableArrayList(ApplicationConstants.SAVED_RECYCLER_VIEW_DATASET_ID);
            // Restoring adapter items
            setOrderData(cartPresentationBeans);
            // Restoring recycler view position
            Objects.requireNonNull(recyclerView.getLayoutManager()).onRestoreInstanceState(listState);
        }
    }

}
