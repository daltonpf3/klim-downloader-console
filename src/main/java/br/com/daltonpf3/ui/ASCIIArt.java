package br.com.daltonpf3.ui;

import br.com.daltonpf3.config.ApplicationPropertiesLoader;

public class ASCIIArt {

    public static String getBanner() {
        return ASCII_ART_V1 + ASCII_ART_DESCRIPTION_V1;
    }

    private static final String ASCII_ART_V1 =
        """
         _  ___     _____ __  __
        | |/ / |   |_   _|  \\/  |
        | ' /| |     | | | |\\/| |
        | . \\| |___  | | | |  | |
        |_|\\_\\_____||___||_|  |_|
         ____   _____        ___   _ _     ___    _    ____  _____ ____
        |  _ \\ / _ \\ \\      / / \\ | | |   / _ \\  / \\  |  _ \\| ____|  _ \\
        | | | | | | \\ \\ /\\ / /|  \\| | |  | | | |/ _ \\ | | | |  _| | |_) |
        | |_| | |_| |\\ V  V / | |\\  | |__| |_| / ___ \\| |_| | |___|  _ <
        |____/ \\___/  \\_/\\_/  |_| \\_|_____\\___/_/   \\_\\____/|_____|_| \\_\\
          ____ ___  _   _ ____   ___  _     _____
         / ___/ _ \\| \\ | / ___| / _ \\| |   | ____|
        | |  | | | |  \\| \\___ \\| | | | |   |  _|
        | |__| |_| | |\\  |___) | |_| | |___| |___
         \\____\\___/|_| \\_|____/ \\___/|_____|_____|

        """;

    private static final String ASCII_ART_DESCRIPTION_V1 =
        """
              App Name: %s
           App Version: %s
         Author's Name: %s
        App Repository: %s
        """.formatted(ApplicationPropertiesLoader.APP_NAME,
                      ApplicationPropertiesLoader.APP_VERSION,
                      ApplicationPropertiesLoader.APP_AUTHOR_NAME,
                      ApplicationPropertiesLoader.APP_REPOSITORY);

}
