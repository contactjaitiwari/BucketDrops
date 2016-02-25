package slidenerd.jait.bucketdrops.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import slidenerd.jait.bucketdrops.extras.Util;

/**
 * Created by Jai on 2/25/2016.
 */
public class BucketRecyclerView extends RecyclerView {
    private List<View> mNonEmptyViews = Collections.emptyList();
    private List<View> mEmptyViews = Collections.emptyList();
    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            toggleView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleView();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            toggleView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleView();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleView();
        }
    };

    public BucketRecyclerView(Context context) {
        super(context);
    }

    public BucketRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void toggleView() {
        if (getAdapter() != null && !mEmptyViews.isEmpty() && !mNonEmptyViews.isEmpty()) {
            if (getAdapter().getItemCount() == 0) {
                Util.showViews(mEmptyViews);
                setVisibility(View.GONE);
                Util.hideViews(mNonEmptyViews);
            } else {
                Util.showViews(mNonEmptyViews);
                setVisibility(View.VISIBLE);
                Util.hideViews(mEmptyViews);
            }
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();
    }

    public void hideIfEmpty(View... mviews) {
        mNonEmptyViews = Arrays.asList(mviews);
    }

    public void showIfEmpty(View... mEmtyViews) {
        mEmptyViews = Arrays.asList(mEmtyViews);
    }
}
