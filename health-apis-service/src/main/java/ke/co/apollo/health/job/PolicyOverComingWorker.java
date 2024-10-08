package ke.co.apollo.health.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ke.co.apollo.health.common.domain.model.PolicyOverComing;
import ke.co.apollo.health.common.domain.model.request.ComingPolicyListRequest;
import ke.co.apollo.health.common.domain.model.request.PolicyRenewalRequest;
import ke.co.apollo.health.common.domain.model.response.PolicyOverComingResponse;
import ke.co.apollo.health.common.domain.model.response.PolicyRenewalResponse;
import ke.co.apollo.health.domain.entity.PolicyOverComingEntity;
import ke.co.apollo.health.domain.entity.PolicyOverComingRecordEntity;
import ke.co.apollo.health.remote.PolicyRemote;
import ke.co.apollo.health.repository.PolicyOverComingRecordRepository;
import ke.co.apollo.health.repository.PolicyOverComingRepository;
import ke.co.apollo.health.service.PolicyService;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import javax.management.timer.Timer;

@Component
public class PolicyOverComingWorker {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PolicyRemote policyRemote;
    
    @Autowired
    private PolicyOverComingRepository policyOverComingRepository;

    @Autowired
    private PolicyOverComingRecordRepository comingRecordRepository;

    @Autowired
    private PolicyService policyService;


    @Scheduled(cron = "${health.service.update.policy.cron}", zone = "Africa/Nairobi")
    @SchedulerLock(name = "updatePolicyCache", lockAtLeastFor = "PT10M", lockAtMostFor = "PT10M")
    public void updatePolicyCache() {
        logger.info("========= start updatePolicyCache ========= ");
        PolicyOverComingRecordEntity policyOverComingRecord = comingRecordRepository.findByRecordDate(nowDate());
        PolicyOverComingRecordEntity previousPolicyOverComingRecord = comingRecordRepository.findByRecordDate(prevDate());
        int prevTotal = previousPolicyOverComingRecord.getCurrentAmount();
        logger.info("The number of policies saved yesterday was {}", prevTotal);

        ComingPolicyListRequest comingPolicyListRequest;
        if (policyOverComingRecord == null) {
            policyOverComingRecord = new PolicyOverComingRecordEntity();
            policyOverComingRecord.setRecordDate(nowDate());
            policyOverComingRecord.setCurrentAmount(0);
            policyOverComingRecord.setTotalAmount(0);
            policyOverComingRecord.setCreateTime(new Date());
            policyOverComingRecord.setUpdateTime(new Date());
            comingPolicyListRequest = ComingPolicyListRequest.builder().index(prevTotal).limit(20).build();
        } else {
            comingPolicyListRequest = ComingPolicyListRequest.builder().index(policyOverComingRecord.getCurrentAmount()).limit(20).build();
        }

        PolicyOverComingResponse policyOverComingResponse = policyRemote.comingPolicyList(comingPolicyListRequest);

        if (!Objects.isNull(policyOverComingResponse)) {
            updatePolicyOverComingRecord(policyOverComingRecord, policyOverComingResponse);
            logger.info("Number of new policies to add {}", policyOverComingResponse.getPolicyOverComingList().size());
            List<String> policyNumbers = policyOverComingResponse.getPolicyOverComingList().stream().map(PolicyOverComing::getPolicyNumber).collect(Collectors.toList());
            int total = updateComingPolicyListToRds(policyNumbers, policyOverComingResponse);
            logger.info("========= end updatePolicyCache total: {} =========", total);
        } else {
            PolicyOverComingResponse responseWhenRemoteReturnsNull = PolicyOverComingResponse.builder()
                    .total(prevTotal)
                    .build();
            updatePolicyOverComingRecord(policyOverComingRecord, responseWhenRemoteReturnsNull);
        }
    }

    private void updatePolicyOverComingRecord(PolicyOverComingRecordEntity policyOverComingRecord, PolicyOverComingResponse policyOverComingResponse) {
        logger.info("updatePolicyOverComingRecord policyOverComingRecord: {}", policyOverComingRecord);
        logger.info("updatePolicyOverComingRecord policyOverComingResponse: {}", policyOverComingResponse);
        Integer increase = 0;
        if (policyOverComingResponse != null && policyOverComingResponse.getPolicyOverComingList() != null) {
            logger.info("updatePolicyOverComingRecord increase size: {}", increase);
            increase = policyOverComingResponse.getPolicyOverComingList().size();
        }
        logger.info("No new records from Actisure to update");
        policyOverComingRecord.setCurrentAmount(policyOverComingRecord.getCurrentAmount() + increase);
        policyOverComingRecord.setTotalAmount(policyOverComingResponse.getTotal());
        policyOverComingRecord.setUpdateTime(new Date());
        comingRecordRepository.save(policyOverComingRecord);
    }

