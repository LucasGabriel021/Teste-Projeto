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
            checkOut(this);
            
            // Após ser notificado que o check-out foi realizado
            System.out.println(getNome() + " e seu grupo de " + getMembrosFamilia()  + " pessoas, completou o check-out e está deixando o hotel.");

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
                    synchronized (this) {
                        this.concluirEstadia(); // Permitir que o hospede conclua sua estadia
                        this.notifyAll();
                    }
                    System.out.println(hospede.getNome() + " e seu grupo de " + hospede.getMembrosFamilia() + " pessoas fizeram o check-out no hotel.");
                    break;
                }
            }
        }
    }
    
    public void checkOut() {
    	
    }
    
    public int incrementarTentativas() {
        return tentativas++;
    }

    public int getMembrosFamilia() {
        return membrosFamilia;
    }

    public String getNome() {
        return nome;
    }
}
