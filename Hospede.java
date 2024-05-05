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
        Random random = new Random();
        Recepcionista recepcionista = hotel.getRecepciistaAleatoria(); // Obtem uma recepcionista aleatoria

        // Adicionar ao final da fila de espera
        recepcionista.adicionarFilaDeEspera(this);

        try {
            // Este loop simplesmente espera até que o check-out seja chamado pela recepcionista
            // após o check-in ter sido bem-sucedido.
            synchronized (this) {
                wait();  // Aguarda notificação da recepcionista de que o check-out foi realizado
            }

            // Simular duração da estadia
            Thread.sleep(random.nextInt(10000)); // Permanecia do hospede de forma aleatória

            // Check-out após a estadia
            recepcionista.checkOut(this);

            // Após ser notificado que o check-out foi realizado
            System.out.println(getNome() + " completou o check-out e está deixando o hotel.");

        } catch (InterruptedException e) {
            System.out.println("Hospede interrompido: " + getNome() + " não completou sua estadia devido a uma interrupção.");
            Thread.currentThread().interrupt();
        }
    }

    public int getMembrosFamilia() {
        return membrosFamilia;
    }

    public String getNome() {
        return nome;
    }
}
