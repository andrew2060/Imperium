package net.kingdomsofarden.townships.api.configuration;

import com.google.common.base.Optional;
import net.kingdomsofarden.townships.api.util.StoredDataSection;

public interface Configuration {

    /**
     * @param name The name of the region type to retrieve
     * @return The default configuration of that region, may not be present
     */
    Optional<StoredDataSection> getRegionConfiguration(String name);
}
