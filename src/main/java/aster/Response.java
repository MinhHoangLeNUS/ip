package aster;

/**
 * Aster's reply to one message, in a form a graphical interface can show.
 *
 * <p>The message holds every line Aster says in reply, joined by line breaks. Unlike
 * the text interface, it carries no dividers, because a graphical interface frames each
 * reply in its own way.
 *
 * @param message the reply to show, with its lines separated by {@code \n}.
 * @param isExit whether the conversation is over, so that no further message should be
 *     taken.
 */
public record Response(String message, boolean isExit) {
}
