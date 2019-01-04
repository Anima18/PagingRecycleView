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

    public ItemViewHolder(View itemView) {
        super(itemView);
        codeTv = itemView.findViewById(R.id.list_code_tv);
        nameTv = itemView.findViewById(R.id.list_name_tv);
        imageView = itemView.findViewById(R.id.list_avatar_iv);
    }

    @Override
    public void bindto(Item data) {
        codeTv.setText(data.getLogin());
        nameTv.setText(data.getId()+"");
        Picasso.get().load(data.getAvatar_url()).into(imageView);

    }
}
