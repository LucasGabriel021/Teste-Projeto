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
                        System.out.println("Hospede " + hospede.getNome() + " deixou uma reclamação e foi embora.");
                        continue;
                    }
                    System.out.println("Hospede " + hospede.getNome() + " vai passear pela cidade e tentar novamente mais tarde.");
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
        synchronized (hotel) {
            if (alocarGrupo(hospede)) {
                System.out.println("Check-in realizado com sucesso para " + hospede.getNome() + " e seus familiares.");
                return true;
            } else {
                System.out.println("Check-in falhou: Não há quartos disponíveis para " + hospede.getNome());
                return false;
            }
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
                    System.out.println("Hospede " + hospede.getNome() + " fez checkout.");
                    break;
                }
            }
        }
    }


    public void adicionarFilaDeEspera(Hospede hospede) {
        filaDeEspera.offer(hospede);
        System.out.println("O hóspede " + hospede.getNome() + " está aguardando na fila de espera " + "junto ao seus familiares " + hospede.getMembrosFamilia());
    }
}