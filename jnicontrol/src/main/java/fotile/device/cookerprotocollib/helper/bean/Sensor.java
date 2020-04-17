package fotile.device.cookerprotocollib.helper.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名称：Sensor
 * 创建时间：2017/11/6 11:38
 * 文件作者：yaohx
 * 功能描述：空气管家传感器
 */
public class Sensor implements Parcelable {

    public Sensor(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject.has("air")) {
                air = jsonObject.getString("air");
            }
            if (jsonObject.has("airsensorstate")) {
                airsensorstate = jsonObject.getString("airsensorstate");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 空气质量
     * optimal：优
     * good：良
     * poor：较差
     * bad：很差
     *
     */
    public String air;
    /**
     * 空气质量传感器状态
     * off：关闭
     * preheat：预热
     * work：工作中
     * clear：清除完成
     * checkover：自检完成
     */
    public String airsensorstate;

    @Override
    public String toString() {
        String s1 = "air：" + air + "；airsensorstate：" + airsensorstate;
        return s1;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.air);
        dest.writeString(this.airsensorstate);
    }

    protected Sensor(Parcel in) {
        this.air = in.readString();
        this.airsensorstate = in.readString();
    }

    public static final Creator<Sensor> CREATOR = new Creator<Sensor>() {
        @Override
        public Sensor createFromParcel(Parcel source) {
            return new Sensor(source);
        }

        @Override
        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };
}
