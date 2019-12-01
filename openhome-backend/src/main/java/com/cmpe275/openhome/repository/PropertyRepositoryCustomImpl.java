package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.payload.SearchRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class PropertyRepositoryCustomImpl implements PropertyRepositoryCustom{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Property> findProperties(SearchRequest searchRequest) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Property> queryObj = cb.createQuery(Property.class);
        Root<Property> from = queryObj.from(Property.class);

        CriteriaQuery<Property> query = queryObj.select(from).
        where(cb.equal(from.get("addressCity"), searchRequest.getCity()));

        System.out.println(query.toString());

        return entityManager.createQuery(query)
                .getResultList();
    }
}
