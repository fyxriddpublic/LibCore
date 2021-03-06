package com.fyxridd.lib.core.matcher;

import com.fyxridd.lib.core.api.UtilApi;
import com.fyxridd.lib.core.api.inter.StringMatcher;

import java.util.regex.Pattern;

/**
 * 字符串匹配
 */
public class StringMatcherImpl implements StringMatcher {
    private int mode;

    //普通配置数据
    private boolean sensitive;//大小写是否敏感
    private String normalStr;//普通匹配串

    //正则匹配数据
    private Pattern pattern;//正则匹配串

    public StringMatcherImpl(String s) {
        String[] args = s.split(" ");
        mode = Integer.parseInt(args[0]);
        switch (mode) {
            case 1:
                sensitive = args[1].equalsIgnoreCase("y");
                normalStr = UtilApi.combine(args, " ", 2, args.length);
                break;
            case 2:
                pattern = Pattern.compile(UtilApi.combine(args, " ", 1, args.length));
                break;
        }
    }

    /**
     * 检测是否匹配
     * @param str 要检测的字符串
     * @return 是否匹配成功
     */
    @Override
    public boolean check(String str) {
        switch (mode) {
            case 1:
                if (sensitive) return normalStr.equals(str);
                else return normalStr.equalsIgnoreCase(str);
            case 2:
                return pattern.matcher(str).matches();
        }
        return false;
    }
}
