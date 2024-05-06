import java.util.Random;

public class Recepcionista extends Thread {
    private Hotel hotel;

    public Recepcionista(Hotel hotel) {
        this.hotel = hotel;
    }

    public void checkIn(Hospede hospede) {
        if (hotel.temQuartoDisponivel()) {
            hotel.alocarQuarto(hospede);
            System.out.println("Recepcionista fez check-in para " + hospede.getNome());
        } else {
            System.out.println("Não há quartos disponíveis para " + hospede.getNome());
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        while (true) {
            try {
                Thread.sleep(random.nextInt(5000)); // Tempo de espera aleatório antes de atender um novo hóspede
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (hotel) {
                Hospede proximoHospede = hotel.proximoFilaEspera();
                if(proximoHospede != null) {
                    System.out.println("Hospede(s) na fila(s)" + proximoHospede);
                }

                if (proximoHospede != null) {
                    checkIn(proximoHospede);
                }
            }
        }
    }
}