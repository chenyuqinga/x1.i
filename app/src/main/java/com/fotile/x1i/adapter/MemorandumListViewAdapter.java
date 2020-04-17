package com.fotile.x1i.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fotile.message.bean.MemorandumDb;
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

public class MemorandumListViewAdapter extends BaseAdapter {

    private Context context;
    private List<MemorandumDb> memorandumDbList;
    private DataChangeIdListener dataChangeIdListener;
    private List<String> list;

    public MemorandumListViewAdapter(Context context, List<MemorandumDb> memorandumDbList, DataChangeIdListener dataChangeIdListener) {
        this.context = context;
        this.memorandumDbList = memorandumDbList;
        this.dataChangeIdListener = dataChangeIdListener;
        this.list = MessageDataBaseUtil.getInstance().getDateAllMemorandum();
    }

    @Override
    public int getCount() {
        return memorandumDbList.size();
    }

    @Override
    public Object getItem(int position) {
        return memorandumDbList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationListViewHolder holder;

        int size = memorandumDbList.size();
        final int curPosition = size - position - 1;
        TimeBean tb = DateUtil.getTimeBeanFormatFromDetail(memorandumDbList.get(curPosition).getDate() + " " +
                memorandumDbList.get(curPosition).getTime());
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
        final MemorandumDb memorandumDb = memorandumDbList.get(curPosition);


        String time = DateUtil.thanTen(tb.getHour()) + ":" + DateUtil.thanTen(tb.getMinute());
        String content;
        if (!memorandumDb.getTitle().equals("")) {
            content = memorandumDb.getTitle() + ": " + memorandumDb.getContent();
        } else if (memorandumDb.getFrom_name() != null && !memorandumDb.getFrom_name().equals("")) {
            content = memorandumDb.getFrom_name() + ": " + memorandumDb.getContent();
        } else {
            content = context.getString(R.string.memorandum_form_name) + ": " + memorandumDb.getContent();
        }
        holder.tvTimeSort.setText(DateUtil.showDate(context, tb));
        holder.tvTime.setText(time);
        holder.tvContent.setText(content);
        holder.tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memorandumDbList.remove(curPosition);
                MemorandumListViewAdapter.this.notifyDataSetChanged();

                MessageDataBaseUtil.getInstance().deleteIdMemorandum(memorandumDb);
                memorandumDbList = MessageDataBaseUtil.getInstance().queryAllMemorandum();
                notifyDataSetChanged();

                list = MessageDataBaseUtil.getInstance().getDateAllMemorandum();

            }
        });

        return convertView;
    }
    public void setMemorandumDbList(List<MemorandumDb> memorandumDbList) {
        this.memorandumDbList = memorandumDbList;
        list = MessageDataBaseUtil.getInstance().getDateAllMemorandum();
        this.notifyDataSetChanged();
    }
    public interface DataChangeIdListener {
        void onNotificationChangId();

    }

}
