# PagingRecycleView
Android的分页列表，主要有以下特性：
1. 基于RecycleView
2. 支持上拉更新，下拉加载更多,支持预加载数据
3. 支持网络加载状态显示
4. 加载失败后，支持重新加载

## 使用


```
compile 'com.anima:PagingRecycleView:1.0.1'
```

## 效果
![image](https://raw.githubusercontent.com/Anima18/PagingRecycleView/master/images/image1.gif)

## 示例
##### 1. 定义PagingRecycleView  
- pagesize 一页的条数
- layoutid 列表项的布局文件

```
<com.anima.paginglistview.PagingRecycleView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/pagingListView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:pagesize="30"
    app:lagoutid="@layout/listview_two_line_with_avatar"/>
```
##### 2. 创建列表项ViewHolder  
ViewHolder要继承*PagingRecycleItemViewHolder*, Item是数据对象类型.  
在ViewHolder构造方法,初始化列表项控件;在bindTo方法设置控件值.

```
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
``` 

##### 3. 分页列表设置ViewHolder  

```
PagingRecycleView pagingListView = findViewById(R.id.pagingListView);
pagingListView.setItemViewHolderCreator(new PagingRecycleAdapter.OnItemViewHolderCreator() {
    @Override
    public PagingRecycleItemViewHolder create(View itemView) {
        return new ItemViewHolder(itemView);
    }
});
```

##### 4. 分页列表绑定数据源  
*loadData(int beginNo, int endNo, int page, final PagingRecycleView.LoadCallback<Item> callback)*

- beginNo 加载数据的开始条数
- endNo 加载数据的结束条数
- page 加载数据的分页数
- callback 加载结束的回调函数

```
pagingListView.bindDataSource(new PagingRecycleView.OnDataSource<Item>() {
    @Override
    public void loadData(int beginNo, int endNo, int page, final PagingRecycleView.LoadCallback<Item> callback) {
       String url = "https://api.github.com/users/yeasy/followers?page="+page;

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
```

