package com.example.robolectric.jetifierissue;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {
    private static final String ACTION_TEST_BROADCAST_RECEIVER = "hello";

    @Test
    public void testLocalBroadcastManager1() throws InterruptedException {
        testLocalBroadcastManager();
    }

    @Test
    public void testLocalBroadcastManager2() throws InterruptedException {
        testLocalBroadcastManager();
    }

    public void testLocalBroadcastManager() throws InterruptedException {
        MyBroadcastReceiver receiver = new MyBroadcastReceiver();
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(RuntimeEnvironment.application);
        broadcastManager.registerReceiver(receiver, new IntentFilter(ACTION_TEST_BROADCAST_RECEIVER));
        broadcastManager.sendBroadcast(new Intent(ACTION_TEST_BROADCAST_RECEIVER));
        receiver.await();
    }

    static class MyBroadcastReceiver extends BroadcastReceiver {
        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onReceive(Context context, Intent intent) {
            assertEquals(context.getApplicationContext(), RuntimeEnvironment.application);
            latch.countDown();
        }

        public void await() throws InterruptedException {
            latch.await(2, TimeUnit.SECONDS);
        }
    }
}
