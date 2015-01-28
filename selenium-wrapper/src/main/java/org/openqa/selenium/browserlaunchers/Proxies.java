package org.openqa.selenium.browserlaunchers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;

/**
 * This is a hack to temporarily fix an issue with the PhantomJS Driver that
 * depended on a deprecated class that was removed in a recent selenium version.
 *
 * See https://github.com/detro/ghostdriver/issues/397
 * and
 * https://github.com/detro/ghostdriver/pull/399
 */
public class Proxies {
    public static Proxy extractProxy(Capabilities capabilities) {
        return Proxy.extractFrom(capabilities);
    }
}
