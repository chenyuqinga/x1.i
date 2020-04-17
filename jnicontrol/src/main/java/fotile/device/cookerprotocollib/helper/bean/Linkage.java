package fotile.device.cookerprotocollib.helper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名称：Linkage
 * 创建时间：2017/12/19 14:19
 * 文件作者：yaohx
 * 功能描述：设备联动标志位
 */

public class Linkage implements Parcelable {
    /**
     * 烟蒸
     */
    public String steam;
    /**
     * 烟烤
     */
    public String oven;
    /**
     * 烟蒸微
     */
    public String steammicro;

    public String ikcc;


    public Linkage(String linkage) {
        try {
            JSONObject jsonObject = new JSONObject(linkage);
            if (jsonObject.has("steam")) {
                steam = jsonObject.getString("steam");
            }
            if (jsonObject.has("oven")) {
                oven = jsonObject.getString("oven");
            }
            if (jsonObject.has("steammicro")) {
                steammicro = jsonObject.getString("steammicro");
            }
            if (jsonObject.has("ikcc")) {
                ikcc = jsonObject.getString("ikcc");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "【steam：" + steam + "；oven：" + oven + "；steammicro：" + steammicro + "；ikcc：" + ikcc + "】";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.steam);
        dest.writeString(this.oven);
        dest.writeString(this.steammicro);
        dest.writeString(this.ikcc);
    }

    protected Linkage(Parcel in) {
        this.steam = in.readString();
        this.oven = in.readString();
        this.steammicro = in.readString();
        this.ikcc = in.readString();
    }

    public static final Creator<Linkage> CREATOR = new Creator<Linkage>() {
        @Override
        public Linkage createFromParcel(Parcel source) {
            return new Linkage(source);
        }

        @Override
        public Linkage[] newArray(int size) {
            return new Linkage[size];
        }
    };
}
