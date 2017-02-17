package com.example.android.newdemowithnavgation;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

/**
 * Created by cheny on 2017/2/12.
 */

public class NewsAdapter extends CursorAdapter {

    public NewsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView NewsView;


        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            NewsView = (TextView) view.findViewById(R.id.list_item_webTitle);

        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String imageUrl = cursor.getString(NewsFragment.COL_THUMBNAIL);
        if(imageUrl != null || imageUrl.length() > 0) {
            Picasso.with(context).load(imageUrl ).into(viewHolder.iconView);
        }

        String webTitle = cursor.getString(NewsFragment.COL_WEBTITLE);
        viewHolder.NewsView.setText(webTitle);
    }
}
