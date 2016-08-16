package org.mobicents.servlet.restcomm.cache;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActorFactory;
import akka.testkit.TestActorRef;
import org.apache.commons.io.FileUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.net.URI;

/**
 * Created by gdubina on 16.08.2016.
 */
public class WavNoCaсheTest {

    private URI externalUrl = URI.create("http://external/file.wav");
    private String cacheUri = "http://127.0.0.1";
    private String cacheDir = "/tmp";
    private URI expectedLocal = URI.create(cacheUri + "/1.wav");

    private FileDownloader downloader;
    private ActorSystem system;

    @Before
    public void before() throws Exception {
        downloader = Mockito.mock(FileDownloader.class);
        system = ActorSystem.create();

        //clean tmp dir
        String hash = new Sha256Hash(externalUrl.toString()).toHex();
        final File resultFile = new File(cacheDir + "/" + hash + ".wav");
        if (resultFile.exists()) {
            resultFile.delete();
        }

        Mockito.when(downloader.download(Mockito.any(URI.class), Mockito.any(File.class))).then(new Answer<URI>() {
            @Override
            public URI answer(InvocationOnMock invocationOnMock) throws Throwable {
                FileUtils.copyFile(new File("src/test/resources/restcomm.xml"), resultFile);
                return expectedLocal;
            }
        });
    }

    @After
    public void after() throws Exception {
        system.shutdown();
    }

    private DiskCache cache(final boolean cacheNo) {
        final TestActorRef<DiskCache> ref = TestActorRef.create(system, new Props(new UntypedActorFactory() {

            @Override
            public Actor create() throws Exception {
                return new DiskCache(downloader, cacheDir, cacheUri, true, cacheNo);
            }
        }), "test");
        return ref.underlyingActor();
    }

    @Test
    public void testWavCached() throws Exception {
        DiskCache cache = cache(false);
        URI response1 = cache.cache(new DiskCacheRequest(externalUrl));
        Assert.assertEquals(expectedLocal, response1);

        URI response2 = cache.cache(new DiskCacheRequest(externalUrl));
        Assert.assertEquals(expectedLocal, response2);

        Mockito.verify(downloader, Mockito.times(1)).download(Mockito.any(URI.class), Mockito.any(File.class));
    }

    @Test
    public void testWavNoCached() throws Exception {
        DiskCache cache = cache(true);
        URI response1 = cache.cache(new DiskCacheRequest(externalUrl));
        Assert.assertEquals(externalUrl, response1);

        URI response2 = cache.cache(new DiskCacheRequest(externalUrl));
        Assert.assertEquals(externalUrl, response2);

        Mockito.verify(downloader, Mockito.never()).download(Mockito.any(URI.class), Mockito.any(File.class));
    }

}
