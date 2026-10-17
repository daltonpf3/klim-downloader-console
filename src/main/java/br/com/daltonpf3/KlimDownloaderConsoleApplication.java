package br.com.daltonpf3;

import br.com.daltonpf3.service.YtDlpService;
import br.com.daltonpf3.ui.ConsoleUI;

public class KlimDownloaderConsoleApplication {
    public static void main(String[] args) {
        YtDlpService service = new YtDlpService();
        ConsoleUI ui = new ConsoleUI(service);
        ui.start();
    }
}
