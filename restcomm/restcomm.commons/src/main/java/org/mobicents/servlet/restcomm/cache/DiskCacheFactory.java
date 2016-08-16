package org.mobicents.servlet.restcomm.cache;

import org.apache.commons.configuration.Configuration;
import org.mobicents.servlet.restcomm.configuration.RestcommConfiguration;
import org.mobicents.servlet.restcomm.configuration.sets.CacheConfigurationSet;

public final class DiskCacheFactory {
    CacheConfigurationSet cfg;
    private boolean wavNoCache;

    public DiskCacheFactory(Configuration cfg) {
        this.wavNoCache = cfg.subset("runtime-settings").getBoolean("cache-no-wav", false);
    }

    public DiskCacheFactory(RestcommConfiguration cfg) {
        this.cfg = cfg.getCache();

        wavNoCache = this.cfg.isNoWavCache();
    }

    public DiskCache getDiskCache(final String location, final String uri) {
        return new DiskCache(location, uri, false, wavNoCache);
    }

    public DiskCache getDiskCache() {
        return getDiskCache(cfg.getCachePath(), cfg.getCacheUri());
    }
}
