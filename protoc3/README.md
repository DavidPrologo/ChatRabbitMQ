# ProtocolBufferersExample
Exemplo do uso de Protocol Bufferes no marshalling de mensagens
```
export PATH=$PATH:/home/ubuntu/workspace/sistemas-distribuidos/ExemploProtocolBuffers/protobuf-compiler/bin

protoc --java_out=src/main/java/ src/main/proto/mensagem.proto

mvn clean compile assembly:single

```
