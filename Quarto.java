import java.util.ArrayList;
import java.util.List;

public class Quarto {
    private int numero;
    private List<Hospede> hospedes;
    private boolean chaveNaRecepcao;
    private boolean vago; // Indica se o quarto esta vago
    private int ocupacaoAtual;
    public static final int CAPACIDADE_MAXIMA = 4;
    private boolean limpo = false; // Estado para rastrear se o quarto foi limpo

    public Quarto(int numero) {
        this.numero = numero;
        this.hospedes = new ArrayList<>();
        this.chaveNaRecepcao = true;
        this.ocupacaoAtual = 0;
        this.vago = true;  // Inicialmente, todos os quartos estão vazios
    }

    public synchronized boolean isVago() {
        return ocupacaoAtual == 0;
    }

    public synchronized List<Hospede> getHospedes() {
        return new ArrayList<>(hospedes);  // Retorna uma cópia para prevenir nodificações externas
    }

    public synchronized boolean adicionarHospede(Hospede hospede, int numeroDeMembros) {
        if (ocupacaoAtual + numeroDeMembros <= CAPACIDADE_MAXIMA) {
            hospedes.add(hospede);
            ocupacaoAtual += numeroDeMembros;
            setVago(false);
            System.out.println("Hospede " + hospede.getNome() + " e " + hospede.getMembrosFamilia() + " adicionados ao quarto de número: " + getNumero());
            return true;
        }
        System.out.println("Falha ao adicionar hospede " + hospede.getNome() + " ao quarto de número " + getNumero() + " O quarto esta cheio ou não está vago!");
        return false;
    }

    public synchronized boolean removerHospede(Hospede hospede) {
        if (hospedes.remove(hospede)) {
            ocupacaoAtual -= 1; // Ajuste conforme a realidade de sua aplicação
            System.out.println("Hospede " + hospede.getNome() + " e seus familiares " + hospede.getMembrosFamilia() + " removido do quarto " + numero);
            if (ocupacaoAtual == 0) {
                chaveNaRecepcao = true;
                setVago(true);  // O quarto está agora vago
                System.out.println("Quarto " + numero + " agora está vago.");
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isChaveNaRecepcao() {
        return chaveNaRecepcao;
    }

    public synchronized void setChaveNaRecepcao(boolean chaveNaRecepcao) {
        this.chaveNaRecepcao = chaveNaRecepcao;
    }

    public int getNumero() {
        return numero;
    }

    public synchronized void setVago(boolean vago) {
        this.vago = vago;
    }

    public boolean isLimpo() {
        return limpo;
    }

    public void setLimpo(boolean limpo) {
        this.limpo = limpo;
    }
}
