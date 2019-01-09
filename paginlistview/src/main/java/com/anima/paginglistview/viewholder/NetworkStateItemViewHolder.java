package com.anima.paginglistview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anima.paginglistview.R;
import com.anima.paginglistview.PagingStatus;

/**
 * Created by jianjianhong on 18-12-26
 */
public class NetworkStateItemViewHolder extends  RecyclerView.ViewHolder {
    private LinearLayout layout;
    private ProgressBar progressBar;
    private Button retryButton;
    private TextView errorMessage;

    public NetworkStateItemViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.networkState_layout);
        progressBar = itemView.findViewById(R.id.networkState_progress_bar);
        retryButton = itemView.findViewById(R.id.networkState_retry_button);
        errorMessage = itemView.findViewById(R.id.networkState_error_msg);

    }

    public void bindTo(PagingStatus pagingStatus, Boolean isLoadMore, View.OnClickListener retryCallback) {
        if(isLoadMore) {
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
            layout.setLayoutParams(linearParams);
        }else {
            LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(linearParams);
        }
        progressBar.setVisibility(toVisibility(pagingStatus.status == PagingStatus.Status.RUNNING));
        retryButton.setVisibility(toVisibility(pagingStatus.status == PagingStatus.Status.FAILED));
        pagingStatus.msg = pagingStatus.status == PagingStatus.Status.EMPTY ? "没有数据" : pagingStatus.msg;
        errorMessage.setVisibility(toVisibility(pagingStatus.msg != null));
        errorMessage.setText(pagingStatus.msg);

        if(retryCallback != null) {
            retryButton.setOnClickListener(retryCallback);
        }
    }

    private static int toVisibility(Boolean constraint) {
        return constraint ? View.VISIBLE : View.GONE;
    }
}
