package com.softserveinc.ita.homeproject.api.tests.ownerships;

import com.softserveinc.ita.homeproject.ApiException;
import com.softserveinc.ita.homeproject.api.ApartmentOwnershipApi;
import com.softserveinc.ita.homeproject.api.tests.query.OwnershipQuery;
import com.softserveinc.ita.homeproject.api.tests.utils.ApiClientUtil;
import com.softserveinc.ita.homeproject.model.BaseReadView;
import com.softserveinc.ita.homeproject.model.ReadOwnership;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QueryOwnershipIT {

    private final ApartmentOwnershipApi ownershipApi = new ApartmentOwnershipApi(ApiClientUtil.getClient());

    private static final long testOwnershipId = 10000001L;

    private static final long testApartmentId = 100000000L;

    @Test
    void getAllOwnershipsAscSort() throws ApiException {

        List<ReadOwnership> queryResponse = new OwnershipQuery.Builder(ownershipApi)
                .apartmentId(testApartmentId)
                .pageNumber(1)
                .pageSize(10)
                .sort("id,asc")
                .build().perform();

        assertThat(queryResponse).isSortedAccordingTo(Comparator.comparing(BaseReadView::getId));
    }

    @Test
    void getAllOwnershipsDescSort() throws ApiException {
        List<ReadOwnership> queryResponse = new OwnershipQuery.Builder(ownershipApi)
                .apartmentId(testApartmentId)
                .pageNumber(1)
                .pageSize(10)
                .sort("id,desc")
                .build().perform();

        assertThat(queryResponse).isSortedAccordingTo(Comparator.comparing(BaseReadView::getId).reversed());
    }

    @Test
    void getAllOwnershipsFilteredByOwnershipPart() throws ApiException {

        List<ReadOwnership> queryResponse = new OwnershipQuery.Builder(ownershipApi)
                .apartmentId(testApartmentId)
                .pageNumber(1)
                .pageSize(10)
                .sort("id,asc")
                .filter("ownershipPart=bt=(0.2,0.6)")
                .build().perform();

        queryResponse
                .forEach(element -> assertTrue(Objects.requireNonNull(element.getOwnershipPart())
                        .compareTo(BigDecimal.valueOf(0.2)) > 0 && element.getOwnershipPart().compareTo(BigDecimal.valueOf(0.6)) < 0));
        assertThat(queryResponse).isSortedAccordingTo(Comparator.comparing(BaseReadView::getId));
    }

    @Test
    void getAllOwnershipsByOwnershipId() throws ApiException {
        List<ReadOwnership> queryResponse = new OwnershipQuery.Builder(ownershipApi)
                .apartmentId(testApartmentId)
                .pageNumber(1)
                .pageSize(10)
                .sort("id,desc")
                .id(testOwnershipId)
                .build().perform();
        queryResponse.forEach(element -> assertEquals(testOwnershipId, element.getId()));
    }

    @Test
    void getAllOwnershipsByOwnershipPart() throws ApiException {
        ReadOwnership readOwnership = ownershipApi.getOwnership(testApartmentId, testOwnershipId);

        BigDecimal ownershipPart = readOwnership.getOwnershipPart();

        List<ReadOwnership> queryResponse = new OwnershipQuery.Builder(ownershipApi)
                .apartmentId(testApartmentId)
                .pageNumber(1)
                .pageSize(10)
                .sort("id,asc")
                .ownershipPart(ownershipPart)
                .build().perform();

        queryResponse
                .forEach(element -> assertEquals(element.getOwnershipPart(), readOwnership.getOwnershipPart()));
    }

    @Test
    void getAllOwnershipsByUserId() throws ApiException {
        ReadOwnership readOwnership = ownershipApi.getOwnership(testApartmentId, testOwnershipId);

        Long userId = Objects.requireNonNull(readOwnership.getUser()).getId();

        List<ReadOwnership> queryResponse = new OwnershipQuery.Builder(ownershipApi)
                .apartmentId(testApartmentId)
                .pageNumber(1)
                .pageSize(10)
                .sort("id,asc")
                .userId(userId)
                .build().perform();

        queryResponse
                .forEach(element -> assertEquals(element.getUser(), readOwnership.getUser()));
    }
}
