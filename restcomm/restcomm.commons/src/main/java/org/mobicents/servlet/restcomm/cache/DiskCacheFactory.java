package org.mobicents.servlet.restcomm.cache;

import org.apache.commons.configuration.Configuration;

public final class DiskCacheFactory {
    private boolean wavNoCache;

    public DiskCacheFactory(Configuration cfg) {
        this.wavNoCache = cfg.subset("runtime-settings").getBoolean("cache-no-wav", false);
    }

    public DiskCache getDiskCache(final String location, final String uri) {
        return new DiskCache(location, uri, true, wavNoCache);
    }

    public boolean isWavNoCache() {
        return wavNoCache;
    }
}
