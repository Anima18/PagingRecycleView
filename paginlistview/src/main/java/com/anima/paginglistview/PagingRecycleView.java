package com.anima.paginglistview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.anima.paginglistview.viewholder.PagingRecycleItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianjianhong on 18-12-25
 */
public class PagingRecycleView<T> extends FrameLayout implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private Context context;
    private RecyclerView recycleView;
    private SwipeRefreshLayout refreshLayout;
    private PagingRecycleAdapter<T> mRefreshAdapter;

    /**
     * 可分页状态,当没有数据或者发生错误,分页状态禁止
     */
    private boolean pageable = true;

    /**
     * 分页请求数据下标,默认1
     */
    private int beginNo = 1;

    private int page = 1;
    /**
     * 每页数目,默认30
     */
    private int pageSize = 30;

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener;

    private OnDataSource<T> dataSource;

    private OnDataDelete<T> dataDelete;

    private LoadCallback<T> callback = new LoadCallback<T>() {
        @Override
        public void onResult(@NonNull List<T> data) {
            refreshLayout.setRefreshing(false);
            setPageable(data);
            if(data.size() > 0) {
                mRefreshAdapter.addDatas(data);
                mRefreshAdapter.setNetworkState(PagingStatus.LOADED);
                beginNo += data.size();
                page++;
            }else {
                mRefreshAdapter.setNetworkState(PagingStatus.EMPTY);
            }
        }

        @Override
        public void onError(@NonNull String message) {
            refreshLayout.setRefreshing(false);
            mRefreshAdapter.retryCallback = new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    requestData();
                }
            };
            mRefreshAdapter.setNetworkState(PagingStatus.error(message));
            setPageable(null);
        }
    };

    public PagingRecycleView(@NonNull Context context) {
        this(context,null);
    }

    public PagingRecycleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagingRecycleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PagingRecycleView);
        int layoutId = array.getResourceId(R.styleable.PagingRecycleView_lagoutid, 0);
        pageSize = array.getInt(R.styleable.PagingRecycleView_pagesize, 30);
        array.recycle();

        if(layoutId != 0) {
            initView();
            mRefreshAdapter = new PagingRecycleAdapter<T>(context, layoutId);
            recycleView.setAdapter(mRefreshAdapter);
            initEvent();
        }
    }

    private void initView() {
        View root = View.inflate(context, R.layout.view_swipe_refresh_recyclerview, this);
        refreshLayout = root.findViewById(R.id.view_swipe_refresh_layout);
        recycleView = root.findViewById(R.id.view_swipe_refresh_recycleview);
        recycleView.setLayoutManager(new LinearLayoutManager(context));

    }

    private void initEvent() {
        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                beginNo = 1;
                page = 1;
                pageable = true;
                mRefreshAdapter.setData(new ArrayList<T>());
                if(dataSource != null) {
                    requestData();
                }
            }
        };
        refreshLayout.setOnRefreshListener(onRefreshListener);

        recycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastVisibleItem ;
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /**
                 * 1. 列表向下滚动
                 * 2. 可以滑动
                 * 3. 上页数据加载成功
                 * 4. 预加载一页数据
                 */
                if(dy > 0 && pageable && mRefreshAdapter.pagingStatus == PagingStatus.LOADED && lastVisibleItem + pageSize >= mRefreshAdapter.getItemCount()){
                    Log.i("onScrollStateChanged", "PagingStatus.LOGING");
                    requestData();
                }

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });
    }

    private void requestData() {
        mRefreshAdapter.setNetworkState(PagingStatus.LOGING);
        dataSource.loadData(beginNo, beginNo + pageSize, page, callback);
    }

    private void setPageable(List<T> data) {
        if(data != null && data.size() > 0) {
            pageable = true;
        }else {
            pageable = false;
        }
    }

    public void setItemViewHolderCreator(PagingRecycleAdapter.OnItemViewHolderCreator itemViewHolderCreator) {
        this.mRefreshAdapter.setItemViewHolderCreator(itemViewHolderCreator);
    }

    public void bindDataSource(OnDataSource dataSource) {
        this.dataSource = dataSource;
        refreshLayout.setRefreshing(true);
        onRefreshListener.onRefresh();
    }

    public void setDataDelete(OnDataDelete dataDelete) {
        this.dataDelete = dataDelete;
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycleView);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof PagingRecycleItemViewHolder) {
            // backup of removed item for undo purpose

            final T deletedItem = mRefreshAdapter.getData().get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mRefreshAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            final boolean[] isDelete = {true};
            View rootView = ((Activity)context).getWindow().getDecorView().getRootView();
            Snackbar snackbar = Snackbar.make(rootView, "删除这条记录", Snackbar.LENGTH_LONG);
            snackbar.setAction("撤销", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    isDelete[0] = false;
                    insertData(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(context.getResources().getColor(R.color.message_default_action));
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    super.onDismissed(snackbar, event);
                    if(isDelete[0]) {
                      dataDelete.deleteData(deletedItem, deletedIndex);
                    }
                }
                @Override
                public void onShown(Snackbar snackbar) {
                    super.onShown(snackbar);
                }
            });
            snackbar.show();
        }
    }

    public void insertData(T t, int position) {
        mRefreshAdapter.restoreItem(t, position);
    }

    public interface OnDataSource<T> {
        void loadData(int beginNo, int endNo, int page, LoadCallback<T> callback);
    }

    public interface OnDataDelete<T> {
        void deleteData(T t, int position);
    }


    public abstract static class LoadCallback<T> {
        public abstract void onResult(@NonNull List<T> data);

        public abstract void onError(@NonNull String message);
    }
}
