package com.project.shared.client.html5;

import com.google.gwt.dom.client.Node;

/* Selection IDL from http://html5.org/specs/dom-range.html#selection
 * interface Selection {
      readonly attribute Node? anchorNode;
      readonly attribute unsigned int anchorOffset;
      readonly attribute Node? focusNode;
      readonly attribute unsigned int focusOffset;

      readonly attribute boolean isCollapsed;
      void               collapse(Node parentNode, unsigned int offset);
      void               collapseToStart();
      void               collapseToEnd();

      void               extend(Node parentNode, unsigned int offset);
      void               modify(DOMString alter, DOMString direction, DOMString granularity);

      void               selectAllChildren(Node parentNode);
      void               deleteFromDocument();

      readonly attribute unsigned int rangeCount;
      Range              getRangeAt(unsigned int index);
      void               addRange(Range range);
      void               removeRange(Range range);
      void               removeAllRanges();

      stringifier;
  }
 */
public interface Selection  {
    Node getAnchorNode();
    int getAnchorOffset();
    Node getFocusNode();
    int getFocusOffset();

    boolean isCollapsed();

    void collapse(Node parentNode, int offset);
    void collapseToStart();
    void collapseToEnd();

    void extend(Node parentNode, int offset);
    void modify(String alter, String direction, String granularity);

    void selectAllChildren(Node parentNode);
    void deleteFromDocument();

    int getRangeCount();
    Range getRangeAt(int index);
    void addRange(Range range);
    void removeRange(Range range);
    void removeAllRanges();
}