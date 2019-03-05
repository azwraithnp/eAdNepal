package com.azwraithnp.eadnepal.main.Adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.azwraithnp.eadnepal.R;
import com.azwraithnp.eadnepal.main.Models.Album;
import com.bumptech.glide.Glide;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> albumList;
    private String displayType;
    private String displayUrl;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            count = view.findViewById(R.id.count);
            thumbnail = view.findViewById(R.id.thumbnail);
        }
    }


    public CardAdapter(Context mContext, List<Album> albumList, String displayType) {
        this.mContext = mContext;
        this.albumList = albumList;
        this.displayType = displayType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if(displayType.equals("photo") || displayType.equals("photoall"))
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_photo, parent, false);
            displayUrl = "http://eadnepal.com/client/pages/target/uploads/";
        }
        else if(displayType.equals("video") || displayType.equals("videoall"))
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_video, parent, false);
            displayUrl = "http://eadnepal.com/client/pages/target%20video/uploads/";

        }
        else if(displayType.equals("audio") || displayType.equals("audioall"))
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_audio, parent, false);
            displayUrl = "http://eadnepal.com/client/pages/target%20audio/uploads/";
        }
        else
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_photo, parent, false);
        }

        if(displayType.equals("audio") || displayType.equals("video") || displayType.equals("photo"))
        {
            itemView.getLayoutParams().width = (int)(getScreenWidth() / 3);
        }

        return new MyViewHolder(itemView);
    }

    public int getScreenWidth() {

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        return size.x;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Album album = albumList.get(position);

        if(album.getName().equals("View more"))
        {
            holder.title.setText(album.getName());
            Glide.with(mContext).load(R.drawable.ic_baseline_add_24px).into(holder.thumbnail);
            holder.thumbnail.setPadding(60, 100, 60, 100);
        }
        else
        {
            holder.title.setText(album.getName());
            holder.count.setText(album.getTimeCount() + " seconds");
            //  loading album cover using Glide library
            if(displayType.equals("audio") || displayType.equals("audioall"))
            {
                Glide.with(mContext).load(R.drawable.baseline_audiotrack_black_24).into(holder.thumbnail);
            }
            else
            {
                Glide.with(mContext).load(displayUrl + album.getThumbnail()).into(holder.thumbnail);
            }

        }

    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
//    private void showPopupMenu(View view) {
//        // inflate menu
//        PopupMenu popup = new PopupMenu(mContext, view);
//        MenuInflater inflater = popup.getMenuInflater();
//        inflater.inflate(R.menu.menu_album, popup.getMenu());
//        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
//        popup.show();
//    }

    /**
     * Click listener for popup menu items
     */
//    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
//
//        public MyMenuItemClickListener() {
//        }
//
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            switch (menuItem.getItemId()) {
//                case R.id.action_add_favourite:
//                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
//                    return true;
//                case R.id.action_play_next:
//                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
//                    return true;
//                default:
//            }
//            return false;
//        }
//    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
