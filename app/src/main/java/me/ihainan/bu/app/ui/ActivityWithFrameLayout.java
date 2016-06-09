package me.ihainan.bu.app.ui;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import me.ihainan.bu.app.R;
import me.ihainan.bu.app.ui.assist.SwipeActivity;
import me.ihainan.bu.app.ui.fragment.FollowingListFragment;
import me.ihainan.bu.app.ui.fragment.NotificationListFragment;
import me.ihainan.bu.app.ui.fragment.PersonalThreadAndPostFragment;

public class ActivityWithFrameLayout extends SwipeActivity {
    // Tags
    private final static String TAG = ActivityWithFrameLayout.class.getSimpleName();
    public final static String TITLE_TAG = TAG + "TITLE_TAG";
    public final static String FRAGMENT_TAG = TAG + "FRAGMENT_TAG";

    // UI references
    private Toolbar mToolbar;

    // Data
    private String mTitle;
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_framelayout);

        // Get bundle data
        getExtra();

        // Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setTitle(mTitle);

        // FrameLayout
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, mFragment);
        fragmentTransaction.commit();

        // Swipe
        setSwipeAnyWhere(false);
    }

    private void getExtra() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mTitle = bundle.getString(TITLE_TAG);
            String fragmentClassName = bundle.getString(FRAGMENT_TAG);
            if (FollowingListFragment.class.getSimpleName().equals(fragmentClassName)) {
                mFragment = new FollowingListFragment();
            } else if ((PersonalThreadAndPostFragment.class.getSimpleName()).equals(fragmentClassName)) {
                mFragment = new PersonalThreadAndPostFragment();
            } else if ((NotificationListFragment.class.getSimpleName()).equals(fragmentClassName)) {
                mFragment = new NotificationListFragment();
            }

            if (mFragment != null) mFragment.setArguments(bundle);
        }
    }
}
