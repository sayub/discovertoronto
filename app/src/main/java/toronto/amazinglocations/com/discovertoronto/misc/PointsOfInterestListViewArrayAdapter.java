/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.misc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
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
            //return BitmapFactory.decodeResource(mContext.getResources(), imageResourceId[0]);
            return decodeSampledBitmapFromResource(mContext.getResources(), imageResourceId[0], 30, 30);
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

    public Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}