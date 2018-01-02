package example;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class ExampleBotApplication {
    @Autowired
    private LineMessagingClient lineMessagingClient;

    public static void main(String args[]) {
        SpringApplication.run(ExampleBotApplication.class, args);
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    private TextMessage handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String originalText = content.getText();
        String text = originalText.toLowerCase();

        if (text == "profile") {
            String userId = event.getSource().getUserId();
            final String displayName;
            final String statusMessage;
            String reply = lineMessagingClient
                .getProfile(userId)
                .whenComplete((userProfile, throwable) -> replyProfile());
            return new TextMessage(reply);
        }
        else return new TextMessage(originalText + " - ExampleBot");
    }

    private String replyProfile(UserProfileResponse userProfile) {
        String displayName = userProfile.getDisplayName();
        String statusMessage = userProfile.getStatusMessage(); 
        return displayName + "\nStatus : " + statusMessage;
    }
}
