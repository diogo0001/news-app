package domain.ckl_1_android_diogo_tavares;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import spencerstudios.com.bungeelib.Bungee;

// ---------------------------------------------------------------------------------------------
// List View and options
// ---------------------------------------------------------------------------------------------
public class ListActivity extends AppCompatActivity {

    private Realm realm;
    private RealmResults<Article> articlesRealm;
    private RecyclerView recyclerView;
    private ArticlesAdapter myAdapter;
    private Toolbar toolbar;
    private String sortMode;
    private String filterMode;
    private int dialogSortIndex;
    private int dialogFilterIndex;
    private SharedPreferences sp;
    private RealmHandleSortAndFilter sortHandler;
    private String[] tagItems;

    // ---------------------------------------------------------------------------------------------
    // Create activity
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Log.i("Log","onCreate() -- ListActivity");

        sp = getPreferences(MODE_PRIVATE);
        sortMode = sp.getString("sortMode", "none");           // First time gets default string "none"
        dialogSortIndex = sp.getInt("dialogIndex",2);
        dialogFilterIndex = 0;                                        // Always reset the filter
        filterMode = "none";
        Log.i("L", "shared sortMode: "+sortMode);

        realm = Realm.getDefaultInstance();

        getTagsList();
        toolbarInit();
    }

    // ---------------------------------------------------------------------------------------------
    // View list and click item
    // ---------------------------------------------------------------------------------------------
    public void showListRealm(RealmResults<Article> list){
        if(list != null) {
            recyclerView = findViewById(R.id.recycler_view);
            myAdapter = new ArticlesAdapter(list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListActivity.this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

            RecycleClick.addTo(recyclerView).setOnItemClickListener((recyclerView, position, v) -> {

                Log.i("LOG", " *** Click ***");
                Log.i("L", "Status article position 1: " + position + " title " + articlesRealm.get(position).getTitle());

                // set position as checked
                realm.beginTransaction();
                articlesRealm.get(position).setCheck(true);
                realm.insertOrUpdate(articlesRealm.get(position));
                realm.commitTransaction();

                String title = articlesRealm.get(position).getTitle();

                Log.i("L", "Status article checked after action: " + articlesRealm.get(position).isChecked());
                openActivityArticle(title);
            });
        } else {
            Log.i("LOG", "List -- null");
            Toast.makeText(ListActivity.this, "Unable to show list!", Toast.LENGTH_LONG).show();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Show the details by article title
    // ---------------------------------------------------------------------------------------------
    public void openActivityArticle(String title){
        Log.i("LOG", "openActivityArticle()");
        Intent intent = new Intent(this,ContentActivity.class);
        intent.putExtra("title",title);
        startActivity(intent);
        Bungee.swipeLeft(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Init toolbar
    // ---------------------------------------------------------------------------------------------
    public void toolbarInit(){
        toolbar = findViewById(R.id.toolbarList);
        setSupportActionBar(toolbar);
    }

    // ---------------------------------------------------------------------------------------------
    // Inflate the menu options in toolbar
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // ---------------------------------------------------------------------------------------------
    // Menu options handle
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.sort:
                Log.i("LOG", "Sort");
                sortOptions();
                return true;
            case R.id.filter:
                Log.i("LOG", "Filter");
                filterOptions();
                return true;
            case R.id.update:
                Log.i("LOG", "Update");
                onBackPressed();
                return true;
            default:
                Log.i("LOG", "Something wrong");
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------------------------------------------------
    // Dialog sort options logic
    // ---------------------------------------------------------------------------------------------
    public void sortOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort by:");

        //list of sort options sorted
        String[] items = {"Author","Date","None","Tilte","Website"};

        builder.setSingleChoiceItems(items, dialogSortIndex,
                (dialog, which) -> {
                    dialogSortIndex = which;
                });

        String positiveText = "Ok";
        builder.setPositiveButton(positiveText,
                (dialog, which) -> {                            // which == -1
                    // positive button logic
                    switch (dialogSortIndex) {
                        case 0: sortMode = "authors"; break;
                        case 1: sortMode = "date"; break;
                        case 2: sortMode = "none"; break;
                        case 3: sortMode = "title"; break;
                        case 4: sortMode = "website"; break;
                    }

                    // save the state on shared preferences
                    sp.edit().putInt("dialogIndex",dialogSortIndex).apply();
                    sp.edit().putString("sortMode",sortMode).apply();

                    sortHandler.setSortMode(sortMode);

                    if(dialogFilterIndex!=0){
                        articlesRealm = sortHandler.getListByFilter();
                    } else {
                        articlesRealm = sortHandler.getListBySort();
                    }
                    showListRealm(articlesRealm);
                    Log.i("Log","*** Selected ok ** Option "+sortMode);
                });

        String negativeText ="Cancel";
        builder.setNegativeButton(negativeText,
                (dialog, which) -> {                    // which == -2
                    // negative button logic
                    Log.i("Log","*** Canceled *** "+which);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.i("Log"," Sort dialog.show() ");
    }

    // ---------------------------------------------------------------------------------------------
    // Dialog filter options logic
    // ---------------------------------------------------------------------------------------------
    public void filterOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filter by tags:");

        builder.setSingleChoiceItems(tagItems, dialogFilterIndex,
                (dialog, which) -> {
                    dialogFilterIndex = which;
                });

        String positiveText = "Ok";
        builder.setPositiveButton(positiveText,
                (dialog, which) -> {                            // which == -1
                    // positive button logic
                    switch (dialogFilterIndex) {
                        case 0: filterMode = "none"; break;
                        case 1: filterMode = "Politics"; break;
                        case 2: filterMode = "Science"; break;
                        case 3: filterMode = "Sports"; break;
                        case 4: filterMode = "Tech"; break;
                    }
                    sortHandler.setFilterMode(filterMode);
                    articlesRealm = sortHandler.getListByFilter();
                    showListRealm(articlesRealm);
                    Log.i("Log","*** Selected OK ** filter option "+filterMode);
                });

        String negativeText ="Cancel";
        builder.setNegativeButton(negativeText,
                (dialog, which) -> {                    // which == -2
                    // negative button logic
                    Log.i("Log","*** Canceled *** "+which);
                });

        AlertDialog dialog = builder.create();
        dialog.show();
        Log.i("Log","Filter dialog.show() ");
    }

    // ---------------------------------------------------------------------------------------------
    // Get the list of tags for filter options
    // ---------------------------------------------------------------------------------------------
    public void getTagsList(){
        List<String> list = new ArrayList<String>();
        RealmResults<Article> artList;
        artList = realm.where(Article.class).findAll();

        for(Article a: artList){
            for(int i=0;i<a.getTags().size();i++) {
                if(!list.contains(a.getTags().get(i).getLabel())) {
                    list.add(a.getTags().get(i).getLabel());
                }
            }
        }
        Collections.sort(list);
        list.add(0,"None");
        tagItems = list.toArray(new String[0]);
        Log.i("LOG","Tags list full: "+tagItems);
    }

    // ---------------------------------------------------------------------------------------------
    // Overrides and set transition animation
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("Log","onBackPressed() -- ContentActivity");
        Bungee.zoom(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Call the show list function here... when come back from details need to show the list again
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Log", "onResume() -- ListActivity");
        sortHandler = new RealmHandleSortAndFilter(realm,filterMode,sortMode);

        if(dialogFilterIndex!=0){
            articlesRealm = sortHandler.getListByFilter();
        } else {
            articlesRealm = sortHandler.getListBySort();
        }
        showListRealm(articlesRealm);
    }
}
