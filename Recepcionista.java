import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Recepcionista extends Thread {
    private Hotel hotel;
    private BlockingQueue<Hospede> filaDeEspera;
    private static final int RETRY_DELAY_MS = 5000; // 5 segundos de espera

    public Recepcionista(Hotel hotel) {
        this.hotel = hotel;
        this.filaDeEspera = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Hospede hospede = filaDeEspera.take();
                if (!checkIn(hospede)) { // Utiliza o checkIn modificado
                    if (hospede.incrementarTentativas() >= 2) {
                        System.out.println(hospede.getNome() + " deixou uma reclamação e foi embora após duas tentaivas de check-in.");
                        continue;
                    }
                    System.out.println(hospede.getNome() + " vai passear pela cidade e tentar novamente mais tarde realizar o check-in.");
                    Thread.sleep(RETRY_DELAY_MS); // Hospede passeia pela cidade
                    filaDeEspera.put(hospede);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }


    public boolean alocarGrupo(Hospede hospede) {
        int membrosDaFamilia = hospede.getMembrosFamilia();
        synchronized(hotel) {
            while(membrosDaFamilia > 0) {
                Quarto quarto = hotel.getVagoQuarto();
                if(quarto == null) {
                    return false;
                }
                int membrosNoQuarto = Math.min(membrosDaFamilia, Quarto.CAPACIDADE_MAXIMA);
                quarto.adicionarHospede(hospede, membrosNoQuarto);
                membrosDaFamilia -= membrosNoQuarto;
            }
        }
        return true;
    }

    public boolean checkIn(Hospede hospede) {
//        synchronized (hotel) {
//            if (alocarGrupo(hospede)) {
//                System.out.println("Check-in realizado com sucesso para " + hospede.getNome() + " e seu gurpo de " + hospede.getMembrosFamilia());
//                return true;
//            } else {
//                System.out.println("Check-in falhou: Não há quartos disponíveis para " + hospede.getNome() + " e seu gurpo de " + hospede.getMembrosFamilia());
//                return false;
//            }
//        }
        synchronized (hotel) {
            int membrosRestantes = hospede.getMembrosFamilia();
            while(membrosRestantes > 0) {
                Quarto quarto = hotel.getVagoQuarto();
                if(quarto == null) {
                    System.out.println("Check-in falhou: Não há quartos disponíveis para " + hospede.getNome() + " e seu grupo de " + hospede.getMembrosFamilia() + " pessoas.");
                    return false;
                }
                int membrosNoQuarto = Math.min(membrosRestantes, Quarto.CAPACIDADE_MAXIMA);
                quarto.adicionarHospede(hospede, membrosNoQuarto);
                membrosRestantes -= membrosNoQuarto;
            }
            System.out.println("Check-in realizado com sucesso para " + hospede.getNome() + ". Grupo alocado em quartos conforme disponibilidade.");
            if (membrosRestantes > 0) {
                System.out.println("Atenção: Não foi possível alocar todos os membros em um único quarto. O restante foi alocado em outro quarto.");
            }
            return true;
        }

    }

    public void checkOut(Hospede hospede) {
        synchronized (hotel) {
            for (Quarto quarto : hotel.getQuartos()) {
                if (quarto.getHospedes().contains(hospede)) {
                    quarto.removerHospede(hospede);
                    if (quarto.getHospedes().isEmpty()) {
                        quarto.setChaveNaRecepcao(true);  // Pronto para limpeza
                        quarto.setVago(true);  // Marcar como vago imediatamente
                        quarto.setLimpo(false); // Marcar como não limpo
                        System.out.println("Quarto " + quarto.getNumero() + " está agora vago e pronto para limpeza.");
                        hotel.notifyAll();  // Notificar que o quarto está pronto para limpeza
                    }
                    synchronized (hospede) {
                        hospede.concluirEstadia(); // Permitir que o hospede conclua sua estadia
                        hospede.notifyAll();
                    }
                    System.out.println(hospede.getNome() + " e seu grupo de " + hospede.getMembrosFamilia() + " pessoas fizeram o check-out no hotel.");
                    break;
                }
            }
        }
    }


    public void adicionarFilaDeEspera(Hospede hospede) {
        filaDeEspera.offer(hospede);
        System.out.println(hospede.getNome() + " está aguardando na fila de espera " + "junto ao seu grupo de " + hospede.getMembrosFamilia()  + " pessoas.");
    }
}