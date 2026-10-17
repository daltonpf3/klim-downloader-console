package br.com.daltonpf3.ui;

import br.com.daltonpf3.config.ApplicationPropertiesLoader;
import br.com.daltonpf3.exception.DownloaderException;
import br.com.daltonpf3.enums.DownloadType;
import br.com.daltonpf3.model.MediaOption;
import br.com.daltonpf3.model.VideoMetadata;
import br.com.daltonpf3.service.YtDlpService;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {

    private final YtDlpService ytDlpService;
    private final Scanner scanner;

    public ConsoleUI(YtDlpService ytDlpService) {
        this.ytDlpService = ytDlpService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try (scanner) {
            DownloadType mode = ApplicationPropertiesLoader.APP_DOWNLOAD_TYPE;

            System.out.println(ASCIIArt.getBanner());
            System.out.printf("Current Mode: %s%n%n", mode);
            System.out.print("Enter the video URL: ");
            String url = scanner.nextLine().trim();

            if (url.isEmpty()) {
                System.out.println("Invalid URL. Closing...");
                return;
            }

            System.out.printf("%nSearching for available %s options... Please wait.%n", mode.name().toLowerCase());
            VideoMetadata metadata = ytDlpService.fetchVideoMetadata(url, mode);
            List<MediaOption> options = metadata.options();

            if (options.isEmpty()) {
                System.out.printf("No %s options found for this URL.%n", mode.name().toLowerCase());
                return;
            }

            System.out.println("🎵 Media found: " + metadata.title());
            printOptions(options, mode);

            MediaOption selectedOption = promptOptionSelection(options);
            if (selectedOption == null) return;

            boolean convertToMp3 = (mode == DownloadType.AUDIO) && shouldConvertToMp3();

            System.out.println("\nStarting download...");
            ytDlpService.downloadMedia(url, selectedOption.formatId(), mode, convertToMp3);
            System.out.printf("%nDownload completed successfully! The file is in the '%s/' folder.%n",
                    ApplicationPropertiesLoader.APP_DOWNLOAD_FOLDER);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter only the option number.");
        } catch (DownloaderException e) {
            System.out.println("\n" + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nAn unexpected error occurred: " + e.getMessage());
        }
    }

    private void printOptions(List<MediaOption> options, DownloadType mode) {
        System.out.printf("%n=== Available %s Options ===%n", mode);
        for (int i = 0; i < options.size(); i++) {
            MediaOption f = options.get(i);
            if (mode == DownloadType.VIDEO) {
                System.out.printf("[%d] ID: %-5s | Ext: %-4s | Res: %-9s | FPS: %-3d | Bitrate: %7.1f kbps | %s (%s)%n",
                        i, f.formatId(), f.ext(), f.resolution(), f.fps(), f.bitrate(), f.getFormattedSize(), f.codec());
            } else {
                System.out.printf("[%d] ID: %-5s | Ext: %-4s | Bitrate: %6.1f kbps | %s (%s)%n",
                        i, f.formatId(), f.ext(), f.bitrate(), f.getFormattedSize(), f.codec());
            }
        }
    }

    private MediaOption promptOptionSelection(List<MediaOption> options) {
        System.out.print("\nEnter the number of the option you wish to download: ");
        int choice = Integer.parseInt(scanner.nextLine().trim());

        if (choice < 0 || choice >= options.size()) {
            System.out.println("Invalid option!");
            return null;
        }
        return options.get(choice);
    }

    private boolean shouldConvertToMp3() {
        System.out.println("ApplicationPropertiesLoader.APP_AUTO_DOWNLOAD_MP3: " + ApplicationPropertiesLoader.APP_AUTO_DOWNLOAD_MP3);
        if (ApplicationPropertiesLoader.APP_AUTO_DOWNLOAD_MP3.equals("yes")) {
            return true;
        }
        System.out.print("Do you want to automatically convert to .mp3? (y/N): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }
}
