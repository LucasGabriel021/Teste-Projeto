# Sistema de Reserva e Controle de Quartos em um Hotel

Este projeto em Java 17 simula um sistema de reserva e controle de quartos em um hotel, utilizando threads para representar as diferentes entidades envolvidas: quartos, hóspedes, camareiras, recepcionistas e hotel.

## Entidades Representadas

- Quartos
- Hóspedes
- Camareiras
- Recepcionistas
- Hotel

## Regras do Sistema

- Os recepcionistas devem alocar os hóspedes apenas em quartos vagos.
- Cada quarto possui capacidade para até 4 hóspedes, e caso um grupo ou família exceda esse número, devem ser divididos em vários quartos.
- Quando os hóspedes saem do quarto para passear, devem deixar a chave na recepção.
- Uma camareira só pode entrar em um quarto se estiver vago ou se os hóspedes não estiverem presentes, ou seja, se a chave estiver na recepção.
- A limpeza dos quartos é realizada sempre após a saída dos hóspedes. Os hóspedes só podem retornar após a limpeza estar concluída.
- Um quarto vago que passa por limpeza não pode ser alocado para um novo hóspede.
- Caso não haja quartos vagos, o hóspede deve aguardar em uma fila até que um quarto fique disponível. Se a espera for muito longa, ele pode passear pela cidade e retornar posteriormente para tentar alugar um quarto novamente.
- Se uma pessoa tentar alugar um quarto duas vezes sem sucesso, ela deixa uma reclamação e vai embora.

## Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para propor melhorias, relatar problemas ou enviar solicitações de incorporação.
