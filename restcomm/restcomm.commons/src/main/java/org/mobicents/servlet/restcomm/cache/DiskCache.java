/*
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2014, Telestax Inc and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 */
package org.mobicents.servlet.restcomm.cache;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.mobicents.servlet.restcomm.configuration.RestcommConfiguration;
import org.mobicents.servlet.restcomm.http.CustomHttpClientBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
public final class DiskCache extends UntypedActor {
    // Logger.
    private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    private final String location;
    private final String uri;

    // flag for cache disabling in *.wav files usage case
    private boolean wavNoCache = false;

    public DiskCache(final String location, final String uri, final boolean create, final boolean wavNoCache) {
        super();

        this.wavNoCache = wavNoCache;

        // Format the cache path.
        String temp = location;
        if (!temp.endsWith("/")) {
            temp += "/";
        }
        // Create the cache path if specified.
        final File path = new File(temp);
        //        if (create) {
        //            path.mkdirs();
        //        }

        // Make sure the cache path exists and is a directory.
        if (!path.exists() || !path.isDirectory()) {
            //            throw new IllegalArgumentException(location + " is not a valid cache location.");
            path.mkdirs();
        }
        // Format the cache URI.
        this.location = temp;
        temp = uri;
        if (!temp.endsWith("/")) {
            temp += "/";
        }
        this.uri = temp;
    }

    public DiskCache(final String location, final String uri, final boolean create) {
        this(location, uri, create, false);
    }

    public DiskCache(final String location, final String uri) {
        this(location, uri, false);
    }

    private URI cache(final Object message) throws IOException, URISyntaxException {
        final DiskCacheRequest request = (DiskCacheRequest) message;

        if (request.hash() == null) {
            if (request.uri().getScheme().equalsIgnoreCase("file")) {
                File origFile = new File(request.uri());
                File destFile = new File(location + origFile.getName());
                if (!destFile.exists())
                    FileUtils.moveFile(origFile, destFile);

                return URI.create(this.uri + destFile.getName());

            } else {
                //Handle all the rest
                // This is a request to cache a URI
                String hash = null;
                URI uri = null;
                if (request.uri().toString().contains("hash")) {
                    String fragment = request.uri().getFragment();
                    hash = fragment.replace("hash=", "");
                    String uriStr = ((request.uri().toString()).replace(fragment, "")).replace("#", "");
                    uri = URI.create(uriStr);
                } else {
                    uri = request.uri();
                    hash = new Sha256Hash(uri.toString()).toHex();
                }

                final String extension = extension(uri).toLowerCase();
                final File path = new File(location + hash + "." + extension);
                if (!path.exists()) {
                    final File tmp = new File(path + "." + "tmp");
                    InputStream input = null;
                    OutputStream output = null;
                    HttpClient client = null;
                    HttpResponse httpResponse = null;
                    try {
                        if (request.uri().getScheme().equalsIgnoreCase("https")) {
                            //Handle the HTTPS URIs
                            client = CustomHttpClientBuilder.build(RestcommConfiguration.getInstance().getMain());
                            URI result = new URIBuilder()
                                    .setScheme(uri.getScheme())
                                    .setHost(uri.getHost())
                                    .setPort(uri.getPort())
                                    .setPath(uri.getPath())
                                    .build();

                            HttpGet httpRequest = new HttpGet(result);
                            httpResponse = client.execute((HttpUriRequest) httpRequest);
                            int code = httpResponse.getStatusLine().getStatusCode();

                            if (code >= 400) {
                                String requestUrl = httpRequest.getRequestLine().getUri();
                                String errorReason = httpResponse.getStatusLine().getReasonPhrase();
                                String httpErrorMessage = String.format(
                                        "Error while fetching http resource: %s \n Http error code: %d \n Http error message: %s", requestUrl,
                                        code, errorReason);
                                logger.warning(httpErrorMessage);
                            }
                            input = httpResponse.getEntity().getContent();
                        } else {
                            input = uri.toURL().openStream();
                        }
                        output = new FileOutputStream(tmp);
                        final byte[] buffer = new byte[4096];
                        int read = 0;
                        do {
                            read = input.read(buffer, 0, 4096);
                            if (read > 0) {
                                output.write(buffer, 0, read);
                            }
                        } while (read != -1);
                        tmp.renameTo(path);
                    } finally {
                        if (input != null) {
                            input.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                        if (httpResponse != null) {
                            ((CloseableHttpResponse) httpResponse).close();
                            httpResponse = null;
                        }
                        if (client != null) {
                            HttpClientUtils.closeQuietly(client);
                            client = null;
                        }
                    }
                }
                URI result = URI.create(this.uri+ hash + "." + extension);
                return result;
            }
        } else {
            // This is a check cache request
            final String extension = "wav";
            final String hash = request.hash();
            final String filename = hash + "." + extension;
            Path p = Paths.get(location+filename);

            if (Files.exists(p)) {
                // return URI.create(matchedFile.getAbsolutePath());
                return URI.create(this.uri + filename);
            } else {
                throw new FileNotFoundException(filename);
            }
        }
    }

    private String extension(final URI uri) {
        final String path = uri.getPath();
        return path.substring(path.lastIndexOf(".") + 1);
    }

    private DiskCacheResponse getResponse(final Object message) throws Exception {
        if (wavNoCache) {
            URI uri = ((DiskCacheRequest) message).uri();
            String path = uri.getPath();

            if(!("file".equalsIgnoreCase(uri.getScheme()) && path.endsWith(".wav"))) {
                return new DiskCacheResponse(uri);
            }
        }

        DiskCacheResponse response;
        try {
            response = new DiskCacheResponse(cache(message));
        } catch (final Exception exception) {
            logger.error("Error while caching", exception);
            response = new DiskCacheResponse(exception);
        }

        return response;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        final Class<?> klass = message.getClass();
        final ActorRef self = self();
        final ActorRef sender = sender();
        if (DiskCacheRequest.class.equals(klass)) {
            sender.tell(getResponse(message), self);
        }
    }
}
