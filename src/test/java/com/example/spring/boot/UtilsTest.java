package com.example.spring.boot;

import org.junit.Test;

import java.io.UncheckedIOException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class UtilsTest {

    @Test
    public void userCanReadStringFromResourceFile() {
        assertThat(Utils.readString("FileWithTextForUtilsTest.txt"),
                equalTo("Text from file to test Utils"));
    }

    @Test(expected= UncheckedIOException.class)
    public void userShouldGetUncheckedExceptionIfResourceIsAbsent() {
        Utils.readString("UNKNOWN_RESOURCE_NAME");
    }
}
