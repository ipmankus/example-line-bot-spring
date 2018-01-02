package example;

import java.util.concurrent.CompletableFuture;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.sun.corba.se.impl.protocol.giopmsgheaders.ReplyMessage;

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

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) {
        String originalText = content.getText();
        String text = originalText.toLowerCase();

        if (text == "profile") {
            String userId = event.getSource().getUserId();
            final String displayName;
            final String statusMessage;
            lineMessagingClient
                .getProfile(userId)
                .whenComplete((userProfile, throwable) -> replyProfile(replyToken, userProfile));
        }
        else replyDefault(replyToken, originalText);
    }

    private void replyProfile(String replyToken, UserProfileResponse userProfile) {
        String displayName = userProfile.getDisplayName();
        String statusMessage = userProfile.getStatusMessage(); 
        String reply = displayName + "\nStatus : " + statusMessage;

        List<Message> messages;
        messages.add(new TextMessage(reply));
        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages));
    }

    private void replyDefault(String replyToken, String originalText) {
        String reply = originalText + " - ExampleBot";

        List<Message> messages;
        messages.add(new TextMessage(reply));
        lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages));
    }
}
