package com.azwraithnp.eadnepal.main.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Dashboard.HomeFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {

    private ArrayList<String> imageList;
    private LayoutInflater inflater;
    private Context context;
    HomeFragment homeFragment;

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }

    public SlidingImage_Adapter(Context context, ArrayList<String> imageModelArrayList, HomeFragment homeFragment) {
        this.context = context;
        this.imageList = imageModelArrayList;
        inflater = LayoutInflater.from(context);
        this.homeFragment = homeFragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {

        int layout_file = 0;

        if(position > 0)
        {
            layout_file = R.layout.slidingimages_play_layout;
        }
        else
        {
            layout_file = R.layout.slidingimages_layout;
        }

        View imageLayout = inflater.inflate(layout_file, view, false);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position == 0)
                {
                    homeFragment.viewPagerPicture();
                }
                else if(position == 1)
                {
                    homeFragment.viewPagerAudio();
                }
                else
                {
                    homeFragment.viewPagerVideo();
                }

            }
        });

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        switch (position)
        {
            case 0:
                Glide.with(context).load("http://eadnepal.com/client/pages/target/uploads/" + imageList.get(position)).into(imageView);
                break;

            case 1:
                Glide.with(context).load(R.drawable.baseline_audiotrack_black_24).into(imageView);
                break;

            case 2:
                Glide.with(context).load("http://eadnepal.com/client/pages/target video/uploads/" + imageList.get(position)).into(imageView);
                break;

            default:
                break;
        }



        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
