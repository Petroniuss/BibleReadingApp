package juniordev.iet.com.biblereading;

public class Article {

    private String mTitle, mText, mAuthor;

    public Article(String title, String text, String author){
        mTitle = title;
        mText = text;
        mAuthor = author;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getText() {
        return mText;
    }

    public String getTitle() {
        return mTitle;
    }
}
