package toronto.amazinglocations.com.discovertoronto.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import toronto.amazinglocations.com.discovertoronto.R;
import toronto.amazinglocations.com.discovertoronto.RoundImage;
import toronto.amazinglocations.com.discovertoronto.misc.PointOfInterest;

public class PointsOfInterestListViewArrayAdapter extends ArrayAdapter<PointOfInterest> {
    private Context mContext;
    private int mLayoutResourceId;
    private PointOfInterest mItems[];

    public PointsOfInterestListViewArrayAdapter(Context context, int layoutResourceId, PointOfInterest items[]) {
        super(context, layoutResourceId, items);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Inflating the row view.
        View rowView = inflater.inflate(mLayoutResourceId, parent, false);

        // Retrieing the individual components of the row.
        ImageView pointOfInterestImageView = (ImageView) rowView.findViewById(R.id.pointOfInterestImageView);
        TextView pointOfInterestTextView = (TextView) rowView.findViewById(R.id.pointOfInterestTextView);

        RoundImage pointOfInterestImage = new RoundImage(mItems[position].getImage());
        pointOfInterestImageView.setImageDrawable(pointOfInterestImage);
        pointOfInterestTextView.setText(mItems[position].getName());

        return rowView;
    }
}