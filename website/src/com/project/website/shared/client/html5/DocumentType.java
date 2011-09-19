package com.project.website.shared.client.html5;

/* DocumentType IDL from http://dvcs.w3.org/hg/domcore/raw-file/tip/Overview.html#documentfragment
 * interface DocumentType : Node {
  readonly attribute DOMString name;
  readonly attribute DOMString publicId;
  readonly attribute DOMString systemId;
};
 */
public interface DocumentType
{
    String getName();
    String getPublicId();
    String getSystemId();
}
