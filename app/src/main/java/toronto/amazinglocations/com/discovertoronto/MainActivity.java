package toronto.amazinglocations.com.discovertoronto;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import toronto.amazinglocations.com.discovertoronto.misc.LocationEnabledChecker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ENABLE_LOCATION_REQUEST = 1;  // The request code.
    private static int currentlySelectedTabPosition = 0;
    private boolean wasLocationSettingsActivityVisible = false;
    private ArrayList<ImageView> mTabSelectors;
    private ArrayList<View> mTabIndicators;
    private ViewPager mPager;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume() {
        super.onResume();

        boolean isLocationEnabled = LocationEnabledChecker.isLocationEnabled(this);
        // If Google location is not enabled, need to show the Activity from which user can enable it.
        if (!isLocationEnabled && !wasLocationSettingsActivityVisible) {
            Toast.makeText(this, getResources().getString(R.string.gps_network_not_enabled), Toast.LENGTH_LONG).show();
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, ENABLE_LOCATION_REQUEST);
            return;
        }
        // Otherwise if location is enabled, continue.
        else if (isLocationEnabled) {
            wasLocationSettingsActivityVisible = false;
            enableActivity();
        }
        else {
            finish();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If location settings Activity was visible, the boolean flag would be true.
        if (requestCode == ENABLE_LOCATION_REQUEST) {
            wasLocationSettingsActivityVisible = true;
        }
    }

    protected void enableActivity() {
        // Setting up the Fragment class names to be used for the different pages by the ViewPager.
        ArrayList<String> pageFragmentClassNames = new ArrayList<String>();
        pageFragmentClassNames.add(PointsOfInterestListViewFragment.class.getName());
        pageFragmentClassNames.add(BixiBikeLocationsViewFragment.class.getName());
        pageFragmentClassNames.add(CurrentLocationViewFragment.class.getName());

        // Getting a reference to the ViewPager and setting its Adapter.
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), pageFragmentClassNames));
        // Registering listener for ViewPager swipe event.
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelectedIndicator(position);
            }
        });

        // ArrayList holding the page tabs at the top.
        mTabSelectors = new ArrayList<ImageView>();
        // Getting references to the page tabs and registering listeners for press event.
        ImageView pointsOfInterestTabImageView = (ImageView)findViewById(R.id.pointsOfInterestTabImageView);
        pointsOfInterestTabImageView.setOnClickListener(this);

        ImageView bixiBikeLocationsTabImageView = (ImageView)findViewById(R.id.bixiBikeLocationsTabImageView);
        bixiBikeLocationsTabImageView.setOnClickListener(this);

        ImageView navigationTabImageView = (ImageView)findViewById(R.id.navigationTabImageView);
        navigationTabImageView.setOnClickListener(this);

        // Adding page tabs to ArrayList.
        mTabSelectors.add(pointsOfInterestTabImageView);
        mTabSelectors.add(bixiBikeLocationsTabImageView);
        mTabSelectors.add(navigationTabImageView);

        // ArrayList holding the page tab indicators. These indicators let the user know the position of the currently
        // selected tab through special coloring.
        mTabIndicators = new ArrayList<View>();

        // Getting references to the indicators.
        View pointsOfInterestTabIndicator = (View)findViewById(R.id.pointsOfInterestTabIndicator);
        View bixiBikeLocationsTabIndicator = (View)findViewById(R.id.bixiBikeLocationsTabIndicator);
        View navigationTabIndicator = (View)findViewById(R.id.navigationTabIndicator);

        // Adding the indicators to ArrayList.
        mTabIndicators.add(pointsOfInterestTabIndicator);
        mTabIndicators.add(bixiBikeLocationsTabIndicator);
        mTabIndicators.add(navigationTabIndicator);

        // Setting the currently selected tab. 'currentlySelectedTabPosition' remembers the current tab position even after a
        // screen orientation change.
        mPager.setCurrentItem(currentlySelectedTabPosition);
        setSelectedIndicator(currentlySelectedTabPosition);
    }

    public void onClick(View view) {
        int id = view.getId();
        // Looping through the ArrayList, and after getting the index of the clicked tab, changing the displayed Fragment.
        for(int i = 0; i < mTabSelectors.size(); i++) {
            if (id == mTabSelectors.get(i).getId()) {
                mPager.setCurrentItem(i);
                break;
            }
        }
    }

    private void setSelectedIndicator(int position) {
        currentlySelectedTabPosition = position;
        // Looping through the ArrayList, and setting the color of the selected tab indicator to blue.
        for (int i = 0; i <  mTabIndicators.size(); i++) {
            if (position == i) {
                mTabIndicators.get(i).setBackgroundResource(R.color.colorPrimary);
            }
            else {
                mTabIndicators.get(i).setBackgroundResource(R.color.colorTransluscent);
            }
        }
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<String> mFragmentPageNames;

        public MainPagerAdapter(FragmentManager fm, ArrayList<String> fragmentPageNames) {
            super(fm);
            mFragmentPageNames = fragmentPageNames;
        }

        public Fragment getItem(int position) {
            return Fragment.instantiate(MainActivity.this, mFragmentPageNames.get(position));
        }

        public int getCount() {
            return mFragmentPageNames.size();
        }
    }
}