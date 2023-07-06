package cloud.framework.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * 密码工具类
 *
 * @author xmc
 */
public class PasswordUtils {

    /**
     * 盐
     */
    private static final String KEY = "worldxmc";

    /**
     * 字符集编码方式
     */
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * 加密
     */
    public static String crypto(String resource) {
        try {
            byte[] bs = resource.getBytes(CHARSET_NAME);
            byte[] kbs = KEY.getBytes(CHARSET_NAME);
            StringBuilder builder = new StringBuilder("1");
            for (int i = 0; i < bs.length; i++) {
                int temp = bs[i] + kbs[i % kbs.length] + 256;
                builder.append(String.format("%3d", Math.abs(temp)).replace(' ', '0'));
            }
            BigInteger bigInteger = new BigInteger(builder.toString(), 10);
            return bigInteger.toString(36);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解密
     */
    public static String deCrypto(String code) {
        try {
            byte[] kbs = KEY.getBytes(CHARSET_NAME);
            BigInteger bigInteger = new BigInteger(code, 36);
            String source = bigInteger.toString(10);
            int len = source.length() / 3;
            byte[] bs = new byte[len];
            for (int i = 0; i < bs.length; i++) {
                int temp = Integer.parseInt(source.substring(3 * i + 1, 3 * (i + 1) + 1));
                bs[i] = (byte) (temp - kbs[i % kbs.length] - 256);
            }
            return new String(bs, CHARSET_NAME);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * MD5
     */
    public static String md5(String password){
        try {
            //获取密码的md5字节数组
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] bytes = md5.digest(password.getBytes());
            //遍历数组，如果是负数加256，保证转化为16进制后都是两位数，拼接后为32位
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes) {
                int n = b<0?b+256:b;
                stringBuffer.append(Integer.toHexString(n));
            }
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}