import java.util.Random;

public class Hospede extends Thread{
    private Hotel hotel;
    private String nome;
    private int membrosFamilia;

    public Hospede(Hotel hotel, String nome, int membrosFamilia) {
        this.hotel = hotel;
        this.nome = nome;
        this.membrosFamilia = membrosFamilia;
    }

    @Override
    public void run() {
        boolean checkedIn = false;
        boolean isEsperandoFila = false; // Booleano que verifica se a um hospede na fila
        Random random = new Random();
        while (!checkedIn) {
            if (hotel.checkIn(this)) {
                checkedIn = true;
                System.out.println(nome + " fez check-in.");
            } else {
                if(!isEsperandoFila) { // Só exibir mensagem e adicionar na fila se não estiver já esperando
                    System.out.println(nome + " está esperando por um quarto.");
                    if (!hotel.adicionarFilaEspera(this)) {
                        System.out.println(nome + " deixou uma reclamação e foi embora.");
                        return;
                    }
                    isEsperandoFila = true; // Indica que o hospede esta na fila!
                }

                try {
                    Thread.sleep(random.nextInt(5000)); // Tempo aleatório para passear
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            Thread.sleep(random.nextInt(10000)); // Tempo aleatório para permanecer no quarto
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        hotel.checkOut(this);
        System.out.println(nome + " fez check-out.");

    }

    public int getMembrosFamilia() {
        return membrosFamilia;
    }

    public String getNome() {
        return nome;
    }
}
