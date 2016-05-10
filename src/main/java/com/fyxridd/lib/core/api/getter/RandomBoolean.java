package com.fyxridd.lib.core.api.getter;

import com.fyxridd.lib.core.api.MathApi;
import com.fyxridd.lib.core.api.hashList.ChanceHashList;
import com.fyxridd.lib.core.api.hashList.ChanceHashListImpl;

/**
 * 随机布尔值获取器(true/false)
 */
public class RandomBoolean implements RandomGetter<Boolean>{
    private ChanceHashList<Boolean> chances = new ChanceHashListImpl<>();

    /**
     * @param data '<true概率>:<false概率>'或'true/false',概率>=0
     */
    public RandomBoolean(String data) {
        String[] args = data.split(":");
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("true")) {
                chances.addChance(true, 1);
                chances.addChance(false, 0);
            }else {
                chances.addChance(true, 0);
                chances.addChance(false, 1);
            }
        }else {
            chances.addChance(true, Integer.parseInt(args[0]));
            chances.addChance(false, Integer.parseInt(args[1]));
        }
    }

    /**
     * @param extra 此处可以加成
     * @return 布尔值
     */
    @Override
    public Boolean get(int extra) {
        if (extra > 0) {
            int trueChance = chances.getChance(true)*(100+extra);
            int falseChance = chances.getChance(false)*100;
            return MathApi.nextInt(0, falseChance + trueChance - 1) < trueChance;
        }else return chances.getRandom();
    }
}
