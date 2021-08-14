package com.liux.android.test;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;

public class MavenDependencyRobolectricTestRunner extends RobolectricTestRunner {

    static {
        System.setProperty("robolectric.dependency.repo.id", "aliyun");
        System.setProperty("robolectric.dependency.repo.url", "https://maven.aliyun.com/repository/public/");
        System.setProperty("robolectric.dependency.repo.username", "");
        System.setProperty("robolectric.dependency.repo.password", "");
    }

    public MavenDependencyRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }
}
