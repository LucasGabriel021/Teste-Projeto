public class Camareira extends Thread {
    private Hotel hotel;

    public Camareira(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            Quarto quartoParaLimpar = null;
            synchronized (hotel) {
                while ((quartoParaLimpar = encontrarQuartoParaLimpar()) == null) {
                    try {
                        hotel.wait();  // Espera até que um quarto esteja pronto para limpeza
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            if (quartoParaLimpar != null) {
                limparQuarto(quartoParaLimpar);
                synchronized (hotel) {
                    quartoParaLimpar.setVago(true);  // Marca o quarto como vago após a limpeza
                    quartoParaLimpar.setChaveNaRecepcao(false);  // A chave retorna à recepção
                    hotel.notifyAll();  // Notifica que a limpeza do quarto foi concluída
                }
            }
        }
    }

    private Quarto encontrarQuartoParaLimpar() {
        synchronized (hotel) {
            for (Quarto quarto : hotel.getQuartos()) {
                if (quarto.isChaveNaRecepcao() && quarto.isVago() && !quarto.isLimpo()) {
                    return quarto;
                }
            }
            return null;
        }
    }

    public void limparQuarto(Quarto quarto) {
        synchronized (quarto) {
            System.out.println("Camareira está limpando o quarto " + quarto.getNumero());
            try {
                Thread.sleep(5000); // Tempo para limpar o quarto
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            quarto.setLimpo(true);
            quarto.setVago(true); // Marcar o quarto como vago somente depois da limpeza
            quarto.setChaveNaRecepcao(false); // Chave não está na recepção porque o quarto está pronto
            System.out.println("Camareira terminou de limpar o quarto " + quarto.getNumero());
        }
        synchronized (hotel) {
            hotel.notifyAll(); // Notifica que a limpeza do quarto foi concluída
        }
    }

}
