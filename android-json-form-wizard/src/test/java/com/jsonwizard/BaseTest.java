package com.jsonwizard;

import com.vijay.jsonwizard.BuildConfig;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public abstract class BaseTest {
    protected static final int CONSTANT_INT_50 = 50;
    protected static final int CONSTANT_INT_20 = 20;
    protected static final int CONSTANT_INT_16 = 16;
    protected static final int CONSTANT_INT_0 = 0;
    protected static final int CONSTANT_INT_1 = 1;
    protected static final String DEFAULT_ERROR_MESSAGE = "Default Error Message";
    protected static final String DEFAULT_TEST_MESSAGE = "Native Form Test";
}