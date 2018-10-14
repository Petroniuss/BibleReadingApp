package juniordev.iet.com.biblereading;

import java.util.ArrayList;
import java.util.List;

public class ArticleLab {

    private static ArticleLab sLab;

    private List<Article> mArticles;

    public static ArticleLab get(){
        if (sLab == null) {
            sLab = new ArticleLab();
        }

        return sLab;
    }

    private ArticleLab(){
        mArticles = new ArrayList<>();
    }

    public Article getArticle(int position){
        return mArticles.get(position);
    }

    public List<Article> getArticles() {
        return mArticles;
    }

    public void addArticle(Article article) {
        mArticles.add(article);
    }

    public void clear(){
        mArticles = new ArrayList<>();
    }
}
