package com.sap.olingo.jpa.processor.core.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.apache.olingo.commons.api.ex.ODataException;
import org.apache.olingo.server.api.ODataApplicationException;
import org.junit.Before;
import org.junit.Test;

import com.sap.olingo.jpa.metadata.api.JPAEdmProvider;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAAssociationAttribute;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAAttribute;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAEntityType;
import com.sap.olingo.jpa.metadata.core.edm.mapper.api.JPAPath;
import com.sap.olingo.jpa.metadata.core.edm.mapper.exception.ODataJPAModelException;
import com.sap.olingo.jpa.processor.core.api.JPAODataContextAccessDouble;
import com.sap.olingo.jpa.processor.core.api.JPAODataSessionContextAccess;
import com.sap.olingo.jpa.processor.core.testmodel.Organization;
import com.sap.olingo.jpa.processor.core.util.EdmEntitySetDouble;
import com.sap.olingo.jpa.processor.core.util.TestBase;
import com.sap.olingo.jpa.processor.core.util.TestHelper;

public class TestJPAQueryFromClause extends TestBase {
  private JPAExecutableQuery cut;
  private JPAEntityType jpaEntityType;

  @Before
  public void setup() throws ODataException {
    helper = new TestHelper(emf, PUNIT_NAME);
    jpaEntityType = helper.getJPAEntityType("Organizations");
    JPAODataSessionContextAccess context = new JPAODataContextAccessDouble(new JPAEdmProvider(PUNIT_NAME, emf, null,
        TestBase.enumPackages), ds);
    createHeaders();
    cut = new JPAQuery(null, new EdmEntitySetDouble(nameBuilder, "Organizations"), context, null, emf
        .createEntityManager(), headers);
  }

  @Test
  public void checkFromListContainsRoot() throws ODataApplicationException {
    Map<String, From<?, ?>> act = cut.createFromClause(new ArrayList<JPAAssociationAttribute>(),
        new ArrayList<JPAPath>());
    assertNotNull(act.get(jpaEntityType.getInternalName()));
  }

  @Test
  public void checkFromListOrderByContainsOne() throws ODataJPAModelException, ODataApplicationException {
    List<JPAAssociationAttribute> orderBy = new ArrayList<>();
    JPAAttribute exp = helper.getJPAAssociation("Organizations", "roles");
    orderBy.add((JPAAssociationAttribute) exp);

    Map<String, From<?, ?>> act = cut.createFromClause(orderBy, new ArrayList<JPAPath>());
    assertNotNull(act.get(exp.getInternalName()));
  }

  @Test
  public void checkFromListOrderByOuterJoinOne() throws ODataJPAModelException, ODataApplicationException {
    List<JPAAssociationAttribute> orderBy = new ArrayList<>();
    JPAAttribute exp = helper.getJPAAssociation("Organizations", "roles");
    orderBy.add((JPAAssociationAttribute) exp);

    Map<String, From<?, ?>> act = cut.createFromClause(orderBy, new ArrayList<JPAPath>());

    @SuppressWarnings("unchecked")
    Root<Organization> root = (Root<Organization>) act.get(jpaEntityType.getInternalName());
    Set<Join<Organization, ?>> joins = root.getJoins();
    assertEquals(1, joins.size());

    for (Join<Organization, ?> join : joins) {
      assertEquals(JoinType.LEFT, join.getJoinType());
    }
  }

  @Test
  public void checkFromListOrderByOuterJoinOnConditionOne() throws ODataJPAModelException, ODataApplicationException {
    List<JPAAssociationAttribute> orderBy = new ArrayList<>();
    JPAAttribute exp = helper.getJPAAssociation("Organizations", "roles");
    orderBy.add((JPAAssociationAttribute) exp);

    Map<String, From<?, ?>> act = cut.createFromClause(orderBy, new ArrayList<JPAPath>());

    @SuppressWarnings("unchecked")
    Root<Organization> root = (Root<Organization>) act.get(jpaEntityType.getInternalName());
    Set<Join<Organization, ?>> joins = root.getJoins();
    assertEquals(1, joins.size());

    for (Join<Organization, ?> join : joins) {
      assertNull(join.getOn());
    }
  }

  @Test
  public void checkFromListDescriptionAssozationAllFields() throws ODataApplicationException, ODataJPAModelException {
    List<JPAAssociationAttribute> orderBy = new ArrayList<>();
    List<JPAPath> descriptionPathList = new ArrayList<>();
    JPAEntityType entity = helper.getJPAEntityType("Organizations");
    descriptionPathList.add(entity.getPath("Address/CountryName"));

    JPAAttribute attri = helper.getJPAAttribute("Organizations", "address");
    JPAAttribute exp = attri.getStructuredType().getAttribute("countryName");

    Map<String, From<?, ?>> act = cut.createFromClause(orderBy, descriptionPathList);
    assertEquals(2, act.size());
    assertNotNull(act.get(exp.getInternalName()));
  }

  @Test
  public void checkFromListDescriptionAssozationAllFields2() throws ODataApplicationException, ODataJPAModelException {
    List<JPAAssociationAttribute> orderBy = new ArrayList<>();
    List<JPAPath> descriptionPathList = new ArrayList<>();
    JPAEntityType entity = helper.getJPAEntityType("Organizations");
    descriptionPathList.add(entity.getPath("Address/RegionName"));

    JPAAttribute attri = helper.getJPAAttribute("Organizations", "address");
    JPAAttribute exp = attri.getStructuredType().getAttribute("regionName");

    Map<String, From<?, ?>> act = cut.createFromClause(orderBy, descriptionPathList);
    assertEquals(2, act.size());
    assertNotNull(act.get(exp.getInternalName()));
  }
}
