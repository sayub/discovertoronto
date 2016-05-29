/*
* @author  Saad Muhammad Ayub
* Copyright 2016, Saad Muhammad Ayub, All rights reserved.
*/

package toronto.amazinglocations.com.discovertoronto.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import toronto.amazinglocations.com.discovertoronto.R;
import toronto.amazinglocations.com.discovertoronto.misc.PointOfInterest;
import toronto.amazinglocations.com.discovertoronto.misc.PointsOfInterestListViewArrayAdapter;

public class PointsOfInterestListViewFragment extends Fragment {
    private static final String CLASS = PointsOfInterestListViewFragment.class.getSimpleName();
    private static PointOfInterest sItemsToArray[] = null;
    private PointsOfInterestListViewArrayAdapter mAdapter;

    private int[] mPointsOfInterestImageResourceIds = {
            R.drawable.aquarium, R.drawable.artgalleryontario, R.drawable.cntower,
            R.drawable.rom, R.drawable.torontocityhall, R.drawable.torontoeatoncentre,
            R.drawable.torontozoo, R.drawable.yorkdalemall, R.drawable.hockeyhalloffame,
            R.drawable.aircanadacentre, R.drawable.batashoemuseum, R.drawable.harbourfrontcentre,
            R.drawable.ontariosciencecentre
    };

    private String[] mPointsOfInterestNames = {
            "Ripley's Aquarium", "Art Gallery of Ontario", "CN Tower",
            "Royal Ontario Museum", "Toronto City Hall", "Toronto Eaton Centre",
            "Toronto Zoo", "Yorkdale Mall", "Hockey Hall of Fame",
            "Air Canada Centre", "Bata Shoe Museum", "Harbourfront Centre", "Ontario Science Centre"
    };

    private double mLats[] = {43.642424, 43.653607, 43.642566, 43.66771, 43.65344, 43.653597, 43.817699,
            43.725887, 43.646988, 43.643466, 43.667257, 43.639118, 43.716465};
    private double mLngs[] = {-79.385865, -79.392512, -79.387057, -79.394777, -79.38409, -79.381455, -79.18589,
            -79.453206, -79.377264, -79.379099, -79.400084, -79.382891, -79.338713};
    private String mURLs[] =
            {"http://www.ripleyaquariums.com/canada/", "http://www.ago.net/", "http://www.cntower.ca/en-ca/home.html",
                    "http://www.rom.on.ca/en", "http://www.toronto.ca/"
                    , "http://www.torontoeatoncentre.com/en/Pages/default.aspx", "http://www.torontozoo.com/",
                    "http://yorkdale.com/", "http://www.hhof.com/", "http://www.theaircanadacentre.com/",
                    "http://www.batashoemuseum.ca/", "http://www.harbourfrontcentre.com/", "http://www.ontariosciencecentre.ca/"
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_points_of_interest_list_view, container, false);

        // Once pointsOfInterestItems' is populated and the values are sorted based on names, it should remain in memory until
        // the app is killed.
        if (sItemsToArray == null) {
            ArrayList<PointOfInterest> pointsOfInterestItems = new ArrayList<PointOfInterest>();

            for (int i = 0; i < mPointsOfInterestImageResourceIds.length; i++) {
                //Bitmap bitmap = decodeSampledBitmapFromResource(getResources(), mPointsOfInterestImages[i], 60, 60);
                pointsOfInterestItems.add(new PointOfInterest(mPointsOfInterestImageResourceIds[i], mPointsOfInterestNames[i], mLats[i], mLngs[i], mURLs[i]));
            }
            // Sorting the ArrayList 'items'.
            Collections.sort(pointsOfInterestItems, new PointsOfInterestComparator());

            // Also stays in memory for performance reasons until app is killed.
            sItemsToArray = new PointOfInterest[pointsOfInterestItems.size()];

            for (int i = 0; i < pointsOfInterestItems.size(); i++) {
                sItemsToArray[i] = pointsOfInterestItems.get(i);
            }

            pointsOfInterestItems = null;
        }
        // Creating the adapter for the ListView.
        mAdapter = new PointsOfInterestListViewArrayAdapter(getActivity(), R.layout.listview_row, sItemsToArray);
        // Getting reference to the ListView.
        ListView listView = (ListView)view.findViewById(R.id.pointsOfInterestListView);
        // Setting the ListView Adapter.
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent webViewIntent = new Intent(getActivity(), WebViewActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("name", sItemsToArray[position].getName());
                bundle.putDouble("lat", sItemsToArray[position].getLatitude());
                bundle.putDouble("lng", sItemsToArray[position].getLongitude());
                bundle.putString("url", sItemsToArray[position].getURL());
                webViewIntent.putExtras(bundle);

                getActivity().startActivity(webViewIntent);
            }
        });

        return view;
    }

    public void onDestroyView() {
        Log.i(CLASS, "onDestroyView()");
        super.onDestroyView();
    }

    class PointsOfInterestComparator implements Comparator<PointOfInterest> {
        @Override
        public int compare(PointOfInterest c1, PointOfInterest c2) {
            return c1.getName().compareTo(c2.getName());
        }
    }
}
