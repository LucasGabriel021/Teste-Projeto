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
        while (true) {
            try {
                Hospede hospede = filaDeEspera.take();
                if (!checkIn(hospede)) { // Tenta fazer check-in, se falhar, re-adiciona à fila
                    Thread.sleep(RETRY_DELAY_MS); // Espera antes de tentar novamente
                    filaDeEspera.put(hospede);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private boolean checkIn(Hospede hospede) {
        synchronized(hotel) {
            Quarto quarto = hotel.getVagoQuarto();
            if(quarto != null) {
                int membrosFamilia = hospede.getMembrosFamilia();
                quarto.adicionarHospede(hospede, membrosFamilia);
                System.out.println("Check-in realizado por " + hospede.getNome() + " e seus familiares " + hospede.getMembrosFamilia() + " foi alocado no quarto de número " + quarto.getNumero());
               return true;
            } else {
                System.out.println("Quartos: " + hotel.getVagoQuarto());
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
                        quarto.setVago(true);  // Marcar como vago imediatamente se necessário
                    }
                    synchronized (hospede) {
                        hospede.notifyAll();  // Notifica o hóspede para finalizar sua thread
                    }
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