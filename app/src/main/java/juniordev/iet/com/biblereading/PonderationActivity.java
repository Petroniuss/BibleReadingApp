package juniordev.iet.com.biblereading;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Date;
import java.util.List;

public class PonderationActivity extends AppCompatActivity {


    private static final String TAG = "PondActivity";

    private ViewPager mViewPager;
    private List<Article> mArticles;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ponderation_pager);

        Log.i(TAG, "Activity started");

        mViewPager = findViewById(R.id.view_pager);
        mArticles = ArticleLab.get().getArticles();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                return PonderationFragment.newInstance(i);
            }

            @Override
            public int getCount() {
                return mArticles.size();
            }
        });

    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, PonderationActivity.class);
        Log.i(TAG, "request for new intent");
        return intent;
    }

}
