package org.mobicents.servlet.restcomm.cache;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.mobicents.servlet.restcomm.configuration.RestcommConfiguration;
import org.mobicents.servlet.restcomm.configuration.sets.CacheConfigurationSet;
import scala.concurrent.duration.FiniteDuration;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.JavaTestKit;

@Ignore
public final class WavNoCacheTest {
    private static final String TTS_FILE_NAME = "tts-message.wav";

    private ActorSystem system;
    private ActorRef cache;

    private CacheConfigurationSet cacheCfg;

    private URLStreamHandler handler;
    private URLConnection mockConnection;

    public WavNoCacheTest() {
        super();
    }

    @Before
    public void before() throws Exception {
        RestcommConfiguration restcommCfg = createCfg("/restcomm-cache-no-wav.xml");
        cacheCfg = restcommCfg.getCache();

        system = ActorSystem.create();
        cache = cache(restcommCfg);
    }

    @After
    public void after() throws Exception {
        system.shutdown();
    }

    private RestcommConfiguration createCfg(final String cfgFileName) throws ConfigurationException,
                                                                             MalformedURLException {
        URL url = this.getClass().getResource(cfgFileName);

        Configuration xml = new XMLConfiguration(url);
        return new RestcommConfiguration(xml);
    }

    private ActorRef cache(final RestcommConfiguration cfg) {
        return system.actorOf(new Props(new UntypedActorFactory() {
            private static final long serialVersionUID = 1L;

            @Override
            public Actor create() throws Exception {
                return new DiskCacheFactory(cfg).getDiskCache();
            }
        }));
    }

    @Test
    public void checkTTSWavFileCaching() throws URISyntaxException, IOException {
        URL resource = WavNoCacheTest.class.getResource("/wav" + File.separator + TTS_FILE_NAME);
        final String pathToResource = new File(resource.toURI()).getAbsolutePath();

        final String fileUri = "file:" + pathToResource;

        new JavaTestKit(system) {
            {
                final ActorRef observer = getRef();
                cache.tell(new DiskCacheRequest(URI.create(fileUri)), observer);

                final DiskCacheResponse response = this.expectMsgClass(FiniteDuration.create(30, TimeUnit.SECONDS),
                                                                       DiskCacheResponse.class);
                assertTrue(response.succeeded());

                final File file = new File(cacheCfg.getCachePath() + File.separator + TTS_FILE_NAME);
                assertTrue(file.exists());
                assertTrue(file.length() > 0);

                final URI result = response.get();
                final URI cacheUri = URI.create(cacheCfg.getCacheUri() + File.separator + TTS_FILE_NAME);
                assertTrue(result.equals(cacheUri));

                if (file.exists() && (file.length() > 0)) {
                    FileUtils.moveFile(file, new File(pathToResource));
                }
            }
        };
    }

    @Test
    public void checkExternalWavFileCaching() throws Exception{

    }
}
