package com.anima.paginglistviewtest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.anima.paginglistview.PagingRecycleAdapter;
import com.anima.paginglistview.PagingRecycleView;
import com.anima.paginglistview.viewholder.PagingRecycleItemViewHolder;
import com.google.gson.reflect.TypeToken;
import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.entity.ResponseStatus;
import com.ut.requsetmanager.request.NetworkRequestImpl;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagingRecycleView pagingListView = findViewById(R.id.pagingListView);
        pagingListView.setItemViewHolderCreator(new PagingRecycleAdapter.OnItemViewHolderCreator() {
            @Override
            public PagingRecycleItemViewHolder create(View itemView) {
                return new ItemViewHolder(itemView);
            }
        });

        pagingListView.bindDataSource(new PagingRecycleView.OnDataSource<Item>() {
            @Override
            public void loadData(int beginNo, int endNo, int page, final PagingRecycleView.LoadCallback<Item> callback) {
               String url = "https://api.github.com/users/anima18/followers?page="+page;

                NetworkRequestImpl.create(MainActivity.this)
                        .setUrl(url)
                        .setMethod(NetworkRequestImpl.GET)
                        .setDataType(new TypeToken<List<Item>>() { }.getType())
                        .send(new DataRequestCallback<List<Item>>() {
                            @Override
                            public void onResult(List<Item> data, ResponseStatus status) {
                                if(200 == status.getCode()) {
                                    callback.onResult(data);
                                }else {
                                    callback.onError(status.message);
                                }
                            }
                        });
            }
        });

    }
}
