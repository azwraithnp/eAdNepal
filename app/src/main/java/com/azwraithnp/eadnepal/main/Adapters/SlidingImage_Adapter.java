package com.azwraithnp.eadnepal.main.Adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Dashboard.HomeFragment;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

import java.util.ArrayList;

public class SlidingImage_Adapter extends PagerAdapter {

    private ArrayList<Album> imageList;
    private ArrayList<String> albumTypes;
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

    public SlidingImage_Adapter(Context context, ArrayList<Album> imageModelArrayList, ArrayList<String> albumTypes, HomeFragment homeFragment) {
        this.context = context;
        this.imageList = imageModelArrayList;
        this.albumTypes = albumTypes;
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

        Log.d("DEBUG_TEST", "Size: " + imageList.size() + ":" + albumTypes.size());

        layout_file = R.layout.empty_slidingimage_layout;

        if(imageList.get(position).equals("abc"))
        {
            layout_file = R.layout.empty_slidingimage_layout;
        }
        else
        {
            if(albumTypes.size() > 0)
            {
                if(!albumTypes.get(position).equals("photo")) {
                    layout_file = R.layout.slidingimages_play_layout;
                }
                else
                {
                    layout_file = R.layout.slidingimages_layout;
                }
            }
        }

        View imageLayout = inflater.inflate(layout_file, view, false);

        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageList.get(position).getThumbnail().equals("abc"))
                {

                }
                else {
                    if(albumTypes.size() > 0)
                    {
                        if(albumTypes.get(position).equals("photo"))
                        {
                            homeFragment.viewPagerPicture();
                        }
                        else if(albumTypes.get(position).equals("audio"))
                        {
                            homeFragment.viewPagerAudio();
                        }
                        else{
                            homeFragment.viewPagerVideo();
                        }
                    }
                }
            }
        });

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.image);

        if(albumTypes.size() > 0)
        {
            if(albumTypes.get(position).equals("photo"))
            {
                Glide.with(context).load("https://eadnepal.com/client/pages/target/uploads/" + imageList.get(position).getThumbnail()).into(imageView);
            }
            else if(albumTypes.get(position).equals("audio"))
            {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(context).load(R.drawable.audio).into(imageView);
            }
            else{
                Glide.with(context).load("https://eadnepal.com/client/pages/target video/uploads/" + imageList.get(position).getThumbnail()).into(imageView);
            }
        }

//        switch (position)
//        {
//            case 0:
//                Glide.with(context).load("http://eadnepal.com/client/pages/target/uploads/" + imageList.get(position)).into(imageView);
//                break;
//
//            case 1:
//                Glide.with(context).load(R.drawable.baseline_music_note_black_48dp).into(imageView);
//                break;
//
//            case 2:
//                Glide.with(context).load("http://eadnepal.com/client/pages/target video/uploads/" + imageList.get(position)).into(imageView);
//                break;
//
//            default:
//                break;
//        }


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
