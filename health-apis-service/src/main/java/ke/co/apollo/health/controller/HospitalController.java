package ke.co.apollo.health.controller;

import java.io.IOException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.domain.entity.HospitalEntity;
import ke.co.apollo.health.domain.request.HospitalSearchRequest;
import ke.co.apollo.health.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Hospital API")
public class HospitalController {

  @Autowired
  private HospitalService hospitalService;

  @PostMapping("/hospital/locations")
  @ApiOperation("Get Hospital locations")
  public ResponseEntity<DataWrapper> getLocations() {

    return ResponseEntity.ok(new DataWrapper(hospitalService.getLocations()));

  }

  @PostMapping("/hospital/initial")
  @ApiOperation("Get Hospital Initial Data")
  public ResponseEntity<DataWrapper> getInitialData() {
    return ResponseEntity.ok(new DataWrapper(hospitalService.getInitialData()));
  }


  @PostMapping("/hospital/search")
  @ApiOperation("Search Hospital Data")
  public ResponseEntity<DataWrapper> searchHospital(
      @ApiParam(name = "HospitalSearchRequest", value = "Hospital Search Request", required = true)
      @Valid @RequestBody HospitalSearchRequest request) {
    Page<HospitalEntity> response = hospitalService.searchHospitals(request);
    return ResponseEntity.ok(new DataWrapper(response.getContent()));
  }

  @PostMapping("/hospital/updateLocation")
  @ApiOperation("Search Hospital Data")
  public ResponseEntity<DataWrapper> updateLocation(@NotNull @RequestPart(value = "file") final MultipartFile multipartFile) throws IOException {
    return ResponseEntity.ok(new DataWrapper(hospitalService.updateHospitalLocation(multipartFile)));
  }

  @PostMapping("/hospital/updateHospital")
  @ApiOperation("updateHospital Data")
  public ResponseEntity<DataWrapper> updateHospital(@NotNull @RequestPart(value = "file") final MultipartFile multipartFile) throws IOException {
    hospitalService.updateHospital(multipartFile);
    return ResponseEntity.ok(new DataWrapper());
  }

  @PostMapping("/hospital/updateHospitalList")
  @ApiOperation("Update Hospital List")
  public ResponseEntity<DataWrapper> updateHospitalList(@NotNull @RequestPart(value = "file") final MultipartFile multipartFile) throws IOException {
    hospitalService.updateHospitalList(multipartFile);
    return ResponseEntity.ok(new DataWrapper());
  }
}
