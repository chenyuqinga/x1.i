package com.fotile.x1i.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.fotile.message.bean.NotificationDb;
import com.fotile.message.bean.TimeBean;
import com.fotile.message.util.MessageDataBaseUtil;
import com.fotile.x1i.R;
import com.fotile.x1i.util.DateUtil;
import com.fotile.x1i.viewholder.NotificationListViewHolder;

import java.util.List;

/**
 * 文件名称：NotificationListViewAdapter
 * 创建时间：2018/11/10 12:20
 * 文件作者：chenyqi
 * 功能描述：
 */

public class NotificationListViewAdapter extends BaseAdapter {

    private Context context;
    private List<NotificationDb> notificationDbList;
    private List<String> list;
    private DataChangeIdListener dataChangeIdListener;

    public NotificationListViewAdapter(Context context, List<NotificationDb> notificationDbList,DataChangeIdListener dataChangeIdListener) {
        this.context = context;
        this.notificationDbList = notificationDbList;
        this.list = MessageDataBaseUtil.getInstance().getDateAllNotification();
        this.dataChangeIdListener = dataChangeIdListener;
    }

    @Override
    public int getCount() {
        return notificationDbList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationDbList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationListViewHolder holder;

        int size = notificationDbList.size();
        final int curPosition = size - position - 1;
        TimeBean tb = DateUtil.getTimeBeanFormatFromDetail(notificationDbList.get(curPosition).getDate() + " " +
                notificationDbList.get(curPosition).getTime());
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notification_info, parent, false);
            holder = new NotificationListViewHolder();
            holder.tvTimeSort = (TextView) convertView.findViewById(R.id.tv_time_sort);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time_notification);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tv_content_notification);
            holder.tvDelete = (ImageView) convertView.findViewById(R.id.img_item_delete);
            convertView.setTag(holder);
        } else {
            holder = (NotificationListViewHolder) convertView.getTag();
        }
        final NotificationDb notificationDb = notificationDbList.get(curPosition);

        String time = DateUtil.thanTen(tb.getHour()) + ":" + DateUtil.thanTen(tb.getMinute());
        String content = notificationDb.getContent();
        holder.tvTimeSort.setText(DateUtil.showDate(context, tb));
        holder.tvTime.setText(time);
        holder.tvContent.setText(content);
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageDataBaseUtil.getInstance().deleteIdNotification(notificationDb);
                notificationDbList = MessageDataBaseUtil.getInstance().queryAllNotification();
                notifyDataSetChanged();
                dataChangeIdListener.onNotificationChangId();
                list = MessageDataBaseUtil.getInstance().getDateAllNotification();

            }
        });

        return convertView;
    }
    public void setNotificationDbList(List<NotificationDb> notificationDbList) {
        this.notificationDbList = notificationDbList;
        list = MessageDataBaseUtil.getInstance().getDateAllNotification();
        this.notifyDataSetChanged();
    }
    public interface DataChangeIdListener {
        void onNotificationChangId();

    }

}
