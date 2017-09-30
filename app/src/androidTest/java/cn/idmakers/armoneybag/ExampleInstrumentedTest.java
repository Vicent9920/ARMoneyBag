package cn.idmakers.armoneybag;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Base64;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("cn.idmakers.armoneybag", appContext.getPackageName());
    }

    @Test
    public void testString() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        String str = "30.557728,104.047129";
        String encode = Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
        String decode = new String(Base64.decode(encode, Base64.NO_WRAP));
        Log.e("TAG", "onCreate:"+encode+",decode:"+decode);

    }
}
