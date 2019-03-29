package com.anima.paginglistview.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jianjianhong on 18-12-25
 */
abstract public class PagingRecycleItemViewHolder<T> extends RecyclerView.ViewHolder {
    public PagingRecycleItemViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindto(@NonNull T data);

    public abstract View itemView();

    public abstract View backgroundView();
}
