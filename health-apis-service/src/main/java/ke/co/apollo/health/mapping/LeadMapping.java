package ke.co.apollo.health.mapping;

import ke.co.apollo.health.common.domain.model.Lead;
import ke.co.apollo.health.common.domain.model.SearchCondition;
import ke.co.apollo.health.domain.request.LeadAddRequest;
import ke.co.apollo.health.domain.request.LeadSearchRequest;
import ke.co.apollo.health.domain.request.LeadUpdateRequest;
import ke.co.apollo.health.domain.response.LeadResponse;
import org.springframework.beans.BeanUtils;

public class LeadMapping {

  private LeadMapping() {

  }

  public static Lead leadAddRequest2Entity(LeadAddRequest dto) {
    Lead entity = new Lead();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  public static Lead leadUpdateRequest2Entity(LeadUpdateRequest dto) {
    Lead entity = new Lead();
    if (dto != null) {
      BeanUtils.copyProperties(dto, entity);
    }
    return entity;
  }

  public static LeadResponse entity2LeadResponse(Lead entity) {
    LeadResponse dto = new LeadResponse();
    if (entity != null) {
      BeanUtils.copyProperties(entity, dto);
    }
    return dto;
  }

  public static SearchCondition leadSearchRequest2SearchCondition(LeadSearchRequest dto) {
    SearchCondition searchCondition = new SearchCondition();
    if (dto != null) {
      BeanUtils.copyProperties(dto, searchCondition);
    }
    return searchCondition;
  }

}
