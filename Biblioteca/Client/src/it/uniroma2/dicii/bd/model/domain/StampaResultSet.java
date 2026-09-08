package it.uniroma2.dicii.bd.model.domain;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class StampaResultSet {
    public static void printResultsTable(ResultSet rs, OutputStream output) throws SQLException {
        // Set up the output stream
        PrintWriter out = new PrintWriter(output);

        // Get some "meta data" (column names, etc.) about the results
        ResultSetMetaData metadata = rs.getMetaData();

        // Variables to hold important data about the table to be displayed
        int numcols = metadata.getColumnCount(); // how many columns
        String[] labels = new String[numcols]; // the column labels
        int[] colwidths = new int[numcols]; // the width of each
        int[] colpos = new int[numcols]; // start position of each
        int linewidth; // total width of table

        // Figure out how wide the columns are, where each one begins,
        // how wide each row of the table will be, etc.
        linewidth = 1; // for the initial '|'.
        for (int i = 0; i < numcols; i++) { // for each column
            colpos[i] = linewidth; // save its position
            labels[i] = metadata.getColumnLabel(i + 1); // get its label

            int size = metadata.getColumnDisplaySize(i + 1);
            if (size == -1) size = 30; // Some drivers return -1...
            if (size > 500) size = 30; // Don't allow unreasonable sizes

            int labelsize = labels[i].length();
            if (labelsize > size) size = labelsize;

            colwidths[i] = size + 1; // save the column the size
            linewidth += colwidths[i] + 2; // increment total size
        }

        // Create a horizontal divider line we use in the table.
        StringBuffer divider = new StringBuffer(linewidth);
        StringBuffer blankline = new StringBuffer(linewidth);
        for (int i = 0; i < linewidth; i++) {
            divider.insert(i, '-');
            blankline.insert(i, " ");
        }

        for (int i = 0; i < numcols; i++)
            divider.setCharAt(colpos[i] - 1, '+');
        divider.setCharAt(linewidth - 1, '+');

        out.println(divider);

        StringBuffer line = new StringBuffer(blankline.toString());
        line.setCharAt(0, '|');
        for (int i = 0; i < numcols; i++) {
            int pos = colpos[i] + 1 + (colwidths[i] - labels[i].length()) / 2;
            overwrite(line, pos, labels[i]);
            overwrite(line, colpos[i] + colwidths[i], " |");
        }

        out.println(line);
        out.println(divider);

        while (rs.next()) {
            line = new StringBuffer(blankline.toString());
            line.setCharAt(0, '|');
            for (int i = 0; i < numcols; i++) {
                Object value = rs.getObject(i + 1);
                if (value != null)
                    overwrite(line, colpos[i] + 1, value.toString().trim());
                overwrite(line, colpos[i] + colwidths[i], " |");
            }
            out.println(line);
        }

        out.println(divider);
        out.flush();
    }

    /**
     * Metodo helper mancante nello snippet originale:
     * sovrascrive i caratteri nel StringBuffer partendo da una posizione specifica.
     */
    private static void overwrite(StringBuffer buffer, int pos, String str) {
        for (int i = 0; i < str.length(); i++) {
            // Verifica di non eccedere i limiti del buffer
            if (pos + i < buffer.length()) {
                buffer.setCharAt(pos + i, str.charAt(i));
            }
        }
    }


}
