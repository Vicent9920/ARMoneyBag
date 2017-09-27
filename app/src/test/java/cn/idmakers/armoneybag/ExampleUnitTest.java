package cn.idmakers.armoneybag;

import org.junit.Test;

import static org.junit.Assert.*;

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
       StringBuilder str = new StringBuilder();
        str.append("3.14.");
        if(str.indexOf(".")>1){
            str.delete(str.length()-2,str.length()-1);
        }
        System.out.println(str.toString());
    }
}