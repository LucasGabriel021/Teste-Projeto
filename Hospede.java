

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
        Random random = new Random();
        while (!checkedIn) {
            if (hotel.checkIn(this)) {
                checkedIn = true;
                System.out.println(nome + " fez check-in.");
            } else {
                System.out.println(nome + " está esperando por um quarto.");
                if (!hotel.adicionarFilaEspera(this)) {
                    System.out.println(nome + " deixou uma reclamação e foi embora.");
                    return;
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
}
