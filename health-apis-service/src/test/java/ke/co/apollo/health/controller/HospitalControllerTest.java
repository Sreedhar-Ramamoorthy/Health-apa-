package ke.co.apollo.health.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import ke.co.apollo.health.domain.entity.HospitalEntity;
import ke.co.apollo.health.domain.request.HospitalSearchRequest;
import ke.co.apollo.health.service.HospitalService;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.entity.LocationEntity;
import ke.co.apollo.health.domain.response.HospitalInitialResponse;
import ke.co.apollo.health.service.HospitalService;

class HospitalControllerTest {

    @InjectMocks
    HospitalController hospitalController;

    @Mock
    HospitalService hospitalService;



    MockMultipartFile file;

    @BeforeEach
    void setUpMocks(){
        initMocks(this);

        file 
            = new MockMultipartFile(
                "file", 
                "hello.txt", 
                MediaType.TEXT_PLAIN_VALUE, 
                "Hello, World!".getBytes()
            );
    }


    @Test
    void searchHospitalTest(){
        // Page<HospitalEntity> hos = new PageImpl<>(HospitalEntity.builder().build());

        List<HospitalEntity> content = Collections.emptyList();
        Pageable pageable = PageRequest.of(0, 10);
        Page<HospitalEntity> hos = new PageImpl<>(content, pageable, 0);

        when(hospitalService.searchHospitals(any())).thenReturn(hos);
        ResponseEntity<DataWrapper> wrapper = hospitalController.searchHospital(HospitalSearchRequest.builder().build());
        assertNotNull(wrapper);
        }


    @Test
    void updateLocationTest() throws IOException {
        when(hospitalService.updateHospitalLocation(any())).thenReturn(true);
        ResponseEntity<DataWrapper> wrapper = hospitalController.updateLocation(file);
        assertNotNull(wrapper);
        }

    @Test
    void updateHospitaltest() throws IOException {
        doNothing().when(hospitalService).updateHospital(any());
        ResponseEntity<DataWrapper> wrapper = hospitalController.updateHospital(file);
        assertNotNull(wrapper);
        }

    @Test
    void getInitialDataTest() {
        when(hospitalService.getInitialData()).thenReturn(HospitalInitialResponse.builder().build());
        ResponseEntity<DataWrapper> wrapper = hospitalController.getInitialData();
        assertNotNull(wrapper);
        }

    @Test
    void getLocationsTest() {
        when(hospitalService.getLocations()).thenReturn(Collections.singletonList(LocationEntity.builder().build()));
        ResponseEntity<DataWrapper> wrapper = hospitalController.getLocations();
        assertNotNull(wrapper);
        }

    @Test
    void updateHospitalListSuccess() throws Exception {
        doNothing().when(hospitalService).updateHospitalList(any());
        ResponseEntity<DataWrapper> wrapper = hospitalController.updateHospitalList(file);
        assertNotNull(wrapper);
    }
        
}
