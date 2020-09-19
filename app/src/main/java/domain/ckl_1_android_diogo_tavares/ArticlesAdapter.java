package domain.ckl_1_android_diogo_tavares;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.CustomViewHolder> {

    private List<Article> articles;

    // ---------------------------------------------------------------------------------------------

    public ArticlesAdapter(List<Article> articles){
        this.articles = articles;
    }

    // ---------------------------------------------------------------------------------------------

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        public TextView title, author, date;
        public ImageView image, eyeImg;

        public CustomViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            date = (TextView) view.findViewById(R.id.date);
            image = (ImageView)view.findViewById(R.id.imageView);
            eyeImg = (ImageView)view.findViewById(R.id.eyeCheck);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_layout, parent, false);
        return new CustomViewHolder(itemView);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        Article article = articles.get(position);

        if(article.getImageURL()!=null && !article.getImageURL().isEmpty()) {
            Context context = holder.image.getContext();
            Picasso.with(context)
                    .load(article.getImageURL())
                    .fit()
                    .centerCrop()
                    .into(holder.image);
        }
        else{
            holder.image.setImageResource(R.drawable.image_off);
            holder.image.setAlpha(0.65f);
        }

        holder.title.setText(article.getTitle());
        holder.date.setText(article.getDate()+"  "+article.getAuthor());

        if(article.isChecked()==true){
            holder.eyeImg.setImageResource(R.drawable.eye_check);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public int getItemCount() {
        return articles.size();
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
