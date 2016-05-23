package toronto.amazinglocations.com.discovertoronto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ArrayList<ImageView> mTabSelectors;
    private ArrayList<View> mTabIndicators;
    private ViewPager mPager;

    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected void onResume() {
        super.onResume();

        // Setting up the Fragment class names to be used for the different pages by the ViewPager.
        ArrayList<String> pageFragmentClassNames = new ArrayList<String>();
        pageFragmentClassNames.add(PointsOfInterestListViewFragment.class.getName());
        pageFragmentClassNames.add(BixiBikeLocationsViewFragment.class.getName());
        pageFragmentClassNames.add(CurrentLocationViewFragment.class.getName());

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), pageFragmentClassNames));
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setSelectedIndicator(position);
            }
        });

        mTabSelectors = new ArrayList<ImageView>();

        ImageView pointsOfInterestTabImageView = (ImageView)findViewById(R.id.pointsOfInterestTabImageView);
        pointsOfInterestTabImageView.setOnClickListener(this);

        ImageView bixiBikeLocationsTabImageView = (ImageView)findViewById(R.id.bixiBikeLocationsTabImageView);
        bixiBikeLocationsTabImageView.setOnClickListener(this);

        ImageView navigationTabImageView = (ImageView)findViewById(R.id.navigationTabImageView);
        navigationTabImageView.setOnClickListener(this);

        mTabSelectors.add(pointsOfInterestTabImageView);
        mTabSelectors.add(bixiBikeLocationsTabImageView);
        mTabSelectors.add(navigationTabImageView);

        mTabIndicators = new ArrayList<View>();

        View pointsOfInterestTabIndicator = (View)findViewById(R.id.pointsOfInterestTabIndicator);
        View bixiBikeLocationsTabIndicator = (View)findViewById(R.id.bixiBikeLocationsTabIndicator);
        View navigationTabIndicator = (View)findViewById(R.id.navigationTabIndicator);

        mTabIndicators.add(pointsOfInterestTabIndicator);
        mTabIndicators.add(bixiBikeLocationsTabIndicator);
        mTabIndicators.add(navigationTabIndicator);

        setSelectedIndicator(0);
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

    public void onClick(View view) {
        int id = view.getId();

        for(int i = 0; i < mTabSelectors.size(); i++) {
            if (id == mTabSelectors.get(i).getId()) {
                mPager.setCurrentItem(i);
            }
        }
    }

    private void setSelectedIndicator(int position) {
        for (int i = 0; i <  mTabIndicators.size(); i++) {
            if (position == i) {
                mTabIndicators.get(i).setBackgroundResource(R.color.colorPrimary);
            }
            else {
                mTabIndicators.get(i).setBackgroundResource(R.color.colorTransluscent);
            }
        }
    }
}