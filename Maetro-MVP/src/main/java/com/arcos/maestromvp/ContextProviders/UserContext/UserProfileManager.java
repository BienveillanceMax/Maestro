package com.arcos.maestromvp.ContextProviders.UserContext;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Service
public class UserProfileManager {

    private static final List<String> GENRES = List.of(
            "Rock", "Classic Rock", "Hard Rock", "Metal", "Pop", "K-Pop", "Hip Hop",
            "Rap", "R&B", "Jazz", "Blues", "Electronic", "Techno", "House", "Country",
            "Folk", "Reggae", "Latin", "Classical", "Lo-Fi"
    );

    public void completeUserProfile(UserProfile userProfile) {
        // On construit le terminal une seule fois.
        // Note: system(true) se connecte au terminal de l'OS.
        try (Terminal terminal = TerminalBuilder.builder().system(true).dumb(true).build()) {

            PrintWriter writer = terminal.writer();

            writer.println("\n==========================================");
            writer.println("       Welcome to Maestro MVP Setup       ");
            writer.println("==========================================\n");
            writer.flush(); // Important avec JLine pour forcer l'affichage

            // 1. Simple Boolean (Y/N)
            boolean openToDiscovery = askBoolean(terminal, "Are you open to discovering new music?");

            // 2. Interactive Like Selection
            Set<String> likedGenres = runInteractiveSelector(
                    terminal,
                    "Select Genres You LIKE (Arrow Keys to Move, SPACE to Toggle, ENTER to Confirm)",
                    GENRES,
                    Collections.emptySet()
            );

            // 3. Interactive Hate Selection (Pass liked as excluded)
            Set<String> hatedGenres = runInteractiveSelector(
                    terminal,
                    "Select Genres You HATE",
                    GENRES,
                    likedGenres
            );

            // 4. Free text input
            writer.println("\nTell us anything else about your taste:");

            // Le terminal peut être resté en mode 'raw' à cause du sélecteur,
            // le LineReader gère cela automatiquement mais c'est bien de s'assurer que l'écho est actif.
            LineReader lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .build();

            String additionalInfo = lineReader.readLine("> ");

            // Save
            userProfile.setUserProfile(new ArrayList<>(likedGenres), new ArrayList<>(hatedGenres), openToDiscovery, additionalInfo);

            writer.println("Profile saved successfully!");
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Affiche une liste interactive avec cases à cocher.
     */
    private Set<String> runInteractiveSelector(Terminal terminal, String title, List<String> options, Set<String> disabledOptions) {
        terminal.enterRawMode(); // Passage explicite en mode RAW pour capturer les touches sans Entrée
        terminal.puts(InfoCmp.Capability.cursor_invisible); // Cache le curseur

        PrintWriter writer = terminal.writer();
        Set<String> selected = new HashSet<>();
        int selectedIndex = 0;
        int pageSize = 10;
        int startRow = 0;

        NonBlockingReader reader = terminal.reader();

        // On calcule la taille totale de l'interface pour savoir combien de lignes effacer ensuite
        // Titre (2 lignes) + PageSize + Footer (1 ligne)
        int totalRenderLines = pageSize + 3;

        boolean running = true;
        boolean firstRun = true;

        while (running) {
            // 1. Gestion du scroll
            if (selectedIndex >= startRow + pageSize) startRow = selectedIndex - pageSize + 1;
            if (selectedIndex < startRow) startRow = selectedIndex;

            // 2. Rendu de l'interface
            // Si ce n'est pas le premier passage, on remonte le curseur pour écraser l'affichage précédent
            if (!firstRun) {
                writer.print("\033[" + totalRenderLines + "A"); // Code ANSI "Cursor Up N lines"
            }

            // Construction de l'affichage en mémoire (String) pour éviter le scintillement
            StringBuilder buffer = new StringBuilder();

            // Header
            buffer.append("\u001B[1m").append(title).append("\u001B[0m\n");
            buffer.append("--------------------------------\n");

            // List items
            for (int i = 0; i < pageSize; i++) {
                int dataIndex = startRow + i;
                if (dataIndex < options.size()) {
                    String genre = options.get(dataIndex);
                    boolean isCursorOnRow = (dataIndex == selectedIndex);
                    boolean isSelected = selected.contains(genre);
                    boolean isDisabled = disabledOptions.contains(genre);

                    String prefix = isCursorOnRow ? " > " : "   ";
                    String checkbox = isSelected ? "[*]" : "[ ]";
                    if (isDisabled) checkbox = " - ";

                    String line = String.format("%s%s %s", prefix, checkbox, genre);

                    if (isCursorOnRow) {
                        buffer.append("\u001B[46m\u001B[30m").append(line).append("\u001B[0m"); // Cyan BG
                    } else if (isDisabled) {
                        buffer.append("\u001B[90m").append(line).append("\u001B[0m"); // Gray text
                    } else {
                        buffer.append(line);
                    }
                }
                // Important : il faut effacer le reste de la ligne au cas où le texte précédent était plus long
                buffer.append("\u001B[K\n");
            }

            // Footer / Scroll info
            String scrollInfo = String.format("... %d more (Item %d of %d) ...",
                    Math.max(0, options.size() - (startRow + pageSize)), selectedIndex + 1, options.size());
            buffer.append(scrollInfo).append("\u001B[K"); // Clear rest of line

            writer.println(buffer.toString());
            writer.flush();
            firstRun = false;

            // 3. Lecture des touches
            int read;
            try {
                read = reader.read();
            } catch (IOException e) { break; }

            if (read == 13) { // ENTER
                running = false;
            } else if (read == 32) { // SPACE
                String current = options.get(selectedIndex);
                if (!disabledOptions.contains(current)) {
                    if (selected.contains(current)) selected.remove(current);
                    else selected.add(current);
                }
            } else if (read == 27) { // ANSI Escape
                try {
                    if (reader.read() == 91) { // '['
                        int arrow = reader.read();
                        if (arrow == 65) { // UP
                            if (selectedIndex > 0) selectedIndex--;
                        } else if (arrow == 66) { // DOWN
                            if (selectedIndex < options.size() - 1) selectedIndex++;
                        }
                    }
                } catch (IOException e) { break; }
            } else if (read == 'q' || read == 'Q') {
                running = false; // Optionnel : quitter avec Q
            }
        }

        // Nettoyage final
        terminal.puts(InfoCmp.Capability.cursor_visible);
        return selected;
    }

    private boolean askBoolean(Terminal terminal, String prompt) {
        PrintWriter writer = terminal.writer();
        writer.println(prompt + " (y/n)");
        writer.flush();

        terminal.enterRawMode();

        while(true) {
            int c;
            try {
                c = terminal.reader().read();
            } catch (IOException e) {
                return false;
            }

            if (c == 'y' || c == 'Y') {
                writer.println("Yes");
                writer.flush();
                return true;
            }
            if (c == 'n' || c == 'N') {
                writer.println("No");
                writer.flush();
                return false;
            }
        }
    }
}
