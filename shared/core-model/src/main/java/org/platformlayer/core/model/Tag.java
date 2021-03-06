package org.platformlayer.core.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Tag {
    public String key;
    public String value;

    public static final String OWNER_ID = "owner";

    // public static final String PLATFORM_LAYER_ID = "platformlayerid";

    public static final String PARENT = "parent";
    // public static final String RELATED = "linked";
    public static final String ASSIGNED = "assigned";

    public static final String IMAGE_ID = "imageid";

    public static final String INSTANCE_KEY = "instancekey";
    public static final String NETWORK_ADDRESS = "net.address";

    public static final String HOST_POLICY = "host.policy";
    public static final String PUBLIC_ENDPOINT = "public-endpoint";
    public static final String UNIQUE_ID = "uniqueid";

    public static final String IMAGE_OS_DISTRIBUTION = "platformlayer.org__os.distribution";
    public static final String IMAGE_OS_VERSION = "platformlayer.org__os.version";
    public static final String IMAGE_TYPE = "platformlayer.org__type";

    public Tag() {
    }

    public Tag(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tag other = (Tag) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Tag [" + key + "=" + value + "]";
    }

    public static Tag buildParentTag(PlatformLayerKey key) {
        return buildTag(Tag.PARENT, key);
    }

    public static Tag buildTag(String tagKey, PlatformLayerKey itemKey) {
        return new Tag(tagKey, itemKey.getUrl());
    }

}
