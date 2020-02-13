package android.text;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/13{TIME}
 */
public class TextUtils {
    public static boolean isDigitsOnly(CharSequence str) {
        final int len = str.length();
        for (int cp, i = 0; i < len; i += Character.charCount(cp)) {
            cp = Character.codePointAt(str, i);
            if (!Character.isDigit(cp)) {
                return false;
            }
        }
        return true;
    }
}
