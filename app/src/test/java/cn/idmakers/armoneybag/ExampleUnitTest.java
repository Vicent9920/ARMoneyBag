package cn.idmakers.armoneybag;

import org.junit.Test;

import cn.idmakers.armoneybag.scan.utils.DesUtils;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testString() throws Exception {
      String str = "104.047968,30.556863";
        str = DesUtils.strToUnicode(str);
        System.out.println("加密后："+str);
        str = DesUtils.unicodeToString(str);
        System.out.println("解密后："+str);
    }
}