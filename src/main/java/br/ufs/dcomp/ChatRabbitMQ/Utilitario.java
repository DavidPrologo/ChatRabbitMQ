package br.ufs.dcomp.ChatRabbitMQ;
import java.util.Date;
import java.text.SimpleDateFormat;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import java.nio.Buffer;
import java.util.List;
import java.util.HashMap;
import static jdk.nashorn.internal.objects.ArrayBufferView.buffer;

public class Utilitario{
    public static byte[] serializar(HashMap<String, Object> message){
        // Agrupando dados do contÃ©udo da mensagem a ser enviada
        ProtoMensagem.Conteudo.Builder conteudo1 = ProtoMensagem.Conteudo.newBuilder();
        conteudo1.setTipo(" ");
        conteudo1.setCorpo((ByteString)(message.get("conteudo")));
        //conteudo1.setCorpo(ByteString.copyFromUtf8(msgEnviar));
        conteudo1.setNome(" ");

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
            //Extraindo o conteudo recebido
            for (ProtoMensagem.Conteudo conteudosRecebidos: conteudoRecebido.getConteudoList()) {
                ByteString corpo = conteudosRecebidos.getCorpo();
                corpoConteudoString = corpo.toStringUtf8();
                String tipo = conteudosRecebidos.getTipo();
                String nome = conteudosRecebidos.getNome();
            }
                String emissor = emissorRecebido.substring(1);
            message.put("data",dataRecebida);
            message.put("hora",horaRecebida);
            message.put("emissor",emissorRecebido);
            message.put("nomegrupo",nomeGrupo);
            message.put("corpoconteudo",corpoConteudoString);
            message.put("receptor",receptor);
        }catch(Exception e){System.out.println(e);}
        return message;
    }
    public static String data_e_hora(){
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
        String hora = (new SimpleDateFormat("HH:mm:ss")).format(dataHoraAtual);
        return ("("+data+" às "+hora+")");
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
    
}