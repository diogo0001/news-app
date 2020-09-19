package domain.ckl_1_android_diogo_tavares;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Article extends RealmObject {

    @PrimaryKey

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("authors")
    @Expose
    private String authors;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("content")
    @Expose
    private String content;

    @SerializedName("tags")
    @Expose
    private RealmList<Tags> tags;

    @SerializedName("image_url")
    @Expose
    private String image_url;

    private boolean isChecked;

    public Article(){
        this.isChecked = false;
    }

    //----------------------------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------------------------
    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getWebSite() {return website;}
    public void setWebSite(String webSite) {this.website = webSite;}

    public String getAuthor() {return authors;}
    public void setAuthor(String author) {this.authors = author;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}

    public String getImageURL() {return image_url;}
    public void setImageURL(String imageURL) {this.image_url = imageURL;}

    public boolean isChecked() {return isChecked;}
    public void setCheck(boolean check) {this.isChecked = check;}

    public RealmList<Tags> getTags() { return tags; }
    public void setTags(RealmList<Tags> tags) { this.tags = tags; }
}
