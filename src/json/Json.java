package json;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Json {
    public static boolean isNashorn = false;
    public static Class<?> SCRIPT_OBJECT_CLASS;
    static {
        try {
            Object pre = jsonToObject("{'key':'value'}");
            isNashorn = pre.getClass().getName()
                    .equals("jdk.nashorn.api.scripting.ScriptObjectMirror");
            Json.SCRIPT_OBJECT_CLASS = Class
                    .forName(isNashorn ? "jdk.nashorn.api.scripting.ScriptObjectMirror"
                            : "sun.org.mozilla.javascript.internal.Scriptable");
        } catch (ScriptException e) {
            System.err.println("json err");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            System.err.println("class loader err");
            System.exit(1);
        }
    }

    private Json() {
    }

    public static Map<String, Object> toJson(String data) {
        Map<String, Object> map = null;
        try {
            Object object = jsonToObject(data);
            if (isNashorn) {
                map = scriptObjectToMapWithNashorn(object);
            } else {
                map = scriptObjectToMapWithRhino(object);
            }
        } catch (Exception e) {
        }
        return map;
    }

    public static Object jsonToObject(String data) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Object obj = engine.eval(String.format("(%s)", data));
        return obj;
    }

    public static Map<String, Object> scriptObjectToMapWithNashorn(Object object)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException, ClassNotFoundException {
        @SuppressWarnings("rawtypes")
        Object[] keys = ((java.util.Set) object.getClass().getMethod("keySet")
                .invoke(object)).toArray();
        Method getMethod = object.getClass().getMethod("get",
                Class.forName("java.lang.Object"));

        Map<String, Object> map = new HashMap<String, Object>();
        for (Object key : keys) {
            Object val = getMethod.invoke(object, key);
            if (SCRIPT_OBJECT_CLASS.isInstance(val)) {
                map.put(key.toString(), scriptObjectToMapWithNashorn(val));
            } else {
                map.put(key.toString(), val.toString());
            }
        }
        return map;
    }

    public static Map<String, Object> scriptObjectToMapWithRhino(Object object)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException, ClassNotFoundException {
        boolean isArray = Class.forName(
                "sun.org.mozilla.javascript.internal.NativeArray").isInstance(
                object);
        Object[] keys = (Object[]) object.getClass().getMethod("getIds")
                .invoke(object);
        Method getMethod = isArray ? object.getClass().getMethod("get",
                int.class, Json.SCRIPT_OBJECT_CLASS) : object.getClass()
                .getMethod("get", Class.forName("java.lang.String"),
                        Json.SCRIPT_OBJECT_CLASS);

        Map<String, Object> map = new HashMap<String, Object>();
        for (Object key : keys) {
            Object val = isArray ? getMethod
                    .invoke(object, (Integer) key, null) : getMethod.invoke(
                    object, key.toString(), null);
            if (SCRIPT_OBJECT_CLASS.isInstance(val)) {
                map.put(key.toString(), scriptObjectToMapWithRhino(val));
            } else {
                map.put(key.toString(), val.toString());
            }
        }
        return map;
    }

}
