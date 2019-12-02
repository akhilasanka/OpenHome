package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.payload.SearchRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PropertyRepositoryCustom {

    List<Property> findPropertiesBySearchCriteria(SearchRequest searchRequest, Set<Long> property_ids);
}
