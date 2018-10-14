package juniordev.iet.com.biblereading;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static juniordev.iet.com.biblereading.ChapterFragment.ADRESS_URL;
import static juniordev.iet.com.biblereading.ChapterFragment.getDateString;

//This is the class responsible for downloading articles
public class ArticleProvider {


    private static final String ADRESS = "http://niezbednik.niedziela.pl";
    private static final String TAG = "ArticleProvider";
    private static int sRunningThreads = 0;
    private List<String> urls;
    private onArticlesDownloadListener mListener;

    public ArticleProvider(onArticlesDownloadListener listener) {
        urls = new ArrayList<>();
        mListener = listener;
    }

    public void go(Date date) {
        new FetchUrlsTask().execute(date);
    }

    // works fine
    private void findUrls(String mText) {
        StringBuilder sb = new StringBuilder(mText);
        int startSearch = sb.indexOf("<p class=\"text-center\">Rozważania</p>");
        int utlimateEnd = sb.indexOf("</dd>", startSearch);
        int start = startSearch;
        int end;
        while (start < utlimateEnd) {
            start = sb.indexOf("href=\"", start) + 6;
            end = sb.indexOf("\"", start);
            if (start < utlimateEnd) {
                urls.add(sb.substring(start, end));
                Log.i("LAL", sb.substring(start, end));
            }
            start = end;
        }
    }

    //Looks fine to me
    private void formatResponse(String r) {
        StringBuilder sb = new StringBuilder(r);
        String author, article, title;

        int titleStart = sb.indexOf("<h1>") + 4;
        int titleEnd = sb.indexOf("</h1>", titleStart);
        int authorStart = sb.indexOf("<h4 class=\"text-right\">", titleEnd) + 23;
        int authorEnd = sb.indexOf("</h4>", authorStart);
        int articleStart = sb.indexOf("<p>", titleEnd);
        int articleEnd = authorStart - 23;

        if (articleStart > articleEnd) return;
        if (authorStart > authorEnd) return;
        if (titleStart > titleEnd) return;

        Log.i(TAG, titleStart + "|" + titleEnd);
        Log.i(TAG, authorStart + "|" + authorEnd);
        Log.i(TAG, articleStart + "|" + articleEnd);

        title = sb.substring(titleStart, titleEnd);
        author = sb.substring(authorStart, authorEnd);
        article = sb.substring(articleStart, articleEnd)
                .replaceAll("<p>", "")
                .replaceAll("</p>", "\n");

        Log.i(TAG, title + article + author);

        Article art = new Article(title, article, author);
        ArticleLab.get().addArticle(art);
        //we'll bind that to a view;
    }

    public interface onArticlesDownloadListener {
        void onFinished();
    }

    private class FetchUrlsTask extends AsyncTask<Date, Void, String> {
        @Override
        protected String doInBackground(Date... dates) {
            String mText = "";
            try {
                mText = new TextFetcher().getUrlString(ADRESS_URL + getDateString(dates[0]));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return mText;
        }

        @Override
        protected void onPostExecute(String mText) {
            findUrls(mText);
            Iterator<String> iterator = urls.iterator();
            while (iterator.hasNext()) {
                new FetchArticleTask().execute(iterator.next()); // actually that's poorly written
                sRunningThreads++;
            }
        }
    }

    private class FetchArticleTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                response = new TextFetcher().getUrlString(ADRESS + urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            formatResponse(response);
            sRunningThreads--;
            if (sRunningThreads == 0) {
                mListener.onFinished();
            }
        }
    }

}
