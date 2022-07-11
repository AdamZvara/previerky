package com.example.tomas.strelby;

import org.junit.Assert;
import org.junit.Test;

import static com.example.tomas.common.Utilities.splitName;

/**
 * Created by tomas on 9/3/17.
 */

public class UtilitiesTest {
    @Test
    public void splitNameTest() throws Exception
    {
        String[] expected1 = {"Tomas","Zvara"} ;
        String[] expected2 = {"Tomas",""} ;
        Assert.assertArrayEquals(expected1, splitName("Tomas Zvara"));
        Assert.assertArrayEquals(expected2, splitName("Tomas"));
    }
}
