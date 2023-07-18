package SimpleMath;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.riceball.gpknives.MainActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class MyMath extends CordovaPlugin {
    private static final String TAG = "MyMath";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.e(TAG, "js调用java--" + action);
        if (action.equals("Plus")) {
            // String message = args.getString(0);
            // this.coolMethod(message, callbackContext);
            MainActivity.getInstance().coolMethod(args, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
