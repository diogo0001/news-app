package domain.ckl_1_android_diogo_tavares;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class Tags extends RealmObject{

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("label")
    @Expose
    private String label;

    public Tags(){}

    public Tags(int id, String label) {
        this.id = id;
        this.label = label;
    }

    //----------------------------------------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------------------------------------
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
