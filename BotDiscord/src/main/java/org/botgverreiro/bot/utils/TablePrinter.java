package org.botgverreiro.bot.utils;

import java.util.List;

public class TablePrinter {

    public static String formatTable(List<String> header, List<List<String>> table) {
        StringBuilder formattedTable = new StringBuilder();

        if (table == null || table.isEmpty()) {
            formattedTable.append("Table is empty.");
            return formattedTable.toString();
        }

        // Determine the maximum number of columns in any row
        int maxColumns = 0;
        for (List<String> row : table) {
            if (row.size() > maxColumns) {
                maxColumns = row.size();
            }
        }

        // Calculate the maximum width of each column
        int[] columnWidths = new int[maxColumns];
        for (List<String> row : table) {
            for (int i = 0; i < row.size(); i++) {
                if (row.get(i).length() > columnWidths[i]) {
                    columnWidths[i] = row.get(i).length();
                }
            }
        }
        appendLine(formattedTable, columnWidths, 0);

        // Append the header row
        appendRow(formattedTable, header, columnWidths);

        // Append a line between header and body
        appendLine(formattedTable, columnWidths, 1);

        // Append the rest of the table content
        table.forEach(t -> appendRow(formattedTable, t, columnWidths));
        appendLine(formattedTable, columnWidths, 2);

        return formattedTable.toString();
    }

    // Helper method to append a row to the formatted table
    private static void appendRow(StringBuilder table, List<String> row, int[] columnWidths) {
        table.append("│");
        for (int i = 0; i < row.size(); i++) {
            table.append(padRight(row.get(i), columnWidths[i])).append("  ");
        }
        table.append("│\n");
    }

    // Helper method to append a line separator between header and body
    private static void appendLine(StringBuilder table, int[] columnWidths, int code) {
        if (code == 0)
            table.append("┌");
        else if (code == 1)
            table.append("├");
        else if (code == 2)
            table.append("└");
        for (int width : columnWidths) {
            table.append("─".repeat(Math.max(1, width + 2)));
        }
        if (code == 0)
            table.append("┐");
        else if (code == 1)
            table.append("┤");
        else if (code == 2)
            table.append("┘");
        table.append("\n");
    }

    // Helper method to pad a string to the right with spaces
    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
