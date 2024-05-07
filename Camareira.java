public class Camareira extends Thread {
    private Hotel hotel;

    public Camareira(Hotel hotel) {
        this.hotel = hotel;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            synchronized (hotel) {
                try {
                    hotel.wait();  // Esperar até que um quarto esteja disponível para limpeza
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                // Após ser notificado, encontrar um quarto para limpar
                Quarto quartoParaLimpar = encontrarQuartoParaLimpar();
                if (quartoParaLimpar != null) {
                    limparQuarto(quartoParaLimpar);
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
            quarto.pegarChaveDaRecepcao("Camareira");
            System.out.println("Camareira está limpando o quarto " + quarto.getNumero());

            try {
                Thread.sleep(5000); // Tempo para limpar o quarto
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            quarto.setLimpo(true);
            quarto.setVago(true); // Marcar o quarto como vago somente depois da limpeza
            System.out.println("Camareira terminou de limpar o quarto " + quarto.getNumero());
            quarto.deixarChaveNaRecepcao("Camareira"); // A camareira deixa a chave na recepção
        }
        synchronized (hotel) {
            hotel.notifyAll(); // Notifica que a limpeza do quarto foi concluída
        }
    }

}
