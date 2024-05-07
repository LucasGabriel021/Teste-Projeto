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
            System.out.println(hospede.getNome() + " e seu grupo de " + hospede.getMembrosFamilia() + " pessoas " + " foram adicionados ao quarto de número: " + getNumero());
            return true;
        }
        System.out.println("Falha ao adicionar " + hospede.getNome() + " ao quarto de número " + getNumero() + " O quarto esta cheio ou não está vago!");
        if(numeroDeMembros > CAPACIDADE_MAXIMA) {
            System.out.println("Devido ao tamanho do grupo, serão alocados em mais de um quarto.");
        }
        return false;
    }

    public synchronized boolean removerHospede(Hospede hospede) {
        if (hospedes.remove(hospede)) {
            ocupacaoAtual -= 1; // Ajuste conforme a realidade de sua aplicação
            System.out.println(hospede.getNome() + " e seu grupo de " + hospede.getMembrosFamilia()  + " pessoas foram removido(s) do quarto " + numero);
            if (ocupacaoAtual == 0) {
                chaveNaRecepcao = true;
                setVago(true);  // O quarto está agora vago
                System.out.println("O quarto " + numero + " agora está vago.");
            }
            return true;
        }
        return false;
    }

    public synchronized boolean isChaveNaRecepcao() {
        return chaveNaRecepcao;
    }

    public synchronized void deixarChaveNaRecepcao(String nome) {
        this.chaveNaRecepcao = true;
        System.out.println("Chave do quarto " + numero + " deixada na recepção por " + nome);
    }

    public synchronized void pegarChaveDaRecepcao(String nome) {
        this.chaveNaRecepcao = false;
        System.out.println("Chave do quarto " + numero + " retirada da recepção por " + nome);
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
