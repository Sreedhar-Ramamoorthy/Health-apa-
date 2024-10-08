package ke.co.apollo.health.feign;


import feign.Headers;
import ke.co.apollo.health.domain.request.EmailAttachmentBytesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification", url = "${apa.notification-apis.service.base-url}")
public interface NotificationClient {
    @PostMapping(value = "/notification/email/sendEmailAttachmentBytes", produces = "application/json", consumes = "application/json")
    @Headers("Content-Type: application/json")
    String sendEmailAttachmentBytes(@RequestBody String emailAttachmentBytesRequest);
}
