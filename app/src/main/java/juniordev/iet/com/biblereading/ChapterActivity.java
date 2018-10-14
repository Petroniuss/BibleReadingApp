package juniordev.iet.com.biblereading;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.Date;

public class ChapterActivity extends SingleFragmentActivity {

    public static final String EXTRA_CHAPTER = "juniordev.iet.chapter";
    public static final String EXTRA_DATE = "juniordev.iet.date";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    @Override
    protected Fragment createFragment() {
        String chapter = getIntent().getStringExtra(EXTRA_CHAPTER);
        Date date = (Date) getIntent().getSerializableExtra(EXTRA_DATE);

        return ChapterFragment.newInstance(chapter, date);
    }

    public static Intent newIntent(Context packageContext, String chapter, Date date){
        Intent intent = new Intent(packageContext, ChapterActivity.class);

        intent.putExtra(EXTRA_CHAPTER, chapter);
        intent.putExtra(EXTRA_DATE, date);

        return intent;
    }

}
