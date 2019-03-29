package com.anima.paginglistviewtest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.anima.paginglistview.viewholder.PagingRecycleItemViewHolder;
import com.squareup.picasso.Picasso;

/**
 * Created by jianjianhong on 19-1-3
 */
public class ItemViewHolder extends PagingRecycleItemViewHolder<Item> {

    TextView codeTv;
    TextView nameTv;
    ImageView imageView;
    View foregroundView;
    View backgroundView;

    public ItemViewHolder(View itemView) {
        super(itemView);
        codeTv = itemView.findViewById(R.id.list_code_tv);
        nameTv = itemView.findViewById(R.id.list_name_tv);
        imageView = itemView.findViewById(R.id.list_avatar_iv);
        foregroundView = itemView.findViewById(R.id.view_foreground);
        backgroundView = itemView.findViewById(R.id.view_background);
    }

    @Override
    public void bindto(Item data) {
        codeTv.setText(data.getLogin());
        nameTv.setText(data.getId()+"");
        Picasso.get().load(data.getAvatar_url()).into(imageView);

    }

    @Override
    public View itemView() {
        return foregroundView;
    }

    @Override
    public View backgroundView() {
        return backgroundView;
    }
}
