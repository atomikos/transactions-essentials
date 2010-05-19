<!ELEMENT xi:include (xi:fallback?) >
<!ATTLIST xi:include
xmlns:xi            CDATA       #FIXED       "http://www.w3.org/2001/XInclude"
href                CDATA       #REQUIRED
parse               (xml|text)  "xml"
xpointer            CDATA       #IMPLIED
encoding            CDATA       #IMPLIED
accept              CDATA       #IMPLIED
accept-charset      CDATA       #IMPLIED
accept-language     CDATA       #IMPLIED >
<!ELEMENT xi:fallback ANY >
<!ATTLIST xi:fallback
xmlns:xi            CDATA       #FIXED "http://www.w3.org/2001/XInclude" >
<!ENTITY % local.preface.class    "| xi:include" >
<!ENTITY % local.part.class       "| xi:include" >
<!ENTITY % local.chapter.class    "| xi:include" >
<!ENTITY % local.divcomponent.mix "| xi:include" >
<!ENTITY % local.para.char.mix    "| xi:include" >
<!ENTITY % local.info.class       "| xi:include" >
<!ENTITY % local.common.attrib    "xml:base CDATA #IMPLIED
xmlns:xi            CDATA       #FIXED       'http://www.w3.org/2001/XInclude'" >

