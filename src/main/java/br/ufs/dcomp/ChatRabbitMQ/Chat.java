package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.util.Scanner;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.google.protobuf.ByteString;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedOutputStream;

public class Chat{

    private String destinatario;
    private String destinatarioEnv;
    private Usuario usuario;
    private Grupo grupo;
    private String mensagem;
    private Channel channel;
    private Channel channelArq;
    private Consumer consumer;
    private Consumer consumerArq;
    
    private Connection connection;
    private ConnectionFactory factory;
    
    private Connection connectionArq;
    private ConnectionFactory factoryArq;
    
    private HashMap<String, iAcao>comandos;
    
    private Scanner entrada;
    
    
    public static void main(String[] argv) throws Exception {
        //porta 15672
        new Chat("david", "123", "3.84.134.71");
    }
    
    public Chat(String usuario, String senha, String proxy)throws Exception{
        this.factory = new ConnectionFactory();
        this.factory.setUri("amqp://"+usuario+":"+senha+"@"+proxy);
        this.connection = this.factory.newConnection();
        
        // this.factoryArq = new ConnectionFactory();
        // this.factoryArq.setUri("amqp://"+usuario+":"+senha+"@"+proxy);
        // this.connectionArq = this.factoryArq.newConnection();
        
        this.channel = this.connection.createChannel();
        this.channelArq = this.connection.createChannel();
        
        this.mensagem = "";
        
        this.comandos = new HashMap<String, iAcao>();
        this.povoar(this.comandos);
        this.grupo = new Grupo(this);
        this.grupo.povoar(this.comandos);
        this.usuario = new Usuario(this);
        this.usuario.povoar(this.comandos);
        //Entrada de dados
        this.entrada = new Scanner(System.in); 
        //Garantir a quebra de linha no Mac, Windowns e Linux
        
        this.pedirUsuario();
        this.pedirDestinatario();
        this.rodar();
        
    }
    public void rodar() throws Exception{
        //this.channel.close();
        //this.channelArq.close();
        while (true) {
            //this.channel = this.connection.createChannel();
            //this.channelArq = this.connection.createChannel();
            
            //imprimir na tela ex: usuárioDestino >>   
            System.out.print(this.getDestinatario()+">> ");
            this.mensagem = entrada.nextLine();
            String chave = Utilitario.getComando(mensagem);
            iAcao acao = comandos.get(chave);
            if(acao != null){
                acao.execute();
            }else{
                System.out.println("comando inválido, por faver tente novamente:");
            }
            //this.channel.close();
            //this.channelArq.close();
        }
        
    }
    public void pedirUsuario()throws Exception{
        //Inserir usuário e criar fila no Rabbitmq
        System.out.println("Digite um nome de usuário");
        System.out.print("User: ");
        //acrescenta o @ na frente do usuário
        String nome = entrada.nextLine();
        this.usuario.setNome("@"+nome);
          
        System.out.println("Criando fila para acesso ....");
        this.channel.queueDeclare(usuario.getNome()/*QUEUE_NAME*/, false,   false,     false,       null);
        this.channelArq.queueDeclare(usuario.getNomeEnv()/*QUEUE_NAME*/, false,   false,     false,       null);
        this.consumer = new Consumidor(this);
        this.consumerArq = new ConsumidorArq(channelArq);
        
        this.channel.basicConsume(usuario.getNome(), true, this.consumer);
        this.channelArq.basicConsume(usuario.getNomeEnv(), true, this.consumerArq);
                
        
    }
    public void pedirDestinatario(){
        System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
        System.out.print(">> ");
        
        this.mensagem = entrada.nextLine() ;
        String chave = Utilitario.getComando(this.mensagem);
        while(!chave.equals("@") && !chave.equals("#")){
            System.out.println("Usuário Incorreto!");
            System.out.println("Selecione um usuário para conversar, digite @ antes do nome:" );
            System.out.print(">> ");
            this.mensagem = entrada.nextLine();
            chave = Utilitario.getComando(this.mensagem);
        }
        this.destinatario = this.mensagem;
        try{  this.comandos.get(chave).execute(); }
        catch(Exception e){ System.out.println(e); }
    }
    public void setDestinatario(String destinatario){
        this.destinatario = destinatario;
    }
    public String getDestinatario(){ return this.destinatario; }
    public String getDestinatarioEnv(){
        char sinal = this.destinatario.charAt(0);
        return (sinal+"Env"+this.destinatario.substring(1));
    }
    public String getMensagem(){ return this.mensagem; }
    public Channel getChannel(){ return this.channel; }
    public Channel getChannelArq(){ return this.channelArq; }
    public Usuario getUsuario(){return this.usuario; }
    
