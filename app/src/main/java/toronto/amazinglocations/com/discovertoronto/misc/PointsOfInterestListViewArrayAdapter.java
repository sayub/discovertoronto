/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import toronto.amazinglocations.com.discovertoronto.R;
import toronto.amazinglocations.com.discovertoronto.ui.RoundImage;

public class PointsOfInterestListViewArrayAdapter extends ArrayAdapter<PointOfInterest> {
    private Context mContext;
    private int mLayoutResourceId;
    private PointOfInterest mItems[];

    static class ViewHolder {
        ImageView pointOfInterestImageView;
        TextView pointOfInterestTextView;
    }

    public PointsOfInterestListViewArrayAdapter(Context context, int layoutResourceId, PointOfInterest items[]) {
        super(context, layoutResourceId, items);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // Reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.listview_row, null);
            // Configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.pointOfInterestImageView = (ImageView) rowView.findViewById(R.id.pointOfInterestImageView);
            viewHolder.pointOfInterestTextView = (TextView) rowView.findViewById(R.id.pointOfInterestTextView);
            rowView.setTag(viewHolder);
        }

        // Fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        new ImageLoaderAsyncTask(holder).execute(new Integer(mItems[position].getImageResourceId()));
        holder.pointOfInterestTextView.setText(mItems[position].getName());

        return rowView;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private class ImageLoaderAsyncTask extends AsyncTask<Integer, Void, Bitmap> {
        private ViewHolder mHolder;

        public ImageLoaderAsyncTask(ViewHolder holder) {
            mHolder = holder;
        }
        protected Bitmap doInBackground(Integer... imageResourceId) {
            //return BitmapFactory.decodeResource(mContext.getResources(), imageResourceId[0]);
            return decodeSampledBitmapFromResource(mContext.getResources(), imageResourceId[0], 50, 50);
        }
        protected void onPostExecute(Bitmap image) {
            RoundImage pointOfInterestImage = new RoundImage(image);
            mHolder.pointOfInterestImageView.setImageDrawable(pointOfInterestImage);
        }
    }
}