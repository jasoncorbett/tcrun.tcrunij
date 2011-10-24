package org.tcrun.flowbee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Calendar;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.tcrun.selenium.SelfAwarePage;
import org.tcrun.selenium.WebDriverWrapper;

/**
 *
 * @author slambson
 */
public class FlowBee {

    private WebDriverWrapper m_browser;
    private static XLogger logger = XLoggerFactory.getXLogger("test." + FlowBee.class.getName());

    public FlowBee(WebDriverWrapper p_browser) {
        m_browser = p_browser;
    }

    public <CONTEXT> ArrayList<String> Flow(CONTEXT context, SelfAwarePage endPage, ArrayList<SelfAwarePage> flowPageList) throws Exception {
        HashMap pageFoundCount = new HashMap();
        int sleep = 1000; // 1 second
        int MAX_WAIT_FOR_KNOWN_PAGE = 60; // seconds
        int MAX_TIMES_PAGE_FIND_IS_ALLOWED = 5;
        Calendar endTime = Calendar.getInstance();
        int flowPageCount = 0;
        NullPage nullPage = new NullPage();
        SelfAwarePage currentPage = nullPage;
        ArrayList<String> pageList = new ArrayList<String>();
        Iterator it = flowPageList.iterator();
        // setting up the end time for the first page
        endTime.add(Calendar.SECOND, MAX_WAIT_FOR_KNOWN_PAGE);
        while (it.hasNext()) {
            SelfAwarePage page = (SelfAwarePage) it.next();
            pageFoundCount.put(page, 0);
        }

        while (currentPage.getClass() != endPage.getClass()) {
            if (Calendar.getInstance().after(endTime)) {
                logger.error("Current page URL: {}", m_browser.getPageUrl());
                logger.error("Current page title: {}", m_browser.getPageTitle());
                m_browser.saveHTMLSource("flowbee_known_page_timeout");
                m_browser.takeScreenShot("flowbee_known_page_timeout");
                throw new KnownPageTimeoutException("Timed out waiting for a known page");
            }
            currentPage = GetCurrentPage(flowPageList);
            if (currentPage != null) {
                // removing then readding the page to optimize the searching of pages
                flowPageList.remove(currentPage);
                flowPageList.add(currentPage);
                flowPageCount = flowPageCount + 1;
                logger.debug("Current Page: " + currentPage.getClass().toString());
                pageFoundCount.put(currentPage, (Integer) pageFoundCount.get(currentPage) + 1);
                pageList.add(currentPage.getClass().toString());
                if ((Integer) pageFoundCount.get(currentPage) >= MAX_TIMES_PAGE_FIND_IS_ALLOWED) {
                    throw new PageFoundTooManyTimesException("Found page too many times");
                }
                if (currentPage.getClass() != endPage.getClass()) {
                    try {
                        currentPage.handlePage(m_browser, context);
                    } catch (KnownErrorPageFoundException e) {
                        throw new KnownErrorPageFoundException(e.getMessage());
                    } catch (NullPointerException e) {
                        logger.info("Got a Null Pointer Exception, usually this means a frame we were in no longer exists");
                        logger.info("Attempting to get back to a known place");
                        try {
                            Thread.sleep(1000);
                            m_browser.getDriver().switchTo().activeElement();
                        } catch (Exception ef) {
                            try {
                                Thread.sleep(1000);
                                m_browser.getDriver().switchTo().defaultContent();
                            } catch (Exception eg) {
                            }
                        }
                    } catch (Exception e) {
                        logger.info("Got an exception calling handlePage for page {}, here is the exception {}", currentPage.getClass().toString(), e.getMessage());
                    }
                    // resetting end time for timeout of next page
                    endTime = Calendar.getInstance();
                    endTime.add(Calendar.SECOND, MAX_WAIT_FOR_KNOWN_PAGE);
                }
            } else {
                currentPage = nullPage;
                // Did not find a known page, we do not want to wait forever for a known page though
                try {
                    Thread.sleep(sleep);
                } catch (Exception ie) {
                }
            }
        }
        logger.debug("Pages Found to complete flow: " + flowPageCount);
        return pageList;
    }

    private SelfAwarePage GetCurrentPage(ArrayList<SelfAwarePage> flowPageList) {
        SelfAwarePage currentPage = null;
        Iterator it = flowPageList.iterator();
        while (it.hasNext()) {
            SelfAwarePage page = (SelfAwarePage) it.next();
            try {
                if (page.isCurrentPage(m_browser) == true) {
                    currentPage = page;
                    break;
                }
            } catch (NullPointerException e) {
                logger.info("Got a Null Pointer Exception, usually this means a frame we were in no longer exists");
                logger.info("Attempting to get back to a known place");
                try {
                    Thread.sleep(1000);
                    m_browser.getDriver().switchTo().activeElement();
                } catch (Exception ef) {
                    try {
                        Thread.sleep(1000);
                        m_browser.getDriver().switchTo().defaultContent();
                    } catch (Exception eg) {
                    }
                }
            } catch (Exception e) {
                logger.info("Got an exception calling isCurrentPage for page {}, here is the exception {}", page.getClass().toString(), e.getMessage());
            }
        }

        return currentPage;
    }
}

class NullPage implements SelfAwarePage<Customer> {

    @Override
    public boolean isCurrentPage(WebDriverWrapper browser) {
        return false;
    }

    @Override
    public void handlePage(WebDriverWrapper browser, Customer context) {
        // nothing to do here
    }
}
