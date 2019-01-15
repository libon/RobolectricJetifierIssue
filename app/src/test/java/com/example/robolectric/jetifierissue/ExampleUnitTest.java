package com.example.robolectric.jetifierissue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.support.v4.Shadows;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    @Test
    public void exampleTest() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().resume().visible().get();
        SwipeRefreshLayout swipeRefreshLayout = activity.findViewById(R.id.swipe_refresh_layout);
        Shadows.shadowOf(swipeRefreshLayout);
    }
}
