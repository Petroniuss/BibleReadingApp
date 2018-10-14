package juniordev.iet.com.biblereading;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import juniordev.iet.com.biblereading.databinding.ListItemViewBinding;
import juniordev.iet.com.biblereading.databinding.FragmentReadingListBinding;

public class ReadingListFragment extends Fragment {

    public static final String[] CHAPTERS = {"Pierwsze czytanie",
        "Psalm", "Aklamacja", "Ewangelia", "Rozważania"
    };

    private static final int REQUEST_DATE = 0x0;
    private static final String TAG = "ReadingListFragment";

    private Date mChosenDate;
    private FragmentReadingListBinding mListBinding;

    public static ReadingListFragment newInstance() {

        Bundle args = new Bundle();

        ReadingListFragment fragment = new ReadingListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mChosenDate == null) {
            mChosenDate = Calendar.getInstance().getTime();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mListBinding = DataBindingUtil
                .inflate(inflater, R.layout.fragment_reading_list, container, false);

        mListBinding.readingListView.setAdapter(
                new ListArrayAdapter(getActivity(), R.layout.list_item_view, CHAPTERS));

        mListBinding.dateTextView.setText(getFormattedTime(mChosenDate));

        mListBinding.readingListCalendarIcon.setOnClickListener(v -> {
            DatePickerFragment dialog = DatePickerFragment.newInstance(mChosenDate);
            dialog.setTargetFragment(ReadingListFragment.this, REQUEST_DATE);
            dialog.show(getFragmentManager(), "Dialog_Date");
        });

        return mListBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mChosenDate = date;
            mListBinding.dateTextView.setText(getFormattedTime(mChosenDate));
        }
    }

    //Utility method
    public static String getFormattedTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        
        return day + " " + month.toUpperCase();
    }

    private class ListArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] mValues;
        private final int mLayoutResId;


        public ListArrayAdapter(Context context, int layourResId, String[] values){
            super(context, layourResId, values);

            this.context = context;
            this.mValues = values;
            this.mLayoutResId = layourResId;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ListItemViewBinding binding = DataBindingUtil.inflate(
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                    mLayoutResId, parent, false);

            binding.readingListItemTextView.setText(mValues[position]);
            binding.readingListItemTextView.setOnClickListener(v -> {
                if(isNetworkAvaible() && position == CHAPTERS.length - 1) {
                    mListBinding.listProgressBar.setVisibility(View.VISIBLE);
                    ArticleLab.get().clear();
                    //all the heavy lifting goes up here
                    new ArticleProvider(new ArticleProvider.onArticlesDownloadListener() {
                        @Override
                        public void onFinished() {
                            Log.i(TAG, "finished downloading"); //not quite there yet :/

                            mListBinding.listProgressBar.setVisibility(View.GONE);
                            startActivity(PonderationActivity.newIntent(context));
                        }
                    }).go(mChosenDate);
                    // -- - - - - - -- - -

                } else if(isNetworkAvaible()){
                    startActivity(ChapterActivity.newIntent(context, CHAPTERS[position], mChosenDate));
                } else {
                    Toast.makeText(context,
                            "Make sure there's internet connection", Toast.LENGTH_SHORT)
                            .show();
                }
            });

            return binding.getRoot();
        }

        private boolean isNetworkAvaible(){
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null;
        }
    }

}
