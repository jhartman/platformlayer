package org.platformlayer.ops.helpers;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.platformlayer.Filter;
import org.platformlayer.core.model.InstanceBase;
import org.platformlayer.core.model.ItemBase;
import org.platformlayer.core.model.PlatformLayerKey;
import org.platformlayer.core.model.Tag;
import org.platformlayer.core.model.Tags;
import org.platformlayer.ids.ServiceType;
import org.platformlayer.ops.CloudContext;
import org.platformlayer.ops.Machine;
import org.platformlayer.ops.OpsException;
import org.platformlayer.ops.OpsSystem;
import org.platformlayer.ops.OpsTarget;
import org.platformlayer.ops.machines.PlatformLayerCloudHelpers;
import org.platformlayer.ops.machines.PlatformLayerHelpers;
import org.platformlayer.ops.machines.ServiceProviderHelpers;
import org.platformlayer.service.instancesupervisor.v1.PersistentInstance;

public class InstanceHelpers {
    static final Logger log = Logger.getLogger(InstanceHelpers.class);

    @Inject
    CloudContext cloud;

    @Inject
    PlatformLayerHelpers platformLayer;

    @Inject
    OpsSystem ops;

    @Inject
    ServiceContext service;

    @Inject
    PlatformLayerCloudHelpers cloudHelpers;

    public InstanceBase findInstance(Tags tags, PlatformLayerKey modelKey) throws OpsException {
        // We have to connect to the underlying machine not-via-DNS for Dns service => use instance id
        // TODO: Should we always use the instance id??
        {
            String instanceKey = tags.findUnique(Tag.INSTANCE_KEY);

            if (instanceKey != null) {
                InstanceBase instance = cloud.findInstanceByInstanceKey(PlatformLayerKey.parse(instanceKey));
                return instance;
            }
        }

        {
            // TODO: Do we have to skip this if we've been passed a PersistentInstances?

            // String conductorId = ops.buildUrl(modelKey);

            Tag parentTag = Tag.buildParentTag(modelKey);

            // // TODO: Fix this so that we don't get everything...
            // for (PersistentInstance persistentInstance : platformLayer.listItems(PersistentInstance.class)) {
            // String systemId = persistentInstance.getTags().findUnique(Tag.PARENT_ID);
            // if (Objects.equal(conductorId, systemId)) {
            // String instanceKey = persistentInstance.getTags().findUnique(Tag.INSTANCE_KEY);
            // if (instanceKey != null) {
            // return cloud.findMachineByInstanceKey(instanceKey);
            // }
            // }
            // }

            for (PersistentInstance persistentInstance : platformLayer.listItems(PersistentInstance.class, Filter.byTag(parentTag))) {
                String instanceKey = persistentInstance.getTags().findUnique(Tag.INSTANCE_KEY);
                if (instanceKey != null) {
                    return cloud.findInstanceByInstanceKey(PlatformLayerKey.parse(instanceKey));
                }
            }

        }

        return null;
    }

    public InstanceBase findInstance(ItemBase item) throws OpsException {
        Tags tags = item.getTags();
        PlatformLayerKey platformLayerKey = OpsSystem.toKey(item);

        return findInstance(tags, platformLayerKey);
    }

    public Machine findMachine(ItemBase item) throws OpsException {
        InstanceBase instance = findInstance(item);
        if (instance == null) {
            return null;
        }

        return cloudHelpers.toMachine(instance);
    }

    public Machine getMachine(ItemBase item) throws OpsException {
        Machine machine = findMachine(item);
        if (machine == null) {
            throw new OpsException("Could not determine instance for: " + item);
        }
        return machine;
    }

    @Inject
    SshKeys sshKeys;

    @Inject
    ServiceProviderHelpers serviceProviders;

    public OpsTarget getTarget(ItemBase item) throws OpsException {
        Machine machine = getMachine(item);
        return getTarget(item, machine);
    }

    public OpsTarget getTarget(ItemBase item, Machine machine) throws OpsException {
        ServiceType serviceType = serviceProviders.getServiceType(item.getClass());

        // TODO: This is so evil...
        SshKey sshKey = sshKeys.findOtherServiceKey(serviceType);

        return machine.getTarget(sshKey);
    }

}
