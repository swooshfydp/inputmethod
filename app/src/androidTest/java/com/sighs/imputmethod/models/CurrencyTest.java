package com.sighs.imputmethod.models;

import android.content.Context;
import android.test.AndroidTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by stuart on 2/14/17.
 */
public class CurrencyTest extends AndroidTestCase {
    Context context;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
        assertNotNull(context);
    }

    @Test
    public void TestLoadFromJsonOneInstance() throws Exception {
        Currency[] testSet = Currency.loadFromJson("test1.json", context);
        assertNotNull(testSet);
        assertEquals(testSet.length, 1);
        assertEquals(testSet[0].getId(), "FiveHundred");
        assertEquals(testSet[0].getValue(), 500.0f);
        assertNotNull(testSet[0].getImageSrc(1));
        assertNotNull(testSet[0].getImageSrc(5));
        assertEquals(testSet[0].getBaseImage(), testSet[0].getImageSrc(1));
    }

    @Test
    public void TestLoadFromJsonZeroInstances() throws Exception {
        Currency[] testSet = Currency.loadFromJson("test2.json", context);
        assertNotNull(testSet);
        assertEquals(testSet.length, 0);
    }

    @Test
    public void TestLoadFromJsonNoFile() throws Exception {
        Currency[] testSet = Currency.loadFromJson("", context);
        assertNull(testSet);
    }
}