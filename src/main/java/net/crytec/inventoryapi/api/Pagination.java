package net.crytec.inventoryapi.api;

import java.util.Arrays;

public class Pagination {

  private int currentPage;
  private ClickableItem[] items = new ClickableItem[0];
  private int entriesPerPage = 5;


  public ClickableItem[] getPageItems() {
    return Arrays.copyOfRange(items, currentPage * entriesPerPage, (currentPage + 1) * entriesPerPage);
  }

  public int getPage() {
    return currentPage;
  }


  public Pagination page(final int page) {
    currentPage = page;
    return this;
  }


  public boolean isFirst() {
    return currentPage == 0;
  }


  public boolean isLast() {
    final int pageCount = (int) Math.ceil((double) items.length / entriesPerPage);
    return currentPage >= pageCount - 1;
  }


  public Pagination first() {
    currentPage = 0;
    return this;
  }


  public Pagination previous() {
    if (!isFirst()) {
      currentPage--;
    }

    return this;
  }


  public Pagination next() {
    if (!isLast()) {
      currentPage++;
    }

    return this;
  }


  public Pagination last() {
    currentPage = items.length / entriesPerPage;
    return this;
  }


  public Pagination addToIterator(final SlotIterator iterator) {
    for (final ClickableItem item : getPageItems()) {
      iterator.next().set(item);

      if (iterator.ended()) {
        break;
      }
    }

    return this;
  }


  public Pagination setItems(final ClickableItem... items) {
    this.items = items;
    return this;
  }


  public Pagination setItemsPerPage(final int entriesPerPage) {
    this.entriesPerPage = entriesPerPage;
    return this;
  }

}
