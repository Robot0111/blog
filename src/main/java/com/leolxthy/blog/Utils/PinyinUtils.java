package com.leolxthy.blog.Utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class PinyinUtils {

    public static String toSlug(String chinese) {
        if (chinese == null) return "";

        // 1. 只保留中文、字母和数字，去掉特殊符号
        String clean = chinese.replaceAll("[^\\u4E00-\\u9FA5a-zA-Z0-9]", "");

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE); // 全小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE); // 不要声调（如 ā 改为 a）

        StringBuilder sb = new StringBuilder();
        try {
            for (char c : clean.toCharArray()) {
                // 如果是中文则转换
                if (Character.toString(c).matches("[\\u4E00-\\u9FA5]")) {
                    String[] pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
                    if (pinyin != null && pinyin.length > 0) {
                        sb.append(pinyin[0]).append("-"); // 每个字用横杠隔开
                    }
                } else {
                    // 如果是字母或数字直接拼接
                    sb.append(Character.toLowerCase(c));
                }
            }
        } catch (Exception e) {
            return "post-" + System.currentTimeMillis(); // 出错保底
        }

        // 2. 去掉末尾多余的横杠，并加上时间戳后缀确保唯一性（防止 SubUrl 重复导致 Oracle 报错）
        String result = sb.toString().replaceAll("-$", "");
        return result + "-" + (System.currentTimeMillis() % 10000);
    }
}
