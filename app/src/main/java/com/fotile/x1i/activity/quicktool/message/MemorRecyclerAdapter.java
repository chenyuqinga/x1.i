package com.fotile.x1i.activity.quicktool.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.message.bean.MemorandumDb;
import com.fotile.message.bean.NotificationDb;
import com.fotile.message.bean.TimeBean;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.adapter.common.CommonRecyclerAdapter;
import com.fotile.x1i.util.DateUtil;

import java.util.List;

/**
 * 文件名称：NotificationRecyclerAdapter
 * 创建时间：2019/6/25 13:53
 * 文件作者：yaohx
 * 功能描述：备忘录列表adapter
 */
public class MemorRecyclerAdapter extends CommonRecyclerAdapter<MemorandumDb> {

    public MemorRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_notification, parent, false);
        MemorandumDbViewHolder viewHolder = new MemorandumDbViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        int size = items.size();
        //list中日期排序为倒叙，计算curPosition
        final int curPosition = size - position - 1;
        TimeBean tb = DateUtil.getTimeBeanFormatFromDetailMemor(items.get(curPosition));
        String data = DateUtil.showDate(context, tb);

        MemorandumDb memorandumDb = items.get(curPosition);
        MemorandumDbViewHolder viewHolder = (MemorandumDbViewHolder) holder;
        viewHolder.txtContext.setText(data + " " + memorandumDb.getTime() + "  " + memorandumDb.getContent());
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearItem(curPosition);
            }
        });

    }

    private void clearItem(int curPosition) {
        MessageDataBaseUtil.getInstance().deleteIdMemorandum(items.get(curPosition));
        items = MessageDataBaseUtil.getInstance().queryAllMemorandum();

        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<MemorandumDb> list) {
        super.addItems(list);
    }

    class MemorandumDbViewHolder extends RecyclerView.ViewHolder {
        TextView txtContext;
        ImageView imgDelete;

        public MemorandumDbViewHolder(View itemView) {
            super(itemView);
            txtContext = (TextView) itemView.findViewById(R.id.txt_content);
            imgDelete = (ImageView) itemView.findViewById(R.id.img_delete);
        }
    }

}
