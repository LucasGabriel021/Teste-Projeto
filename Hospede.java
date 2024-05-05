import java.util.Random;

public class Hospede extends Thread{
    private Hotel hotel;
    private String nome;
    private int membrosFamilia;
    private int tentativas = 0;
    private boolean estadiaConcluida = false;

    public Hospede(Hotel hotel, String nome, int membrosFamilia) {
        this.hotel = hotel;
        this.nome = nome;
        this.membrosFamilia = membrosFamilia;
    }

    @Override
    public void run() {
        Random random = new Random();
        Recepcionista recepcionista = hotel.getRecepciistaAleatoria(); // Obtem uma recepcionista aleatoria
        // recepcionista.adicionarFilaDeEspera(this); // Adiciona a fila de epsera

        // Adicionar ao final da fila de espera
        recepcionista.adicionarFilaDeEspera(this);

        try {
            // Espera ativamente por um quarto (check-in)
            synchronized (this) {
                while(recepcionista.checkIn(this)) {
                    System.out.println("Aguardando check-in para " + getNome());
                    wait(1000); // Espera um pouco antes de tentar novamente
                }
            }

            // Simular duração da estadia
            sleep(random.nextInt(10000) + 1000); // Permanecia do hospede de forma aleatória
            concluirEstadia();

            // Este loop aguarda até o checkout
            synchronized (this) {
                while(!estadiaConcluida) {
                    wait();  // Aguarda notificação da recepcionista de que o check-out foi realizado
                }
            }

            // Check-out após a estadia
            recepcionista.checkOut(this);

            // Após ser notificado que o check-out foi realizado
            System.out.println(getNome() + " completou o check-out e está deixando o hotel.");

        } catch (InterruptedException e) {
            System.out.println("Hospede interrompido: " + getNome() + " não completou sua estadia devido a uma interrupção.");
            Thread.currentThread().interrupt();
        }
    }

    public void concluirEstadia() {
        synchronized (this) {
            estadiaConcluida = true;
            notifyAll(); // Notificar para prosseguir ao checkout
        }
    }

    public int incrementarTentativas() {
        return ++tentativas;
    }

    public int getMembrosFamilia() {
        return membrosFamilia;
    }

    public String getNome() {
        return nome;
    }
}
