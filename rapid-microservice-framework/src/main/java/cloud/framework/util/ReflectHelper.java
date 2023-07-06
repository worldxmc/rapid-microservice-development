package cloud.framework.util;

import java.lang.reflect.Field;

/**
 * 反射工具类
 *
 * @author xmc
 */
public class ReflectHelper {

    public ReflectHelper() {
    }

    /**
     * 获取对象的某个属性
     */
    public static Field getField(Object obj, String fieldName) {
        Class superClass = obj.getClass();
        while(superClass != Object.class) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException var4) {
                superClass = superClass.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取对象的某个属性的值
     */
    public static Object getFieldValue(Object obj, String fieldName) throws SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = getField(obj, fieldName);
        Object value = null;
        if (field != null) {
            if (field.isAccessible()) {
                value = field.get(obj);
            } else {
                field.setAccessible(true);
                value = field.get(obj);
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * 为对象的指定属性赋值
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if (field.isAccessible()) {
            field.set(obj, value);
        } else {
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        }
    }

    /**
     * 获取指定类型对象的所有属性
     */
    public static Field[] getFields(Class<?> clazz) {
        Field[] result = clazz.getDeclaredFields();

        for(Class superClass = clazz.getSuperclass(); superClass != null; superClass = superClass.getSuperclass()) {
            Field[] tempField = superClass.getDeclaredFields();
            Field[] tempResult = new Field[result.length + tempField.length];

            int i;
            for(i = 0; i < result.length; ++i) {
                tempResult[i] = result[i];
            }

            for(i = 0; i < tempField.length; ++i) {
                tempResult[result.length + i] = tempField[i];
            }

            result = tempResult;
        }
        return result;
    }

}