package com.guidodelbo.usercrud.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.guidodelbo.usercrud.shared.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AmazonSES {

    @Value("${baseUrl}")
    private String url;

    @Value("${amazonSES.aws.emailFrom}")
    private String FROM;

    // The subject line for the email.
    final String SUBJECT = "One last step to complete your registration with UserCrud";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    @Value("${amazonSES.aws.accessKeyId}")
    private String accessKeyId;

    @Value("${amazonSES.aws.secretKey}")
    private String secretKey;

    // The HTML body for the email.
    private String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our app. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='$baseUrl/verification-service/email-verification.html?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";

    // The email body for recipients with non-HTML email clients.
    private String TEXTBODY = "Please verify your email address. "
            + "Thank you for registering with our app. To complete registration process and be able to log in,"
            + " open then the following URL in your browser window: "
            + "$baseUrl/verification-service/email-verification.html?token=$tokenValue"
            + " Thank you! And we are waiting for you inside!";

    private String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our app. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='$baseUrl/verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";

    // The email body for recipients with non-HTML email clients.
    private String PASSWORD_RESET_TEXTBODY = "A request to reset your password "
            + "Hi, $firstName! "
            + "Someone has requested to reset your password with our app. If it were not you, please ignore it."
            + " otherwise please open the link below in your browser window to set a new password:"
            + "$baseUrl/verification-service/password-reset.html?token=$tokenValue"
            + " Thank you!";


    public void verifyEmail(UserDto userDto) {

        System.setProperty("aws.accessKeyId", accessKeyId);
        System.setProperty("aws.secretKey", secretKey);

        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.SA_EAST_1)
                .build();

        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken()).replace("$baseUrl", url);
        String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken()).replace("$baseUrl", url);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                .withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
                .withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
                .withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
                .withSource(FROM);

        client.sendEmail(request);

        System.out.println("Email sent!");
    }

    public boolean sendPasswordResetRequest(String firstName, String email, String token)
    {
        boolean returnValue = false;

        AmazonSimpleEmailService client =
                AmazonSimpleEmailServiceClientBuilder.standard()
                        .withRegion(Regions.SA_EAST_1).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName).replace("$baseUrl", url);

        String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
        textBodyWithToken = textBodyWithToken.replace("$firstName", firstName).replace("$baseUrl", url);


        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses( email ) )
                .withMessage(new Message()
                .withBody(new Body()
                .withHtml(new Content()
                .withCharset("UTF-8").withData(htmlBodyWithToken))
                .withText(new Content()
                .withCharset("UTF-8").withData(textBodyWithToken)))
                .withSubject(new Content()
                .withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);

        if(result != null && (result.getMessageId()!=null && !result.getMessageId().isEmpty()))
            returnValue = true;

        return returnValue;
    }
}
