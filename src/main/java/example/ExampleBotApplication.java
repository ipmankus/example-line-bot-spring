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
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();
        return handleTextContent(event.getReplyToken(), event, message);
    }

    private TextMessage handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String originalText = content.getText();
        String text = originalText.toLowerCase();

        if (text == "profile") {
            String userId = event.getSource().getUserId();
            final String displayName;
            final String statusMessage;
            lineMessagingClient
                .getProfile(userId)
                .whenComplete((userProfile, throwable) -> replyProfile(userProfile));
        }
        else return replyDefault(originalText);
    }

    private TextMessage replyProfile(UserProfileResponse userProfile) {
        String displayName = userProfile.getDisplayName();
        String statusMessage = userProfile.getStatusMessage(); 
        String reply = displayName + "\nStatus : " + statusMessage;
        return new TextMessage(reply);
    }

    private TextMessage replyDefault(String originalText) {
        String reply = originalText + " - ExampleBot";
        return new TextMessage(reply);
    }
}
