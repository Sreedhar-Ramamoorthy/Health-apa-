package ke.co.apollo.health.service.impl;

import com.github.pagehelper.page.PageMethod;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import ke.co.apollo.health.common.domain.model.Lead;
import ke.co.apollo.health.common.domain.model.LeadSearchCondition;
import ke.co.apollo.health.common.domain.model.SearchCondition;
import ke.co.apollo.health.config.MdcConfig;
import ke.co.apollo.health.domain.response.LeadListResponse;
import ke.co.apollo.health.domain.response.LeadResponse;
import ke.co.apollo.health.enums.SortOption;
import ke.co.apollo.health.mapper.health.LeadMapper;
import ke.co.apollo.health.mapping.LeadMapping;
import ke.co.apollo.health.service.LeadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImpl implements LeadService {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  LeadMapper leadMapper;

  @Override
  public String createLead(Lead lead, String agentId) {
    String leadId = null;
    lead.setCreateTime(new Date());
    lead.setUpdateTime(new Date());
    if (leadMapper.insert(lead, agentId) == 1) {
      leadId = lead.getLeadId();
    }
    return leadId;
  }

  @Override
  public String updateLead(Lead lead, String agentId) {
    lead.setUpdateTime(new Date());
    if (leadMapper.update(lead, agentId) == 1) {
      return lead.getLeadId();
    }
    return null;
  }

  @Override
  public boolean deleteLead(String leadId, String agentId) {
    return leadMapper.delete(leadId, agentId) > 0;
  }

  @Override
  public List<Lead> getLeadList(SearchCondition searchCondition, String agentId) {
    logger.debug("get lead list, searchCondition: {}, agentId: {}", searchCondition, agentId);
    LeadSearchCondition leadSearchCondition = buildLeadSearchCondition(searchCondition);
    PageMethod.startPage(searchCondition.getIndex(), searchCondition.getLimit());
    return leadMapper.getLeads(leadSearchCondition, agentId);
  }

  @Override
  public int getLeadTotolCount(SearchCondition searchCondition, String agentId) {
    logger
        .debug("get lead total count, searchCondition: {}, agentId: {}", searchCondition, agentId);
    return leadMapper.getTotalLeads(agentId);
  }

  @Override
  public LeadListResponse getLeadListAndCount(SearchCondition searchCondition, String agentId) {
    return CompletableFuture.supplyAsync(MdcConfig.mdcSupplier(() -> getLeadList(searchCondition, agentId)))
        .thenCombine(
            CompletableFuture.supplyAsync(MdcConfig.mdcSupplier(() -> getLeadTotolCount(searchCondition, agentId))),
            (leads, total) -> {
              List<LeadResponse> leadResponses = new ArrayList<>();
              leads.stream()
                  .forEach(lead -> leadResponses.add(LeadMapping.entity2LeadResponse(lead)));
              return LeadListResponse.builder().leads(leadResponses).total(total).build();
            }

        ).exceptionally(e -> {
          logger.error(" get lead list and count failed, error: {}", e.getMessage());
          return null;
        }).join();
  }

  private LeadSearchCondition buildLeadSearchCondition(SearchCondition searchCondition) {
    LeadSearchCondition leadSearchCondition = LeadSearchCondition.builder().build();
    if (searchCondition.getSearchKey() != null) {
      leadSearchCondition.setName(searchCondition.getSearchKey());
    }
    if (SortOption.TIME.getValue().equals(searchCondition.getSort())) {
      leadSearchCondition.setOrderbyCause("create_time asc");
    } else if (SortOption.NAME.getValue().equals(searchCondition.getSort())) {
      leadSearchCondition.setOrderbyCause("first_name asc, last_name asc, create_time desc");
    } else {
      leadSearchCondition.setOrderbyCause("create_time desc");
    }

    String filter = searchCondition.getFilter();
    if (StringUtils.isNotEmpty(filter)) {
      leadSearchCondition.setProduct("$." + searchCondition.getFilter().toLowerCase());
    }

    return leadSearchCondition;
  }
}
