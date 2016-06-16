/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import toronto.amazinglocations.com.discovertoronto.R;
import toronto.amazinglocations.com.discovertoronto.ui.RoundImage;

public class PointsOfInterestListViewArrayAdapter extends ArrayAdapter<PointOfInterest> {
    private Context mContext;
    private int mLayoutResourceId;
    private List<PointOfInterest> mItems;

    static class ViewHolder {
        ImageView pointOfInterestImageView;
        TextView pointOfInterestTextView;
    }

    private class ImageLoaderAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
        private ViewHolder mHolder;

        public ImageLoaderAsyncTask(ViewHolder holder) {
            mHolder = holder;
        }
        protected Bitmap doInBackground(Integer... imageResourceId) {
            return OptimizedImageLoader.decodeSampledBitmapFromResource(mContext.getResources(), imageResourceId[0], 30, 30);
        }
        protected void onPostExecute(Bitmap image) {
            mHolder.pointOfInterestImageView.setImageDrawable(new RoundImage(image));
        }
    }

    public PointsOfInterestListViewArrayAdapter(Context context, int layoutResourceId, List<PointOfInterest> items) {
        super(context, layoutResourceId, items);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        // Reuse views
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_row, null);

            // Configure view holder
            holder = new ViewHolder();
            holder.pointOfInterestImageView = (ImageView) convertView.findViewById(R.id.pointOfInterestImageView);
            holder.pointOfInterestTextView = (TextView) convertView.findViewById(R.id.pointOfInterestTextView);

            convertView.setTag(holder);
        }
        else {
            // Fill data
            holder = (ViewHolder) convertView.getTag();
        }

        new ImageLoaderAsyncTask(holder).execute(new Integer(mItems.get(position).getImageResourceId()));
        holder.pointOfInterestTextView.setText(mItems.get(position).getName());

        return convertView;
    }
}