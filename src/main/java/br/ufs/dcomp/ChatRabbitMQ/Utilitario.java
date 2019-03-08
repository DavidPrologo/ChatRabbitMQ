package br.ufs.dcomp.ChatRabbitMQ;
import java.util.Date;
import java.text.SimpleDateFormat;
// import com.rabbitmq.client.*;
// import java.io.IOException;

public class Utilitario{
    public static String data_e_hora(){
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
        String hora = (new SimpleDateFormat("HH:mm:ss")).format(dataHoraAtual);
        return ("("+data+" às "+hora+")");
    }
    public static void serialize(){
        
        // // Agrupando dados do primeiro telefone
        // ContatoProto.Telefone.Builder bFone1 = ContatoProto.Telefone.newBuilder();
        // bFone1.setNumero("79 99999-0000");
        // bFone1.setTipo(ContatoProto.Tipo.MOVEL);
        
        // // Agrupando dados do segundo telefone
        // ContatoProto.Telefone.Builder bFone2 = ContatoProto.Telefone.newBuilder();
        // bFone2.setNumero("79 3200-2323");
        // bFone2.setTipo(ContatoProto.Tipo.CASA);

        // // Agrupando dados do contato do aluno com os dois telefones acima
        // ContatoProto.Aluno.Builder builderAluno = ContatoProto.Aluno.newBuilder();
        // builderAluno.setMatricula(123456);
        // builderAluno.setNome("Morpheus Santos Sá");
        // builderAluno.setEmail("morpheus@dcomp.ufs.br");
        // builderAluno.addTelefones(bFone1);
        // builderAluno.addTelefones(bFone2);
        
        // // Obtendo o contato do aluno
        // ContatoProto.Aluno contatoAluno = builderAluno.build();
        
        // // Serializando o contato 
        // byte[] buffer = contatoAluno.toByteArray();
        
        // // Escrevendo contato já serializado em arquivo
        // FileOutputStream fos = new FileOutputStream(new File("aluno.bin"));
        // fos.write(buffer);
        // fos.close();
        // System.out.println("Contato escrito em formato binário no arquivo \"aluno.bin\"");
        
        // // Mapeando a mensagem para o formato json
        // String json = JsonFormat.printer().print(contatoAluno);
        
        // // Escrita do conteúdo json em arquivo texto
        // fos = new FileOutputStream(new File("aluno.json"));
        // fos.write(json.getBytes());
        // fos.close();

        // System.out.println("Contato escrito em formato texto/json no arquivo \"aluno.json\"");
    }
}