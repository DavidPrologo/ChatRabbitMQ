package br.ufs.dcomp.ChatRabbitMQ;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.nio.Buffer;
import java.util.List;
import java.util.HashMap;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;

import com.google.protobuf.ByteString;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import static java.lang.System.out;
import java.net.Socket;
import java.util.Scanner;

public class Utilitario{
    
    public static byte[] stringToByteUpload(String uri) throws Exception{
        //uri = "/home/ubuntu/workspace/ChatRabbitMQ/etapa3.md";
        File file = new File(uri);
        
        FileInputStream in = new FileInputStream(file);
        int tamanho = 4096;
        
        byte[] buffer = new byte[tamanho];
        int lidos = -1;  
	    while ((lidos = in.read(buffer, 0, tamanho)) != -1) {  
	        //out.write(buffer, 0, lidos);  
	    }  
        return buffer;
	}
	
    public static byte[] serializar(HashMap<String, Object> message){
        // Agrupando dados do contÃ©udo da mensagem a ser enviada
        ProtoMensagem.Conteudo.Builder conteudo1 = ProtoMensagem.Conteudo.newBuilder();
        conteudo1.setTipo((String)message.get("tipo_arq"));
        conteudo1.setNome((String)message.get("nome_arq"));
        conteudo1.setCorpo((ByteString)(message.get("conteudo")));
        //conteudo1.setCorpo(ByteString.copyFromUtf8(msgEnviar));

        ProtoMensagem.Mensagem.Builder builderMensagem = ProtoMensagem.Mensagem.newBuilder();
        builderMensagem.setReceptor((String)(message.get("receptor")));
        builderMensagem.setEmissor((String)(message.get("emissor")));
        builderMensagem.setData((String)(message.get("data")));
        builderMensagem.setHora((String)(message.get("hora")));
        builderMensagem.setGrupo((String)(message.get("grupo")));
        builderMensagem.addConteudo(conteudo1);
        // Obtendo o Mensagem a ser enviada, com data, hora, emissor e nome do grupo
        ProtoMensagem.Mensagem mensagemObtida = builderMensagem.build();

        // Serializando a mensagem 
        return mensagemObtida.toByteArray();
        
    }
    public static HashMap<String, String> desserializar(byte[] body){
        //Mapeando bytes para a mensagem protobuf
        HashMap<String,String> message = new HashMap<>();
        try{
            ProtoMensagem.Mensagem conteudoRecebido = ProtoMensagem.Mensagem.parseFrom(body);
            String emissorRecebido = conteudoRecebido.getEmissor();
            String dataRecebida = conteudoRecebido.getData();
            String horaRecebida = conteudoRecebido.getHora();
            String nomeGrupo = conteudoRecebido.getGrupo();
            String receptor = conteudoRecebido.getReceptor();
            String corpoConteudoString = null;
            String tipo = null;
            String nome = null;
            //Extraindo o conteudo recebido
            for (ProtoMensagem.Conteudo conteudosRecebidos: conteudoRecebido.getConteudoList()) {
                ByteString corpo = conteudosRecebidos.getCorpo();
                corpoConteudoString = corpo.toStringUtf8();
                tipo = conteudosRecebidos.getTipo();
                nome = conteudosRecebidos.getNome();
                System.out.println(nome);
            }
            emissorRecebido = emissorRecebido.substring(1);
            message.put("data",dataRecebida);
            message.put("hora",horaRecebida);
            message.put("emissor",emissorRecebido);
            message.put("nomegrupo",nomeGrupo);
            message.put("corpoconteudo",corpoConteudoString);
            message.put("tipo_arq", tipo);
            message.put("nome_arq", nome);
            message.put("receptor",receptor);
        }catch(Exception e){System.out.println(e);}
        return message;
    }
    
    public static String getData(){
        return (new SimpleDateFormat("dd/MM/yyyy")).format(new Date());
    }
    
    public static String getHora(){
        return (new SimpleDateFormat("HH:mm:ss")).format(new Date());
    }
    
    public static String getComando(String palavra){
        String acumulador = "";
        if(("").equals(palavra))
            return palavra;
        if(palavra.charAt(0) == '@' || palavra.charAt(0) == '#')
            acumulador +=  palavra.charAt(0);
        else if(palavra.charAt(0) == '!')
            for(int i = 0; i<palavra.length(); i++){
                if(palavra.charAt(i) == ' ')
                    break;
                acumulador += palavra.charAt(i);
            }
        else return "enviar";
        
        return acumulador;
    }
    
    public static String getNomeArq(String caminho_arq){
        String nome_arq = "";
        for(int i = caminho_arq.length()-1; i>=0; i--){
            char c = caminho_arq.charAt(i);
            if(c == '\\' || c =='/')
                break;
            nome_arq = c+nome_arq;
        }
        return nome_arq;
    }
    public static String getExtArq(String nome_arq){
        String ext = "";
        for(int i = nome_arq.length()-1; i>=0; i--){
            char c = nome_arq.charAt(i);
            if(c == '.')
                return ext;
            ext = c+ext;
        }
        return "";
    }
}