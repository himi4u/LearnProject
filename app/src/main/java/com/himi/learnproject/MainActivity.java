package com.himi.learnproject;


import android.app.Activity;
import android.os.Bundle;

import com.himi.learnproject.hotfix.BugTest;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BugTest bugTest = new BugTest();
        bugTest.testMethod();
    }
}
