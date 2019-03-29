package com.anima.paginglistview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anima.paginglistview.viewholder.NetworkStateItemViewHolder;
import com.anima.paginglistview.viewholder.PagingRecycleItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianjianhong on 18-12-25
 */
public class PagingRecycleAdapter<T> extends RecyclerView.Adapter {

    private static final int TYPE_ITEM   = 0;
    private static final int TYPE_FOOTER = 1;

    private Context context;
    private OnItemViewHolderCreator itemViewHolderCreator;
    private int layoutId;
    private List<T> list = new ArrayList();
    public PagingStatus pagingStatus;
    public View.OnClickListener retryCallback;

    protected OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Object item);
    }

    public interface OnItemViewHolderCreator {

        PagingRecycleItemViewHolder create(View itemView);

    }

    public PagingRecycleAdapter(Context context, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("onCreateViewHolder", "viewType:"+viewType);
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            return itemViewHolderCreator.create(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.network_state_item, parent, false);
            return new NetworkStateItemViewHolder(itemView);
        }else {
            throw new IllegalArgumentException("unknown view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof PagingRecycleItemViewHolder) {
            ((PagingRecycleItemViewHolder) holder).bindto(list.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (mOnItemClickListener!=null) {
                    mOnItemClickListener.onItemClick(v, position, list.get(position));
                }
                }
            });

        } else if (holder instanceof NetworkStateItemViewHolder) {
            ((NetworkStateItemViewHolder) holder).bindTo(pagingStatus, getItemCount() > 1, retryCallback);
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + (hasExtraRow() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {

        if (hasExtraRow() && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean hasExtraRow() {
        return  pagingStatus != null && pagingStatus != pagingStatus.LOADED;
    }


    public void setNetworkState(PagingStatus state){

        PagingStatus previousState = this.pagingStatus;
        this.pagingStatus = state;
        boolean hasExtraRow = hasExtraRow();
        if (hasExtraRow && previousState != state) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void addDatas(List<T> data) {
        list.addAll(data);
        this.notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        this.list.clear();
        this.list.addAll(data);
        this.notifyDataSetChanged();
    }

    public List<T> getData() {
        return list;
    }

    public void removeItem(int position) {
        list.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(T item, int position) {
        list.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public void setItemViewHolderCreator(OnItemViewHolderCreator itemViewHolderCreator) {
        this.itemViewHolderCreator = itemViewHolderCreator;
    }
}
