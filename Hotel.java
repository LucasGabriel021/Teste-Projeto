import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Hotel {
    List<Quarto> quartos;
    private BlockingQueue<Hospede> filaEspera;
    private AtomicInteger hospedesAtivos = new AtomicInteger(0);
    List<Camareira> camareiras;
    List<Recepcionista> recepcionistas;
    private Random random;

    public Hotel() {
        quartos = new ArrayList<>();
        camareiras = new ArrayList<>();
        recepcionistas = new ArrayList<>();
        random = new Random();

        // Inicializar os quartos
        for (int i = 0; i < 10; i++) {
            quartos.add(new Quarto(i + 1));
        }

        // Inicializar as camareiras
        for (int i = 0; i < 10; i++) {
            camareiras.add(new Camareira(this));
        }

        // Inicializar os recepcionistas
        for (int i = 0; i < 5; i++) {
            recepcionistas.add(new Recepcionista(this));
        }
    }

    public List<Quarto> getQuartos() {
        return quartos;
    }

    public synchronized Quarto getVagoQuarto() {
        for(Quarto quarto : quartos) {
            if(quarto.isVago()) {
                quarto.setVago(false); // Marcar como ocupado imediatamente
                return quarto;
            }
        }
        return null;
    }

    // Retorna um recepcionista aleatória
    public Recepcionista getRecepciistaAleatoria() {
        int index = random.nextInt(recepcionistas.size());
        return recepcionistas.get(index);
    }

    public synchronized void quartoLiberado() {
        notifyAll();  // Notifica todas as threads esperando que um quarto foi liberado
    }

}
