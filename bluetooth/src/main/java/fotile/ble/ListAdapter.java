package fotile.ble;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import fotile.ble.bean.BleDevice;
import fotile.ble.observer.link.ILinkObserverable;

/**
 * Created by yaohx on 2018/9/3.
 */
@Deprecated
public class ListAdapter extends BaseAdapter {

    private Context context;
    LayoutInflater inflater;
    List<BleDevice> list;

    public ListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<BleDevice> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return null == list ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(R.layout.item, null);
        TextView txtName = (TextView) view.findViewById(R.id.txt);
        TextView txtState = (TextView) view.findViewById(R.id.state);
        txtName.setText(list.get(position).getName());

        int state = list.get(position).linkStatus;
        String str = "";
        if (state == ILinkObserverable.STATE_CONNECTED) {
            str = "已连接";
        } else {
            str = "未连接";
        }
        txtState.setText(str);
        return view;
    }
}
