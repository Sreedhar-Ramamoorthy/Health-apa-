package ke.co.apollo.health.service;

import java.util.List;
import ke.co.apollo.health.common.domain.model.Lead;
import ke.co.apollo.health.common.domain.model.SearchCondition;
import ke.co.apollo.health.domain.response.LeadListResponse;

public interface LeadService {

  String createLead(Lead lead, String agentId);

  String updateLead(Lead lead, String agentId);

  boolean deleteLead(String leadId, String agentId);

  List<Lead> getLeadList(SearchCondition searchCondition, String agentId);

  int getLeadTotolCount(SearchCondition searchCondition, String agentId);

  LeadListResponse getLeadListAndCount(SearchCondition searchCondition, String agentId);

}
