package tina.com.lib;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author yxc
 * @date 2018/9/27
 */
public class Bind {

    public static void bind(Activity activity) {
        try {
            Class bindingClass = Class.forName(activity.getClass().getCanonicalName() + "$Binding");
            Class activityClass = Class.forName(activity.getClass().getCanonicalName());
            Constructor constructor = bindingClass.getDeclaredConstructor(activityClass);
            constructor.newInstance(activity);

        } catch (ClassNotFoundException e) {
            Log.e("Bind", e.toString());
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            Log.e("Bind", e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("Bind", e.toString());
            e.printStackTrace();
        } catch (InstantiationException e) {
            Log.e("Bind", e.toString());
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e("Bind", e.toString());
            e.printStackTrace();
        }

    }
}
