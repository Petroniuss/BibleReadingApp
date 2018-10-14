package juniordev.iet.com.biblereading;

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
import android.widget.ProgressBar;
import android.widget.TextView;

public class PonderationFragment extends Fragment {

    private static final String ARG_POSITION = "index";

    private Article mArticle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Log.i("PondFr", "fragment created");

        mArticle = ArticleLab.get().getArticle(getArguments().getInt(ARG_POSITION));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_reading_page, container, false);

        //set status bar color
        Window window = getActivity().getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(android.R.color.background_dark));
        }

        //inflate the view with article
        ((TextView) v.findViewById(R.id.reading_page_title)).setText(mArticle.getTitle());
        ((TextView) v.findViewById(R.id.reading_page_text)).setText(mArticle.getText() + "\n\n" + mArticle.getAuthor());
        (v.findViewById(R.id.progressBar)).setVisibility(View.GONE);

        return v;
    }

    // url pointing to the article
    public static Fragment newInstance(int pos) {

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pos);

        Fragment fragment = new PonderationFragment();
        fragment.setArguments(args);
        return fragment;
    }


}
