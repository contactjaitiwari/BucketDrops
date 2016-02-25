package slidenerd.jait.bucketdrops.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.RealmResults;
import slidenerd.jait.bucketdrops.R;
import slidenerd.jait.bucketdrops.beans.Drop;

/**
 * Created by Jai on 2/23/2016.
 */
public class AdapterDrops extends RecyclerView.Adapter<AdapterDrops.DropHolder> {
    private static final String TAG = "JT";
    private LayoutInflater mInflater;
    private RealmResults<Drop> mResults;

    public AdapterDrops(Context context, RealmResults<Drop> results) {
        mInflater = LayoutInflater.from(context);
        update(results);
    }

    @Override
    public DropHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_drop, parent, false);
        DropHolder holder = new DropHolder(view);
        Log.i(TAG, "onCreateViewHolder: ");
        return holder;
    }

    @Override
    public void onBindViewHolder(DropHolder holder, int position) {
        Drop drop = mResults.get(position);
        holder.mTextWhat.setText(drop.getWhat());
        Log.i(TAG, "onBindViewHolder: " + position);
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void update(RealmResults<Drop> results) {
        mResults = results;
        notifyDataSetChanged();
    }

    public static class DropHolder extends RecyclerView.ViewHolder {
        TextView mTextWhat;

        public DropHolder(View itemView) {
            super(itemView);
            mTextWhat = (TextView) itemView.findViewById(R.id.tv_what);
        }
    }
}
