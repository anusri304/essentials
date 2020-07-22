package com.example.essentials.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.essentials.R;
import com.example.essentials.activity.bean.ProductPresentationBean;
import com.example.essentials.activity.ui.DynamicHeightNetworkImageView;
import com.example.essentials.activity.ui.ImageLoaderHelper;
import com.example.essentials.utils.ApplicationConstants;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class ProductDetailActivity extends AppCompatActivity {
ProductPresentationBean productPresentationBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product_detail);

        if (getIntent() != null) {
            productPresentationBean = getIntent().getParcelableExtra(ApplicationConstants.PRODUCT_PRESENTATION_BEAN);
            ((CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout)).setTitle(productPresentationBean.getName());
        }

        setImageView();


    }

    private void setImageView() {
        ImageView imageView = (ImageView) findViewById(R.id.image_product_detail);

//       imageView.setImageUrl(
//               productPresentationBean.getImage(),
//                ImageLoaderHelper.getInstance(this).getImageLoader());
//        imageView.setAspectRatio(ApplicationConstants.ASPECT_RATIO);
        Glide.with(this)
                .load( productPresentationBean.getImage())
                //  .load("https://d17h27t6h515a5.cloudfront.net/topher/2017/March/58c5be64_sleepyhollow/sleepyhollow.jpg")
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(imageView);
    }
}
