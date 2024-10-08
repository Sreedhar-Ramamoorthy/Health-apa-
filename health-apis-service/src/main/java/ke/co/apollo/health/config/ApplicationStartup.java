package ke.co.apollo.health.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import ke.co.apollo.health.common.domain.model.request.EmailRequest;
import ke.co.apollo.health.remote.NotificationRemote;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String BODY = "<body>";
    public static final String HTML = "<html>";
    public static final String H_31 = "<h3>";
    public static final String BODY1 = "</body>";
    public static final String HTML1 = "</html>";
    public static final String BR = "<br/>";

    @Value("${git.build.time}")
    String buildTime;

    @Value("${git.commit.id.full}")
    String gitCommitIdFull;

    @Value("${git.branch}")
    String gitBranch;

    @Value("${git.commit.message.full}")
    String commitMessage;

    @Value("${git.total.commit.count}")
    String gitTotalCommitCount;

    @Autowired
    Environment environment;

    @Value("#{'${developer.notification}'.split(',')}")
    private String developerNotificationList;

    @Autowired
    NotificationRemote notificationRemote;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        logger.debug(
            "\n----------------------------------------------------------\n\t"
            + "Build Git Version:\n\t"
            + "buildTime : {} \n\t"
            + "commit.id:  {}\n\t"
            + "git.branch: {}\n\t"
            + "commitMessage: {}\n\t"
            + "gitTotalCommitCount: {}\n\t"
            + "----------------------------------------------------------",
            buildTime,
            gitCommitIdFull,
            gitBranch,
            commitMessage,
            gitTotalCommitCount
                    );
        try {
            if (environment != null && !"local".equals(environment.getActiveProfiles()[0])) {
                String text = HTML + BODY + "Commit Message: " + commitMessage +
                              BR + "Commit gitCommitIdFull: " + gitCommitIdFull + BODY1 + HTML1;
                EmailRequest emailRequest = EmailRequest.builder()
                                                             .emailAddress(developerNotificationList)
                                                             .subject("Health -" + gitBranch + " - Start")
                                                             .text(text)
                                                             .build();
                notificationRemote.sendEmail(emailRequest);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}
