package com.fotile.x1i.activity.quicktool.message;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
 * 功能描述：消息提醒列表adapter
 */
public class NotificationRecyclerAdapter extends CommonRecyclerAdapter<NotificationDb> {

    public NotificationRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_notification, parent, false);
        NotificationViewHolder viewHolder = new NotificationViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int size = items.size();
        //list中日期排序为倒叙，计算curPosition
        final int curPosition = size - position - 1;
        TimeBean tb = DateUtil.getTimeBeanFormatFromDetail(items.get(curPosition) + " " + "00:00:00");
        String data = DateUtil.showDate(context, tb);

        NotificationDb notificationDb = items.get(curPosition);
        NotificationViewHolder viewHolder = (NotificationViewHolder) holder;
        viewHolder.txtContext.setText(data + " " + notificationDb.getTime() + "  " + notificationDb.getContent());
        viewHolder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearItem(curPosition);
            }
        });
    }

    private void clearItem(int curPosition) {
        MessageDataBaseUtil.getInstance().deleteIdNotification(items.get(curPosition));
        items = MessageDataBaseUtil.getInstance().queryAllNotification();

        notifyDataSetChanged();
    }

    @Override
    public void addItems(List<NotificationDb> list) {
        super.addItems(list);
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txtContext;
        ImageView imgDelete;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            txtContext = (TextView) itemView.findViewById(R.id.txt_content);
            imgDelete = (ImageView) itemView.findViewById(R.id.img_delete);
        }
    }

}
