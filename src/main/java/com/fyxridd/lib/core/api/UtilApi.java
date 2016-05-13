package com.fyxridd.lib.core.api;

import com.fyxridd.lib.core.MD5;
import com.fyxridd.lib.core.api.hashList.HashList;
import com.fyxridd.lib.core.api.inter.LastType;
import com.fyxridd.lib.core.api.inter.StringMatcher;
import com.fyxridd.lib.core.matcher.StringMatcherImpl;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilApi {
    private static Random random = new Random();

    public static final long SECONDS = 1000;
    public static final long MINUTE = SECONDS*60;
    public static final long HOUR = MINUTE*60;
    public static final long DAY = HOUR*24;

    private static Pattern ColorPattern = Pattern.compile("&[0123456789abcdeflmnor]");

    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
    static {
        Map<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>(16);
        Map<Class<?>, Class<?>> wrapToPrim = new HashMap<Class<?>, Class<?>>(16);

        add(primToWrap, wrapToPrim, boolean.class, Boolean.class);
        add(primToWrap, wrapToPrim, byte.class, Byte.class);
        add(primToWrap, wrapToPrim, char.class, Character.class);
        add(primToWrap, wrapToPrim, double.class, Double.class);
        add(primToWrap, wrapToPrim, float.class, Float.class);
        add(primToWrap, wrapToPrim, int.class, Integer.class);
        add(primToWrap, wrapToPrim, long.class, Long.class);
        add(primToWrap, wrapToPrim, short.class, Short.class);
        add(primToWrap, wrapToPrim, void.class, Void.class);

        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
    }

    public static <T> Class<T> wrap(Class<T> type) {
        @SuppressWarnings("unchecked")
        Class<T> wrapped = (Class<T>) PRIMITIVE_TO_WRAPPER_TYPE.get(type);
        return (wrapped == null) ? type : wrapped;
    }

    public static <T> Class<T> unwrap(Class<T> type) {
        // cast is safe: long.class and Long.class are both of type Class<Long>
        @SuppressWarnings("unchecked")
        Class<T> unwrapped = (Class<T>) WRAPPER_TO_PRIMITIVE_TYPE.get(type);
        return (unwrapped == null) ? type : unwrapped;
    }

    /**
     * 设置Field的值(即使Field私有也可以)
     */
    public static void setField(Field field, Object obj, Object value) {
        boolean access = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(access);
    }

    /**
     * 使用空构造器
     * @see #newInstance(Class, Class[], Object[])
     */
    public static <T> T newInstance(Class<T> c) {
        return newInstance(c, new Class[0], new Object[0]);
    }

    /**
     * 新建实例(即使构造器私有也可以)
     * @return 异常返回null
     */
    public static <T> T newInstance(Class<T> c, Class[] classParams, Object[] args) {
        try {
            Constructor<T> constructor = c.getDeclaredConstructor(classParams);
            boolean access = constructor.isAccessible();
            constructor.setAccessible(true);
            T result = constructor.newInstance(args);
            constructor.setAccessible(access);
            return result;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 把详细的异常信息转化成字符串,多行以'\n'连接
     */
    public static String convertException(Exception e) {
        boolean first = true;
        String result = "";
        for (StackTraceElement ele:e.getStackTrace()) {
            if (first) first = false;
            else result += "\n";
            result += ele.toString();
        }
        return result;
    }

    /**
     * 获取带后缀的文件名
     * @param fullName 全名,如"a/b/name.hbm.xml"
     * @return 如"a/b/name.hbm.xml"返回"name.hbm.xml"
     */
    public static String getFileNameWithSuffix(String fullName) {
        String[] args = fullName.split("/");
        return args[args.length-1];
    }

    /**
     * 获取无后缀的文件名
     * 允许文件名带.
     * @param fullName 全名,如"a/b/name.hbm.xml"
     * @return 如"a/b/name.hbm.xml"返回"name.hbm"
     */
    public static String getFileNameWithoutSuffix(String fullName) {
        String s = getFileNameWithSuffix(fullName);
        return s.substring(0, s.lastIndexOf("."));
    }

    /**
     * 获取列表元素总数
     * @param list 列表对象,null时返回0
     * @param type 传入的列表类型: 0指List类型,1指Object[]类型,2指Collection类型,3指HashList类型,其它情况下返回0
     * @return 列表元素数量,>=0
     */
    public static int getTotal(Object list, int type) {
        if (list == null) return 0;
        switch (type) {
            case 0:
                return ((List)list).size();
            case 1:
                return ((Object[])list).length;
            case 2:
                return ((Collection)list).size();
            case 3:
                return ((HashList)list).size();
        }
        return 0;
    }

    /**
     * 获取最大页面数
     * @param total 总数,<=0时返回0
     * @param pageSize 分页大小,<=0时返回0
     * @return 最大页面数,0表示无元素,有元素则最小页面数为1
     */
    public static int getMaxPage(int total, int pageSize) {
        if (total <= 0 || pageSize <= 0) return 0;
        if (total%pageSize == 0) return total/pageSize;
        return total/pageSize+1;
    }

    /**
     * 获取指定页的对象列表
     * @param list 列表对象
     * @param type 传入的列表类型: 0指List类型,1指Object[]类型,2指Collection类型,3指HashList类型
     * @param pageSize 分页大小,>=0,0时返回空列表
     * @param page 指定页,页面从1开始
     * @return 对象列表,异常返回空列表
     */
    public static List getPage(Object list, int type, int pageSize, int page) {
        List result = new ArrayList();
        if (list == null) return result;
        if (pageSize == 0) return result;
        int total = getTotal(list, type);
        int maxPage = getMaxPage(total, pageSize);
        if (page >= 1 && page <= maxPage) {
            int begin = (page-1)*pageSize;
            int end = (page == maxPage)?total:page*pageSize;
            switch (type) {
                case 0:
                    List list2 = (List)list;
                    for (int i=begin;i<end;i++) result.add(list2.get(i));
                    break;
                case 1:
                    Object[] array = (Object[])list;
                    for (int i=begin;i<end;i++) result.add(array[i]);
                    break;
                case 2:
                    Collection c = (Collection)list;
                    int index = 0;
                    for (Object o:c) {
                        if (index >= end) break;//结束
                        if (index >= begin) result.add(o);
                        index ++;
                    }
                    break;
                case 3:
                    result = ((HashList) list).getPage(page, pageSize);
                    break;
            }
        }
        return result;
    }

    /**
     * 变量转换,包括:
     * {x}
     * {x,}
     * {,y}
     * {x,y}
     * {,}
     * 如args为['set','name','Jim','Kate'],s为'{4}',则转换后的值为'Kate'
     * @param args 变量来源
     * @param s 不为null
     * @return 转换后的值
     */
    public static String convertArg(String[] args, String s) {
        try {
            if (s.length() >= 3 && s.charAt(0) == '{' && s.charAt(s.length()-1) == '}') {//{...}
                String content = s.substring(1, s.length()-1).toLowerCase();
                if (content.indexOf(',') == -1) {//{1}
                    int pos = Integer.parseInt(content);
                    if (pos < 1 || pos > args.length) return "";
                    return args[pos-1];
                }else if (s.length() == 3){//{,}
                    return UtilApi.combine(args, " ", 0, args.length);
                }else {
                    int index = content.indexOf(",");
                    if (index == 0) {//{,1}
                        int endPos = Integer.parseInt(content.substring(1));
                        if (endPos < 1) return "";
                        return UtilApi.combine(args, " ", 0, endPos-1);
                    }else if (index == content.length()-1) {//{1,}
                        int startPos = Integer.parseInt(content.substring(0, index));
                        if (startPos < 1 || startPos > args.length) return "";
                        return UtilApi.combine(args, " ", startPos-1, args.length);
                    }else {//{1,2}
                        int startPos = Integer.parseInt(content.substring(0, index));
                        int endPos = Integer.parseInt(content.substring(index+1));
                        if (startPos < 1 || endPos < 1 || startPos > endPos || startPos > args.length) return "";
                        return UtilApi.combine(args, " ", startPos-1, endPos-1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 获取随机元素
     * @param c 集合(null时返回null)
     * @return 异常返回null
     */
    public static Object getRandom(Collection c) {
        if (c == null || c.isEmpty()) return null;

        int index = 0;
        int sel = MathApi.nextInt(0, c.size()-1);
        for (Object o : c) {
            if (index++ == sel) return o;
        }
        return null;
    }

    /**
     * 将时间格式串转化为时间
     * @param s 格式'xxDxxHxxMxxSxxI',忽略大小写,表示'xx天xx时xx分xx秒xx毫秒',所有选项都是可选的
     * @return 异常返回0
     */
    public static long getTime(String s) {
        try {
            s = s.toLowerCase();
            long day,hour,minute,second,millisecond;
            int index;

            index = s.indexOf("d");
            if (index != -1) day = Long.parseLong(s.substring(0, index));
            else day = 0;
            if (s.length()-1 > index) s = s.substring(index+1);
            else s = "";

            index = s.indexOf("h");
            if (index != -1) hour = Long.parseLong(s.substring(0, index));
            else hour = 0;
            if (s.length()-1 > index) s = s.substring(index+1);
            else s = "";

            index = s.indexOf("m");
            if (index != -1) minute = Long.parseLong(s.substring(0, index));
            else minute = 0;
            if (s.length()-1 > index) s = s.substring(index+1);
            else s = "";

            index = s.indexOf("s");
            if (index != -1) second = Long.parseLong(s.substring(0, index));
            else second = 0;
            if (s.length()-1 > index) s = s.substring(index+1);
            else s = "";

            index = s.indexOf("i");
            if (index != -1) millisecond = Long.parseLong(s.substring(0, index));
            else millisecond = 0;

            return day*DAY+hour*HOUR+minute*MINUTE+second*SECONDS+millisecond;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取持续时间的显示
     * @param type 显示类型(null时返回"")
     * @param last 持续时间,单位毫秒(<0时返回"")
     * @return 持续时间的显示,异常返回""
     */
    public static String getLastTime(LastType type, long last) {
        if (type == null || last < 0) return "";

        if (type.equals(LastType.Milli)) {
            long day = last/DAY;
            last -= day*DAY;
            long hour = last/HOUR;
            last -= hour*HOUR;
            long minute = last/MINUTE;
            last -= minute*MINUTE;
            long seconds = last/SECONDS;
            last -= seconds*SECONDS;
            long milli = last;
            return get(55, day, hour, minute, seconds, milli).getText();
        }else if (type.equals(LastType.Seconds)) {
            long day = last/DAY;
            last -= day*DAY;
            long hour = last/HOUR;
            last -= hour*HOUR;
            long minute = last/MINUTE;
            last -= minute*MINUTE;
            long seconds = last/SECONDS;
            return get(60, day, hour, minute, seconds).getText();
        }else if (type.equals(LastType.Minute)) {
            long day = last/DAY;
            last -= day*DAY;
            long hour = last/HOUR;
            last -= hour*HOUR;
            long minute = last/MINUTE;
            return get(65, day, hour, minute).getText();
        }else if (type.equals(LastType.HourMinuteSeconds)) {
            long hour = last/HOUR;
            last -= hour*HOUR;
            long minute = last/MINUTE;
            last -= minute*MINUTE;
            long seconds = last/SECONDS;
            return get(67, hour, minute, seconds).getText();
        }else return "";
    }

    /**
     * 从字符串中读取匹配信息
     * 模式:
     *   1. 普通匹配,格式'1 y/n 匹配串',y/n表示大小写敏感/不敏感
     *   2. 正则匹配,格式'2 正则串'
     * @param data 字符串
     * @return 匹配信息,异常返回null
     */
    public static StringMatcher loadMatcher(String data) {
        try {
            return new StringMatcherImpl(data);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 以UTF-8格式读入文本文件
     * @param is 输入流
     * @return 字符串形式的文件内容,异常返回null
     */
    public static String getDataAsText(InputStream is) {
        try {
            StringBuilder sb = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
            while (br.ready()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append(br.readLine());
            }
            br.close();

            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 颜色字符&转换<br>
     * 如'&a'或'&l'会转换,但'&z'不会转换
     * @param msg 要转换的信息,null时会返回null
     * @return 转换后的字符串
     */
    public static String convert(String msg){
        if (msg == null) return null;

        Matcher m = ColorPattern.matcher(msg);
        StringBuffer sb = new StringBuffer();
        while (m.find()) m.appendReplacement(sb, m.group().replace("&", "\u00A7"));
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * @param file 文件
     * @return 行列表
     * @throws FileNotFoundException
     * @throws IOException
     * @throws Exception
     */
    public static List<String> readLinesByUTF8(File file) throws FileNotFoundException, IOException, Exception {
        BufferedReader br = null;
        try {
            List<String> lines = new ArrayList<String>();
            if (!file.exists()) {
                throw new FileNotFoundException();
            }
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.forName("utf-8")));
            String line = br.readLine();
            while (line != null) {
                lines.add(line);
                line = br.readLine();
            }
            return lines;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * 读取utf-8编码的yml文件
     * @param file 要读取的yml文件
     * @return file为null或异常返回null
     */
    public static YamlConfiguration loadConfigByUTF8(File file) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("utf-8"));
            StringBuilder builder = new StringBuilder();
            BufferedReader input = new BufferedReader(reader);
            try
            {
                String line;
                while ((line = input.readLine()) != null) {
                    builder.append(line);
                    builder.append('\n');
                }
            } finally {
                input.close();
            }

            config.loadFromString(builder.toString());
            return config;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 保存配置到utf-8编码的yml文件
     * @param config 要保存的配置
     * @param file 要保存的yml文件
     * @return 是否成功
     */
    public static boolean saveConfigByUTF8(YamlConfiguration config, File file) {
        BufferedWriter output = null;
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8"));
            output = new BufferedWriter(writer);
            String data = config.saveToString();
            output.write(data);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (output != null) try {
                output.flush();
                output.close();
            } catch (IOException e) {
                return false;
            }
        }
    }

    /**
     * 字符串转u码
     * @param str 字符串
     * @return u码
     */
    public static String strToU(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuffer sb = new StringBuffer();
        for (char c:str.toCharArray()) {
            sb.append(" "+Integer.toHexString(c));
        }
        return sb.toString();
    }

    /**
     * u码转字符串
     * @param str u码
     * @return 字符串
     */
    public static String uToStr(String str) {
        if (str == null || str.isEmpty()) return str;
        StringBuffer sb = new StringBuffer();
        try {
            for (String s:str.split(" ")) {
                if (s.isEmpty()) continue;
                int i = Integer.parseInt(s, 16);
                sb.append((char)i);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    //performance
    private static final DecimalFormat format2 = new DecimalFormat("#.00");

    /**
     * 改变精度
     * @param num 需要改变的实数
     * @param accuracy 精确度,表示小数点后保留的位数,>=0,小于0会被当作0
     * @return 改变精度后的实数(返回的小数部分长度可能比精确度小)
     */
    public static double getDouble(double num,int accuracy) {
        if (accuracy < 0) accuracy = 0;

        DecimalFormat format;
        if (accuracy == 2) format = format2;
        else {
            String f = "#.";
            for (int index=0;index<accuracy;index++) f += "0";
            format = new DecimalFormat(f);
        }
        return Double.parseDouble(format.format(num));
    }

    /**
     * 将整数转换为byte数组
     * @param i 整数
     * @return byte数组,长度为4
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * 将byte数组转换为整数
     * @param byteArray byte数组,长度为4
     * @return 对应的整数
     */
    public static int byteArrayToInt(byte[] byteArray) {
        int result = 0;
        int b0 = byteArray[0];
        int b1 = byteArray[1];
        int b2 = byteArray[2];
        int b3 = byteArray[3];
        result = result | (b0 << 24);
        result = result | (b1 << 16 & 0x00FF0000);
        result = result | (b2 << 8 & 0x0000FF00);
        result = result | b3 & 0x000000FF;
        return result;
    }

    /**
     * 字节数组转换为字符串
     * @param target 字节数组
     * @return 字符串
     */
    public static String charsToStr(char[] target) {
        StringBuffer buf = new StringBuffer();
        for (int i=0;i<target.length;i++)
            buf.append(target[i]);
        return buf.toString();
    }

    /**
     * 字符串转换为字节数组
     * @param str 字符串
     * @return 字节数组
     */
    public static char[] StrToChars(String str) {
        char[] buf = new char[str.length()];
        for (int i=0;i<str.length();i++)
            buf[i] = str.charAt(i);
        return buf;
    }

    /**
     * 获取当前时间的简单格式的日期时间显示(格式'yyyy-MM-dd HH:mm')
     * @return 出错返回空字符串
     */
    public static String getSimpleDateTime() {
        return getSimpleDateTime(new Date().getTime());
    }

    /**
     * 获取简单格式的日期时间显示(格式'yyyy-MM-dd HH:mm')
     * @param time 开始计算的时间,单位毫秒
     * @return 出错返回空字符串
     */
    public static String getSimpleDateTime(long time) {
        return getDateTime("yyyy-MM-dd HH:mm", time);
    }

    /**
     * 获取当前时间的适合日志记录文件名格式的日期时间显示(格式'yyyy-MM-dd HH-mm-ss')
     * @return 出错返回空字符串
     */
    public static String getLogDateTime() {
        return getLogDateTime(new Date().getTime());
    }

    /**
     * 获取适合日志记录文件名格式的日期时间显示(格式'yyyy-MM-dd HH-mm-ss')
     * @param time 开始计算的时间,单位毫秒
     * @return 出错返回空字符串
     */
    public static String getLogDateTime(long time) {
        return getDateTime("yyyy-MM-dd HH-mm-ss", time);
    }

    /**
     * 获取日期时间显示
     * @param format 格式
     * @param time 开始计算的时间,单位毫秒
     * @return 出错返回空字符串
     */
    public static String getDateTime(String format, long time) {
        try {
            return new SimpleDateFormat(format).format(new Date(time));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 把变量重新组合成字符串
     * (包含开始与结束位置)
     * @param args 变量
     * @param separator 变量之间用这个分隔
     * @param start 开始位置,0-(args.length-1)
     * @param end 结束位置,0-(args.length-1),大于最大时不会出异常
     * @return 组合后的字符串
     */
    public static String combine(String[] args, String separator, int start, int end) {
        String result = "";
        for (int i=0;i<args.length;i++) {
            if (i < start) continue;
            if (i > end) break;
            if (i > start) result += separator;
            result += args[i];
        }
        return result;
    }

    /**
     * 方向随机偏移
     * @param originYaw 方向的yaw
     * @param originPitch 方向的pitch
     * @param accuracy 散度,>=0(如1.2)
     * @return 新的偏移后的方向
     */
    public static org.bukkit.util.Vector getRandomVector(double originYaw, double originPitch, double accuracy) {
        double yaw = Math.toRadians(-originYaw - 90.0F);
        double pitch = Math.toRadians(-originPitch);
        double[] spread = { 1.0D, 1.0D, 1.0D };
        for (int t = 0; t < 3; t++) spread[t] = ((random.nextDouble() - random.nextDouble()) * accuracy * 0.1D);
        double x = Math.cos(pitch) * Math.cos(yaw) + spread[0];
        double y = Math.sin(pitch) + spread[1];
        double z = -Math.sin(yaw) * Math.cos(pitch) + spread[2];
        org.bukkit.util.Vector dirVel = new org.bukkit.util.Vector(x, y, z);
        return dirVel.normalize();
    }

    /**
     * 检测字符串是否合法,合法表示只包含英文,数字,下划线
     * @param s 字符串
     * @return 是否合法
     */
    public static boolean isValid(String s) {
        return s.matches("^[\\da-zA-Z_]*$");
    }

    /**
     * 分割字符串
     * @param s 字符串
     * @param maxLength 每行最大长度
     * @return
     */
    public static List<String> separateLines(String s, int maxLength) {
        List<String> result = new LinkedList<String>();
        int index = 0;
        while (index < s.length()) {
            result.add(s.substring(index, Math.min(s.length(), index+maxLength)));

            index += maxLength;
        }
        return result;
    }

    /**
     * 分割字符串
     * @param lore 字符串列表
     * @param maxLength 每行最大长度
     * @return
     */
    public static void separateLines(List<String> lore, int maxLength) {
        int index = 0;
        while (index < lore.size()) {
            List<String> lore1 = separateLines(lore.get(index), maxLength);
            if (lore1.size() > 1) {
                lore.set(index, lore1.get(0));
                for (int i=1;i<lore1.size();i++) lore.add(index+i, lore1.get(i));
                index += lore1.size();
                continue;
            }

            index ++;
        }
    }

    /**
     * md5
     * @param msg 原内容
     * @return md5后的结果
     */
    public static String md5(String msg) {
        return MD5.GetMD5Code(msg);
    }

    private static void add(
            Map<Class<?>, Class<?>> forward,
            Map<Class<?>, Class<?>> backward,
            Class<?> key,
            Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    private static FancyMessage get(int id, Object... args) {
        return FormatApi.get(CorePlugin.pn, id, args);
    }
}
