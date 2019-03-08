package br.ufs.dcomp.ChatRabbitMQ;
import com.rabbitmq.client.*;
import java.io.IOException;

public class Consumidor extends DefaultConsumer{
    
    Chat chat;
    public Consumidor(Channel channel, Chat chat){
        super(channel);
        this.chat = chat;
    }
    
    //@Override
    public void handleDelivery
    (
    String consumerTag,
    Envelope envelope, 
    AMQP.BasicProperties properties, 
    byte[] body
    )throws IOException {
    
        String message = new String(body, "UTF-8");
        System.out.println("\n"+message);
        System.out.print(chat.getDestinatario()+">> ");
    
    }

}