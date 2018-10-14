package juniordev.iet.com.biblereading;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ReadingListActivity extends SingleFragmentActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected Fragment createFragment() {
        return ReadingListFragment.newInstance();
    }
}
