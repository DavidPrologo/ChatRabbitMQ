package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;

public class ChatTeste{

    public static String destinatario_static = "";
    
    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://david:123@3.84.134.71");
        
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        
        Scanner entrada = new Scanner(System.in); //Entrada de dados
        String usuario; //Para armazenar o usuário
        //Garantir a quebra de linha no Mac, Windowns e Linux
        String quebraLinha = System.getProperty("line.separator"); //Recebendo metódo que quebra linha 
        
        //Inserir usuário e criar fila no Rabbitmq
        System.out.println("Digite um nome de usuário");
        System.out.print("User: ");
        //acrescenta o @ na frente do usuário
        usuario = entrada.nextLine();
          
        System.out.println("Criando fila para acesso ....");
        channel.queueDeclare(usuario/*QUEUE_NAME*/, false,   false,     false,       null);
        
        
        System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
        System.out.print(">> ");
        
        String destinatario = entrada.nextLine();
        ChatTeste.destinatario_static = destinatario;
        channel.basicConsume("@"+usuario, true, new DefaultConsumer(channel) {
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
                System.out.print(ChatTeste.destinatario_static+">> ");
            
            }
        });
        while(destinatario.charAt(0) != '@' ){
            System.out.println("Usuário Incorreto!");
            System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
            System.out.print(">>");
            destinatario = entrada.nextLine();
        }
        
        while (true) {          
            //imprimir na tela ex: usuárioDestino >>   
            System.out.print(destinatario+">> ");
            String mensagem = entrada.nextLine(); 
            String texto = ChatTeste.data_e_hora()+" "+usuario+" diz: "+mensagem;
                
            channel.basicPublish("", destinatario, null, texto.getBytes("UTF-8"));
        }
    }
    public static String data_e_hora(){
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
        String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);
        return ("("+data+" às "+hora+")");
    }
}