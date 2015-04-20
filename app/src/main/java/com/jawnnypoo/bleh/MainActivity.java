package com.jawnnypoo.bleh;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jawnnypoo.bleh.data.Deal;
import com.jawnnypoo.bleh.data.Theme;
import com.jawnnypoo.bleh.service.MehClient;
import com.jawnnypoo.bleh.service.MehResponse;
import com.jawnnypoo.bleh.util.ColorUtil;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.relex.circleindicator.CircleIndicator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.indicator)
    CircleIndicator indicator;
    @InjectView(R.id.deal_image_view_pager)
    ViewPager imageViewPager;
    ImageAdapter imagePagerAdapter;
    @InjectView(R.id.deal_buy_button)
    Button buy;
    @InjectView(R.id.deal_title)
    TextView title;
    @InjectView(R.id.deal_description)
    TextView description;

    NumberFormat priceFormatter = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        imagePagerAdapter = new ImageAdapter(this);
        imageViewPager.setAdapter(imagePagerAdapter);
        MehClient.instance().getMeh(new Callback<MehResponse>() {
            @Override
            public void success(MehResponse mehResponse, Response response) {
                bindDeal(mehResponse.getDeal());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void bindDeal(final Deal deal) {
        if (deal.isSoldOut()) {
            buy.setEnabled(false);
            buy.setText(R.string.sold_out);
        } else {
            buy.setText(deal.getPriceRange() + "\n" + getString(R.string.buy_it));
            buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPage(deal.getUrl());
                }
            });
        }
        title.setText(deal.getTitle());
        description.setText(deal.getFeatures());
        imagePagerAdapter.setData(deal.getPhotos());
        indicator.setViewPager(imageViewPager);
        bindTheme(deal.getTheme());
    }

    private void bindTheme(Theme theme) {
        int accentColor = Color.parseColor(theme.getAccentColor());
        int backgroundColor = Color.parseColor(theme.getBackgroundColor());
        title.setTextColor(accentColor);
        toolbar.setBackgroundColor(accentColor);
        ColorUtil.setStatusBarAndNavBarColor(getWindow(), ColorUtil.getDarkerColor(accentColor));
        getWindow().getDecorView().setBackgroundColor(backgroundColor);
    }

    private void openPage(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            SnackbarManager.show(
                    Snackbar.with(this)
                            .text(R.string.error_no_browser));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ImageAdapter extends PagerAdapter {

        private Context mContext;
        private ArrayList<String> mData = new ArrayList<>();

        public ImageAdapter(Context context) {
            mContext = context;
        }

        public void setData(Collection<String> data) {
            if (data != null && !data.isEmpty()) {
                mData.clear();
                mData.addAll(data);
                notifyDataSetChanged();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.item_deal_image, collection, false);
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            Glide.with(mContext)
                    .load(mData.get(position))
                    .into(imageView);

            collection.addView(v, 0);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
