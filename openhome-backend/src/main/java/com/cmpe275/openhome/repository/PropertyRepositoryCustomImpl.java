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

        CriteriaQuery<Property> query = queryObj.select(from);

        //System.out.println("------"+searchRequest.getCity());

        if(searchRequest.getCity()!=null && !searchRequest.getCity().isEmpty()){
            query.where(cb.and(cb.equal(from.get("addressCity"), searchRequest.getCity())));
        }
        if(searchRequest.getZip()!=null && !searchRequest.getZip().isEmpty()){
            query.where(cb.and(cb.equal(from.get("addressZipcode"), searchRequest.getZip())));
        }
        if(searchRequest.getSharingType()!=null && !searchRequest.getSharingType().isEmpty()){
            query.where(cb.equal(from.get("sharingType"), searchRequest.getSharingType()));
        }
        if(!searchRequest.getPropertyType().equals("Any")){
            if(searchRequest.getPropertyType()=="house") {
                query.where(cb.equal(from.get("propertyType"), "House"));
            }
            else if(searchRequest.getPropertyType()=="townHouse"){
                query.where(cb.equal(from.get("propertyType"), "Townhouse"));
            }
            else if(searchRequest.getPropertyType()=="condoApt"){
                query.where(cb.equal(from.get("propertyType"), "Condo/Apartment"));
            }
        }
        if(searchRequest.getInternet()!=null && !searchRequest.getInternet().isEmpty()){
            if(searchRequest.getInternet().equals("yes")){
                query.where(cb.equal(from.get("wifiAvailability"), "Yes"));
            }
            else {
                query.where(cb.equal(from.get("wifiAvailability"), "No"));
            }
        }
        if(searchRequest.getMinPrice()!=0.0){
            query.where(cb.and(cb.greaterThanOrEqualTo(from.get("weekdayPrice"),searchRequest.getMinPrice())),
                    cb.greaterThanOrEqualTo(from.get("weekendPrice"),searchRequest.getMinPrice()));
        }
        if(searchRequest.getMaxPrice()!=0.0){
            query.where(cb.and(cb.lessThanOrEqualTo(from.get("weekdayPrice"),searchRequest.getMaxPrice())),
                    cb.lessThanOrEqualTo(from.get("weekendPrice"),searchRequest.getMaxPrice()));
        }
        /*if(searchRequest.getDesc()!=null && !searchRequest.getDesc().isEmpty()){
            String[] keywords = searchRequest.getDesc().split(",");
            for(String keyword: keywords){
                query.where(cb.like(from.get("description"),keyword));
            }
        }*/
            System.out.println(query.toString());

        return entityManager.createQuery(query)
                .getResultList();
    }
}
