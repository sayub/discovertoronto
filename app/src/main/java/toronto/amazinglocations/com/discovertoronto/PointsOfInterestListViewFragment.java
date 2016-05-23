package toronto.amazinglocations.com.discovertoronto;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import toronto.amazinglocations.com.discovertoronto.misc.PointOfInterest;
import toronto.amazinglocations.com.discovertoronto.misc.PointsOfInterestListViewArrayAdapter;

public class PointsOfInterestListViewFragment extends Fragment {
    private int[] mPointsOfInterestImages = {
            R.drawable.aquarium, R.drawable.artgalleryontario, R.drawable.cntower,
            R.drawable.rom, R.drawable.torontocityhall, R.drawable.torontoeatoncentre,
            R.drawable.torontozoo, R.drawable.yorkdalemall, R.drawable.hockey_hall_of_fame,
            R.drawable.aircanadacenter
    };

    private String[] mPointsOfInterestNames = {
            "Ripley's Aquarium", "Art Gallery of Ontario", "CN Tower",
            "Royal Ontario Museum", "Toronto City Hall", "Toronto Eaton Center",
            "Toronto Zoo", "Yorkdale Mall", "Hockey Hall of Fame",
            "Air Canada Center"
    };

    private double mLats[] = {43.642424, 43.653607, 43.642566, 43.66771, 43.65344, 43.653597, 43.817699,
            43.725887, 43.646988, 43.643466};
    private double mLngs[] = {-79.385865, -79.392512, -79.387057, -79.394777, -79.38409, -79.381455, -79.18589,
            -79.453206, -79.377264, -79.379099};
    private String mURLs[] =
            {"http://www.ripleyaquariums.com/canada/", "http://www.ago.net/", "http://www.cntower.ca/en-ca/home.html",
                    "http://www.rom.on.ca/en", "http://www.toronto.ca/"
                    , "http://www.torontoeatoncentre.com/en/Pages/default.aspx", "http://www.torontozoo.com/",
                    "http://yorkdale.com/", "http://www.hhof.com/", "http://www.theaircanadacentre.com/"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_points_of_interest_list_view,
                container, false);

        final PointOfInterest items[] = new PointOfInterest[mPointsOfInterestImages.length];

        for (int i = 0; i < items.length; i++) {
            Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), mPointsOfInterestImages[i], 60, 60);
            //bitmap = bitmap.createScaledBitmap(bitmap, 100, 100, true);
            items[i] = new PointOfInterest(bitmap, mPointsOfInterestNames[i], mLats[i], mLngs[i], mURLs[i]);
        }

        PointsOfInterestListViewArrayAdapter adapter = new PointsOfInterestListViewArrayAdapter(getActivity(), R.layout.listview_row, items);

        ListView listView = (ListView)view.findViewById(R.id.pointsOfInterestListView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent webViewIntent = new Intent(getActivity(), WebViewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("name", items[position].getName());
                bundle.putDouble("lat", items[position].getLatitude());
                bundle.putDouble("lng", items[position].getLongitude());
                bundle.putString("url", items[position].getURL());
                webViewIntent.putExtras(bundle);

                getActivity().startActivity(webViewIntent);
            }
        });

        return view;
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
}
