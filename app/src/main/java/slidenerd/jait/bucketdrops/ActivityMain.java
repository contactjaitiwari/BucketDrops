package slidenerd.jait.bucketdrops;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import slidenerd.jait.bucketdrops.adapters.AdapterDrops;
import slidenerd.jait.bucketdrops.adapters.AddListener;
import slidenerd.jait.bucketdrops.adapters.CompleteListener;
import slidenerd.jait.bucketdrops.adapters.Divider;
import slidenerd.jait.bucketdrops.adapters.Filter;
import slidenerd.jait.bucketdrops.adapters.MarkListener;
import slidenerd.jait.bucketdrops.adapters.ResetListener;
import slidenerd.jait.bucketdrops.adapters.SimpleTouchCallback;
import slidenerd.jait.bucketdrops.beans.Drop;
import slidenerd.jait.bucketdrops.widgets.BucketRecyclerView;

public class ActivityMain extends AppCompatActivity {

    private static final String TAG = "JT";
    Toolbar mToolbar;
    Button mBtnAdd;
    BucketRecyclerView mRecycler;
    Realm mRealm;
    RealmResults<Drop> mResults;
    AdapterDrops mAdapter;
    View mEmptyView;
    private View.OnClickListener mBtnAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showDialogAdd();
        }
    };
    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };

    private RealmChangeListener mChangeListener = new RealmChangeListener() {
        @Override
        public void onChange() {
            Log.d(TAG, "onChange: was called");
            mAdapter.update(mResults);
        }
    };
    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            mAdapter.markComplete(position);
        }
    };
    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };
    private ResetListener mResetListener = new ResetListener() {
        @Override
        public void onReset() {
            AppBucketDrops.save(ActivityMain.this, Filter.NONE);
            loadResults(Filter.NONE);
        }
    };

    private void showDialogAdd() {
        DialogAdd dialog = new DialogAdd();
        dialog.show(getSupportFragmentManager(), "Add");
    }

    private void showDialogMark(int position) {
        DialogMark dialog = new DialogMark();
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialog.setArguments(bundle);
        dialog.setCompleteListener(mCompleteListener);
        dialog.show(getSupportFragmentManager(), "Mark");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean handled = true;
        int filterOption = Filter.NONE;
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;
            case R.id.action_sort_ascending_date:
                filterOption = Filter.LEAST_TIME_LEFT;
                AppBucketDrops.save(this, Filter.LEAST_TIME_LEFT);
                break;
            case R.id.action_sort_descending_date:
                filterOption = Filter.MOST_TIME_LEFT;
                AppBucketDrops.save(this, Filter.MOST_TIME_LEFT);
                break;
            case R.id.action_complete:
                filterOption = Filter.COMPLETE;
                AppBucketDrops.save(this, Filter.COMPLETE);
                break;
            case R.id.action_incomplete:
                filterOption = Filter.INCOMPLETE;
                AppBucketDrops.save(this, Filter.INCOMPLETE);
                break;
            default:
                handled = false;
        }
        loadResults(filterOption);
        return handled;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getDefaultInstance();
        int filterOption = AppBucketDrops.load(this);
        loadResults(filterOption);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mEmptyView = findViewById(R.id.empty_drops);
        mBtnAdd = (Button) findViewById(R.id.btn_add);
        mRecycler = (BucketRecyclerView) findViewById(R.id.rv_drops);
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mRecycler.hideIfEmpty(mToolbar);
        mRecycler.showIfEmpty(mEmptyView);
        mAdapter = new AdapterDrops(this, mRealm, mResults, mMarkListener, mResetListener);
        mAdapter.setHasStableIds(true);
        mAdapter.setAddListener(mAddListener);
        mRecycler.setAdapter(mAdapter);
        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mBtnAdd.setOnClickListener(mBtnAddListener);
        setSupportActionBar(mToolbar);
        initBackgroundImage();
    }


    private void initBackgroundImage() {
        ImageView background = (ImageView) findViewById(R.id.iv_background);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(background);
    }

    private void loadResults(int filterOptions) {
        switch (filterOptions) {
            case Filter.NONE:
                mResults = mRealm.where(Drop.class).findAllAsync();
                Log.d(TAG, "loadResults: " + "Filter.NONE" + " " + filterOptions);
                break;
            case Filter.LEAST_TIME_LEFT:
                mResults = mRealm.where(Drop.class).findAllSortedAsync("when");
                Log.d(TAG, "loadResults: " + "Filter.LEAST_TIME_LEFT" + " " + filterOptions);
                break;
            case Filter.MOST_TIME_LEFT:
                mResults = mRealm.where(Drop.class).findAllSortedAsync("when", Sort.DESCENDING);
                Log.d(TAG, "loadResults: " + "Filter.MOST_TIME_LEFT" + " " + filterOptions);
                break;
            case Filter.COMPLETE:
                mResults = mRealm.where(Drop.class).equalTo("completed", true).findAllAsync();
                Log.d(TAG, "loadResults: " + "Filter.COMPLETE" + " " + filterOptions);
                break;
            case Filter.INCOMPLETE:
                mResults = mRealm.where(Drop.class).equalTo("completed", false).findAllAsync();
                Log.d(TAG, "loadResults: " + "Filter.INCOMPLETE" + " " + filterOptions);
                break;
        }
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResults.removeChangeListener(mChangeListener);
    }
}
