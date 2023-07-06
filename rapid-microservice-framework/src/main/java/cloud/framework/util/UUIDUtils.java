package cloud.framework.util;

import java.util.UUID;

/**
 * UUID工具类
 *
 * @author xmc
 */
public class UUIDUtils {

    /**
     * 获取原始UUID
     */
    public static String getInitialUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * 获取32位UUID
     */
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 批量获取32位UUID
     */
    public static String[] getUUID(int number){
        if (number < 1) {
            return null;
        } else {
            String[] arr = new String[number];

            for(int i = 0; i < number; ++i) {
                arr[i] = getUUID();
            }
            return arr;
        }
    }

    /**
     * 获取long类型UUID
     */
    public static Long getLongUUID(){
        return UUID.randomUUID().getMostSignificantBits() & 9223372036854775807L;
    }

    /**
     * 批量获取long类型UUID
     */
    public static Long[] getLongUUID(int number){
        if (number < 1) {
            return null;
        } else {
            Long[] arr = new Long[number];

            for(int i = 0; i < number; ++i) {
                arr[i] = getLongUUID();
            }
            return arr;
        }
    }

}