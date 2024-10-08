package ke.co.apollo.health.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ke.co.apollo.health.common.domain.model.ProductPremium;
import ke.co.apollo.health.common.domain.result.DataWrapper;
import ke.co.apollo.health.common.domain.result.ReturnCode;
import ke.co.apollo.health.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health/api")
@Api(tags = "Health App Product API")
public class ProductController {

  @Autowired
  ProductService productService;

  @GetMapping("/product/premium")
  @ApiOperation("Get product premium")
  public ResponseEntity<DataWrapper> getProductPremium() {

    ProductPremium productPremium = productService.getProductPremium();
    if (productPremium == null) {
      return ResponseEntity.ok(new DataWrapper(ReturnCode.NO_DATA));
    }
    return ResponseEntity.ok(new DataWrapper(productPremium));
  }

}
