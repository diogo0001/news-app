package domain.ckl_1_android_diogo_tavares;

import android.util.Log;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

//--------------------------------------------------------------------------------------------------
//  Handle the sort and filter options
//--------------------------------------------------------------------------------------------------
public class RealmHandleSortAndFilter {

    private Realm realm;
    private String filterMode;
    private String sortMode;
    private RealmResults<Article> articlesRealm;

    public RealmHandleSortAndFilter(Realm realm, String filterMode, String sortMode) {
        this.realm = realm;
        this.filterMode = filterMode;
        this.sortMode = sortMode;
    }

    //----------------------------------------------------------------------------------------------
    // Sort the list, with all articles and filtered
    //----------------------------------------------------------------------------------------------
    public RealmResults<Article> getListBySort() {
        RealmResults<Article> list;

        if (!sortMode.equals("none") && !sortMode.equals("date")) {    // Normal sorting
            list = realm.where(Article.class)
                    .sort(sortMode, Sort.ASCENDING)
                    .findAll();
            Log.i("LOG", "Sort done sucesfuly");
        } else if (sortMode.equals("date")) {             // If date option, sort by title as well
            list = realm.where(Article.class)
                    .sort(sortMode, Sort.ASCENDING, "title", Sort.ASCENDING)
                    .findAll();
            Log.i("LOG", "Sort done sucesfuly");
        } else {
            list = realm.where(Article.class).findAll();             // No sorting
        }
        return list;
    }

    //----------------------------------------------------------------------------------------------
    // Filter the list with the previous sort
    //----------------------------------------------------------------------------------------------
    public RealmResults<Article> getListByFilter(){

        RealmResults<Article> list;

        if(!sortMode.equals("none")){                                       // There's sorting
            if(!filterMode.equals("none")) {                                // There's filtering
                try {
                    list = realm.where(Article.class)                       // List by previous sort
                            .equalTo("tags.label", filterMode)
                            .sort(sortMode,  Sort.ASCENDING).findAll();
                    Log.i("LOG", "Filter done sucesfuly");
                } catch ( Exception e ){
                    Log.i("LOG", "Filter not sucess");
                    Log.i("LOG error", e.toString());
                    return null;
                }
            } else {                                                         // No filtering
                list = getListBySort();
            }
        } else {                                                             // No sorting
            if(!filterMode.equals("none")) {                                 // Filtering
                try{
                    list = realm.where(Article.class)
                            .equalTo("tags.label", filterMode)
                            .findAll();
                } catch ( Exception e ){
                    Log.i("LOG", "Filter not sucess");
                    Log.i("LOG error", e.toString());
                    return null;
                }
            } else {                                                         // No filtering
                list = realm.where(Article.class).findAll();
            }
        }
        return list;
    }

    //----------------------------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------------------------
    public RealmResults<Article> getArticlesRealm() { return articlesRealm; }
    public void setArticlesRealm(RealmResults<Article> articlesRealm) { this.articlesRealm = articlesRealm; }

    public Realm getRealm() { return realm; }
    public void setRealm(Realm realm) { this.realm = realm; }

    public String getFilterMode() { return filterMode; }
    public void setFilterMode(String filterMode) { this.filterMode = filterMode; }

    public String getSortMode() { return sortMode; }
    public void setSortMode(String sortMode) { this.sortMode = sortMode; }
}
