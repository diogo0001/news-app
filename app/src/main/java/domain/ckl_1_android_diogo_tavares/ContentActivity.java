package domain.ckl_1_android_diogo_tavares;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.Realm;
import spencerstudios.com.bungeelib.Bungee;

// -------------------------------------------------------------------------------------------------
// Details
// -------------------------------------------------------------------------------------------------
public class ContentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Realm realm;

    // ---------------------------------------------------------------------------------------------
    // Create activity
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        Log.i("L", "onCreate() -- ContentActivity");

        realm = Realm.getDefaultInstance();;
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        Article article = realm.where(Article.class).equalTo("title", title).findFirst();

        toolbarInit();
        showContent(article);
        Log.i("L","Title: "+title+" --- "+article.getTitle());
    }

    // ---------------------------------------------------------------------------------------------
    // Show all the details
    // ---------------------------------------------------------------------------------------------
    public void showContent(Article article){
        TextView title, date,content,website;
        ImageView image;

        image = (ImageView)findViewById(R.id.imageView);
        title = (TextView)findViewById(R.id.title);
        date = (TextView)findViewById(R.id.date);
        content = (TextView)findViewById(R.id.content);
        website = (TextView)findViewById(R.id.website);

        if(article.getImageURL()!=null && !article.getImageURL().isEmpty()) {
            Context context = image.getContext();
            Picasso.with(context)
                    .load(article.getImageURL())
                    .fit()
                    .centerCrop()
                    .into(image);
        } else {
            image.setImageResource(R.drawable.image_off);
            image.setAlpha(0.65f);
        }
        title.setText(article.getTitle());
        date.setText(article.getDate()+"  by "+article.getAuthor());
        content.setText(article.getContent());
        website.setText("From "+article.getWebSite());
        showTags(article);
    }

    // ---------------------------------------------------------------------------------------------
    // Show the tag label in color box
    // ---------------------------------------------------------------------------------------------
    public void showTags(Article article){
        LinearLayout lin = (LinearLayout) findViewById(R.id.linear_tags);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(3,2,3,2);

        // For many tags
        for(Tags t: article.getTags()){
            TextView tagBox = new TextView(this);
            tagBox.setText(t.getLabel());
            tagBox.setTextSize(16);
            tagBox.setTextColor(Color.BLACK);
            tagBox.setBackgroundResource(R.drawable.tags_box);
            tagBox.setLayoutParams(lp);
            lin.addView(tagBox);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Init toolbar
    // ---------------------------------------------------------------------------------------------
    public void toolbarInit(){
        toolbar = (Toolbar)findViewById(R.id.toolbarData);
        toolbar.setNavigationIcon(R.drawable.arrow24);
        setSupportActionBar(toolbar);
    }

    // ---------------------------------------------------------------------------------------------
    // Back button
    // ---------------------------------------------------------------------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("Log","onOptionsItemSelected() -- ContentActivity");
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------------------------------------------------
    // Overrides and set transition animation
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onBackPressed() {
        Log.i("Log","onBackPressed() -- ContentActivity");
        super.onBackPressed();
        Bungee.swipeRight(this);
    }
}
