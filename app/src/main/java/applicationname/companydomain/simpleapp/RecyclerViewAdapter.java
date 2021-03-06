package applicationname.companydomain.simpleapp;

/*
    RecyclerView with multiple ViewHolders:
            http://www.digitstory.com/recyclerview-multiple-viewholders/

    Spotify Android SDK:
            https://developer.spotify.com/documentation/android-sdk/

    spotify-web-api-android:
            https://github.com/kaaes/spotify-web-api-android

    Spotify Web API:
            https://developer.spotify.com/documentation/web-api/
 */

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import android.graphics.Color;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    private final static int TYPE_CATEGORY = 0;
    private final static int TYPE_ARTIST = 1;
    private final static int TYPE_TRACK = 2;
    private final static int TYPE_NO_RESULTS = 3;

    private List<Object> topFeed = new ArrayList();
    private Context context;
    private RecyclerView mRecyclerView;

    // Constructor
    public RecyclerViewAdapter(Context context, RecyclerView mRecyclerView){
        this.context = context;
        this.mRecyclerView = mRecyclerView;
    }

    public void setTopFeed(List<Object> topFeed){
        this.topFeed = topFeed;
    }

    @Override
    public int getItemViewType(int pos) {
        if (topFeed.get(pos) instanceof ArtistItem) {
            return TYPE_ARTIST;
        } else if (topFeed.get(pos) instanceof TrackItem) {
            return TYPE_TRACK;
        } else if (topFeed.get(pos) instanceof CategoryItem) {
            return TYPE_CATEGORY;
        }
        return TYPE_NO_RESULTS;
    }

    // Invoked by layout manager to replace the contents of the views
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        int viewType=holder.getItemViewType();
        switch (viewType){
            case TYPE_CATEGORY:
                CategoryItem categoryItem = (CategoryItem) topFeed.get(position);
                CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
                categoryViewHolder.setDetails(categoryItem);
                break;
            case TYPE_ARTIST:
                ArtistItem artistItem = (ArtistItem) topFeed.get(position);
                ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
                artistViewHolder.setDetails(artistItem);
                break;
            case TYPE_TRACK:
                TrackItem trackItem = (TrackItem) topFeed.get(position);
                TrackViewHolder trackViewHolder = (TrackViewHolder) holder;
                trackViewHolder.setDetails(trackItem);
                break;
            case TYPE_NO_RESULTS:
                NoResultsItem noResultsItem = (NoResultsItem) topFeed.get(position);
                NoResultsViewHolder noResultsViewHolder = (NoResultsViewHolder) holder;
                noResultsViewHolder.setDetails(noResultsItem);
                break;
        }
    }

    @Override
    public int getItemCount(){
        return topFeed.size();
    }

    // Invoked by layout manager to create new views
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layout = 0;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType){
            case TYPE_CATEGORY:
                layout = R.layout.layout_category;
                View categoryView = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new CategoryViewHolder(categoryView);
                break;
            case TYPE_ARTIST:
                layout = R.layout.layout_artist;
                View artistView = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new ArtistViewHolder(artistView, mListener);
                break;
            case TYPE_TRACK:
                viewHolder = null;
                layout = R.layout.layout_track;
                View trackView = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new TrackViewHolder(trackView);
                break;
            case TYPE_NO_RESULTS:
                layout = R.layout.layout_noresults;
                View noResultsView = LayoutInflater.from(parent.getContext())
                        .inflate(layout, parent, false);
                viewHolder = new NoResultsViewHolder(noResultsView);
                break;
            default:
                viewHolder = null;
        }

        return viewHolder;
    }

    // Super simple.
    public class NoResultsViewHolder extends RecyclerView.ViewHolder {
        public NoResultsViewHolder(View itemView) {
            super(itemView);
        }
        public void setDetails(NoResultsItem noResultsItem) {
        }
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private TextView itemTime;
        private TextView itemUpdateTime;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView)itemView.findViewById(R.id.itemName);
            itemTime = (TextView)itemView.findViewById(R.id.itemTime);
            itemUpdateTime = (TextView)itemView.findViewById(R.id.itemUpdateTime);
        }

        public void setDetails(CategoryItem cat) {

            DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String date = df.format(Calendar.getInstance().getTime());

            itemName.setText(cat.getTitle());
            itemTime.setText(MainActivity.TIME_LABELS.get(cat.getTimeRange()));
            itemUpdateTime.setText(date);
        }
    }

    public class ArtistViewHolder extends RecyclerView.ViewHolder {

        private TextView itemName;
        private ImageView artistImage;
        private ConstraintLayout parentLayout;

        public ArtistViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemName = (TextView)itemView.findViewById(R.id.itemName);
            artistImage = (ImageView)itemView.findViewById(R.id.trackImage);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, ArtistDetailsActivity.class);

                        ArtistItem artistItem = (ArtistItem) topFeed.get(position);
                        intent.putExtra("artist_id", artistItem.getID());
                        intent.putExtra("artist_name", artistItem.getName());
                        intent.putExtra("artist_url", artistItem.getHdURL());
                        intent.putExtra("top_artist", artistItem.getRank());
                        intent.putExtra("popularity", artistItem.getPopularity());

                        context.startActivity(intent);
                    }
                }
            });
        }

        public void setDetails(ArtistItem artistItem) {
            if (artistItem.getTheColor()) {
                parentLayout.setBackgroundColor(Color.parseColor("#485771"));
            } else {
                parentLayout.setBackgroundColor(Color.parseColor("#3F495B"));
            }

            itemName.setText(artistItem.getName());


            // Get the URL
            String url = artistItem.getURL();

            if (!(url.equals(""))) {
                Glide.with(context)
                        .load(artistItem.getURL())
                        .apply(RequestOptions.circleCropTransform())
                        .into(artistImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.unknown)
                        .apply(RequestOptions.circleCropTransform())
                        .into(artistImage);
            }
        }
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView artistName;
        private ImageView trackImage;
        private ConstraintLayout parentLayout;

        public TrackViewHolder(View itemView) {
            super(itemView);

            itemName = (TextView)itemView.findViewById(R.id.itemName);
            artistName = (TextView)itemView.findViewById(R.id.artistName);
            trackImage = (ImageView) itemView.findViewById(R.id.trackImage);
            parentLayout = (ConstraintLayout) itemView.findViewById(R.id.parent_layout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, TrackDetailsActivity.class);

                        TrackItem trackItem = (TrackItem) topFeed.get(position);
                        intent.putExtra("track_id", trackItem.getID());
                        intent.putExtra("track_title", trackItem.getTitle());
                        intent.putExtra("track_artist", trackItem.getArtist());
                        intent.putExtra("top_track", trackItem.getRank());

                        intent.putExtra("popularity", trackItem.getPopularity());
                        intent.putExtra("dance", trackItem.getDanceability());
                        intent.putExtra("energy", trackItem.getEnergy());
                        intent.putExtra("happiness", trackItem.getHappiness());

                        context.startActivity(intent);
                    }
                }
            });
        }

        public void setDetails(TrackItem trackItem) {
            if (trackItem.getTheColor()) {
                parentLayout.setBackgroundColor(Color.parseColor("#485771"));
            } else {
                parentLayout.setBackgroundColor(Color.parseColor("#3F495B"));
            }

            itemName.setText(trackItem.getTitle());
            artistName.setText(trackItem.getArtist());

            // Get the URL
            String url = trackItem.getURL();

            if (!(url.equals(""))) {
                Glide.with(context)
                        .load(trackItem.getURL())
                        .into(trackImage);
            } else {
                Glide.with(context)
                        .load(R.drawable.unknown)
                        .into(trackImage);
            }
        }
    }
}