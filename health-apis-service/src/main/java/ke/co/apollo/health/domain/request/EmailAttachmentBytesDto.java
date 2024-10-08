package ke.co.apollo.health.domain.request;

import lombok.*;

@ToString
@Builder
@AllArgsConstructor
@Getter
@Setter
public class EmailAttachmentBytesDto {
    private String attachmentName;
    private byte[] bytes;
    private String emailAddress;
    private String subject;
    private String text;
}
