package com.hartwig.healthchecks.common.predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.function.Predicate;

import org.junit.Test;

public class VCFHeaderLinePredicateTest {

    @Test
    public void headerLinePasses() {
        Predicate<String> predicate = new VCFHeaderLinePredicate();
        assertTrue(predicate.test(VCFTestConstants.HEADER_LINE));
    }

    @Test
    public void commentLineFails() {
        Predicate<String> predicate = new VCFHeaderLinePredicate();
        assertFalse(predicate.test(VCFTestConstants.COMMENT_LINE));
    }

    @Test
    public void passDataLine1Fails() {
        Predicate<String> predicate = new VCFHeaderLinePredicate();
        assertFalse(predicate.test(VCFTestConstants.PASS_DATA_LINE_1));
    }

    @Test
    public void passDataLine2Fails() {
        Predicate<String> predicate = new VCFHeaderLinePredicate();
        assertFalse(predicate.test(VCFTestConstants.PASS_DATA_LINE_2));
    }

    @Test
    public void filteredDataLineFails() {
        Predicate<String> predicate = new VCFHeaderLinePredicate();
        assertFalse(predicate.test(VCFTestConstants.FILTERED_DATA_LINE));
    }
}
