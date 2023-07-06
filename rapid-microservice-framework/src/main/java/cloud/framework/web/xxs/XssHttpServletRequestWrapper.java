package cloud.framework.web.xxs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 防Xss攻击请求装饰器
 *
 * @author xmc
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Logger log = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);

    private Map<String, String[]> parameterMap;

    private final byte[] body;

    private static String key = "\\b(and|exec|insert|select|drop|grant|alter|delete|update|count|chr|mid|master|truncate|char|declare|or)\\b|(\\*|;|\\+|'|%)";

    private static final Pattern pattern01 = Pattern.compile("<[\r\n| | ]*script[\r\n| | ]*>(.*?)</[\r\n| | ]*script[\r\n| | ]*>", 2);
    private static final Pattern pattern02 = Pattern.compile("src[\r\n| | ]*=[\r\n| | ]*[\\\"|\\'](.*?)[\\\"|\\']", 42);
    private static final Pattern pattern03 = Pattern.compile("</[\r\n| | ]*script[\r\n| | ]*>", 2);
    private static final Pattern pattern04 = Pattern.compile("<[\r\n| | ]*script(.*?)>", 42);
    private static final Pattern pattern05 = Pattern.compile("eval\\((.*?)\\)", 42);
    private static final Pattern pattern06 = Pattern.compile("e-xpression\\((.*?)\\)", 42);
    private static final Pattern pattern07 = Pattern.compile("javascript[\r\n| | ]*:[\r\n| | ]*", 2);
    private static final Pattern pattern08 = Pattern.compile("vbscript[\r\n| | ]*:[\r\n| | ]*", 2);
    private static final Pattern pattern09 = Pattern.compile("onload(.*?)=", 42);

    public XssHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.parameterMap = request.getParameterMap();
        this.body = StreamUtils.copyToByteArray(request.getInputStream());
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> vector = new Vector(this.parameterMap.keySet());
        return vector.elements();
    }

    @Override
    public String getParameter(String name) {
        String[] results = this.parameterMap.get(name);
        if (results != null && results.length > 0) {
            String value = results[0];
            if (value != null) {
                value = xssEncode(value);
            }
            return value;
        } else {
            return null;
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] results = this.parameterMap.get(name);
        if (results != null && results.length > 0) {
            int length = results.length;
            for (int i = 0; i < length; ++i) {
                results[i] = xssEncode(results[i]);
            }
            return results;
        } else {
            return null;
        }
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(xssEncode(name));
        if (value != null) {
            value = xssEncode(value);
        }
        return value;
    }

    private static String xssEncode(String s) {
        if (s != null && !s.isEmpty()) {
            s = stripXSSAndSql(s);
            StringBuilder sb = new StringBuilder(s.length() + 16);
            for (int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                switch (c) {
                    case '#':
                        sb.append("＃");
                        break;
                    case '&':
                        sb.append("＆");
                        break;
                    case '<':
                        sb.append("＜");
                        break;
                    case '>':
                        sb.append("＞");
                        break;
                    default:
                        sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return s;
        }
    }

    public static String stripXSSAndSql(String value) {
        if (value != null) {
            value = pattern01.matcher(value).replaceAll("");
            value = pattern02.matcher(value).replaceAll("");
            value = pattern03.matcher(value).replaceAll("");
            value = pattern04.matcher(value).replaceAll("");
            value = pattern05.matcher(value).replaceAll("");
            value = pattern06.matcher(value).replaceAll("");
            value = pattern07.matcher(value).replaceAll("");
            value = pattern08.matcher(value).replaceAll("");
            value = pattern09.matcher(value).replaceAll("");
        }
        return value;
    }

    public boolean checkXSSAndSql(String value) {
        boolean flag = false;
        if (value != null) {
            flag = pattern01.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern02.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern03.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern04.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern05.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern06.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern07.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern08.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = pattern09.matcher(value).find();
            if (flag) {
                return flag;
            }
            flag = this.checkSqlKeyWords(value);
        }
        return flag;
    }

    public boolean checkSqlKeyWords(String value) {
        Pattern pattern = Pattern.compile(key);
        Matcher matcher = pattern.matcher(value);
        boolean result = matcher.find();
        if (result) {
            log.error(this.getRequestURI() + "参数中包含不允许sql的关键词(" + matcher.group() + ")");
        }
        return result;
    }

    public final boolean checkParameter() {
        Map<String, String[]> submitParams = new HashMap(this.parameterMap);
        Set<String> submitNames = submitParams.keySet();
        Iterator var3 = submitNames.iterator();
        Object submitValues;
        label06:
        do {
            while (var3.hasNext()) {
                String submitName = (String) var3.next();
                submitValues = submitParams.get(submitName);
                if (submitValues instanceof String) {
                    continue label06;
                }
                if (submitValues instanceof String[]) {
                    String[] var6 = (String[]) submitValues;
                    int var7 = var6.length;
                    for (int var8 = 0; var8 < var7; ++var8) {
                        String submitValue = var6[var8];
                        if (this.checkXSSAndSql(submitValue)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } while (!this.checkXSSAndSql((String) submitValues));
        return true;
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(this.body);
        return new ServletInputStream() {

            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener arg0) {
            }

        };
    }

}