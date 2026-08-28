/**
 * Entry point for the Aster chatbot.
 *
 * <p>At this stage Aster greets the user and then exits. It does not read any
 * input or keep any state.
 */
public class Aster {
    /**
     * Greets the user and exits.
     *
     * @param args command line arguments; not used
     */
    public static void main(String[] args) {
        final String divider = "____________________________________________________________";

        System.out.println(divider);
        System.out.println("Hello! I'm Aster.");
        System.out.println("I'm a simple chatbot, and I'm glad you're here.");
        System.out.println("What can I do for you?");
        System.out.println(divider);
        System.out.println("Goodbye for now. Take care!");
        System.out.println(divider);
    }
}
