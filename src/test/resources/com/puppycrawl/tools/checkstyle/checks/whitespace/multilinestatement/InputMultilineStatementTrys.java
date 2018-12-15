package com.puppycrawl.tools.checkstyle.checks.whitespace.multilinestatement;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InputMultilineStatementTrys
{
    public void tryResourceStatements()
            throws IOException
    {
        String line;

        try (BufferedReader reader = new BufferedReader(new StringReader("toPrint")))
        {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            line = "";
        }
        System.out.print("Line: " + line);

        line = "";
        try (BufferedReader reader = new BufferedReader(new StringReader("toPrint")))
        {
            line = reader.readLine();
        } catch (FileNotFoundException fnf) {
            System.out.println("The file is not found!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new StringReader("toPrint")))
        {
            line = reader.readLine();
        }
        System.out.print("Line: " + line);

        // Should still work
        try (BufferedReader reader = new BufferedReader(new StringReader("toPrint")))
        {
            reader.readLine();
        }
    }

    public void regularTryStatements() throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;

        try
        {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            line = "";
        }
        System.out.print("Line: " + line);

        line = "";
        try {
            line = reader.readLine();
        } catch (FileNotFoundException fnf) {
            System.out.println("StdIn is not found?!?");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print("Line: " + line);

        try {
            System.out.println(reader.readLine());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            System.out.print(reader.readLine());
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }

        try {
            System.out.print(reader.readLine());
        } finally {
            System.out.println();
        }
    }

    public static void fromItTest(String[] expected, String messageFileName) throws Exception {
        final List<Integer> theWarnings = new ArrayList<>();

        // process each of the lines
        try (ByteArrayInputStream inputStream =
                     new ByteArrayInputStream("aasdf".getBytes());
             LineNumberReader lnr = new LineNumberReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            int previousLineNumber = 0;

            for (int i = 0; i < expected.length; i++) {
                final String expectedResult = messageFileName + ":" + expected[i];
                final String actual = lnr.readLine();
                assertEquals("error message " + i, expectedResult, actual);

                String parseInt = actual.substring(5);
                parseInt = parseInt.substring(parseInt.indexOf(':') + 1);
                parseInt = parseInt.substring(0, parseInt.indexOf(':'));
                final int lineNumber = Integer.parseInt(parseInt);

                assertTrue("input file is expected to have a warning comment on line number "
                        + lineNumber, previousLineNumber == lineNumber
                        || theWarnings.remove((Integer) lineNumber));

                previousLineNumber = lineNumber;
            }

            assertEquals("unexpected output: " + lnr.readLine(),
                    expected.length, expected);

            assertEquals("unexpected warnings " + expected, 0, expected.length);
        }
    }
}
