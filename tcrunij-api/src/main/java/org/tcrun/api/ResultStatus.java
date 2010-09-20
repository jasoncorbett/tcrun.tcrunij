package org.tcrun.api;

/**
 * The actual result of the test case: pass, fail, error, or skipped.
 *
 * @author jcorbett
 */
public enum ResultStatus
{
    PASS,
    FAIL,
    BROKEN_TEST,
    NOT_TESTED,
    SKIPPED
}
