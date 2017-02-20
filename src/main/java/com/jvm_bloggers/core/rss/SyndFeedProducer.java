package com.jvm_bloggers.core.rss;

import com.google.common.collect.ImmutableMap;
import com.jvm_bloggers.core.data_fetching.http.ProtocolSwitchingAwareConnectionRedirectHandler;
import com.jvm_bloggers.core.utils.Validators;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class SyndFeedProducer {

    public static final String FAKE_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) "
        + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2227.1 Safari/537.36";

    private final ProtocolSwitchingAwareConnectionRedirectHandler redirectHandler =
        new ProtocolSwitchingAwareConnectionRedirectHandler();

    public Optional<SyndFeed> createFor(String rssUrl) {
        URLConnection urlConnection = null;
        try {
            urlConnection = new URL(rssUrl).openConnection();
            final Map<String, String> headers =
                ImmutableMap.of("User-Agent", FAKE_USER_AGENT);
            urlConnection = redirectHandler.handle(urlConnection, headers);
            @Cleanup
            final InputStream inputStream = urlConnection.getInputStream();
            return Optional.of(new SyndFeedInput().build(new XmlReader(inputStream)));
        } catch (IOException | FeedException ex) {
            log.warn("Error during fetching RSS {} url", rssUrl, ex);
            return Optional.empty();
        } finally {
            IOUtils.close(urlConnection);
        }
    }

    public Optional<String> validUrlFromRss(String rss) {
        Optional<String> url = createFor(rss).map(SyndFeed::getLink);
        return url.filter(Validators::isUrlValid);
    }
}
