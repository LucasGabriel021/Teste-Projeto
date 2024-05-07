import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Recepcionista extends Thread {
    private Hotel hotel;
    private BlockingQueue<Hospede> filaDeEspera;
    private static final int RETRY_DELAY_MS = 5000; // 5 segundos de espera para nova tentativa
    private static final int WALK_DELAY_MS = 10000; // 10 segundos de passeio pela cidade


    public Recepcionista(Hotel hotel) {
        this.hotel = hotel;
        this.filaDeEspera = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Hospede hospede = filaDeEspera.take();
                if (!checkIn(hospede)) {
                    int currentAttempts = hospede.incrementarTentativas();
                    if (currentAttempts >= 2) {
                        System.out.println(hospede.getNome() + " deixou uma reclamação e foi embora após duas tentativas de check-in.");
                        continue; // O hóspede desiste e não é colocado de volta na fila
                    }
                    System.out.println(hospede.getNome() + " vai passear pela cidade e tentar novamente mais tarde.");
                    Thread.sleep(RETRY_DELAY_MS);
                    filaDeEspera.put(hospede); // Coloca o hóspede de volta na fila após o passeio
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    public boolean checkIn(Hospede hospede) {
        synchronized (hotel) {
            if (hospede.isEstadiaConcluida()) {
                System.out.println("Hóspede " + hospede.getNome() + " já concluiu o check-in.");
                return true;
            }

            Quarto quarto = hotel.getVagoQuarto();
            if (quarto != null) {
                if (quarto.adicionarHospede(hospede, hospede.getMembrosFamilia())) {
                    hospede.setEstadiaConcluida(true);
                    System.out.println("Check-in realizado com sucesso para " + hospede.getNome() + ".");
                    return true;
                }
            } else {
                System.out.println("Check-in falhou: Não há quartos disponíveis para " + hospede.getNome());
            }
            return false; // Retorna falso para indicar que o check-in falhou
        }
    }

    public void checkOut(Hospede hospede) {
        synchronized (hotel) {
            boolean quartoLiberado = false;
            for (Quarto quarto : hotel.getQuartos()) {
                if (quarto.getHospedes().contains(hospede)) {
                    quarto.removerHospede(hospede);
                    if (quarto.getHospedes().isEmpty()) {
                        quarto.deixarChaveNaRecepcao(hospede.getNome());
                        quarto.setVago(true);
                        quarto.setLimpo(false);
                        quartoLiberado = true;
                        System.out.println("Quarto " + quarto.getNumero() + " está agora vago e pronto para limpeza.");
                    }
                }
            }
            if (quartoLiberado) {
                hotel.quartoLiberado();  // Notificar que um quarto foi liberado
            }
        }
    }

    public void adicionarFilaDeEspera(Hospede hospede) {
        try {
            filaDeEspera.put(hospede);
            System.out.println(hospede.getNome() + " foi adicionado à fila de espera.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}