    private static String nowDate() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH);
    }

    private static String prevDate() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.DAY_OF_MONTH, -1);
        return now.get(Calendar.YEAR) + "-" + (now.get(Calendar.MONTH) + 1) + "-" + now.get(Calendar.DAY_OF_MONTH);
    }

    @Scheduled(cron = "0 0 0/1 * * ? ")
    @SchedulerLock(name = "updatePolicyDetailsCache", lockAtLeastFor = "PT10M", lockAtMostFor = "PT10M")
    public void updatePolicyDetailsCache() {
        logger.info("========= start updatePolicyDetailsCache");
        Date now = new Date();
        List<PolicyOverComingEntity> policyOverComingEntities = policyOverComingRepository.findAllByNeedToUpdateIsAndRenewalDateBetween(true, DateUtils.addMonths(now, -1), DateUtils.addMonths(now, 2));
        logger.info("main_1.need updatePolicyDetails : {}", policyOverComingEntities.size());
        List<PolicyOverComingEntity> policyOverComingEntitiesLimit = policyOverComingEntities.stream().limit(10).collect(Collectors.toList());
        logger.info("main_1.1.start limit update Policy : {}", policyOverComingEntitiesLimit.size());
        if (CollectionUtils.isNotEmpty(policyOverComingEntitiesLimit)) {
            for (PolicyOverComingEntity policy : policyOverComingEntitiesLimit) {
                try {
                    PolicyRenewalRequest policyRenewalRequest = PolicyRenewalRequest.builder().policyNumber(policy.getPolicyNumber()).effectiveDate(policy.getEffectiveDate()).build();
                    PolicyRenewalResponse policyRenewalResponse = policyService.renewalPolicyForComingWorker(policyRenewalRequest);
                    if (policyRenewalResponse.getPremium() != null) {
                        policy.setLoading(policyRenewalResponse.getPremium().getLoading());
                        policy.setDiscount(policyRenewalResponse.getPremium().getDiscount());
                        policy.setTotalPremium(policyRenewalResponse.getBalance());
                        policy.setEarnedPremium(policyRenewalResponse.getPremium().getEarnedPremium());
                        policy.setLoadingPercentage(policyRenewalResponse.getPremium().getLoadingPercentage());
                        policy.setClaims(policyRenewalResponse.getPremium().getClaimsPaid());
                        policy.setLossRatio(policyRenewalResponse.getPremium().getLossRatio());
                        policy.setChangeInAgePremium(policyRenewalResponse.getPremium().getChangeInAgePremium());
                    }

                } catch (Exception e) {
                    logger.error("updatePolicyDetailsCache {} Exception:{}", policy, e);
                }
                policy.setNeedToUpdate(false);
                policyOverComingRepository.save(policy);
            }
        }
        logger.info("========= end updatePolicyDetailsCache");
    }

    private int updateComingPolicyListToRds(List<String> policyNumbers, PolicyOverComingResponse policyOverComingResponse) {
        List<PolicyOverComingEntity> exitsPolicy = policyOverComingRepository.findAllByPolicyNumberIn(policyNumbers);
        List<String> exitsPolicyNumber = exitsPolicy.stream().map(PolicyOverComingEntity::getPolicyNumber).collect(Collectors.toList());
        List<PolicyOverComing> remotePolicyList = policyOverComingResponse.getPolicyOverComingList();

        List<PolicyOverComing> needUpdateToRds = remotePolicyList.stream().filter(o -> !exitsPolicyNumber.contains(o.getPolicyNumber())).collect(Collectors.toList());
        for (PolicyOverComing policy : needUpdateToRds) {
            logger.info("==== adding policy number {} to Policy OverComing Table", policy.getPolicyNumber());
            PolicyOverComingEntity policyOverComingEntity = PolicyOverComingEntity.builder()
                    .policyNumber(policy.getPolicyNumber())
                    .principalName(policy.getPrincipalName())
                    .renewalDate(policy.getRenewalDate())
                    .effectiveDate(policy.getEffectiveDate())
                    .plan(policy.getPlan())
                    .agentName(policy.getAgentName())
                    .asagentId(policy.getAsagentId())
                    .policyAmount(policy.getPolicyAmount())
                    .claims(policy.getClaims())
                    .email(policy.getEmail())
                    .mobile(policy.getMobile())
                    .createTime(new Date())
                    .updateTime(new Date())
                    .needToUpdate(true)
                    .build();
            policyOverComingRepository.save(policyOverComingEntity);
        }
        return CollectionUtils.size(needUpdateToRds);
    }

    @Scheduled(fixedRate = Timer.ONE_DAY)
    @SchedulerLock(name = "TaskSendingEmailForRenewalsDueInSixtyDays", lockAtLeastFor = "PT1M", lockAtMostFor = "PT1M")
    public void sendRenewalsDueInSixtyDays(){
        policyService.renewalsDueIn60Days();
    }

    @Scheduled(cron = "${cronExpression.policy.renewal.notification}", zone = "${scheduler.zone}")
    @SchedulerLock(name = "SendingSMSEmailForRenewalPolicy", lockAtLeastFor = "PT60M", lockAtMostFor = "PT60M")
    public void renewalNotificationPolicies() throws InterruptedException {
        policyService.renewalNotificationPolicies();
    }

    @Scheduled(cron = "${cronExpression.policy.expiry.notification}", zone = "${scheduler.zone}")
    @SchedulerLock(name = "SendingSMSEmailForExpiredPolicy", lockAtLeastFor = "PT60M", lockAtMostFor = "PT60M")
    public void expiredNotificationPolicies() throws InterruptedException {
        policyService.expiredNotificationPolicies();
    }
}