    private void povoar(HashMap comandos){
        comandos.put("enviar", new iAcao(){
            public void execute()throws Exception{
                HashMap<String,Object> message = new HashMap<String,Object>();
                message.put("receptor", getDestinatario().substring(1));
                message.put("emissor", usuario.getNome());
                message.put("data", Utilitario.getData());
                message.put("hora", Utilitario.getHora());
                message.put("grupo", ""+grupo.getNome());
                message.put("conteudo", ByteString.copyFromUtf8(getMensagem()));
                message.put("nome_arq", "");
                message.put("tipo_arq", "");
                
                char sinal = destinatario.charAt(0);
                String grupo_destino = (sinal == '#')?destinatario:"";
                String usuario_destino = (sinal == '@')?destinatario:"";
                
                channel.basicPublish(grupo_destino, usuario_destino, null,  Utilitario.serializar(message)); 
                //channel.basicPublish("", destinatario, null, texto.getBytes("UTF-8"));
            }
        });
        comandos.put("!upload", new iAcao(){
            public void execute()throws Exception{
                (new Thread(){
                    public void run(){
                        try{
                            String caminho_arq = getMensagem().split(" ")[1];
                            HashMap<String,Object> message = new HashMap<String,Object>();
                            message.put("receptor", getDestinatario().substring(1));
                            message.put("emissor", usuario.getNome());
                            message.put("data", Utilitario.getData());
                            message.put("hora", Utilitario.getHora());
                            message.put("grupo", grupo.getNome());
                            message.put("nome_arq", Utilitario.getNomeArq(caminho_arq));
                            message.put("tipo_arq", Utilitario.getNomeArq(caminho_arq));
                            message.put("conteudo", ByteString.copyFrom(
                                Utilitario.stringToByteUpload(caminho_arq)
                            ));
                            char sinal = getDestinatarioEnv().charAt(0);
                            String grupo_destino = (sinal == '#')?getDestinatarioEnv():"";
                            String usuario_destino = (sinal == '@')?getDestinatarioEnv():"";

                            channelArq.basicPublish(grupo_destino, usuario_destino, null,  Utilitario.serializar(message));
                        }catch(Exception e){System.out.println(e);}
                    }
                }).run();
            }
        });
    }
    public void imprimir(byte[] body){
        HashMap<String,String> message = Utilitario.desserializar(body);
        //Exemplo de saida
        //(26/03/2019 às 01:12:57) NomeDoEmissor diz para NomeDoReceptor: oi
        String receptor = (message.get("grupo") == null) ? (message.get("receptor")) : (message.get("grupo"));
        System.out.println(
            "\n("+message.get("data")+" às "
            +message.get("hora")+") "
            +message.get("emissor")+" diz para "
            +receptor+": "
            +message.get("corpoconteudo")
        );
        if(getDestinatario() != null)
            System.out.print(getDestinatario());
        System.out.print(">>");
        
    }
    class ConsumidorArq extends DefaultConsumer{
        public ConsumidorArq(Channel channel){
            super(channel);
        }
        //@Override
        public void handleDelivery
        (
        String consumerTag,
        Envelope envelope, 
        AMQP.BasicProperties properties, 
        byte[] body
        )
        throws IOException {
            HashMap<String,String> message = Utilitario.desserializar(body);
            //Exemplo de saida
            //(26/03/2019 às 01:12:57) NomeDoEmissor diz para NomeDoReceptor: oi
            System.out.println(
                "\n("+message.get("data")+" às "
                +message.get("hora")+") "
                +message.get("emissor")+" envia arquivo '"
                +message.get("nome_arq")+"' para "
                +message.get("receptor")+": "
            );
            
            FileOutputStream file = new FileOutputStream(new File("/home/ubuntu/workspace/Chat/correios/"+message.get("nome_arq")));
            BufferedOutputStream b = new BufferedOutputStream(file);
            b.write(body);
            b.close();
        }
    
    }
}
