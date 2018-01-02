package example;

import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@LineMessageHandler
public class ExampleBotApplication {
    public static void main(String args[]) {
        SpringApplication.run(ExampleBotApplication.class, args);
    }

    @EventMapping
    public TextMessage handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        String message = event.getMessage().getText();
        if(message.equals("/profile"))
        {
            String userId = event.getSource().getUserId();
            return new TextMessage(userId);
        }
        else {
            return new TextMessage(message + " - ExampleBot123");
        }
    }
}
