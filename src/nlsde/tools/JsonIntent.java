/**
 * JsonIntent.java
 * 
 * @Description: 
 * 
 * @File: JsonIntent.java
 * 
 * @Package nlsde.tools
 * 
 * @Author chaos
 * 
 * @Date 2014-11-28下午4:47:50
 * 
 * @Version V1.0
 */
package nlsde.tools;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author chaos
 * 用于activity之间传递json
 *
 */
public class JsonIntent implements Parcelable {

	private JSONObject mJson;
	 
    public static final Parcelable.Creator<JsonIntent> CREATOR = new Creator<JsonIntent>() {  
        @Override 
        public JsonIntent createFromParcel(Parcel source) {   
            String str = source.readString();
            JsonIntent jsonIntent = new JsonIntent(str); 
            return jsonIntent;  
        }
 
        @Override
        public JsonIntent[] newArray(int size) {
            return new JsonIntent[size];
        }  
    };
     
    JsonIntent() {
        mJson = new JSONObject();
    }
     
    JsonIntent(String json) {
        try {
            mJson = new JSONObject(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
 
    public void setValue(String value) {
        try {
            mJson.put("value", value);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
     
    public String getValue() {
        try {
            return mJson.getString("value");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         
        return null;
    }
     
    public String toString() {
        return mJson.toString();
    }
 
    @Override
    public int describeContents() {
        return 0;
    }
 
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mJson.toString());
    }

}
