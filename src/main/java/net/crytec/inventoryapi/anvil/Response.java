package net.crytec.inventoryapi.anvil;

public class Response {

  /**
   * The text that is to be displayed to the user
   */
  private final String text;

  /**
   * Creates a response to the user's input
   *
   * @param text The text that is to be displayed to the user, which can be null to close the inventory
   */
  private Response(final String text) {
    this.text = text;
  }

  /**
   * Gets the text that is to be displayed to the user
   *
   * @return The text that is to be displayed to the user
   */
  public String getText() {
    return text;
  }

  /**
   * Returns an {@link Response} object for when the anvil GUI is to close
   *
   * @return An {@link Response} object for when the anvil GUI is to close
   */
  public static Response close() {
    return new Response(null);
  }

  /**
   * Returns an {@link Response} object for when the anvil GUI is to display text to the user
   *
   * @param text The text that is to be displayed to the user
   * @return An {@link Response} object for when the anvil GUI is to display text to the user
   */
  public static Response text(final String text) {
    return new Response(text);
  }
}
