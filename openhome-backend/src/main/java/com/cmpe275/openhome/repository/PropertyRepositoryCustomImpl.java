package com.cmpe275.openhome.repository;

import com.cmpe275.openhome.model.Property;
import com.cmpe275.openhome.payload.SearchRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
public class PropertyRepositoryCustomImpl implements PropertyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Property> findPropertiesBySearchCriteria(SearchRequest searchRequest, Set<Long> reserved_property_ids) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Property> cq = cb.createQuery(Property.class);
        Root<Property> propertyTable = cq.from(Property.class);

        List<Predicate> predicates = new ArrayList<Predicate>();

        if (searchRequest.getCity() != null && !searchRequest.getCity().isEmpty()) {
            predicates.add(cb.equal(propertyTable.get("addressCity"), searchRequest.getCity()));
        }
        if (searchRequest.getZip() != null && !searchRequest.getZip().isEmpty()) {
            predicates.add(cb.equal(propertyTable.get("addressZipcode"), searchRequest.getZip()));
        }
        if (searchRequest.getSharingType() != null && !searchRequest.getSharingType().isEmpty()) {
            predicates.add(cb.equal(propertyTable.get("sharingType"), searchRequest.getSharingType()));
        }

        System.out.println("Property type:"+searchRequest.getPropertyType());
        if (!searchRequest.getPropertyType().equals("Any")) {
            if (searchRequest.getPropertyType().equals("house")) {
                predicates.add(cb.equal(propertyTable.get("propertyType"), "House"));
            } else if (searchRequest.getPropertyType().equals("townHouse")) {
                predicates.add(cb.equal(propertyTable.get("propertyType"), "Townhouse"));
            } else if (searchRequest.getPropertyType().equals("condoApt")) {
                predicates.add(cb.equal(propertyTable.get("propertyType"), "Condo/Apartment"));
            }
        }

        if (searchRequest.getInternet() != null && !searchRequest.getInternet().isEmpty()) {
            if (searchRequest.getInternet().equals("yes")) {
                predicates.add(cb.equal(propertyTable.get("wifiAvailability"), "Yes"));
            } else {
                predicates.add(cb.equal(propertyTable.get("wifiAvailability"), "No"));
            }
        }


        if (searchRequest.getMinPrice() != 0.0 && searchRequest.getMaxPrice() != 0.0) {

            Predicate weekday_price_predicate = cb.and(cb.lessThanOrEqualTo(propertyTable.get("weekdayPrice"), searchRequest.getMaxPrice()),
                    cb.greaterThanOrEqualTo(propertyTable.get("weekdayPrice"), searchRequest.getMinPrice()));

            Predicate weekend_price_predicate = cb.and(cb.lessThanOrEqualTo(propertyTable.get("weekendPrice"), searchRequest.getMaxPrice()),
                    cb.greaterThanOrEqualTo(propertyTable.get("weekendPrice"), searchRequest.getMinPrice()));

            Predicate price_predicate = cb.or(weekday_price_predicate, weekend_price_predicate);

            predicates.add(price_predicate);

        } else if (searchRequest.getMinPrice() != 0.0) {
            predicates.add(cb.greaterThanOrEqualTo(propertyTable.get("weekdayPrice"), searchRequest.getMinPrice()));
            predicates.add(cb.greaterThanOrEqualTo(propertyTable.get("weekendPrice"), searchRequest.getMinPrice()));
        } else if (searchRequest.getMaxPrice() != 0.0) {
            predicates.add(cb.lessThanOrEqualTo(propertyTable.get("weekdayPrice"), searchRequest.getMaxPrice()));
            predicates.add(cb.lessThanOrEqualTo(propertyTable.get("weekendPrice"), searchRequest.getMaxPrice()));
        }

        if (searchRequest.getDesc() != null && !searchRequest.getDesc().isEmpty()) {
            String[] keywords = searchRequest.getDesc().split(",");

            List<Predicate> keyword_predicates = new ArrayList<Predicate>();

            for (String keyword : keywords) {
                keyword_predicates.add(cb.like(propertyTable.get("description"), "%"+keyword+"%"));
            }
            predicates.add(cb.or(keyword_predicates.toArray(new Predicate[]{})));
        }

        predicates.add(cb.equal(propertyTable.get("isDeleted"), false));
        if(reserved_property_ids.size()>0){
            predicates.add(cb.not(propertyTable.get("id").in(reserved_property_ids)));
        }


        System.out.println(cb.toString());

        cq.select(propertyTable)
                .where(predicates.toArray(new Predicate[]{}));

        return entityManager.createQuery(cq)
                .getResultList();
    }
}
