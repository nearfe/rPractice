package com.conaxgames.util.namemc;

import java.net.URL;
import java.util.Scanner;

public class NameMCAPI {
    public static boolean hasVoted(String uuid) throws Exception {
        try (Scanner scanner = new Scanner(new URL("https://api.namemc.com/server/minemen.club/likes?profile=" + uuid)
                .openStream()).useDelimiter("\\A")) {
            return Boolean.valueOf(scanner.next());
        }
    }
}
