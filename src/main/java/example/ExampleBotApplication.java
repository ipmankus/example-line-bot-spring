package example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        String message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    private TextMessage handleTextContent(String replyToken, javafx.event.Event event, TextMessageContent content) {
        String text = content.getText().toLowerCase();

        if (text == "profile") {
            String userId = event.getSource().getUserId();
            UserProfileResponse userProfile = lineMessagingClient.getProfile(userId);
            String displayName = userProfile.getDisplayName();
            String statusMessage = userProfile.getStatusMessage();

            String reply = displayName + "\nStatus : " + statusMessage;
            return new TextMessage(reply);
        }        
    }
}
