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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
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

        if(displayType.equals("photo"))
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_photo, parent, false);
        }
        else if(displayType.equals("video"))
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_video, parent, false);

        }
        else if(displayType.equals("audio"))
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_audio, parent, false);

        }
        else
        {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card_photo, parent, false);
        }

        itemView.getLayoutParams().width = (int)(getScreenWidth() / 3);

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
        holder.title.setText(album.getName());
        holder.count.setText(album.getTimeCount() + " seconds");

        //  loading album cover using Glide library
          Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

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
