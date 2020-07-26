package com.example.essentials.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.utils.ApplicationConstants;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class ProductDetailActivity extends AppCompatActivity {
    ProductPresentationBean productPresentationBean;
    CollapsingToolbarLayout collapsingToolbarLayout;
    View mUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product_detail);

        if (getIntent() != null) {
            productPresentationBean = getIntent().getParcelableExtra(ApplicationConstants.PRODUCT_PRESENTATION_BEAN);
            collapsingToolbarLayout = ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout));
            collapsingToolbarLayout.setTitle(productPresentationBean.getName());

            collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        }
        mUpButton = findViewById(R.id.app_bar);

        initLayout();

    }

    private void initLayout() {
        TextView txtViewTitle = findViewById(R.id.product_title_txtview);
        txtViewTitle.setText(productPresentationBean.getName());
        setProductImageView();
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setProductView();
        setProductDescImageView();
    }

    private void setProductDescImageView() {
        ImageView forwardButton = (ImageView) findViewById(R.id.arrowForward);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductDetailActivity.this,ProductDescriptionActivity.class);
                intent.putExtra(ApplicationConstants.PRODUCT_DESC,productPresentationBean.getDescription());
                intent.putExtra(ApplicationConstants.PRODUCT_NAME,productPresentationBean.getName());
                startActivity(intent);
            }
        });
    }

    private void setProductView() {
        TextView txtViewSpecialPrice = findViewById(R.id.product_special_price_txtView);
        TextView txtViewDiscPerc = findViewById(R.id.product_disc_perc_txtView);
        TextView txtViewPrice = findViewById(R.id.product_price_txtView);
        TextView productStockTxtView = (TextView)  findViewById(R.id.product_stock_txtview);
        if(productPresentationBean.getSpecial().equalsIgnoreCase("")){
            txtViewSpecialPrice.setVisibility(View.GONE);
            txtViewDiscPerc.setVisibility(View.GONE);
            txtViewPrice.setPadding(25,20,0,0);
            txtViewPrice.setText(productPresentationBean.getPrice());
        }
        else {
            txtViewPrice.setPaintFlags(txtViewPrice.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
            txtViewPrice.setText(productPresentationBean.getPrice());
            txtViewSpecialPrice.setText( productPresentationBean.getSpecial());
            txtViewPrice.setTextColor(getResources().getColor(R.color.red));
            txtViewDiscPerc.setText(TextUtils.concat(productPresentationBean.getDiscPerc(), " ", ApplicationConstants.OFF));
        }

        productStockTxtView.setText(productPresentationBean.getInStock().equalsIgnoreCase("Yes") ? ApplicationConstants.IN_STOCK :ApplicationConstants.OUT_OF_STOCK);
    }

    private void setProductImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.image_product_detail);

//       imageView.setImageUrl(
//               productPresentationBean.getImage(),
//                ImageLoaderHelper.getInstance(this).getImageLoader());
//        imageView.setAspectRatio(ApplicationConstants.ASPECT_RATIO);
        Glide.with(this)
                .load(productPresentationBean.getImage())
                //  .load("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5be64_sleepyhollow/sleepyhollow.jpg")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }
}
