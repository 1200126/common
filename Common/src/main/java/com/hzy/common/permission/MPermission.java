package com.hzy.common.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.hzy.common.permission.annotation.OnMPermissionDenied;
import com.hzy.common.permission.annotation.OnMPermissionGranted;
import com.hzy.common.permission.annotation.OnMPermissionNeverAskAgain;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MPermission extends BaseMPermission {

    /**  */
    public final int BASIC_PERMISSION_REQUEST_CODE = 100;

    private int requestCode = BASIC_PERMISSION_REQUEST_CODE;
    private String[] permissions;
    private Object object; // activity or fragment

    private static PermissionListener listener;

    private MPermission(Object object) {
        this.object = object;
    }

    public static MPermission with(Activity activity) {
        return new MPermission(activity);
    }

    public static MPermission with(Fragment fragment) {
        return new MPermission(fragment);
    }

    public MPermission setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public MPermission permissions(String... permissions) {
        this.permissions = permissions;
        return this;
    }

    public MPermission setListener(PermissionListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * ********************* request *********************
     */

    @TargetApi(value = Build.VERSION_CODES.M)
    public void request() {
        doRequestPermissions(object, requestCode, permissions);
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void doRequestPermissions(Object object, int requestCode, String[] permissions) {
        if (!isOverMarshmallow()) {
            doExecuteSuccess(object, requestCode);
            if (listener != null) {
                listener.onGranted();
            }
            return;
        }

        List<String> deniedPermissions = findDeniedPermissions(getActivity(object), permissions);
        if (deniedPermissions != null && deniedPermissions.size() > 0) {
            if (object instanceof Activity) {
                ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment) {
                ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else {
                throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
            }
        } else {
            doExecuteSuccess(object, requestCode);
            if (listener != null) {
                listener.onGranted();
            }

        }
    }

    /**
     * ********************* on result *********************
     */

    public static void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        dispatchResult(activity, requestCode, permissions, grantResults);
        if (listener != null) {
            List<String> result1 = MPermission.getNeverAskAgainPermissions(activity, permissions);
            List<String> result2 = MPermission.getDeniedPermissionsWithoutNeverAskAgain(activity, permissions);
            if (result1.isEmpty() && result2.isEmpty()) {
                listener.onGranted();
            } else {
                listener.onDenied(result2);
                listener.onShouldShowRationale(result1);
            }
        }
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        dispatchResult(fragment, requestCode, permissions, grantResults);
        if (listener != null) {
            List<String> result1 = MPermission.getNeverAskAgainPermissions(fragment, permissions);
            List<String> result2 = MPermission.getDeniedPermissionsWithoutNeverAskAgain(fragment, permissions);
            if (result1.isEmpty() && result2.isEmpty()) {
                listener.onGranted();
            } else {
                listener.onDenied(result2);
                listener.onShouldShowRationale(result1);
            }
        }
    }

    private static void dispatchResult(Object obj, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (hasNeverAskAgainPermission(getActivity(obj), deniedPermissions)) {
                doExecuteFailAsNeverAskAgain(obj, requestCode);
            } else {
                doExecuteFail(obj, requestCode);
            }
        } else {
            doExecuteSuccess(obj, requestCode);
        }
    }

    /**
     * ********************* reflect execute result *********************
     */

    private static void doExecuteSuccess(Object activity, int requestCode) {
        executeMethod(activity, findMethodWithRequestCode(activity.getClass(), OnMPermissionGranted.class, requestCode));
    }

    private static void doExecuteFail(Object activity, int requestCode) {
        executeMethod(activity, findMethodWithRequestCode(activity.getClass(), OnMPermissionDenied.class, requestCode));
    }

    private static void doExecuteFailAsNeverAskAgain(Object activity, int requestCode) {
        executeMethod(activity, findMethodWithRequestCode(activity.getClass(), OnMPermissionNeverAskAgain.class, requestCode));
    }

    private static <A extends Annotation> Method findMethodWithRequestCode(Class clazz, Class<A> annotation, int
            requestCode) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annotation) != null &&
                    isEqualRequestCodeFromAnnotation(method, annotation, requestCode)) {
                return method;
            }
        }
        return null;
    }

    private static boolean isEqualRequestCodeFromAnnotation(Method m, Class clazz, int requestCode) {
        if (clazz.equals(OnMPermissionDenied.class)) {
            return requestCode == m.getAnnotation(OnMPermissionDenied.class).value();
        } else if (clazz.equals(OnMPermissionGranted.class)) {
            return requestCode == m.getAnnotation(OnMPermissionGranted.class).value();
        } else if (clazz.equals(OnMPermissionNeverAskAgain.class)) {
            return requestCode == m.getAnnotation(OnMPermissionNeverAskAgain.class).value();
        } else {
            return false;
        }
    }

    /**
     * ********************* reflect execute method *********************
     */

    private static void executeMethod(Object activity, Method executeMethod) {
        executeMethodWithParam(activity, executeMethod, new Object[]{});
    }

    private static void executeMethodWithParam(Object activity, Method executeMethod, Object... args) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
                executeMethod.invoke(activity, args);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 检查权限列表
     *
     * @param context
     * @param op       这个值被hide了，去AppOpsManager类源码找，如位置权限  AppOpsManager.OP_GPS==2
     * @param opString 如判断定位权限 AppOpsManager.OPSTR_FINE_LOCATION
     * @return @see 如果返回值 AppOpsManagerCompat.MODE_IGNORED 表示被禁用了
     */
    public static int checkOp(Context context, int op, String opString) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            //            Object object = context.getSystemService("appops");
            try {
                Class c = object.getClass();
                Class[] cArg = new Class[3];
                cArg[0] = int.class;
                cArg[1] = int.class;
                cArg[2] = String.class;
                Method lMethod = c.getDeclaredMethod("checkOp", cArg);
                return (Integer) lMethod.invoke(object, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
