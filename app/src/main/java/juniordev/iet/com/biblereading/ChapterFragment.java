package juniordev.iet.com.biblereading;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import juniordev.iet.com.biblereading.databinding.FragmentReadingPageBinding;

import static juniordev.iet.com.biblereading.R.color.colorPrimary;

public class ChapterFragment extends Fragment{

    public static final String ADRESS_URL = "http://niezbednik.niedziela.pl/liturgia/";
    private static final String ARG_DATE = "date";
    private static final String ARG_CHAPTER = "chapter";
    private static final String TAG = "ChapterFragment";

    private FragmentReadingPageBinding mBinding;
    private String mChapter;
    private String mText;
    private Date mDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_reading_page, container, false);

        mBinding.readingPageTitle.setText(mChapter);

        //set status bar color
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(android.R.color.background_dark));
        }
        if(isAdded() && mText != null){
            formatResponse(mText);
        }

        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mChapter = getArguments().getString(ARG_CHAPTER);
        if(mChapter.equals(ReadingListFragment.CHAPTERS[0])) mChapter = "1. czytanie";
        mDate = (Date) getArguments().getSerializable(ARG_DATE);

        new FetchTextTask().execute(mDate);
    }

    public static ChapterFragment newInstance(String chapter, Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        args.putString(ARG_CHAPTER, chapter);

        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(args);

        return fragment;
    }
    // replace </p> and <br> with "\n"
    //we should also trim that "                            "
                            // "                            "
    private void formatResponse(String response){
        StringBuilder sb = new StringBuilder(response);
        String title1, title2, text;
        int indexOfTitle1 = sb.indexOf("<h2>" + mChapter) + 4;
        int endIndexOfTitle1 = sb.indexOf("</h2>", indexOfTitle1);

        title1 = sb.substring(indexOfTitle1, endIndexOfTitle1);

        int indexOfTitle2 = sb.indexOf("<em>", endIndexOfTitle1) + 4;
        int endIndexOfTitle2 = sb.indexOf("</em>", indexOfTitle2);

        title2 = sb.substring(indexOfTitle2, endIndexOfTitle2) + ".";

        int indexOfText = sb.indexOf("<p>", endIndexOfTitle2);
        int endIndexOfText = (sb.indexOf("<h2>", indexOfText) < sb.indexOf("</div>", indexOfText))
                ? sb.indexOf("<h2>", indexOfText) :
                    sb.indexOf("</div>", indexOfText);

        text = sb.substring(indexOfText, endIndexOfText)
                .replaceAll("<p>", "")
                .replaceAll("<strong>", "")
                .replaceAll("</strong>", "")
                .replaceAll("<br>", "")
                .replaceAll("</p>", "\n")
                .replaceFirst("                            ", "");
        Log.i(TAG, text);

        mBinding.readingPageTitle.setText(title1);
        mBinding.readingPageText.setText(title2 +"\n" + "\n" + text);
    }

    public static String getDateString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }

    private class FetchTextTask extends AsyncTask<Date, Void, String>{
        @Override
        protected String doInBackground(Date... dates) {
            String result = "";
            try{
                Log.i(TAG, ADRESS_URL + getDateString(dates[0]));
                mText = new TextFetcher()
                        .getUrlString(ADRESS_URL + getDateString(dates[0]));
                Log.i(TAG, result);
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to fetch URL ", ioe);
            }

            return mText;
        }

        @Override
        protected void onPostExecute(String s) {
            formatResponse(s);
            mBinding.progressBar.setVisibility(View.GONE);
        }
    }
}






















