package com.bezkoder.spring.jpa.h2.service;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.bezkoder.spring.jpa.h2.model.LocalAreaRegister;
import com.bezkoder.spring.jpa.h2.model.UnitRegister;
import com.bezkoder.spring.jpa.h2.repository.UnitRegisterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitRegisterService {

    private final UnitRegisterRepository repository;

    public List<UnitRegister> findAllByServiceCodeAndCountryCode(
        String serviceCode, String countryCode) {

        return repository.findAll(new Specification<UnitRegister>() {
            @Override
            public Predicate toPredicate(Root<UnitRegister> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                Subquery<LocalAreaRegister> subquery = query.subquery(LocalAreaRegister.class);
                Root<LocalAreaRegister> subqueryRoot = subquery.from(LocalAreaRegister.class);
                subquery.select(subqueryRoot);
                Predicate unitNrPredicate = builder.equal(subqueryRoot.get("id").get("unitNr"), root.<String> get(
                    "unitNr"));
                Predicate servicePredicate = builder.equal(subqueryRoot.get("id").get("serviceCode"), serviceCode);
                Predicate countryPredicate = builder.equal(subqueryRoot.get("id").get("countryCode"), countryCode);
                subquery.select(subqueryRoot).where(unitNrPredicate, servicePredicate, countryPredicate);
                return builder.exists(subquery);
            }
        });
    }
}

/* Generated Dynamic query
select unitregist0_.unit_nr as unit_nr1_2_ 
from unit_register unitregist0_ 
where exists (
				select localarear1_.country_code, localarear1_.service_code, localarear1_.unit_nr 
				from local_area_register localarear1_ 
				where localarear1_.unit_nr=unitregist0_.unit_nr 
				and localarear1_.service_code=? 
				and localarear1_.country_code=?
			)
*/
