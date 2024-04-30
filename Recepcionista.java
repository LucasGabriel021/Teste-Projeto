
import java.util.Random;

public class Recepcionista extends Thread {

    private Hotel hotel;

    public Recepcionista(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            synchronized (hotel) {
                try {
                    Thread.sleep(random.nextInt(5000)); // Tempo de espera aleatório antes de atender um novo hóspede
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Hospede proximoHospede = hotel.proximoFilaEspera();
                if (proximoHospede != null) {
                    if (hotel.checkIn(proximoHospede)) {
                        System.out.println("Recepcionista alocou um quarto para " + proximoHospede.getName());
                    }
                }
            }
        }
    }
}