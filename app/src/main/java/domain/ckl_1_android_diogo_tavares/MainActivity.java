package domain.ckl_1_android_diogo_tavares;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import spencerstudios.com.bungeelib.Bungee;

//--------------------------------------------------------------------------------------------------
// Load data and create the realm model
//--------------------------------------------------------------------------------------------------
public class MainActivity extends AppCompatActivity {

    private Realm realm;
    private TextView loading;

    // ---------------------------------------------------------------------------------------------
    // Create activity and init realm
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Log.i("Log","onCreate() -- MainActivity");

        loading = (TextView)findViewById(R.id.initText);

        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();
    }

    // ---------------------------------------------------------------------------------------------
    // Get the data with retrofit from url
    // ---------------------------------------------------------------------------------------------
    private void getArticlesFromUrl() {
        ApiClient service = RetroClient.getRetrofitInstance().create(ApiClient.class);
        Call call = service.getArticlesService();
        Log.i("LOG","getArticlesFromUrl() -- call = service.getArticlesService()");

        call.enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                Log.i("LOG", "onResponse()");
                if (response.isSuccessful()) {

                    // load the articles in JsonArray and load realm
                     loadList(response.body());
                    Log.i("LOG", "Get list sucessfull");

                    // call the list activity when all were done
                    openListActivity();
                    Toast.makeText(MainActivity.this, "Loaded new articles!",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.i("LOG", "Get list not sucessfull");
                    checkRealmStatus();
                }
            }
            @Override
            public void onFailure(Call<List<Article>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Failure on load articles from API!",
                        Toast.LENGTH_LONG).show();
                checkRealmStatus();
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    // Load realm db model with data
    // ---------------------------------------------------------------------------------------------
    private void loadList(List<Article> list){

        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(list , new TypeToken<List<Article>>() {}.getType());
        JsonArray jsonArray = element.getAsJsonArray();
        Log.i("Log","//////////////////////////////////////////////////////////////////////////////////////////////////////////");
        Log.i("Log","JsonArray: "+ jsonArray);
        Log.i("Log","//////////////////////////////////////////////////////////////////////////////////////////////////////////");

        try{
            realm.beginTransaction();
            realm.createAllFromJson(Article.class,jsonArray.toString());
            realm.commitTransaction();
            Log.i("LOG", "Realm -- try ok");
        }
        catch( Exception e ){
            Log.i("LOG", "Realm -- catch");
            e.printStackTrace();
            realm.cancelTransaction();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // View in list activity
    // ---------------------------------------------------------------------------------------------
    public void openListActivity(){
        Log.i("LOG", "openListActivity()");
        Intent intent = new Intent(this,ListActivity.class);
        startActivity(intent);
        Bungee.spin(this);
    }

    // ---------------------------------------------------------------------------------------------
    // Check if realm is empty in case of no network connection
    // ---------------------------------------------------------------------------------------------
    public boolean checkRealmStatus(){
        Log.i("LOG", "checkRealmStatus()");
        if (!realm.isEmpty()) {
            openListActivity();
            Toast.makeText(MainActivity.this, "No connection, showing previous data!", Toast.LENGTH_LONG).show();
            return  true;
        } else {
            Toast.makeText(MainActivity.this, "No connection and no data!", Toast.LENGTH_LONG).show();
            // empty list anyway just to show something
            openListActivity();
            return  false;
        }
    }

    // ---------------------------------------------------------------------------------------------
    //  Check network connection
    // ---------------------------------------------------------------------------------------------
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.i("Log","is Online");
            return true;
        } else {
            Log.i("Log","is not Online");
            return false;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // In case of back in activities stack, call the get articles in onResume (reload)
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Log","onResume() -- MainActivity");

        YoYo.with(Techniques.Flash).duration(3000).repeat(20).playOn(loading);

        if (isOnline()) {
            getArticlesFromUrl();
        } else {
            checkRealmStatus();
        }
    }

    // ---------------------------------------------------------------------------------------------
    //  App closed
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("L", "onDestroy() -- MainActivity");
        realm.close();
    }
}
