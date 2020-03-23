package br.com.sankhya.mm.boleto;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    public static String toString(final Exception exception) {
        try (final StringWriter stringWriter = new StringWriter();
             final PrintWriter printWriter = new PrintWriter(stringWriter)) {
            exception.printStackTrace(printWriter);

            return stringWriter.getBuffer().toString();

        } catch (final Exception e) {
            return exception.toString();
        }
    }
}