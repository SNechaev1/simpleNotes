package men.snechaev.simplenotes.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class SharedPreferencesUtil {

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mPreferences;
    private String mFileName;
    private static final String TAG = SharedPreferencesUtil.class.getSimpleName();


    public SharedPreferencesUtil(Context context, String fileName) {
        this.mPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        this.mEditor = this.mPreferences.edit();
        mFileName = fileName;
    }

    public boolean putBoolean(String name, Boolean value) {
        mEditor.putBoolean(name, value);
        boolean result = mEditor.commit();

        Log.d(TAG, " put key : " + name + ", value : " + value + " to file : " + mFileName + " result: " + result);
        return result;
    }

    public Boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

}
