<!-- ...................................................................... -->
<!-- DocBook XML information pool module V4.3 ............................. -->
<!-- File dbpoolx.mod ..................................................... -->

<!-- Copyright 1992-2002 HaL Computer Systems, Inc.,
     O'Reilly & Associates, Inc., ArborText, Inc., Fujitsu Software
     Corporation, Norman Walsh, Sun Microsystems, Inc., and the
     Organization for the Advancement of Structured Information
     Standards (OASIS).

     $Id: dbpoolx.mod,v 1.1.1.1 2006/08/29 10:00:53 guy Exp $

     Permission to use, copy, modify and distribute the DocBook XML DTD
     and its accompanying documentation for any purpose and without fee
     is hereby granted in perpetuity, provided that the above copyright
     notice and this paragraph appear in all copies.  The copyright
     holders make no representation about the suitability of the DTD for
     any purpose.  It is provided "as is" without expressed or implied
     warranty.

     If you modify the DocBook XML DTD in any way, except for declaring and
     referencing additional sets of general entities and declaring
     additional notations, label your DTD as a variant of DocBook.  See
     the maintenance documentation for more information.

     Please direct all questions, bug reports, or suggestions for
     changes to the docbook@lists.oasis-open.org mailing list. For more
     information, see http://www.oasis-open.org/docbook/.
-->

<!-- ...................................................................... -->

<!-- This module contains the definitions for the objects, inline
     elements, and so on that are available to be used as the main
     content of DocBook documents.  Some elements are useful for general
     publishing, and others are useful specifically for computer
     documentation.

     This module has the following dependencies on other modules:

     o It assumes that a %notation.class; entity is defined by the
       driver file or other high-level module.  This entity is
       referenced in the NOTATION attributes for the graphic-related and
       ModeSpec elements.

     o It assumes that an appropriately parameterized table module is
       available for use with the table-related elements.

     In DTD driver files referring to this module, please use an entity
     declaration that uses the public identifier shown below:

     <!ENTITY % dbpool PUBLIC
     "-//OASIS//ELEMENTS DocBook XML Information Pool V4.3//EN"
     "dbpoolx.mod">
     %dbpool;

     See the documentation for detailed information on the parameter
     entity and module scheme used in DocBook, customizing DocBook and
     planning for interchange, and changes made since the last release
     of DocBook.
-->

<!-- ...................................................................... -->
<!-- General-purpose semantics entities ................................... -->

<!ENTITY % yesorno.attvals	"CDATA">

<!-- ...................................................................... -->
<!-- Entities for module inclusions ....................................... -->

<!ENTITY % dbpool.redecl.module "IGNORE">

<!-- ...................................................................... -->
<!-- Entities for element classes and mixtures ............................ -->

<!-- "Ubiquitous" classes: ndxterm.class and beginpage -->

<!ENTITY % local.ndxterm.class "">
<!ENTITY % ndxterm.class
		"indexterm %local.ndxterm.class;">

<!-- Object-level classes ................................................. -->

<!ENTITY % local.list.class "">
<!ENTITY % list.class
		"calloutlist|glosslist|itemizedlist|orderedlist|segmentedlist
		|simplelist|variablelist %local.list.class;">

<!ENTITY % local.admon.class "">
<!ENTITY % admon.class
		"caution|important|note|tip|warning %local.admon.class;">

<!ENTITY % local.linespecific.class "">
<!ENTITY % linespecific.class
		"literallayout|programlisting|programlistingco|screen
		|screenco|screenshot %local.linespecific.class;">

<!ENTITY % local.method.synop.class "">
<!ENTITY % method.synop.class
		"constructorsynopsis
                 |destructorsynopsis
                 |methodsynopsis %local.method.synop.class;">

<!ENTITY % local.synop.class "">
<!ENTITY % synop.class
		"synopsis|cmdsynopsis|funcsynopsis
                 |classsynopsis|fieldsynopsis
                 |%method.synop.class; %local.synop.class;">

<!ENTITY % local.para.class "">
<!ENTITY % para.class
		"formalpara|para|simpara %local.para.class;">

<!ENTITY % local.informal.class "">
<!ENTITY % informal.class
		"address|blockquote
                |graphic|graphicco|mediaobject|mediaobjectco
                |informalequation
		|informalexample
                |informalfigure
                |informaltable %local.informal.class;">

<!ENTITY % local.formal.class "">
<!ENTITY % formal.class
		"equation|example|figure|table %local.formal.class;">

<!-- The DocBook TC may produce an official EBNF module for DocBook. -->
<!-- This PE provides the hook by which it can be inserted into the DTD. -->
<!ENTITY % ebnf.block.hook "">

<!ENTITY % local.compound.class "">
<!ENTITY % compound.class
		"msgset|procedure|sidebar|qandaset|task
                 %ebnf.block.hook;
                 %local.compound.class;">

<!ENTITY % local.genobj.class "">
<!ENTITY % genobj.class
		"anchor|bridgehead|remark|highlights
		%local.genobj.class;">

<!ENTITY % local.descobj.class "">
<!ENTITY % descobj.class
		"abstract|authorblurb|epigraph
		%local.descobj.class;">

<!-- Character-level classes .............................................. -->

<!ENTITY % local.xref.char.class "">
<!ENTITY % xref.char.class
		"footnoteref|xref %local.xref.char.class;">

<!ENTITY % local.gen.char.class "">
<!ENTITY % gen.char.class
		"abbrev|acronym|citation|citerefentry|citetitle|emphasis
		|firstterm|foreignphrase|glossterm|footnote|phrase|orgname
		|quote|trademark|wordasword|personname %local.gen.char.class;">

<!ENTITY % local.link.char.class "">
<!ENTITY % link.char.class
		"link|olink|ulink %local.link.char.class;">

<!-- The DocBook TC may produce an official EBNF module for DocBook. -->
<!-- This PE provides the hook by which it can be inserted into the DTD. -->
<!ENTITY % ebnf.inline.hook "">

<!ENTITY % local.tech.char.class "">
<!ENTITY % tech.char.class
		"action|application
                |classname|methodname|interfacename|exceptionname
                |ooclass|oointerface|ooexception
                |command|computeroutput
		|database|email|envar|errorcode|errorname|errortype|errortext|filename
		|function|guibutton|guiicon|guilabel|guimenu|guimenuitem
		|guisubmenu|hardware|interface|keycap
		|keycode|keycombo|keysym|literal|code|constant|markup|medialabel
		|menuchoice|mousebutton|option|optional|parameter
		|prompt|property|replaceable|returnvalue|sgmltag|structfield
		|structname|symbol|systemitem|uri|token|type|userinput|varname
                %ebnf.inline.hook;
		%local.tech.char.class;">

<!ENTITY % local.base.char.class "">
<!ENTITY % base.char.class
		"anchor %local.base.char.class;">

<!ENTITY % local.docinfo.char.class "">
<!ENTITY % docinfo.char.class
		"author|authorinitials|corpauthor|corpcredit|modespec|othercredit
		|productname|productnumber|revhistory
		%local.docinfo.char.class;">

<!ENTITY % local.other.char.class "">
<!ENTITY % other.char.class
		"remark|subscript|superscript %local.other.char.class;">

<!ENTITY % local.inlineobj.char.class "">
<!ENTITY % inlineobj.char.class
		"inlinegraphic|inlinemediaobject|inlineequation %local.inlineobj.char.class;">

<!-- ...................................................................... -->
<!-- Entities for content models .......................................... -->

<!ENTITY % formalobject.title.content "title, titleabbrev?">

<!-- Redeclaration placeholder ............................................ -->

<!-- For redeclaring entities that are declared after this point while
     retaining their references to the entities that are declared before
     this point -->

<![%dbpool.redecl.module;[
<!-- Defining rdbpool here makes some buggy XML parsers happy. -->
<!ENTITY % rdbpool "">
%rdbpool;
<!--end of dbpool.redecl.module-->]]>

<!-- Object-level mixtures ................................................ -->

<!--
                      list admn line synp para infm form cmpd gen  desc
Component mixture       X    X    X    X    X    X    X    X    X    X
Sidebar mixture         X    X    X    X    X    X    X    a    X
Footnote mixture        X         X    X    X    X
Example mixture         X         X    X    X    X
Highlights mixture      X    X              X
Paragraph mixture       X         X    X         X
Admonition mixture      X         X    X    X    X    X    b    c
Figure mixture                    X    X         X
Table entry mixture     X    X    X         X    d
Glossary def mixture    X         X    X    X    X         e
Legal notice mixture    X    X    X         X    f

a. Just Procedure; not Sidebar itself or MsgSet.
b. No MsgSet.
c. No Highlights.
d. Just Graphic; no other informal objects.
e. No Anchor, BridgeHead, or Highlights.
f. Just BlockQuote; no other informal objects.
-->

<!ENTITY % local.component.mix "">
<!ENTITY % component.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|%compound.class;
		|%genobj.class;		|%descobj.class;
		|%ndxterm.class;        |beginpage
		%local.component.mix;">

<!ENTITY % local.sidebar.mix "">
<!ENTITY % sidebar.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|procedure
		|%genobj.class;
		|%ndxterm.class;        |beginpage
		%local.sidebar.mix;">

<!ENTITY % local.qandaset.mix "">
<!ENTITY % qandaset.mix
		"%list.class;           |%admon.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|procedure
		|%genobj.class;
		|%ndxterm.class;
		%local.qandaset.mix;">

<!ENTITY % local.revdescription.mix "">
<!ENTITY % revdescription.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|procedure
		|%genobj.class;
		|%ndxterm.class;
		%local.revdescription.mix;">

<!ENTITY % local.footnote.mix "">
<!ENTITY % footnote.mix
		"%list.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		%local.footnote.mix;">

<!ENTITY % local.example.mix "">
<!ENTITY % example.mix
		"%list.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%ndxterm.class;        |beginpage
		%local.example.mix;">

<!ENTITY % local.highlights.mix "">
<!ENTITY % highlights.mix
		"%list.class;		|%admon.class;
		|%para.class;
		|%ndxterm.class;
		%local.highlights.mix;">

<!-- %formal.class; is explicitly excluded from many contexts in which
     paragraphs are used -->
<!ENTITY % local.para.mix "">
<!ENTITY % para.mix
		"%list.class;           |%admon.class;
		|%linespecific.class;
					|%informal.class;
		|%formal.class;
		%local.para.mix;">

<!ENTITY % local.admon.mix "">
<!ENTITY % admon.mix
		"%list.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%formal.class;		|procedure|sidebar
		|anchor|bridgehead|remark
		|%ndxterm.class;        |beginpage
		%local.admon.mix;">

<!ENTITY % local.figure.mix "">
<!ENTITY % figure.mix
		"%linespecific.class;	|%synop.class;
					|%informal.class;
		|%ndxterm.class;        |beginpage
		%local.figure.mix;">

<!ENTITY % local.tabentry.mix "">
<!ENTITY % tabentry.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;
		|%para.class;		|graphic|mediaobject
		%local.tabentry.mix;">

<!ENTITY % local.glossdef.mix "">
<!ENTITY % glossdef.mix
		"%list.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%formal.class;
		|remark
		|%ndxterm.class;        |beginpage
		%local.glossdef.mix;">

<!ENTITY % local.legalnotice.mix "">
<!ENTITY % legalnotice.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;
		|%para.class;		|blockquote
		|%ndxterm.class;        |beginpage
		%local.legalnotice.mix;">

<!ENTITY % local.textobject.mix "">
<!ENTITY % textobject.mix
		"%list.class;		|%admon.class;
		|%linespecific.class;
		|%para.class;		|blockquote
		%local.textobject.mix;">

<!ENTITY % local.mediaobject.mix "">
<!ENTITY % mediaobject.mix
		"videoobject|audioobject|imageobject|textobject %local.mediaobject.mix;">

<!ENTITY % local.listpreamble.mix "">
<!ENTITY % listpreamble.mix
		"                  	 %admon.class;
		|%linespecific.class;	|%synop.class;
		|%para.class;		|%informal.class;
		|%genobj.class;		|%descobj.class;
		|%ndxterm.class;        |beginpage
		%local.listpreamble.mix;">

<!-- Character-level mixtures ............................................. -->

<![%sgml.features;[
<!ENTITY % local.ubiq.mix "">
<!ENTITY % ubiq.mix "%ndxterm.class;|beginpage %local.ubiq.mix;">

<!ENTITY % ubiq.exclusion "-(%ubiq.mix)">
<!ENTITY % ubiq.inclusion "+(%ubiq.mix)">

<!ENTITY % footnote.exclusion "-(footnote|%formal.class;)">
<!ENTITY % highlights.exclusion "-(%ubiq.mix;|%formal.class;)">
<!ENTITY % admon.exclusion "-(%admon.class;)">
<!ENTITY % formal.exclusion "-(%formal.class;)">
<!ENTITY % acronym.exclusion "-(acronym)">
<!ENTITY % beginpage.exclusion "-(beginpage)">
<!ENTITY % ndxterm.exclusion "-(%ndxterm.class;)">
<!ENTITY % blockquote.exclusion "-(epigraph)">
<!ENTITY % remark.exclusion "-(remark|%ubiq.mix;)">
<!ENTITY % glossterm.exclusion "-(glossterm)">
<!ENTITY % links.exclusion "-(link|olink|ulink|xref)">
]]><!-- sgml.features -->

<!-- not [sgml.features[ -->
<!ENTITY % local.ubiq.mix "">
<!ENTITY % ubiq.mix "">

<!ENTITY % ubiq.exclusion "">
<!ENTITY % ubiq.inclusion "">

<!ENTITY % footnote.exclusion "">
<!ENTITY % highlights.exclusion "">
<!ENTITY % admon.exclusion "">
<!ENTITY % formal.exclusion "">
<!ENTITY % acronym.exclusion "">
<!ENTITY % beginpage.exclusion "">
<!ENTITY % ndxterm.exclusion "">
<!ENTITY % blockquote.exclusion "">
<!ENTITY % remark.exclusion "">
<!ENTITY % glossterm.exclusion "">
<!ENTITY % links.exclusion "">
<!-- ]] not sgml.features -->

<!--
                    #PCD xref word link cptr base dnfo othr inob (synop)
para.char.mix         X    X    X    X    X    X    X    X    X
title.char.mix        X    X    X    X    X    X    X    X    X
ndxterm.char.mix      X    X    X    X    X    X    X    X    a
cptr.char.mix         X              X    X    X         X    a
smallcptr.char.mix    X                   b                   a
word.char.mix         X         c    X         X         X    a
docinfo.char.mix      X         d    X    b              X    a

a. Just InlineGraphic; no InlineEquation.
b. Just Replaceable; no other computer terms.
c. Just Emphasis and Trademark; no other word elements.
d. Just Acronym, Emphasis, and Trademark; no other word elements.
-->

<!-- The DocBook TC may produce an official forms module for DocBook. -->
<!-- This PE provides the hook by which it can be inserted into the DTD. -->
<!ENTITY % forminlines.hook "">

<!ENTITY % local.para.char.mix "">
<!ENTITY % para.char.mix
		"#PCDATA
		|%xref.char.class;	|%gen.char.class;
		|%link.char.class;	|%tech.char.class;
		|%base.char.class;	|%docinfo.char.class;
		|%other.char.class;	|%inlineobj.char.class;
		|%synop.class;
		|%ndxterm.class;        |beginpage
                %forminlines.hook;
		%local.para.char.mix;">

<!ENTITY % local.title.char.mix "">
<!ENTITY % title.char.mix
		"#PCDATA
		|%xref.char.class;	|%gen.char.class;
		|%link.char.class;	|%tech.char.class;
		|%base.char.class;	|%docinfo.char.class;
		|%other.char.class;	|%inlineobj.char.class;
		|%ndxterm.class;
		%local.title.char.mix;">

<!ENTITY % local.ndxterm.char.mix "">
<!ENTITY % ndxterm.char.mix
		"#PCDATA
		|%xref.char.class;	|%gen.char.class;
		|%link.char.class;	|%tech.char.class;
		|%base.char.class;	|%docinfo.char.class;
		|%other.char.class;	|inlinegraphic|inlinemediaobject
		%local.ndxterm.char.mix;">

<!ENTITY % local.cptr.char.mix "">
<!ENTITY % cptr.char.mix
		"#PCDATA
		|%link.char.class;	|%tech.char.class;
		|%base.char.class;
		|%other.char.class;	|inlinegraphic|inlinemediaobject
		|%ndxterm.class;        |beginpage
		%local.cptr.char.mix;">

<!ENTITY % local.smallcptr.char.mix "">
<!ENTITY % smallcptr.char.mix
		"#PCDATA
					|replaceable
					|inlinegraphic|inlinemediaobject
		|%ndxterm.class;        |beginpage
		%local.smallcptr.char.mix;">

<!ENTITY % local.word.char.mix "">
<!ENTITY % word.char.mix
		"#PCDATA
					|acronym|emphasis|trademark
		|%link.char.class;
		|%base.char.class;
		|%other.char.class;	|inlinegraphic|inlinemediaobject
		|%ndxterm.class;        |beginpage
		%local.word.char.mix;">

<!ENTITY % local.docinfo.char.mix "">
<!ENTITY % docinfo.char.mix
		"#PCDATA
		|%link.char.class;
					|emphasis|trademark
					|replaceable
		|%other.char.class;	|inlinegraphic|inlinemediaobject
		|%ndxterm.class;
		%local.docinfo.char.mix;">
<!--ENTITY % bibliocomponent.mix (see Bibliographic section, below)-->
<!--ENTITY % person.ident.mix (see Bibliographic section, below)-->

<!-- ...................................................................... -->
<!-- Entities for attributes and attribute components ..................... -->

<!-- Effectivity attributes ............................................... -->


<!-- Arch: Computer or chip architecture to which element applies; no
	default -->

<!ENTITY % arch.attrib
	"arch		CDATA		#IMPLIED">

<!-- Condition: General-purpose effectivity attribute -->

<!ENTITY % condition.attrib
	"condition	CDATA		#IMPLIED">

<!-- Conformance: Standards conformance characteristics -->

<!ENTITY % conformance.attrib
	"conformance	NMTOKENS	#IMPLIED">


<!-- OS: Operating system to which element applies; no default -->

<!ENTITY % os.attrib
	"os		CDATA		#IMPLIED">


<!-- Revision: Editorial revision to which element belongs; no default -->

<!ENTITY % revision.attrib
	"revision	CDATA		#IMPLIED">

<!-- Security: Security classification; no default -->

<!ENTITY % security.attrib
	"security	CDATA		#IMPLIED">

<!-- UserLevel: Level of user experience to which element applies; no
	default -->

<!ENTITY % userlevel.attrib
	"userlevel	CDATA		#IMPLIED">


<!-- Vendor: Computer vendor to which element applies; no default -->

<!ENTITY % vendor.attrib
	"vendor		CDATA		#IMPLIED">

<!ENTITY % local.effectivity.attrib "">
<!ENTITY % effectivity.attrib
	"%arch.attrib;
        %condition.attrib;
	%conformance.attrib;
	%os.attrib;
	%revision.attrib;
        %security.attrib;
	%userlevel.attrib;
	%vendor.attrib;
	%local.effectivity.attrib;"
>

<!-- Common attributes .................................................... -->


<!-- Id: Unique identifier of element; no default -->

<!ENTITY % id.attrib
	"id		ID		#IMPLIED">


<!-- Id: Unique identifier of element; a value must be supplied; no
	default -->

<!ENTITY % idreq.attrib
	"id		ID		#REQUIRED">


<!-- Lang: Indicator of language in which element is written, for
	translation, character set management, etc.; no default -->

<!ENTITY % lang.attrib
	"lang		CDATA		#IMPLIED">


<!-- Remap: Previous role of element before conversion; no default -->

<!ENTITY % remap.attrib
	"remap		CDATA		#IMPLIED">


<!-- Role: New role of element in local environment; no default -->

<!ENTITY % role.attrib
	"role		CDATA		#IMPLIED">


<!-- XRefLabel: Alternate labeling string for XRef text generation;
	default is usually title or other appropriate label text already
	contained in element -->

<!ENTITY % xreflabel.attrib
	"xreflabel	CDATA		#IMPLIED">


<!-- RevisionFlag: Revision status of element; default is that element
	wasn't revised -->

<!ENTITY % revisionflag.attrib
	"revisionflag	(changed
			|added
			|deleted
			|off)		#IMPLIED">

<!ENTITY % local.common.attrib "">

<!-- dir: Bidirectional override -->

<!ENTITY % dir.attrib
	"dir		(ltr
			|rtl
			|lro
			|rlo)		#IMPLIED">

<!-- xml:base: base URI -->

<!ENTITY % xml-base.attrib
	"xml:base	CDATA		#IMPLIED">

<!-- Role is included explicitly on each element -->

<!ENTITY % common.attrib
	"%id.attrib;
	%lang.attrib;
	%remap.attrib;
	%xreflabel.attrib;
	%revisionflag.attrib;
	%effectivity.attrib;
	%dir.attrib;
	%xml-base.attrib;
	%local.common.attrib;"
>

<!-- Role is included explicitly on each element -->

<!ENTITY % idreq.common.attrib
	"%idreq.attrib;
	%lang.attrib;
	%remap.attrib;
	%xreflabel.attrib;
	%revisionflag.attrib;
	%effectivity.attrib;
	%dir.attrib;
	%xml-base.attrib;
	%local.common.attrib;"
>

<!-- Semi-common attributes and other attribute entities .................. -->

<!ENTITY % local.graphics.attrib "">

<!-- EntityRef: Name of an external entity containing the content
	of the graphic -->
<!-- FileRef: Filename, qualified by a pathname if desired,
	designating the file containing the content of the graphic -->
<!-- Format: Notation of the element content, if any -->
<!-- SrcCredit: Information about the source of the Graphic -->
<!-- Width: Same as CALS reprowid (desired width) -->
<!-- Depth: Same as CALS reprodep (desired depth) -->
<!-- Align: Same as CALS hplace with 'none' removed; #IMPLIED means
	application-specific -->
<!-- Scale: Conflation of CALS hscale and vscale -->
<!-- Scalefit: Same as CALS scalefit -->

<!ENTITY % graphics.attrib
	"
	entityref	ENTITY		#IMPLIED
	fileref 	CDATA		#IMPLIED
	format		(%notation.class;) #IMPLIED
	srccredit	CDATA		#IMPLIED
	width		CDATA		#IMPLIED
	contentwidth	CDATA		#IMPLIED
	depth		CDATA		#IMPLIED
	contentdepth	CDATA		#IMPLIED
	align		(left
			|right
			|center)	#IMPLIED
	valign		(top
			|middle
			|bottom)	#IMPLIED
	scale		CDATA		#IMPLIED
	scalefit	%yesorno.attvals;
					#IMPLIED
	%local.graphics.attrib;"
>

<!ENTITY % local.keyaction.attrib "">

<!-- Action: Key combination type; default is unspecified if one
	child element, Simul if there is more than one; if value is
	Other, the OtherAction attribute must have a nonempty value -->
<!-- OtherAction: User-defined key combination type -->

<!ENTITY % keyaction.attrib
	"
	action		(click
			|double-click
			|press
			|seq
			|simul
			|other)		#IMPLIED
	otheraction	CDATA		#IMPLIED
	%local.keyaction.attrib;"
>


<!-- Label: Identifying number or string; default is usually the
	appropriate number or string autogenerated by a formatter -->

<!ENTITY % label.attrib
	"label		CDATA		#IMPLIED">


<!-- Format: whether element is assumed to contain significant white
	space -->

<!ENTITY % linespecific.attrib
	"format		NOTATION
			(linespecific)	'linespecific'
         linenumbering	(numbered|unnumbered) 	#IMPLIED
         continuation	(continues|restarts)	#IMPLIED
         startinglinenumber	CDATA		#IMPLIED
         language	CDATA			#IMPLIED">

<!-- Linkend: link to related information; no default -->

<!ENTITY % linkend.attrib
	"linkend	IDREF		#IMPLIED">


<!-- Linkend: required link to related information -->

<!ENTITY % linkendreq.attrib
	"linkend	IDREF		#REQUIRED">


<!-- Linkends: link to one or more sets of related information; no
	default -->

<!ENTITY % linkends.attrib
	"linkends	IDREFS		#IMPLIED">


<!ENTITY % local.mark.attrib "">
<!ENTITY % mark.attrib
	"mark		CDATA		#IMPLIED
	%local.mark.attrib;"
>


<!-- MoreInfo: whether element's content has an associated RefEntry -->

<!ENTITY % moreinfo.attrib
	"moreinfo	(refentry|none)	'none'">


<!-- Pagenum: number of page on which element appears; no default -->

<!ENTITY % pagenum.attrib
	"pagenum	CDATA		#IMPLIED">

<!ENTITY % local.status.attrib "">

<!-- Status: Editorial or publication status of the element
	it applies to, such as "in review" or "approved for distribution" -->

<!ENTITY % status.attrib
	"status		CDATA		#IMPLIED
	%local.status.attrib;"
>


<!-- Width: width of the longest line in the element to which it
	pertains, in number of characters -->

<!ENTITY % width.attrib
	"width		CDATA		#IMPLIED">

<!-- ...................................................................... -->
<!-- Title elements ....................................................... -->

<!ENTITY % title.module "INCLUDE">
<![%title.module;[
<!ENTITY % local.title.attrib "">
<!ENTITY % title.role.attrib "%role.attrib;">

<!ENTITY % title.element "INCLUDE">
<![%title.element;[
<!--doc:The text of the title of a section of a document or of a formal block-level element.
Title is widely used in DocBook. It identifies the titles of documents and parts of documents, and is the required caption on formal objects. It is also allowed as an optional title or caption on many additional block elements.
Category: titles-->
<!ELEMENT title %ho; (%title.char.mix;)*>
<!--end of title.element-->]]>

<!ENTITY % title.attlist "INCLUDE">
<![%title.attlist;[
<!ATTLIST title
		%pagenum.attrib;
		%common.attrib;
		%title.role.attrib;
		%local.title.attrib;
>
<!--end of title.attlist-->]]>
<!--end of title.module-->]]>

<!ENTITY % titleabbrev.module "INCLUDE">
<![%titleabbrev.module;[
<!ENTITY % local.titleabbrev.attrib "">
<!ENTITY % titleabbrev.role.attrib "%role.attrib;">

<!ENTITY % titleabbrev.element "INCLUDE">
<![%titleabbrev.element;[
<!--doc:The abbreviation of a Title.
TitleAbbrev holds an abbreviated version of a Title. One common use of TitleAbbrev is for the text used in running headers or footers, when the proper title is too long to be used conveniently.
Category: titles-->
<!ELEMENT titleabbrev %ho; (%title.char.mix;)*>
<!--end of titleabbrev.element-->]]>

<!ENTITY % titleabbrev.attlist "INCLUDE">
<![%titleabbrev.attlist;[
<!ATTLIST titleabbrev
		%common.attrib;
		%titleabbrev.role.attrib;
		%local.titleabbrev.attrib;
>
<!--end of titleabbrev.attlist-->]]>
<!--end of titleabbrev.module-->]]>

<!ENTITY % subtitle.module "INCLUDE">
<![%subtitle.module;[
<!ENTITY % local.subtitle.attrib "">
<!ENTITY % subtitle.role.attrib "%role.attrib;">

<!ENTITY % subtitle.element "INCLUDE">
<![%subtitle.element;[
<!--doc:A Subtitle identifies the subtitle of a document, or portion of a document.
Category: titles-->
<!ELEMENT subtitle %ho; (%title.char.mix;)*>
<!--end of subtitle.element-->]]>

<!ENTITY % subtitle.attlist "INCLUDE">
<![%subtitle.attlist;[
<!ATTLIST subtitle
		%common.attrib;
		%subtitle.role.attrib;
		%local.subtitle.attrib;
>
<!--end of subtitle.attlist-->]]>
<!--end of subtitle.module-->]]>

<!-- ...................................................................... -->
<!-- Bibliographic entities and elements .................................. -->

<!-- The bibliographic elements are typically used in the document
     hierarchy. They do not appear in content models of information
     pool elements.  See also the document information elements,
     below. -->

<!ENTITY % local.person.ident.mix "">
<!ENTITY % person.ident.mix
		"honorific|firstname|surname|lineage|othername|affiliation
		|authorblurb|contrib %local.person.ident.mix;">

<!ENTITY % local.bibliocomponent.mix "">
<!ENTITY % bibliocomponent.mix
		"abbrev|abstract|address|artpagenums|author
		|authorgroup|authorinitials|bibliomisc|biblioset
		|collab|confgroup|contractnum|contractsponsor
		|copyright|corpauthor|corpname|corpcredit|date|edition
		|editor|invpartnumber|isbn|issn|issuenum|orgname
		|biblioid|citebiblioid|bibliosource|bibliorelation|bibliocoverage
		|othercredit|pagenums|printhistory|productname
		|productnumber|pubdate|publisher|publishername
		|pubsnumber|releaseinfo|revhistory|seriesvolnums
		|subtitle|title|titleabbrev|volumenum|citetitle
		|personname|%person.ident.mix;
		|%ndxterm.class;
		%local.bibliocomponent.mix;">

<!-- I don't think this is well placed, but it needs to be here because of -->
<!-- the reference to bibliocomponent.mix -->
<!ENTITY % local.info.class "">
<!ENTITY % info.class
		"graphic | mediaobject | legalnotice | modespec
		 | subjectset | keywordset | itermset | %bibliocomponent.mix;
                 %local.info.class;">

<!ENTITY % biblioentry.module "INCLUDE">
<![%biblioentry.module;[
<!ENTITY % local.biblioentry.attrib "">
<!ENTITY % biblioentry.role.attrib "%role.attrib;">

<!ENTITY % biblioentry.element "INCLUDE">
<![%biblioentry.element;[
<!--doc:A BiblioEntry is an entry in a Bibliography. The contents of BiblioEntry is a database of named fields. Presentation systems frequently suppress some elements in a BiblioEntry.
Category: bibliography entries-->
<!ELEMENT biblioentry %ho; ((articleinfo | (%bibliocomponent.mix;))+)
                      %ubiq.exclusion;>
<!--end of biblioentry.element-->]]>

<!ENTITY % biblioentry.attlist "INCLUDE">
<![%biblioentry.attlist;[
<!ATTLIST biblioentry
		%common.attrib;
		%biblioentry.role.attrib;
		%local.biblioentry.attrib;
>
<!--end of biblioentry.attlist-->]]>
<!--end of biblioentry.module-->]]>

<!ENTITY % bibliomixed.module "INCLUDE">
<![%bibliomixed.module;[
<!ENTITY % local.bibliomixed.attrib "">
<!ENTITY % bibliomixed.role.attrib "%role.attrib;">

<!ENTITY % bibliomixed.element "INCLUDE">
<![%bibliomixed.element;[
<!--doc:BiblioMixed is an entry in a Bibliography. The contents of BiblioMixed includes all necessary punctuation for formatting. Presentation systems usually display all of the elements in a BiblioMixed.
Category: bibliography entries-->
<!ELEMENT bibliomixed %ho; (#PCDATA | %bibliocomponent.mix; | bibliomset)*
                      %ubiq.exclusion;>
<!--end of bibliomixed.element-->]]>

<!ENTITY % bibliomixed.attlist "INCLUDE">
<![%bibliomixed.attlist;[
<!ATTLIST bibliomixed
		%common.attrib;
		%bibliomixed.role.attrib;
		%local.bibliomixed.attrib;
>
<!--end of bibliomixed.attlist-->]]>
<!--end of bibliomixed.module-->]]>

<!ENTITY % articleinfo.module "INCLUDE">
<![%articleinfo.module;[
<!ENTITY % local.articleinfo.attrib "">
<!ENTITY % articleinfo.role.attrib "%role.attrib;">

<!ENTITY % articleinfo.element "INCLUDE">
<![%articleinfo.element;[
<!--doc:Meta-information for an Article.
The ArticleInfo element is a wrapper for a large collection of meta-information about a Article. Much of this data is bibliographic in nature.Prior to version 4.0 of DocBook, this element was named ArtHeader.
Category: Meta-wrappers-->
<!ELEMENT articleinfo %ho; ((%info.class;)+)
	%beginpage.exclusion;>
<!--end of articleinfo.element-->]]>

<!ENTITY % articleinfo.attlist "INCLUDE">
<![%articleinfo.attlist;[
<!ATTLIST articleinfo
		%common.attrib;
		%articleinfo.role.attrib;
		%local.articleinfo.attrib;
>
<!--end of articleinfo.attlist-->]]>
<!--end of articleinfo.module-->]]>

<!ENTITY % biblioset.module "INCLUDE">
<![%biblioset.module;[
<!ENTITY % local.biblioset.attrib "">
<!ENTITY % biblioset.role.attrib "%role.attrib;">

<!ENTITY % biblioset.element "INCLUDE">
<![%biblioset.element;[
<!--doc:A raw container for related bibliographic information.
BiblioSet is a raw wrapper for a collection of bibliographic information. The purpose of this wrapper is to assert the relationship that binds the collection. For example, in a BiblioEntry for an article in a journal, you might use two BiblioSets to wrap the fields related to the article and the fields related to the journal.
Category: bibliography entries-->
<!ELEMENT biblioset %ho; ((%bibliocomponent.mix;)+)
                      %ubiq.exclusion;>
<!--end of biblioset.element-->]]>

<!-- Relation: Relationship of elements contained within BiblioSet -->


<!ENTITY % biblioset.attlist "INCLUDE">
<![%biblioset.attlist;[
<!ATTLIST biblioset
		relation	CDATA		#IMPLIED
		%common.attrib;
		%biblioset.role.attrib;
		%local.biblioset.attrib;
>
<!--end of biblioset.attlist-->]]>
<!--end of biblioset.module-->]]>

<!ENTITY % bibliomset.module "INCLUDE">
<![%bibliomset.module;[
<!ENTITY % bibliomset.role.attrib "%role.attrib;">
<!ENTITY % local.bibliomset.attrib "">

<!ENTITY % bibliomset.element "INCLUDE">
<![%bibliomset.element;[
<!--doc:A cooked container for related bibliographic information.
BiblioMSet is a cooked wrapper for a collection of bibliographic information. The purpose of this wrapper is to assert the relationship that binds the collection. For example, in a BiblioMixed entry for an article in a journal, you might use two BiblioMSets to wrap the fields related to the article and the fields related to the journal.
Category: bibliography entries-->
<!ELEMENT bibliomset %ho; (#PCDATA | %bibliocomponent.mix; | bibliomset)*
                      %ubiq.exclusion;>
<!--end of bibliomset.element-->]]>

<!-- Relation: Relationship of elements contained within BiblioMSet -->


<!ENTITY % bibliomset.attlist "INCLUDE">
<![%bibliomset.attlist;[
<!ATTLIST bibliomset
		relation	CDATA		#IMPLIED
		%common.attrib;
		%bibliomset.role.attrib;
		%local.bibliomset.attrib;
>
<!--end of bibliomset.attlist-->]]>
<!--end of bibliomset.module-->]]>

<!ENTITY % bibliomisc.module "INCLUDE">
<![%bibliomisc.module;[
<!ENTITY % local.bibliomisc.attrib "">
<!ENTITY % bibliomisc.role.attrib "%role.attrib;">

<!ENTITY % bibliomisc.element "INCLUDE">
<![%bibliomisc.element;[
<!--doc:Untyped bibliographic information.
The BiblioMisc element is a wrapper for bibliographic information that does not fit neatly into the other bibliographic fields (such as Author and Publisher).
Category: bibliography entries-->
<!ELEMENT bibliomisc %ho; (%para.char.mix;)*>
<!--end of bibliomisc.element-->]]>

<!ENTITY % bibliomisc.attlist "INCLUDE">
<![%bibliomisc.attlist;[
<!ATTLIST bibliomisc
		%common.attrib;
		%bibliomisc.role.attrib;
		%local.bibliomisc.attrib;
>
<!--end of bibliomisc.attlist-->]]>
<!--end of bibliomisc.module-->]]>

<!-- ...................................................................... -->
<!-- Subject, Keyword, and ITermSet elements .............................. -->

<!ENTITY % subjectset.content.module "INCLUDE">
<![%subjectset.content.module;[
<!ENTITY % subjectset.module "INCLUDE">
<![%subjectset.module;[
<!ENTITY % local.subjectset.attrib "">
<!ENTITY % subjectset.role.attrib "%role.attrib;">

<!ENTITY % subjectset.element "INCLUDE">
<![%subjectset.element;[
<!--doc:A set of terms describing the subject matter of a document.
A SubjectSet is a container for a set of Subjects. All of the Subjects within a SubjectSet should come from the same controlled vocabulary. A document can be described using terms from more than one controlled vocabulary. In order to do this, you should use the Scheme attribute to distinguish between controlled vocabularies.
Category: keywords-->
<!ELEMENT subjectset %ho; (subject+)>
<!--end of subjectset.element-->]]>

<!-- Scheme: Controlled vocabulary employed in SubjectTerms -->


<!ENTITY % subjectset.attlist "INCLUDE">
<![%subjectset.attlist;[
<!ATTLIST subjectset
		scheme		NMTOKEN		#IMPLIED
		%common.attrib;
		%subjectset.role.attrib;
		%local.subjectset.attrib;
>
<!--end of subjectset.attlist-->]]>
<!--end of subjectset.module-->]]>

<!ENTITY % subject.module "INCLUDE">
<![%subject.module;[
<!ENTITY % local.subject.attrib "">
<!ENTITY % subject.role.attrib "%role.attrib;">

<!ENTITY % subject.element "INCLUDE">
<![%subject.element;[
<!--doc:One of a group of terms describing the subject matter of a document.
A subject categorizes or describes the topic of a document, or section of a document. In DocBook, a Subject is defined by the SubjectTerms that it contains. Subject terms should be drawn from a controlled vocabulary, such as the Library of Congress Subject Headings. If an outside vocabulary is not appropriate, a local or institutional subject set should be created. The advantage of a controlled vocabulary is that it places the document into a known subject space. Searching the subject space with a particular subject term will find all of the documents that claim to have that subject. There's no need to worry about terms that are synonymous with the search item, or homophones of the search term. All of the SubjectTerms in a Subject should describe the same subject, and be from the same controlled vocabulary.
Category: keywords-->
<!ELEMENT subject %ho; (subjectterm+)>
<!--end of subject.element-->]]>

<!-- Weight: Ranking of this group of SubjectTerms relative
		to others, 0 is low, no highest value specified -->


<!ENTITY % subject.attlist "INCLUDE">
<![%subject.attlist;[
<!ATTLIST subject
		weight		CDATA		#IMPLIED
		%common.attrib;
		%subject.role.attrib;
		%local.subject.attrib;
>
<!--end of subject.attlist-->]]>
<!--end of subject.module-->]]>

<!ENTITY % subjectterm.module "INCLUDE">
<![%subjectterm.module;[
<!ENTITY % local.subjectterm.attrib "">
<!ENTITY % subjectterm.role.attrib "%role.attrib;">

<!ENTITY % subjectterm.element "INCLUDE">
<![%subjectterm.element;[
<!--doc:A term in a group of terms describing the subject matter of a document.
A SubjectTerm is an individual subject word or phrase that describes the subject matter of a document or the portion of a document in which it occurs. Subject terms are not expected to contain any markup. They are external descriptions from a controlled vocabulary.
Category: keywords-->
<!ELEMENT subjectterm %ho; (#PCDATA)>
<!--end of subjectterm.element-->]]>

<!ENTITY % subjectterm.attlist "INCLUDE">
<![%subjectterm.attlist;[
<!ATTLIST subjectterm
		%common.attrib;
		%subjectterm.role.attrib;
		%local.subjectterm.attrib;
>
<!--end of subjectterm.attlist-->]]>
<!--end of subjectterm.module-->]]>
<!--end of subjectset.content.module-->]]>

<!ENTITY % keywordset.content.module "INCLUDE">
<![%keywordset.content.module;[
<!ENTITY % keywordset.module "INCLUDE">
<![%keywordset.module;[
<!ENTITY % local.keywordset.attrib "">
<!ENTITY % keywordset.role.attrib "%role.attrib;">

<!ENTITY % keywordset.element "INCLUDE">
<![%keywordset.element;[
<!--doc:A set of keywords describing the content of a document.
A set of keywords, provided by the author, editor, publisher, and so on, can be stored in the document meta-information in a KeywordSet. Keywords can form an important part of an automated indexing or searching strategy for a collection of documents.
Category: keywords-->
<!ELEMENT keywordset %ho; (keyword+)>
<!--end of keywordset.element-->]]>

<!ENTITY % keywordset.attlist "INCLUDE">
<![%keywordset.attlist;[
<!ATTLIST keywordset
		%common.attrib;
		%keywordset.role.attrib;
		%local.keywordset.attrib;
>
<!--end of keywordset.attlist-->]]>
<!--end of keywordset.module-->]]>

<!ENTITY % keyword.module "INCLUDE">
<![%keyword.module;[
<!ENTITY % local.keyword.attrib "">
<!ENTITY % keyword.role.attrib "%role.attrib;">

<!ENTITY % keyword.element "INCLUDE">
<![%keyword.element;[
<!--doc:One of a set of keywords describing the content of a document.
A Keyword is a term describing the content of a document. The keyword applies to the document component that contains it.
Category: keywords-->
<!ELEMENT keyword %ho; (#PCDATA)>
<!--end of keyword.element-->]]>

<!ENTITY % keyword.attlist "INCLUDE">
<![%keyword.attlist;[
<!ATTLIST keyword
		%common.attrib;
		%keyword.role.attrib;
		%local.keyword.attrib;
>
<!--end of keyword.attlist-->]]>
<!--end of keyword.module-->]]>
<!--end of keywordset.content.module-->]]>

<!ENTITY % itermset.module "INCLUDE">
<![%itermset.module;[
<!ENTITY % local.itermset.attrib "">
<!ENTITY % itermset.role.attrib "%role.attrib;">

<!ENTITY % itermset.element "INCLUDE">
<![%itermset.element;[
<!--doc:A set of index terms in the meta-information of a document.
When IndexTerms use the Zone attribute to point to index ranges, it may be handy to hoist them out of the flow and put them in the document meta-information. The ITermSet element, which occurs in the DocBook containers for meta-information, is one place to put them. ITermSet is simply a wrapper around a group of IndexTerms.-->
<!ELEMENT itermset %ho; (indexterm+)>
<!--end of itermset.element-->]]>

<!ENTITY % itermset.attlist "INCLUDE">
<![%itermset.attlist;[
<!ATTLIST itermset
		%common.attrib;
		%itermset.role.attrib;
		%local.itermset.attrib;
>
<!--end of itermset.attlist-->]]>
<!--end of itermset.module-->]]>

<!-- Bibliographic info for "blocks" -->

<!ENTITY % blockinfo.module "INCLUDE">
<![ %blockinfo.module; [
<!ENTITY % local.blockinfo.attrib "">
<!ENTITY % blockinfo.role.attrib "%role.attrib;">

<!ENTITY % blockinfo.element "INCLUDE">
<![ %blockinfo.element; [
<!--doc:Meta-information for a block element.
The blockinfo element is a wrapper for a large collection of meta-information about a block element. Much of this data is bibliographic in nature.
Category: Meta-wrappers-->
<!ELEMENT blockinfo %ho; ((%info.class;)+)
	%beginpage.exclusion;>
<!--end of blockinfo.element-->]]>

<!ENTITY % blockinfo.attlist "INCLUDE">
<![ %blockinfo.attlist; [
<!ATTLIST blockinfo
		%common.attrib;
		%blockinfo.role.attrib;
		%local.blockinfo.attrib;
>
<!--end of blockinfo.attlist-->]]>
<!--end of blockinfo.module-->]]>

<!-- ...................................................................... -->
<!-- Compound (section-ish) elements ...................................... -->

<!-- Message set ...................... -->

<!ENTITY % msgset.content.module "INCLUDE">
<![%msgset.content.module;[
<!ENTITY % msgset.module "INCLUDE">
<![%msgset.module;[
<!ENTITY % local.msgset.attrib "">
<!ENTITY % msgset.role.attrib "%role.attrib;">

<!ENTITY % msgset.element "INCLUDE">
<![%msgset.element;[
<!--doc:MsgSet is a complex structure designed to hold a detailed set of messages, usually error messages. In addition to the actual text of each message, it can contain additional information about each message and the messages related to it.
Category: Error Messages-->
<!ELEMENT msgset %ho; (blockinfo?, (%formalobject.title.content;)?,
                       (msgentry+|simplemsgentry+))>
<!--end of msgset.element-->]]>

<!ENTITY % msgset.attlist "INCLUDE">
<![%msgset.attlist;[
<!ATTLIST msgset
		%common.attrib;
		%msgset.role.attrib;
		%local.msgset.attrib;
>
<!--end of msgset.attlist-->]]>
<!--end of msgset.module-->]]>

<!ENTITY % msgentry.module "INCLUDE">
<![%msgentry.module;[
<!ENTITY % local.msgentry.attrib "">
<!ENTITY % msgentry.role.attrib "%role.attrib;">

<!ENTITY % msgentry.element "INCLUDE">
<![%msgentry.element;[
<!--doc:A wrapper for an entry in a message set.
In a MsgSet, each MsgEntry contains some number of messages (Msgs) and additional informative and explanatory material about them.-->
<!ELEMENT msgentry %ho; (msg+, msginfo?, msgexplan*)>
<!--end of msgentry.element-->]]>

<!ENTITY % msgentry.attlist "INCLUDE">
<![%msgentry.attlist;[
<!ATTLIST msgentry
		%common.attrib;
		%msgentry.role.attrib;
		%local.msgentry.attrib;
>
<!--end of msgentry.attlist-->]]>
<!--end of msgentry.module-->]]>

<!ENTITY % simplemsgentry.module "INCLUDE">
<![ %simplemsgentry.module; [
<!ENTITY % local.simplemsgentry.attrib "">
<!ENTITY % simplemsgentry.role.attrib "%role.attrib;">

<!ENTITY % simplemsgentry.element "INCLUDE">
<![ %simplemsgentry.element; [
<!--doc:A wrapper for a simpler entry in a message set.
SimpleMsgEntry is a simpler alternative to MsgEntry. In a MsgSet, each SimpleMsgEntry contains the text of a message and its explanation.-->
<!ELEMENT simplemsgentry %ho; (msgtext, msgexplan+)>
<!--end of simplemsgentry.element-->]]>

<!ENTITY % simplemsgentry.attlist "INCLUDE">
<![ %simplemsgentry.attlist; [
<!ATTLIST simplemsgentry
		audience	CDATA	#IMPLIED
		level		CDATA	#IMPLIED
		origin		CDATA	#IMPLIED
		%common.attrib;
		%simplemsgentry.role.attrib;
		%local.simplemsgentry.attrib;
>
<!--end of simplemsgentry.attlist-->]]>
<!--end of simplemsgentry.module-->]]>

<!ENTITY % msg.module "INCLUDE">
<![%msg.module;[
<!ENTITY % local.msg.attrib "">
<!ENTITY % msg.role.attrib "%role.attrib;">

<!ENTITY % msg.element "INCLUDE">
<![%msg.element;[
<!--doc:A message in a message set.
In a MsgSet, each MsgEntry contains at least one Msg. A Msg consists of a main message (MsgMain), and optionally one or more submessages (MsgSub) or related messages (MsgRel). Additional information or explanation for the message is contained in the siblings of Msg within the MsgEntry.See MsgSet.-->
<!ELEMENT msg %ho; (title?, msgmain, (msgsub | msgrel)*)>
<!--end of msg.element-->]]>

<!ENTITY % msg.attlist "INCLUDE">
<![%msg.attlist;[
<!ATTLIST msg
		%common.attrib;
		%msg.role.attrib;
		%local.msg.attrib;
>
<!--end of msg.attlist-->]]>
<!--end of msg.module-->]]>

<!ENTITY % msgmain.module "INCLUDE">
<![%msgmain.module;[
<!ENTITY % local.msgmain.attrib "">
<!ENTITY % msgmain.role.attrib "%role.attrib;">

<!ENTITY % msgmain.element "INCLUDE">
<![%msgmain.element;[
<!--doc:The primary component of a message in a message set.
Every Msg must have one primary message. This is stored in the MsgMain. The primary message is distinguished from any number of submessages (MsgSub) or related messages (MsgRel) that a Msg might have.-->
<!ELEMENT msgmain %ho; (title?, msgtext)>
<!--end of msgmain.element-->]]>

<!ENTITY % msgmain.attlist "INCLUDE">
<![%msgmain.attlist;[
<!ATTLIST msgmain
		%common.attrib;
		%msgmain.role.attrib;
		%local.msgmain.attrib;
>
<!--end of msgmain.attlist-->]]>
<!--end of msgmain.module-->]]>

<!ENTITY % msgsub.module "INCLUDE">
<![%msgsub.module;[
<!ENTITY % local.msgsub.attrib "">
<!ENTITY % msgsub.role.attrib "%role.attrib;">

<!ENTITY % msgsub.element "INCLUDE">
<![%msgsub.element;[
<!--doc:A subcomponent of a message in a message set.
A MsgSub represents some subpart of a message. Different MsgSubs might arise in different contexts.-->
<!ELEMENT msgsub %ho; (title?, msgtext)>
<!--end of msgsub.element-->]]>

<!ENTITY % msgsub.attlist "INCLUDE">
<![%msgsub.attlist;[
<!ATTLIST msgsub
		%common.attrib;
		%msgsub.role.attrib;
		%local.msgsub.attrib;
>
<!--end of msgsub.attlist-->]]>
<!--end of msgsub.module-->]]>

<!ENTITY % msgrel.module "INCLUDE">
<![%msgrel.module;[
<!ENTITY % local.msgrel.attrib "">
<!ENTITY % msgrel.role.attrib "%role.attrib;">

<!ENTITY % msgrel.element "INCLUDE">
<![%msgrel.element;[
<!--doc:A related component of a message in a message set.
Every Msg has one primary message (MsgMain). It may also have any number of related messages, stored in MsgRel elements within the same Msg. Related messages are usually messages that appear elsewhere in response to the same event (or set of events) that triggered the main message. For example, if a network client produces a failure or warning message, a related message might appear on the server console.-->
<!ELEMENT msgrel %ho; (title?, msgtext)>
<!--end of msgrel.element-->]]>

<!ENTITY % msgrel.attlist "INCLUDE">
<![%msgrel.attlist;[
<!ATTLIST msgrel
		%common.attrib;
		%msgrel.role.attrib;
		%local.msgrel.attrib;
>
<!--end of msgrel.attlist-->]]>
<!--end of msgrel.module-->]]>

<!-- MsgText (defined in the Inlines section, below)-->

<!ENTITY % msginfo.module "INCLUDE">
<![%msginfo.module;[
<!ENTITY % local.msginfo.attrib "">
<!ENTITY % msginfo.role.attrib "%role.attrib;">

<!ENTITY % msginfo.element "INCLUDE">
<![%msginfo.element;[
<!--doc:Information about a message in a message set.
MsgInfo provides additional information about a Msg in a MsgEntry.-->
<!ELEMENT msginfo %ho; ((msglevel | msgorig | msgaud)*)>
<!--end of msginfo.element-->]]>

<!ENTITY % msginfo.attlist "INCLUDE">
<![%msginfo.attlist;[
<!ATTLIST msginfo
		%common.attrib;
		%msginfo.role.attrib;
		%local.msginfo.attrib;
>
<!--end of msginfo.attlist-->]]>
<!--end of msginfo.module-->]]>

<!ENTITY % msglevel.module "INCLUDE">
<![%msglevel.module;[
<!ENTITY % local.msglevel.attrib "">
<!ENTITY % msglevel.role.attrib "%role.attrib;">

<!ENTITY % msglevel.element "INCLUDE">
<![%msglevel.element;[
<!--doc:The level of importance or severity of a message in a message set.
MsgLevel is part of the additional information associated with a message in a MsgSet. It identifies the relative importance or severity of a message.-->
<!ELEMENT msglevel %ho; (%smallcptr.char.mix;)*>
<!--end of msglevel.element-->]]>

<!ENTITY % msglevel.attlist "INCLUDE">
<![%msglevel.attlist;[
<!ATTLIST msglevel
		%common.attrib;
		%msglevel.role.attrib;
		%local.msglevel.attrib;
>
<!--end of msglevel.attlist-->]]>
<!--end of msglevel.module-->]]>

<!ENTITY % msgorig.module "INCLUDE">
<![%msgorig.module;[
<!ENTITY % local.msgorig.attrib "">
<!ENTITY % msgorig.role.attrib "%role.attrib;">

<!ENTITY % msgorig.element "INCLUDE">
<![%msgorig.element;[
<!--doc:The origin of a message in a message set.
MsgOrig is part of the additional information associated with a message in a MsgSet. It identifies the origin or source of a particular Msg, for example, a piece of hardware, the operating system, or an application.-->
<!ELEMENT msgorig %ho; (%smallcptr.char.mix;)*>
<!--end of msgorig.element-->]]>

<!ENTITY % msgorig.attlist "INCLUDE">
<![%msgorig.attlist;[
<!ATTLIST msgorig
		%common.attrib;
		%msgorig.role.attrib;
		%local.msgorig.attrib;
>
<!--end of msgorig.attlist-->]]>
<!--end of msgorig.module-->]]>

<!ENTITY % msgaud.module "INCLUDE">
<![%msgaud.module;[
<!ENTITY % local.msgaud.attrib "">
<!ENTITY % msgaud.role.attrib "%role.attrib;">

<!ENTITY % msgaud.element "INCLUDE">
<![%msgaud.element;[
<!--doc:The audience to which a message in a message set is relevant.
MsgAud is part of the additional information associated with a message in a MsgSet. It identifies the audience to which a particular Msg is relevant.-->
<!ELEMENT msgaud %ho; (%para.char.mix;)*>
<!--end of msgaud.element-->]]>

<!ENTITY % msgaud.attlist "INCLUDE">
<![%msgaud.attlist;[
<!ATTLIST msgaud
		%common.attrib;
		%msgaud.role.attrib;
		%local.msgaud.attrib;
>
<!--end of msgaud.attlist-->]]>
<!--end of msgaud.module-->]]>

<!ENTITY % msgexplan.module "INCLUDE">
<![%msgexplan.module;[
<!ENTITY % local.msgexplan.attrib "">
<!ENTITY % msgexplan.role.attrib "%role.attrib;">

<!ENTITY % msgexplan.element "INCLUDE">
<![%msgexplan.element;[
<!--doc:Explanatory material relating to a message in a message set.
A MsgExplan contains some sort of explanatory information about a Msg or a set of Msgs in a MsgEntry.-->
<!ELEMENT msgexplan %ho; (title?, (%component.mix;)+)>
<!--end of msgexplan.element-->]]>

<!ENTITY % msgexplan.attlist "INCLUDE">
<![%msgexplan.attlist;[
<!ATTLIST msgexplan
		%common.attrib;
		%msgexplan.role.attrib;
		%local.msgexplan.attrib;
>
<!--end of msgexplan.attlist-->]]>
<!--end of msgexplan.module-->]]>
<!--end of msgset.content.module-->]]>

<!ENTITY % task.content.module "INCLUDE">
<![%task.content.module;[
<!ENTITY % task.module "INCLUDE">
<![%task.module;[
<!ENTITY % local.task.attrib "">
<!ENTITY % task.role.attrib "%role.attrib;">

<!ENTITY % task.element "INCLUDE">
<![%task.element;[
<!--doc:A task to be completed.
A task encapsulates a procedure providing an explicit location for summary information, identifying prerequisites for the task, examples, and pointers to related information.-->
<!ELEMENT task %ho; (blockinfo?,(%ndxterm.class;)*,
                     (%formalobject.title.content;),
                     tasksummary?,
                     taskprerequisites?,
                     procedure,
                     example*,
                     taskrelated?)>
<!--end of task.element-->]]>

<!ENTITY % task.attlist "INCLUDE">
<![%task.attlist;[
<!ATTLIST task
		%common.attrib;
		%task.role.attrib;
		%local.task.attrib;
>
<!--end of task.attlist-->]]>
<!--end of task.module-->]]>

<!ENTITY % tasksummary.module "INCLUDE">
<![%tasksummary.module;[
<!ENTITY % local.tasksummary.attrib "">
<!ENTITY % tasksummary.role.attrib "%role.attrib;">

<!ENTITY % tasksummary.element "INCLUDE">
<![%tasksummary.element;[
<!--doc:A summary of a task.
A tasksummary provides introductory or summary information about a task.-->
<!ELEMENT tasksummary %ho; (blockinfo?,
                            (%formalobject.title.content;)?,
                            (%component.mix;)+)>
<!--end of tasksummary.element-->]]>

<!ENTITY % tasksummary.attlist "INCLUDE">
<![%tasksummary.attlist;[
<!ATTLIST tasksummary
		%common.attrib;
		%tasksummary.role.attrib;
		%local.tasksummary.attrib;
>
<!--end of tasksummary.attlist-->]]>
<!--end of tasksummary.module-->]]>

<!ENTITY % taskprerequisites.module "INCLUDE">
<![%taskprerequisites.module;[
<!ENTITY % local.taskprerequisites.attrib "">
<!ENTITY % taskprerequisites.role.attrib "%role.attrib;">

<!ENTITY % taskprerequisites.element "INCLUDE">
<![%taskprerequisites.element;[
<!--doc:The prerequisites for a task.
The taskprerequisites element is used to describe preparations that must be made before a task is attempted.-->
<!ELEMENT taskprerequisites %ho; (blockinfo?,
                                  (%formalobject.title.content;)?,
                                  (%component.mix;)+)>
<!--end of taskprerequisites.element-->]]>

<!ENTITY % taskprerequisites.attlist "INCLUDE">
<![%taskprerequisites.attlist;[
<!ATTLIST taskprerequisites
		%common.attrib;
		%taskprerequisites.role.attrib;
		%local.taskprerequisites.attrib;
>
<!--end of taskprerequisites.attlist-->]]>
<!--end of taskprerequisites.module-->]]>

<!ENTITY % taskrelated.module "INCLUDE">
<![%taskrelated.module;[
<!ENTITY % local.taskrelated.attrib "">
<!ENTITY % taskrelated.role.attrib "%role.attrib;">

<!ENTITY % taskrelated.element "INCLUDE">
<![%taskrelated.element;[
<!--doc:Information related to a task.
The taskrelated element provides other, relevant information about a task (cross references to other parts of the document, suggested next steps, etc.).-->
<!ELEMENT taskrelated %ho; (blockinfo?,
                            (%formalobject.title.content;)?,
                            (%component.mix;)+)>
<!--end of taskrelated.element-->]]>

<!ENTITY % taskrelated.attlist "INCLUDE">
<![%taskrelated.attlist;[
<!ATTLIST taskrelated
		%common.attrib;
		%taskrelated.role.attrib;
		%local.taskrelated.attrib;
>
<!--end of taskrelated.attlist-->]]>
<!--end of taskrelated.module-->]]>
<!--end of task.content.module-->]]>

<!-- QandASet ........................ -->
<!ENTITY % qandaset.content.module "INCLUDE">
<![ %qandaset.content.module; [
<!ENTITY % qandaset.module "INCLUDE">
<![ %qandaset.module; [
<!ENTITY % local.qandaset.attrib "">
<!ENTITY % qandaset.role.attrib "%role.attrib;">

<!ENTITY % qandaset.element "INCLUDE">
<![ %qandaset.element; [
<!--doc:A question-and-answer set.
A QandASet is a list consisting of Questions and Answers.QandASets can be divided into sections.Every entry in a QandASet must contain a Question, but Answers are optional (some questions have no answers), and may be repeated (some questions have more than one answer).Common uses for QandASets include reader questionnaires and lists of Frequently Asked Questions (FAQs). For the purpose of a FAQ, DocBook V3.1 added the FAQ class to Article.-->
<!ELEMENT qandaset %ho; (blockinfo?, (%formalobject.title.content;)?,
			(%qandaset.mix;)*,
                        (qandadiv+|qandaentry+))>
<!--end of qandaset.element-->]]>

<!ENTITY % qandaset.attlist "INCLUDE">
<![ %qandaset.attlist; [
<!ATTLIST qandaset
		defaultlabel	(qanda|number|none)       #IMPLIED
		%common.attrib;
		%qandaset.role.attrib;
		%local.qandaset.attrib;>
<!--end of qandaset.attlist-->]]>
<!--end of qandaset.module-->]]>

<!ENTITY % qandadiv.module "INCLUDE">
<![ %qandadiv.module; [
<!ENTITY % local.qandadiv.attrib "">
<!ENTITY % qandadiv.role.attrib "%role.attrib;">

<!ENTITY % qandadiv.element "INCLUDE">
<![ %qandadiv.element; [
<!--doc:A titled division in a QandASet.
QandADiv is a section of a QandASet. A question and answer set might be divided into sections in order to group different sets of questions together, perhaps by topic.A QandASet may contain any number of QandADiv or QandAEntry elements, but it cannot contain a mixture of both at the same level.-->
<!ELEMENT qandadiv %ho; (blockinfo?, (%formalobject.title.content;)?,
			(%qandaset.mix;)*,
			(qandadiv+|qandaentry+))>
<!--end of qandadiv.element-->]]>

<!ENTITY % qandadiv.attlist "INCLUDE">
<![ %qandadiv.attlist; [
<!ATTLIST qandadiv
		%common.attrib;
		%qandadiv.role.attrib;
		%local.qandadiv.attrib;>
<!--end of qandadiv.attlist-->]]>
<!--end of qandadiv.module-->]]>

<!ENTITY % qandaentry.module "INCLUDE">
<![ %qandaentry.module; [
<!ENTITY % local.qandaentry.attrib "">
<!ENTITY % qandaentry.role.attrib "%role.attrib;">

<!ENTITY % qandaentry.element "INCLUDE">
<![ %qandaentry.element; [
<!--doc:A question/answer set within a QandASet.
A QandAEntry is an entry in a QandASet. Each QandAEntry defines a Question and (possibly) its Answer or Answers.Since V4.2, the preferred way to associate a revision history with a QandAEntry is in the BlockInfo. The use of RevHistory directly in QandAEntry is deprecated.-->
<!ELEMENT qandaentry %ho; (blockinfo?, revhistory?, question, answer*)>
<!--end of qandaentry.element-->]]>

<!ENTITY % qandaentry.attlist "INCLUDE">
<![ %qandaentry.attlist; [
<!ATTLIST qandaentry
		%common.attrib;
		%qandaentry.role.attrib;
		%local.qandaentry.attrib;>
<!--end of qandaentry.attlist-->]]>
<!--end of qandaentry.module-->]]>

<!ENTITY % question.module "INCLUDE">
<![ %question.module; [
<!ENTITY % local.question.attrib "">
<!ENTITY % question.role.attrib "%role.attrib;">

<!ENTITY % question.element "INCLUDE">
<![ %question.element; [
<!--doc:A question in a QandASet.
A Question in a QandAEntry poses a question or states a problem that is addressed by the following Answer(s).Answers are optional (some questions have no answers) and may be repeated (some questions have more than one answer).-->
<!ELEMENT question %ho; (label?, (%qandaset.mix;)+)>
<!--end of question.element-->]]>

<!ENTITY % question.attlist "INCLUDE">
<![ %question.attlist; [
<!ATTLIST question
		%common.attrib;
		%question.role.attrib;
		%local.question.attrib;
>
<!--end of question.attlist-->]]>
<!--end of question.module-->]]>

<!ENTITY % answer.module "INCLUDE">
<![ %answer.module; [
<!ENTITY % local.answer.attrib "">
<!ENTITY % answer.role.attrib "%role.attrib;">

<!ENTITY % answer.element "INCLUDE">
<![ %answer.element; [
<!--doc:An answer to a question posed in a QandASet.
Within a QandAEntry, a Question may have an Answer. An Answer is optional (some questions have no answers) and may be repeated (some questions have more than one answer).-->
<!ELEMENT answer %ho; (label?, (%qandaset.mix;)*, qandaentry*)>
<!--end of answer.element-->]]>

<!ENTITY % answer.attlist "INCLUDE">
<![ %answer.attlist; [
<!ATTLIST answer
		%common.attrib;
		%answer.role.attrib;
		%local.answer.attrib;
>
<!--end of answer.attlist-->]]>
<!--end of answer.module-->]]>

<!ENTITY % label.module "INCLUDE">
<![ %label.module; [
<!ENTITY % local.label.attrib "">
<!ENTITY % label.role.attrib "%role.attrib;">

<!ENTITY % label.element "INCLUDE">
<![ %label.element; [
<!--doc:A label on a Question or Answer.
The Label of a Question or Answer identifies the label that is to be used when formatting the question or answer.-->
<!ELEMENT label %ho; (%word.char.mix;)*>
<!--end of label.element-->]]>

<!ENTITY % label.attlist "INCLUDE">
<![ %label.attlist; [
<!ATTLIST label
		%common.attrib;
		%label.role.attrib;
		%local.label.attrib;
>
<!--end of label.attlist-->]]>
<!--end of label.module-->]]>
<!--end of qandaset.content.module-->]]>

<!-- Procedure ........................ -->

<!ENTITY % procedure.content.module "INCLUDE">
<![%procedure.content.module;[
<!ENTITY % procedure.module "INCLUDE">
<![%procedure.module;[
<!ENTITY % local.procedure.attrib "">
<!ENTITY % procedure.role.attrib "%role.attrib;">

<!ENTITY % procedure.element "INCLUDE">
<![%procedure.element;[
<!--doc:A list of operations to be performed in a well-defined sequence.
A Procedure encapsulates a task composed of Steps (and possibly, SubSteps). Procedures are usually performed sequentially, unless individual Steps direct the reader explicitly.Often it is important to assure that certain conditions exist before a procedure is performed, and that the outcome of the procedure matches the expected results. DocBook does not provide explicit semantic markup for these pre- and post-conditions. Instead, they must be described as steps (check the pre-conditions in the first step and the results in the last step), or described outside the body of the procedure.The task element, added to DocBook in V4.3, provides some of this infrastructure.-->
<!ELEMENT procedure %ho; (blockinfo?, (%formalobject.title.content;)?,
                          (%component.mix;)*, step+)>
<!--end of procedure.element-->]]>

<!ENTITY % procedure.attlist "INCLUDE">
<![%procedure.attlist;[
<!ATTLIST procedure
		%common.attrib;
		%procedure.role.attrib;
		%local.procedure.attrib;
>
<!--end of procedure.attlist-->]]>
<!--end of procedure.module-->]]>

<!ENTITY % step.module "INCLUDE">
<![%step.module;[
<!ENTITY % local.step.attrib "">
<!ENTITY % step.role.attrib "%role.attrib;">

<!ENTITY % step.element "INCLUDE">
<![%step.element;[
<!--doc:A Step identifies a unit of action in a Procedure. If a finer level of granularity is required for some steps, you can embed SubSteps in a Step. EmbeddedSubSteps contain Steps, so that substeps can be nested to any depth.-->
<!ELEMENT step %ho; (title?, (((%component.mix;)+, ((substeps|stepalternatives), (%component.mix;)*)?)
                    | ((substeps|stepalternatives), (%component.mix;)*)))>
<!--end of step.element-->]]>

<!-- Performance: Whether the Step must be performed -->
<!-- not #REQUIRED! -->


<!ENTITY % step.attlist "INCLUDE">
<![%step.attlist;[
<!ATTLIST step
		performance	(optional
				|required)	"required"
		%common.attrib;
		%step.role.attrib;
		%local.step.attrib;
>
<!--end of step.attlist-->]]>
<!--end of step.module-->]]>

<!ENTITY % substeps.module "INCLUDE">
<![%substeps.module;[
<!ENTITY % local.substeps.attrib "">
<!ENTITY % substeps.role.attrib "%role.attrib;">

<!ENTITY % substeps.element "INCLUDE">
<![%substeps.element;[
<!--doc:A wrapper for steps that occur within steps in a procedure.
A Procedure describes a sequence of Steps that a reader is expected to perform. If a finer level of granularity is required for some steps, you can use SubSteps to embed substeps within a Step. SubSteps contain Steps, so substeps can be nested to any depth.-->
<!ELEMENT substeps %ho; (step+)>
<!--end of substeps.element-->]]>

<!-- Performance: whether entire set of substeps must be performed -->
<!-- not #REQUIRED! -->

<!ENTITY % substeps.attlist "INCLUDE">
<![%substeps.attlist;[
<!ATTLIST substeps
		performance	(optional
				|required)	"required"
		%common.attrib;
		%substeps.role.attrib;
		%local.substeps.attrib;
>
<!--end of substeps.attlist-->]]>
<!--end of substeps.module-->]]>

<!ENTITY % stepalternatives.module "INCLUDE">
<![%stepalternatives.module;[
<!ENTITY % local.stepalternatives.attrib "">
<!ENTITY % stepalternatives.role.attrib "%role.attrib;">

<!ENTITY % stepalternatives.element "INCLUDE">
<![%stepalternatives.element;[
<!--doc:Alternative steps in a procedure.
Most steps in a procedure are sequential: do the first, then the second, then the third. Sometimes procedures provide an explicit ordering: do step 7 next.The step alternatives element was added to support the semantics of alternative steps: perform exactly one of the following steps. The reader is presumably given some criteria for deciding which one to choose, but the significant difference is that only one of the steps is performed.-->
<!ELEMENT stepalternatives %ho; (step+)>
<!--end of stepalternatives.element-->]]>

<!-- Performance: Whether (one of) the alternatives must be performed -->
<!-- not #REQUIRED! -->

<!ENTITY % stepalternatives.attlist "INCLUDE">
<![%stepalternatives.attlist;[
<!ATTLIST stepalternatives
		performance	(optional
				|required)	"required"
		%common.attrib;
		%stepalternatives.role.attrib;
		%local.stepalternatives.attrib;
>
<!--end of stepalternatives.attlist-->]]>
<!--end of stepalternatives.module-->]]>
<!--end of procedure.content.module-->]]>

<!-- Sidebar .......................... -->

<!ENTITY % sidebar.content.model "INCLUDE">
<![ %sidebar.content.model; [

<!ENTITY % sidebarinfo.module "INCLUDE">
<![ %sidebarinfo.module; [
<!ENTITY % local.sidebarinfo.attrib "">
<!ENTITY % sidebarinfo.role.attrib "%role.attrib;">

<!ENTITY % sidebarinfo.element "INCLUDE">
<![ %sidebarinfo.element; [
<!--doc:Meta-information for a Sidebar.
The SidebarInfo element is a wrapper for a large collection of meta-information about a Sidebar. Much of this data is bibliographic in nature.-->
<!ELEMENT sidebarinfo %ho; ((%info.class;)+)
	%beginpage.exclusion;>
<!--end of sidebarinfo.element-->]]>

<!ENTITY % sidebarinfo.attlist "INCLUDE">
<![ %sidebarinfo.attlist; [
<!ATTLIST sidebarinfo
		%common.attrib;
		%sidebarinfo.role.attrib;
		%local.sidebarinfo.attrib;
>
<!--end of sidebarinfo.attlist-->]]>
<!--end of sidebarinfo.module-->]]>

<!ENTITY % sidebar.module "INCLUDE">
<![%sidebar.module;[
<!ENTITY % local.sidebar.attrib "">
<!ENTITY % sidebar.role.attrib "%role.attrib;">

<!ENTITY % sidebar.element "INCLUDE">
<![%sidebar.element;[
<!--doc:A portion of a document that is isolated from the main narrative flow.
A Sidebar is a short piece of text, rarely longer than a single column or page, that is presented outside the narrative flow of the main text. Sidebars are often used for digressions or interesting observations that are related, but not directly relevant, to the main text.
Category: block things-->
<!ELEMENT sidebar %ho; (sidebarinfo?,
                   (%formalobject.title.content;)?,
                   (%sidebar.mix;)+)>
<!--end of sidebar.element-->]]>

<!ENTITY % sidebar.attlist "INCLUDE">
<![%sidebar.attlist;[
<!ATTLIST sidebar
		%common.attrib;
		%sidebar.role.attrib;
		%local.sidebar.attrib;
>
<!--end of sidebar.attlist-->]]>
<!--end of sidebar.module-->]]>
<!--end of sidebar.content.model-->]]>

<!-- ...................................................................... -->
<!-- Paragraph-related elements ........................................... -->

<!ENTITY % abstract.module "INCLUDE">
<![%abstract.module;[
<!ENTITY % local.abstract.attrib "">
<!ENTITY % abstract.role.attrib "%role.attrib;">

<!ENTITY % abstract.element "INCLUDE">
<![%abstract.element;[
<!--doc:A summary.
An abstract can occur in most components of DocBook. It is expected to contain some sort of summary of the content with which it is associated (by containment).
Category: block things-->
<!ELEMENT abstract %ho; (title?, (%para.class;)+)>
<!--end of abstract.element-->]]>

<!ENTITY % abstract.attlist "INCLUDE">
<![%abstract.attlist;[
<!ATTLIST abstract
		%common.attrib;
		%abstract.role.attrib;
		%local.abstract.attrib;
>
<!--end of abstract.attlist-->]]>
<!--end of abstract.module-->]]>

<!ENTITY % authorblurb.module "INCLUDE">
<![%authorblurb.module;[
<!ENTITY % local.authorblurb.attrib "">
<!ENTITY % authorblurb.role.attrib "%role.attrib;">

<!ENTITY % authorblurb.element "INCLUDE">
<![%authorblurb.element;[
<!--doc:A short description or note about an author.
A short description of an author.
Category: author-->
<!ELEMENT authorblurb %ho; (title?, (%para.class;)+)>
<!--end of authorblurb.element-->]]>

<!ENTITY % authorblurb.attlist "INCLUDE">
<![%authorblurb.attlist;[
<!ATTLIST authorblurb
		%common.attrib;
		%authorblurb.role.attrib;
		%local.authorblurb.attrib;
>
<!--end of authorblurb.attlist-->]]>
<!--end of authorblurb.module-->]]>

<!ENTITY % personblurb.module "INCLUDE">
<![%personblurb.module;[
<!ENTITY % local.personblurb.attrib "">
<!ENTITY % personblurb.role.attrib "%role.attrib;">

<!ENTITY % personblurb.element "INCLUDE">
<![%personblurb.element;[
<!--doc:A short description or note about a person.
A short description of a person.
Category: author-->
<!ELEMENT personblurb %ho; (title?, (%para.class;)+)>
<!--end of personblurb.element-->]]>

<!ENTITY % personblurb.attlist "INCLUDE">
<![%personblurb.attlist;[
<!ATTLIST personblurb
		%common.attrib;
		%personblurb.role.attrib;
		%local.personblurb.attrib;
>
<!--end of personblurb.attlist-->]]>
<!--end of personblurb.module-->]]>

<!ENTITY % blockquote.module "INCLUDE">
<![%blockquote.module;[

<!ENTITY % local.blockquote.attrib "">
<!ENTITY % blockquote.role.attrib "%role.attrib;">

<!ENTITY % blockquote.element "INCLUDE">
<![%blockquote.element;[
<!--doc:A quotation set off from the main text.
Block quotations are set off from the main text, as opposed to occurring inline.
Category: block things-->
<!ELEMENT blockquote %ho; (blockinfo?, title?, attribution?, (%component.mix;)+)
                      %blockquote.exclusion;>
<!--end of blockquote.element-->]]>

<!ENTITY % blockquote.attlist "INCLUDE">
<![%blockquote.attlist;[
<!ATTLIST blockquote
		%common.attrib;
		%blockquote.role.attrib;
		%local.blockquote.attrib;
>
<!--end of blockquote.attlist-->]]>
<!--end of blockquote.module-->]]>

<!ENTITY % attribution.module "INCLUDE">
<![%attribution.module;[
<!ENTITY % local.attribution.attrib "">
<!ENTITY % attribution.role.attrib "%role.attrib;">

<!ENTITY % attribution.element "INCLUDE">
<![%attribution.element;[
<!--doc:The source of a block quote or epigraph.
An Attribution identifies the source to whom aBlockQuote or Epigraph is ascribed.
Category: blockquotes-->
<!ELEMENT attribution %ho; (%para.char.mix;)*>
<!--end of attribution.element-->]]>

<!ENTITY % attribution.attlist "INCLUDE">
<![%attribution.attlist;[
<!ATTLIST attribution
		%common.attrib;
		%attribution.role.attrib;
		%local.attribution.attrib;
>
<!--end of attribution.attlist-->]]>
<!--end of attribution.module-->]]>

<!ENTITY % bridgehead.module "INCLUDE">
<![%bridgehead.module;[
<!ENTITY % local.bridgehead.attrib "">
<!ENTITY % bridgehead.role.attrib "%role.attrib;">

<!ENTITY % bridgehead.element "INCLUDE">
<![%bridgehead.element;[
<!--doc:A free-floating heading.
Some documents, usually legacy documents, use headings that are not tied to the normal sectional hierarchy. These headings may be represented in DocBook with the BridgeHead element. BridgeHeads may also be useful in fiction or journalistic works that don't have a nested hierarchy.
Category: Sections-->
<!ELEMENT bridgehead %ho; (%title.char.mix;)*>
<!--end of bridgehead.element-->]]>

<!-- Renderas: Indicates the format in which the BridgeHead
		should appear -->


<!ENTITY % bridgehead.attlist "INCLUDE">
<![%bridgehead.attlist;[
<!ATTLIST bridgehead
		renderas	(other
				|sect1
				|sect2
				|sect3
				|sect4
				|sect5)		#IMPLIED
		%common.attrib;
		%bridgehead.role.attrib;
		%local.bridgehead.attrib;
>
<!--end of bridgehead.attlist-->]]>
<!--end of bridgehead.module-->]]>

<!ENTITY % remark.module "INCLUDE">
<![%remark.module;[
<!ENTITY % local.remark.attrib "">
<!ENTITY % remark.role.attrib "%role.attrib;">

<!ENTITY % remark.element "INCLUDE">
<![%remark.element;[
<!--doc:A remark (or comment) intended for presentation in a draft manuscript.
The Remark element is designed to hold remarks, for example, editorial comments, that are useful while the document is in the draft stage, but are not intended for final publication. Remarks are available almost anywhere and have a particularly broad content model. Your processing system may or may not support either the use of comments everywhere they are allowed or the full generality of the Remark content model.Prior to version 4.0 of DocBook, this element was named Comment.-->
<!ELEMENT remark %ho; (%para.char.mix;)*
                      %remark.exclusion;>
<!--end of remark.element-->]]>

<!ENTITY % remark.attlist "INCLUDE">
<![%remark.attlist;[
<!ATTLIST remark
		%common.attrib;
		%remark.role.attrib;
		%local.remark.attrib;
>
<!--end of remark.attlist-->]]>
<!--end of remark.module-->]]>

<!ENTITY % epigraph.module "INCLUDE">
<![%epigraph.module;[
<!ENTITY % local.epigraph.attrib "">
<!ENTITY % epigraph.role.attrib "%role.attrib;">

<!ENTITY % epigraph.element "INCLUDE">
<![%epigraph.element;[
<!--doc:A short inscription at the beginning of a document or component.
An Epigraph is a short inscription, often a quotation or poem, set at the beginning of a document or component. Epigraphs are usually related somehow to the content that follows them and may help set the tone for the component.
Category: block things-->
<!ELEMENT epigraph %ho; (attribution?, ((%para.class;)|literallayout)+)>
<!--end of epigraph.element-->]]>

<!ENTITY % epigraph.attlist "INCLUDE">
<![%epigraph.attlist;[
<!ATTLIST epigraph
		%common.attrib;
		%epigraph.role.attrib;
		%local.epigraph.attrib;
>
<!--end of epigraph.attlist-->]]>
<!-- Attribution (defined above)-->
<!--end of epigraph.module-->]]>

<!ENTITY % footnote.module "INCLUDE">
<![%footnote.module;[
<!ENTITY % local.footnote.attrib "">
<!ENTITY % footnote.role.attrib "%role.attrib;">

<!ENTITY % footnote.element "INCLUDE">
<![%footnote.element;[
<!--doc:This element is a wrapper around the contents of a footnote.Additional references to the same footnote may be generated with FootnoteRef.-->
<!ELEMENT footnote %ho; ((%footnote.mix;)+)
                      %footnote.exclusion;>
<!--end of footnote.element-->]]>

<!ENTITY % footnote.attlist "INCLUDE">
<![%footnote.attlist;[
<!ATTLIST footnote
		%label.attrib;
		%common.attrib;
		%footnote.role.attrib;
		%local.footnote.attrib;
>
<!--end of footnote.attlist-->]]>
<!--end of footnote.module-->]]>

<!ENTITY % highlights.module "INCLUDE">
<![%highlights.module;[
<!ENTITY % local.highlights.attrib "">
<!ENTITY % highlights.role.attrib "%role.attrib;">

<!ENTITY % highlights.element "INCLUDE">
<![%highlights.element;[
<!--doc:A summary of the main points of the discussed component.
Highlights are generally presented at the beginning of a component and offer some sort of summary of the main points that will be discussed.
Category: block things-->
<!ELEMENT highlights %ho; ((%highlights.mix;)+)
                      %highlights.exclusion;>
<!--end of highlights.element-->]]>

<!ENTITY % highlights.attlist "INCLUDE">
<![%highlights.attlist;[
<!ATTLIST highlights
		%common.attrib;
		%highlights.role.attrib;
		%local.highlights.attrib;
>
<!--end of highlights.attlist-->]]>
<!--end of highlights.module-->]]>

<!ENTITY % formalpara.module "INCLUDE">
<![%formalpara.module;[
<!ENTITY % local.formalpara.attrib "">
<!ENTITY % formalpara.role.attrib "%role.attrib;">

<!ENTITY % formalpara.element "INCLUDE">
<![%formalpara.element;[
<!--doc:A paragraph with a title.
Formal paragraphs have a title.
Category: paras-->
<!ELEMENT formalpara %ho; (title, (%ndxterm.class;)*, para)>
<!--end of formalpara.element-->]]>

<!ENTITY % formalpara.attlist "INCLUDE">
<![%formalpara.attlist;[
<!ATTLIST formalpara
		%common.attrib;
		%formalpara.role.attrib;
		%local.formalpara.attrib;
>
<!--end of formalpara.attlist-->]]>
<!--end of formalpara.module-->]]>

<!ENTITY % para.module "INCLUDE">
<![%para.module;[
<!ENTITY % local.para.attrib "">
<!ENTITY % para.role.attrib "%role.attrib;">

<!ENTITY % para.element "INCLUDE">
<![%para.element;[
<!--doc:A Para is a paragraph. Paragraphs in DocBook may contain almost all inlines and most block elements. Sectioning and higher-level structural elements are excluded. DocBook offers two variants of paragraph:SimPara, which cannot contain block elements, and FormalPara, which has a title. Some processing systems may find the presence of block elements in a paragraph difficult to handle. On the other hand, it is frequently most logical, from a structural point of view, to include block elements, especially informal block elements, in the paragraphs that describe their content. There is no easy answer to this problem.
Category: paras-->
<!ELEMENT para %ho; (%para.char.mix; | %para.mix;)*>
<!--end of para.element-->]]>

<!ENTITY % para.attlist "INCLUDE">
<![%para.attlist;[
<!ATTLIST para
		%common.attrib;
		%para.role.attrib;
		%local.para.attrib;
>
<!--end of para.attlist-->]]>
<!--end of para.module-->]]>

<!ENTITY % simpara.module "INCLUDE">
<![%simpara.module;[
<!ENTITY % local.simpara.attrib "">
<!ENTITY % simpara.role.attrib "%role.attrib;">

<!ENTITY % simpara.element "INCLUDE">
<![%simpara.element;[
<!--doc:A paragraph that contains only text and inline markup, no block elements.
A SimPara is a simple paragraph, one that may contain only character data and inline elements. the Para element is less restrictive; it may also contain block level structures (lists, figures, and so on).
Category: paras-->
<!ELEMENT simpara %ho; (%para.char.mix;)*>
<!--end of simpara.element-->]]>

<!ENTITY % simpara.attlist "INCLUDE">
<![%simpara.attlist;[
<!ATTLIST simpara
		%common.attrib;
		%simpara.role.attrib;
		%local.simpara.attrib;
>
<!--end of simpara.attlist-->]]>
<!--end of simpara.module-->]]>

<!ENTITY % admon.module "INCLUDE">
<![%admon.module;[
<!ENTITY % local.admon.attrib "">
<!ENTITY % admon.role.attrib "%role.attrib;">


<!ENTITY % caution.element "INCLUDE">
<![%caution.element;[
<!--doc:A note of caution.
A Caution is an admonition, usually set off from the main text. In some types of documentation, the semantics of admonitions are clearly defined (Caution might imply the possibility of harm to equipment whereas Warning might imply harm to a person) However, DocBook makes no such assertions.
Category: admonitions-->
<!ELEMENT caution %ho; (title?, (%admon.mix;)+)
                      %admon.exclusion;>
<!--end of caution.element-->]]>

<!ENTITY % caution.attlist "INCLUDE">
<![%caution.attlist;[
<!ATTLIST caution
		%common.attrib;
		%admon.role.attrib;
		%local.admon.attrib;
>
<!--end of caution.attlist-->]]>


<!ENTITY % important.element "INCLUDE">
<![%important.element;[
<!--doc:An admonition set off from the text.
Important is an admonition set off from the main text. In some types of documentation, the semantics of admonitions are clearly defined (Caution might imply the possibility of harm to equipment whereas Warning might imply harm to a person), but DocBook makes no such assertions.
Category: admonitions-->
<!ELEMENT important %ho; (title?, (%admon.mix;)+)
                      %admon.exclusion;>
<!--end of important.element-->]]>

<!ENTITY % important.attlist "INCLUDE">
<![%important.attlist;[
<!ATTLIST important
		%common.attrib;
		%admon.role.attrib;
		%local.admon.attrib;
>
<!--end of important.attlist-->]]>


<!ENTITY % note.element "INCLUDE">
<![%note.element;[
<!--doc:A message set off from the text.
A Note is an admonition set off from the main text. In some types of documentation, the semantics of admonitions are clearly defined (Caution might imply the possibility of harm to equipment whereas Warning might imply harm to a person), but DocBook makes no such assertions.
Category: admonitions-->
<!ELEMENT note %ho; (title?, (%admon.mix;)+)
                      %admon.exclusion;>
<!--end of note.element-->]]>

<!ENTITY % note.attlist "INCLUDE">
<![%note.attlist;[
<!ATTLIST note
		%common.attrib;
		%admon.role.attrib;
		%local.admon.attrib;
>
<!--end of note.attlist-->]]>

<!ENTITY % tip.element "INCLUDE">
<![%tip.element;[
<!--doc:A suggestion to the user, set off from the text.
A Tip is an admonition set off from the main text. In some types of documentation, the semantics of admonitions are clearly defined (Caution might imply the possibility of harm to equipment whereas Warning might imply harm to a person), but DocBook makes no such assertions.
Category: admonitions-->
<!ELEMENT tip %ho; (title?, (%admon.mix;)+)
                      %admon.exclusion;>
<!--end of tip.element-->]]>

<!ENTITY % tip.attlist "INCLUDE">
<![%tip.attlist;[
<!ATTLIST tip
		%common.attrib;
		%admon.role.attrib;
		%local.admon.attrib;
>
<!--end of tip.attlist-->]]>


<!ENTITY % warning.element "INCLUDE">
<![%warning.element;[
<!--doc:An admonition set off from the text.
A Warning is an admonition, usually set off from the main text. In some types of documentation, the semantics of admonitions are clearly defined (Caution might imply the possibility of harm to equipment whereas Warning might imply harm to a person), but DocBook makes no such assertions.
Category: admonitions-->
<!ELEMENT warning %ho; (title?, (%admon.mix;)+)
                      %admon.exclusion;>
<!--end of warning.element-->]]>

<!ENTITY % warning.attlist "INCLUDE">
<![%warning.attlist;[
<!ATTLIST warning
		%common.attrib;
		%admon.role.attrib;
		%local.admon.attrib;
>
<!--end of warning.attlist-->]]>

<!--end of admon.module-->]]>

<!-- ...................................................................... -->
<!-- Lists ................................................................ -->

<!-- GlossList ........................ -->

<!ENTITY % glosslist.module "INCLUDE">
<![%glosslist.module;[
<!ENTITY % local.glosslist.attrib "">
<!ENTITY % glosslist.role.attrib "%role.attrib;">

<!ENTITY % glosslist.element "INCLUDE">
<![%glosslist.element;[
<!--doc:A wrapper for a set of GlossEntrys.
While Glossarys are usually limited to component or section boundaries, appearing at the end of a Book or Chapter, for instance, GlossLists can appear anywhere that the other list types are allowed. Using a GlossList in running text, instead of a VariableList, for example, maintains the semantic distinction of a Glossary. This distinction may be necessary if you want to automatically point to the members of the list with GlossTerms in the body of the text.-->
<!ELEMENT glosslist %ho; (blockinfo?, (%formalobject.title.content;)?, glossentry+)>
<!--end of glosslist.element-->]]>

<!ENTITY % glosslist.attlist "INCLUDE">
<![%glosslist.attlist;[
<!ATTLIST glosslist
		%common.attrib;
		%glosslist.role.attrib;
		%local.glosslist.attrib;
>
<!--end of glosslist.attlist-->]]>
<!--end of glosslist.module-->]]>

<!ENTITY % glossentry.content.module "INCLUDE">
<![%glossentry.content.module;[
<!ENTITY % glossentry.module "INCLUDE">
<![%glossentry.module;[
<!ENTITY % local.glossentry.attrib "">
<!ENTITY % glossentry.role.attrib "%role.attrib;">

<!ENTITY % glossentry.element "INCLUDE">
<![%glossentry.element;[
<!--doc:An entry in a Glossary or GlossList.
GlossEntry is a wrapper around a glossary term and its definition.-->
<!ELEMENT glossentry %ho; (glossterm, acronym?, abbrev?,
                      (%ndxterm.class;)*,
                      revhistory?, (glosssee|glossdef+))>
<!--end of glossentry.element-->]]>

<!-- SortAs: String by which the GlossEntry is to be sorted
		(alphabetized) in lieu of its proper content -->


<!ENTITY % glossentry.attlist "INCLUDE">
<![%glossentry.attlist;[
<!ATTLIST glossentry
		sortas		CDATA		#IMPLIED
		%common.attrib;
		%glossentry.role.attrib;
		%local.glossentry.attrib;
>
<!--end of glossentry.attlist-->]]>
<!--end of glossentry.module-->]]>

<!-- GlossTerm (defined in the Inlines section, below)-->
<!ENTITY % glossdef.module "INCLUDE">
<![%glossdef.module;[
<!ENTITY % local.glossdef.attrib "">
<!ENTITY % glossdef.role.attrib "%role.attrib;">

<!ENTITY % glossdef.element "INCLUDE">
<![%glossdef.element;[
<!--doc:A definition in a GlossEntry.
A GlossDef contains the description or definition of a GlossTerm.
Category: Glossary-->
<!ELEMENT glossdef %ho; ((%glossdef.mix;)+, glossseealso*)>
<!--end of glossdef.element-->]]>

<!-- Subject: List of subjects; keywords for the definition -->


<!ENTITY % glossdef.attlist "INCLUDE">
<![%glossdef.attlist;[
<!ATTLIST glossdef
		subject		CDATA		#IMPLIED
		%common.attrib;
		%glossdef.role.attrib;
		%local.glossdef.attrib;
>
<!--end of glossdef.attlist-->]]>
<!--end of glossdef.module-->]]>

<!ENTITY % glosssee.module "INCLUDE">
<![%glosssee.module;[
<!ENTITY % local.glosssee.attrib "">
<!ENTITY % glosssee.role.attrib "%role.attrib;">

<!ENTITY % glosssee.element "INCLUDE">
<![%glosssee.element;[
<!--doc:A cross-reference from one GlossEntry to another.
GlossSee directs the reader to another GlossEntry instead of this one. A See cross-reference occurs in place of the definition.
Category: Glossary-->
<!ELEMENT glosssee %ho; (%para.char.mix;)*>
<!--end of glosssee.element-->]]>

<!-- OtherTerm: Reference to the GlossEntry whose GlossTerm
		should be displayed at the point of the GlossSee -->


<!ENTITY % glosssee.attlist "INCLUDE">
<![%glosssee.attlist;[
<!ATTLIST glosssee
		otherterm	IDREF		#IMPLIED
		%common.attrib;
		%glosssee.role.attrib;
		%local.glosssee.attrib;
>
<!--end of glosssee.attlist-->]]>
<!--end of glosssee.module-->]]>

<!ENTITY % glossseealso.module "INCLUDE">
<![%glossseealso.module;[
<!ENTITY % local.glossseealso.attrib "">
<!ENTITY % glossseealso.role.attrib "%role.attrib;">

<!ENTITY % glossseealso.element "INCLUDE">
<![%glossseealso.element;[
<!--doc:A cross-reference from one GlossEntry to another.
GlossSeeAlso directs the reader to another GlossEntry for additional information. It is presented in addition to theGlossDef.
Category: Glossary-->
<!ELEMENT glossseealso %ho; (%para.char.mix;)*>
<!--end of glossseealso.element-->]]>

<!-- OtherTerm: Reference to the GlossEntry whose GlossTerm
		should be displayed at the point of the GlossSeeAlso -->


<!ENTITY % glossseealso.attlist "INCLUDE">
<![%glossseealso.attlist;[
<!ATTLIST glossseealso
		otherterm	IDREF		#IMPLIED
		%common.attrib;
		%glossseealso.role.attrib;
		%local.glossseealso.attrib;
>
<!--end of glossseealso.attlist-->]]>
<!--end of glossseealso.module-->]]>
<!--end of glossentry.content.module-->]]>

<!-- ItemizedList and OrderedList ..... -->

<!ENTITY % itemizedlist.module "INCLUDE">
<![%itemizedlist.module;[
<!ENTITY % local.itemizedlist.attrib "">
<!ENTITY % itemizedlist.role.attrib "%role.attrib;">

<!ENTITY % itemizedlist.element "INCLUDE">
<![%itemizedlist.element;[
<!--doc:A list in which each entry is marked with a bullet or other dingbat.
In an ItemizedList, each member of the list is marked with a bullet, dash, or other symbol.
Category: Lists-->
<!ELEMENT itemizedlist %ho; (blockinfo?, (%formalobject.title.content;)?,
 			    (%listpreamble.mix;)*, listitem+)>

<!--end of itemizedlist.element-->]]>

<!-- Spacing: Whether the vertical space in the list should be
		compressed -->
<!-- Mark: Keyword, e.g., bullet, dash, checkbox, none;
		list of keywords and defaults are implementation specific -->


<!ENTITY % itemizedlist.attlist "INCLUDE">
<![%itemizedlist.attlist;[
<!ATTLIST itemizedlist		spacing		(normal
				|compact)	#IMPLIED
		%mark.attrib;
		%common.attrib;
		%itemizedlist.role.attrib;
		%local.itemizedlist.attrib;
>
<!--end of itemizedlist.attlist-->]]>
<!--end of itemizedlist.module-->]]>

<!ENTITY % orderedlist.module "INCLUDE">
<![%orderedlist.module;[
<!ENTITY % local.orderedlist.attrib "">
<!ENTITY % orderedlist.role.attrib "%role.attrib;">

<!ENTITY % orderedlist.element "INCLUDE">
<![%orderedlist.element;[
<!--doc:A list in which each entry is marked with a sequentially incremented label.
In an OrderedList, each member of the list is marked with a numeral, letter, or other sequential symbol (such as roman numerals).
Category: Lists-->
<!ELEMENT orderedlist %ho; (blockinfo?, (%formalobject.title.content;)?,
 			    (%listpreamble.mix;)*, listitem+)>

<!--end of orderedlist.element-->]]>

<!-- Numeration: Style of ListItem numbered; default is expected
		to be Arabic -->
<!-- InheritNum: Specifies for a nested list that the numbering
		of ListItems should include the number of the item
		within which they are nested (e.g., 1a and 1b within 1,
		rather than a and b) -->
<!-- Continuation: Where list numbering begins afresh (Restarts,
		the default) or continues that of the immediately preceding
		list (Continues) -->
<!-- Spacing: Whether the vertical space in the list should be
		compressed -->


<!ENTITY % orderedlist.attlist "INCLUDE">
<![%orderedlist.attlist;[
<!ATTLIST orderedlist
		numeration	(arabic
				|upperalpha
				|loweralpha
				|upperroman
				|lowerroman)	#IMPLIED
		inheritnum	(inherit
				|ignore)	"ignore"
		continuation	(continues
				|restarts)	"restarts"
		spacing		(normal
				|compact)	#IMPLIED
		%common.attrib;
		%orderedlist.role.attrib;
		%local.orderedlist.attrib;
>
<!--end of orderedlist.attlist-->]]>
<!--end of orderedlist.module-->]]>

<!ENTITY % listitem.module "INCLUDE">
<![%listitem.module;[
<!ENTITY % local.listitem.attrib "">
<!ENTITY % listitem.role.attrib "%role.attrib;">

<!ENTITY % listitem.element "INCLUDE">
<![%listitem.element;[
<!--doc:A wrapper for the elements of a list item.
The ListItem element is a wrapper around an item in a list. In an ItemizedList or an OrderedList, the ListItem surrounds the entire list item. In a VariableList, ListItem surrounds the definition part of the list item.
Category: Lists-->
<!ELEMENT listitem %ho; ((%component.mix;)+)>
<!--end of listitem.element-->]]>

<!-- Override: Indicates the mark to be used for this ListItem
		instead of the default mark or the mark specified by
		the Mark attribute on the enclosing ItemizedList -->


<!ENTITY % listitem.attlist "INCLUDE">
<![%listitem.attlist;[
<!ATTLIST listitem
		override	CDATA		#IMPLIED
		%common.attrib;
		%listitem.role.attrib;
		%local.listitem.attrib;
>
<!--end of listitem.attlist-->]]>
<!--end of listitem.module-->]]>

<!-- SegmentedList .................... -->
<!ENTITY % segmentedlist.content.module "INCLUDE">
<![%segmentedlist.content.module;[
<!ENTITY % segmentedlist.module "INCLUDE">
<![%segmentedlist.module;[
<!ENTITY % local.segmentedlist.attrib "">
<!ENTITY % segmentedlist.role.attrib "%role.attrib;">

<!ENTITY % segmentedlist.element "INCLUDE">
<![%segmentedlist.element;[
<!--doc:A segmented list, a list of sets of elements.
A SegmentedList consists of a set of headings (SegTitles) and a list of parallel sets of elements. Every SegListItem contains a set of elements that have a one-to-one correspondence with the headings. Each of these elements is contained in a Seg.
Category: Lists-->
<!ELEMENT segmentedlist %ho; ((%formalobject.title.content;)?,
                         segtitle+,
                         seglistitem+)>
<!--end of segmentedlist.element-->]]>

<!ENTITY % segmentedlist.attlist "INCLUDE">
<![%segmentedlist.attlist;[
<!ATTLIST segmentedlist
		%common.attrib;
		%segmentedlist.role.attrib;
		%local.segmentedlist.attrib;
>
<!--end of segmentedlist.attlist-->]]>
<!--end of segmentedlist.module-->]]>

<!ENTITY % segtitle.module "INCLUDE">
<![%segtitle.module;[
<!ENTITY % local.segtitle.attrib "">
<!ENTITY % segtitle.role.attrib "%role.attrib;">

<!ENTITY % segtitle.element "INCLUDE">
<![%segtitle.element;[
<!--doc:The title of an element of a list item in a segmented list.
Each heading in a SegmentedList is contained in its own SegTitle. The relationship between SegTitles and Segs is implicit in the document; the first SegTitle goes with the first Seg in each SegListItem, the second SegTitle goes with the second Seg, and so on.-->
<!ELEMENT segtitle %ho; (%title.char.mix;)*>
<!--end of segtitle.element-->]]>

<!ENTITY % segtitle.attlist "INCLUDE">
<![%segtitle.attlist;[
<!ATTLIST segtitle
		%common.attrib;
		%segtitle.role.attrib;
		%local.segtitle.attrib;
>
<!--end of segtitle.attlist-->]]>
<!--end of segtitle.module-->]]>

<!ENTITY % seglistitem.module "INCLUDE">
<![%seglistitem.module;[
<!ENTITY % local.seglistitem.attrib "">
<!ENTITY % seglistitem.role.attrib "%role.attrib;">

<!ENTITY % seglistitem.element "INCLUDE">
<![%seglistitem.element;[
<!--doc:A list item in a segmented list.
A SegmentedList consists of a set of headings (SegTitles) and a list of parallel sets of elements. Each set of elements is stored in a SegListItem.-->
<!ELEMENT seglistitem %ho; (seg+)>
<!--end of seglistitem.element-->]]>

<!ENTITY % seglistitem.attlist "INCLUDE">
<![%seglistitem.attlist;[
<!ATTLIST seglistitem
		%common.attrib;
		%seglistitem.role.attrib;
		%local.seglistitem.attrib;
>
<!--end of seglistitem.attlist-->]]>
<!--end of seglistitem.module-->]]>

<!ENTITY % seg.module "INCLUDE">
<![%seg.module;[
<!ENTITY % local.seg.attrib "">
<!ENTITY % seg.role.attrib "%role.attrib;">

<!ENTITY % seg.element "INCLUDE">
<![%seg.element;[
<!--doc:An element of a list item in a segmented list.
A SegmentedList consists of a set of headings (SegTitles) and a list of parallel sets of elements. Every SegListItem contains a set of elements that have a one-to-one correspondence with the headings. Each of these elements is contained in a Seg.-->
<!ELEMENT seg %ho; (%para.char.mix;)*>
<!--end of seg.element-->]]>

<!ENTITY % seg.attlist "INCLUDE">
<![%seg.attlist;[
<!ATTLIST seg
		%common.attrib;
		%seg.role.attrib;
		%local.seg.attrib;
>
<!--end of seg.attlist-->]]>
<!--end of seg.module-->]]>
<!--end of segmentedlist.content.module-->]]>

<!-- SimpleList ....................... -->

<!ENTITY % simplelist.content.module "INCLUDE">
<![%simplelist.content.module;[
<!ENTITY % simplelist.module "INCLUDE">
<![%simplelist.module;[
<!ENTITY % local.simplelist.attrib "">
<!ENTITY % simplelist.role.attrib "%role.attrib;">

<!ENTITY % simplelist.element "INCLUDE">
<![%simplelist.element;[
<!--doc:An undecorated list of single words or short phrases.
A SimpleList is a list of words or phrases. It offers a convenient alternative to the other list elements for inline content.
Category: Lists-->
<!ELEMENT simplelist %ho; (member+)>
<!--end of simplelist.element-->]]>

<!-- Columns: The number of columns the array should contain -->
<!-- Type: How the Members of the SimpleList should be
		formatted: Inline (members separated with commas etc.
		inline), Vert (top to bottom in n Columns), or Horiz (in
		the direction of text flow) in n Columns.  If Column
		is 1 or implied, Type=Vert and Type=Horiz give the same
		results. -->


<!ENTITY % simplelist.attlist "INCLUDE">
<![%simplelist.attlist;[
<!ATTLIST simplelist
		columns		CDATA		#IMPLIED
		type		(inline
				|vert
				|horiz)		"vert"
		%common.attrib;
		%simplelist.role.attrib;
		%local.simplelist.attrib;
>
<!--end of simplelist.attlist-->]]>
<!--end of simplelist.module-->]]>

<!ENTITY % member.module "INCLUDE">
<![%member.module;[
<!ENTITY % local.member.attrib "">
<!ENTITY % member.role.attrib "%role.attrib;">

<!ENTITY % member.element "INCLUDE">
<![%member.element;[
<!--doc:An element of a simple list.
A Member is an element of a SimpleList. Unlike the other lists, items in a SimpleList are constrained to character data and inline elements.-->
<!ELEMENT member %ho; (%para.char.mix;)*>
<!--end of member.element-->]]>

<!ENTITY % member.attlist "INCLUDE">
<![%member.attlist;[
<!ATTLIST member
		%common.attrib;
		%member.role.attrib;
		%local.member.attrib;
>
<!--end of member.attlist-->]]>
<!--end of member.module-->]]>
<!--end of simplelist.content.module-->]]>

<!-- VariableList ..................... -->

<!ENTITY % variablelist.content.module "INCLUDE">
<![%variablelist.content.module;[
<!ENTITY % variablelist.module "INCLUDE">
<![%variablelist.module;[
<!ENTITY % local.variablelist.attrib "">
<!ENTITY % variablelist.role.attrib "%role.attrib;">

<!ENTITY % variablelist.element "INCLUDE">
<![%variablelist.element;[
<!--doc:A list in which each entry is composed of a set of one or more terms and an associated description.
A VariableList is a list consisting of Terms and their definitions or descriptions.
Category: Lists-->
<!ELEMENT variablelist %ho; (blockinfo?, (%formalobject.title.content;)?,
 			    (%listpreamble.mix;)*, varlistentry+)>
<!--end of variablelist.element-->]]>

<!-- TermLength: Length beyond which the presentation engine
		may consider the Term too long and select an alternate
		presentation of the Term and, or, its associated ListItem. -->


<!ENTITY % variablelist.attlist "INCLUDE">
<![%variablelist.attlist;[
<!ATTLIST variablelist
		termlength	CDATA		#IMPLIED
		%common.attrib;
		%variablelist.role.attrib;
		%local.variablelist.attrib;
>
<!--end of variablelist.attlist-->]]>
<!--end of variablelist.module-->]]>

<!ENTITY % varlistentry.module "INCLUDE">
<![%varlistentry.module;[
<!ENTITY % local.varlistentry.attrib "">
<!ENTITY % varlistentry.role.attrib "%role.attrib;">

<!ENTITY % varlistentry.element "INCLUDE">
<![%varlistentry.element;[
<!--doc:A wrapper for a set of terms and the associated description in a variable list.
A VarListEntry is an entry in a VariableList. Each VarListEntry contains one or more Terms and their description or definition.-->
<!ELEMENT varlistentry %ho; (term+, listitem)>
<!--end of varlistentry.element-->]]>

<!ENTITY % varlistentry.attlist "INCLUDE">
<![%varlistentry.attlist;[
<!ATTLIST varlistentry
		%common.attrib;
		%varlistentry.role.attrib;
		%local.varlistentry.attrib;
>
<!--end of varlistentry.attlist-->]]>
<!--end of varlistentry.module-->]]>

<!ENTITY % term.module "INCLUDE">
<![%term.module;[
<!ENTITY % local.term.attrib "">
<!ENTITY % term.role.attrib "%role.attrib;">

<!ENTITY % term.element "INCLUDE">
<![%term.element;[
<!--doc:The word or phrase being defined or described in a variable list.
The Term in a VarListEntry identifies the thing that is described or defined by that entry.-->
<!ELEMENT term %ho; (%para.char.mix;)*>
<!--end of term.element-->]]>

<!ENTITY % term.attlist "INCLUDE">
<![%term.attlist;[
<!ATTLIST term
		%common.attrib;
		%term.role.attrib;
		%local.term.attrib;
>
<!--end of term.attlist-->]]>
<!--end of term.module-->]]>

<!-- ListItem (defined above)-->
<!--end of variablelist.content.module-->]]>

<!-- CalloutList ...................... -->

<!ENTITY % calloutlist.content.module "INCLUDE">
<![%calloutlist.content.module;[
<!ENTITY % calloutlist.module "INCLUDE">
<![%calloutlist.module;[
<!ENTITY % local.calloutlist.attrib "">
<!ENTITY % calloutlist.role.attrib "%role.attrib;">

<!ENTITY % calloutlist.element "INCLUDE">
<![%calloutlist.element;[
<!--doc:A list of Callouts.
A CalloutList is a list of annotations or descriptions. Each Callout points to the area on a Graphic, ProgramListing, or Screen that it augments. The areas are identified by coordinates in an Area or AreaSet, or by an explicit CO element.
Category: Callouts-->
<!ELEMENT calloutlist %ho; ((%formalobject.title.content;)?, callout+)>
<!--end of calloutlist.element-->]]>

<!ENTITY % calloutlist.attlist "INCLUDE">
<![%calloutlist.attlist;[
<!ATTLIST calloutlist
		%common.attrib;
		%calloutlist.role.attrib;
		%local.calloutlist.attrib;
>
<!--end of calloutlist.attlist-->]]>
<!--end of calloutlist.module-->]]>

<!ENTITY % callout.module "INCLUDE">
<![%callout.module;[
<!ENTITY % local.callout.attrib "">
<!ENTITY % callout.role.attrib "%role.attrib;">

<!ENTITY % callout.element "INCLUDE">
<![%callout.element;[
<!--doc:A “called out” description of a marked Area.
A callout is a visual device for associating annotations with an image, program listing, or similar figure. Each location is identified with a mark, and the annotation is identified with the same mark. This is somewhat analogous to the notion of footnotes in print. An example will help illustrate the concept. In the following example, the synopsis for the mv command is annotated with two marks. Note the location of the old and new filenames. Somewhere else in the document, usually close by, a CalloutList provides a description for each of the callouts: Each Callout contains an annotation for an individual callout or a group of callouts. The Callout points to the areas that it annotates with ID references. The areas are identified by coordinates in an Area or AreaSet, or by an explicit CO element.-->
<!ELEMENT callout %ho; ((%component.mix;)+)>
<!--end of callout.element-->]]>

<!-- AreaRefs: IDs of one or more Areas or AreaSets described
		by this Callout -->


<!ENTITY % callout.attlist "INCLUDE">
<![%callout.attlist;[
<!ATTLIST callout
		arearefs	IDREFS		#REQUIRED
		%common.attrib;
		%callout.role.attrib;
		%local.callout.attrib;
>
<!--end of callout.attlist-->]]>
<!--end of callout.module-->]]>
<!--end of calloutlist.content.module-->]]>

<!-- ...................................................................... -->
<!-- Objects .............................................................. -->

<!-- Examples etc. .................... -->

<!ENTITY % example.module "INCLUDE">
<![%example.module;[
<!ENTITY % local.example.attrib "">
<!ENTITY % example.role.attrib "%role.attrib;">

<!ENTITY % example.element "INCLUDE">
<![%example.element;[
<!--doc:A formal example, with a title.
Example is a formal example with a title. Examples often contain ProgramListings or other large, block elements. Frequently they are given IDs and referenced from the text with XRef or Link.
Category: figures, examples, etc.-->
<!ELEMENT example %ho; (blockinfo?, (%formalobject.title.content;), (%example.mix;)+)
		%formal.exclusion;>
<!--end of example.element-->]]>

<!ENTITY % example.attlist "INCLUDE">
<![%example.attlist;[
<!ATTLIST example
		floatstyle	CDATA			#IMPLIED
		%label.attrib;
		%width.attrib;
		%common.attrib;
		%example.role.attrib;
		%local.example.attrib;
>
<!--end of example.attlist-->]]>
<!--end of example.module-->]]>

<!ENTITY % informalexample.module "INCLUDE">
<![%informalexample.module;[
<!ENTITY % local.informalexample.attrib "">
<!ENTITY % informalexample.role.attrib "%role.attrib;">

<!ENTITY % informalexample.element "INCLUDE">
<![%informalexample.element;[
<!--doc:A displayed example without a title.
InformalExample is a wrapper for an example without a title. Examples often contain ProgramListings or other large block elements.
Category: figures, examples, etc.-->
<!ELEMENT informalexample %ho; (blockinfo?, (%example.mix;)+)>
<!--end of informalexample.element-->]]>

<!ENTITY % informalexample.attlist "INCLUDE">
<![%informalexample.attlist;[
<!ATTLIST informalexample
		floatstyle	CDATA			#IMPLIED
		%width.attrib;
		%common.attrib;
		%informalexample.role.attrib;
		%local.informalexample.attrib;
>
<!--end of informalexample.attlist-->]]>
<!--end of informalexample.module-->]]>

<!ENTITY % programlistingco.module "INCLUDE">
<![%programlistingco.module;[
<!ENTITY % local.programlistingco.attrib "">
<!ENTITY % programlistingco.role.attrib "%role.attrib;">

<!ENTITY % programlistingco.element "INCLUDE">
<![%programlistingco.element;[
<!--doc:A program listing with associated areas used in callouts.
Callouts, such as numbered bullets, are an annotation mechanism. In an online system, these bullets are frequently hot, and clicking on them sends you to the corresponding annotation. A ProgramListingCO is a wrapper around an AreaSpec and a ProgramListing. An AreaSpec identifies the locations (coordinates) in the ProgramListing where the callouts occur. The ProgramListingCO may also contain the list of annotations in a CalloutList, although the CalloutList may also occur outside of the wrapper, elsewhere in the document. It is also possible to embed CO elements directly in the verbatim text, in order to avoid having to calculate the correct coordinates. If you decided to go this route, use a ProgramListing and a CalloutList without theProgramListingCO wrapper. A ProgramListingCO must specify at least one coordinate. For a complete description of callouts, see Callout.
Category: Callouts-->
<!ELEMENT programlistingco %ho; (areaspec, programlisting, calloutlist*)>
<!--end of programlistingco.element-->]]>

<!ENTITY % programlistingco.attlist "INCLUDE">
<![%programlistingco.attlist;[
<!ATTLIST programlistingco
		%common.attrib;
		%programlistingco.role.attrib;
		%local.programlistingco.attrib;
>
<!--end of programlistingco.attlist-->]]>
<!-- CalloutList (defined above in Lists)-->
<!--end of informalexample.module-->]]>

<!ENTITY % areaspec.content.module "INCLUDE">
<![%areaspec.content.module;[
<!ENTITY % areaspec.module "INCLUDE">
<![%areaspec.module;[
<!ENTITY % local.areaspec.attrib "">
<!ENTITY % areaspec.role.attrib "%role.attrib;">

<!ENTITY % areaspec.element "INCLUDE">
<![%areaspec.element;[
<!--doc:A collection of regions in a graphic or code example.
An AreaSpec holds a collection of regions and/or region sets in a graphic, program listing, or screen that are associated with Callout descriptions. See Area for a description of the attributes.
Category: Callouts-->
<!ELEMENT areaspec %ho; ((area|areaset)+)>
<!--end of areaspec.element-->]]>

<!-- Units: global unit of measure in which coordinates in
		this spec are expressed:

		- CALSPair "x1,y1 x2,y2": lower-left and upper-right
		coordinates in a rectangle describing repro area in which
		graphic is placed, where X and Y dimensions are each some
		number 0..10000 (taken from CALS graphic attributes)

		- LineColumn "line column": line number and column number
		at which to start callout text in "linespecific" content

		- LineRange "startline endline": whole lines from startline
		to endline in "linespecific" content

		- LineColumnPair "line1 col1 line2 col2": starting and ending
		points of area in "linespecific" content that starts at
		first position and ends at second position (including the
		beginnings of any intervening lines)

		- Other: directive to look at value of OtherUnits attribute
		to get implementation-specific keyword

		The default is implementation-specific; usually dependent on
		the parent element (GraphicCO gets CALSPair, ProgramListingCO
		and ScreenCO get LineColumn) -->
<!-- OtherUnits: User-defined units -->


<!ENTITY % areaspec.attlist "INCLUDE">
<![%areaspec.attlist;[
<!ATTLIST areaspec
		units		(calspair
				|linecolumn
				|linerange
				|linecolumnpair
				|other)		#IMPLIED
		otherunits	NMTOKEN		#IMPLIED
		%common.attrib;
		%areaspec.role.attrib;
		%local.areaspec.attrib;
>
<!--end of areaspec.attlist-->]]>
<!--end of areaspec.module-->]]>

<!ENTITY % area.module "INCLUDE">
<![%area.module;[
<!ENTITY % local.area.attrib "">
<!ENTITY % area.role.attrib "%role.attrib;">

<!ENTITY % area.element "INCLUDE">
<![%area.element;[
<!--doc:A region defined for a Callout in a graphic or code example.
An Area is an empty element holding information about a region in a graphic, program listing, or screen. The region is generally decorated with a number, symbol, or other distinctive mark. The mark is usually used as the label for the Callout in a CalloutList, which allows the reader to identify which callouts are associated with which regions. The marks may be generated by the processing application from the Areas, or it may be added by some other process. (This is an interchange issue. See .) For a complete description of callouts, see Callout.-->
<!ELEMENT area %ho; EMPTY>
<!--end of area.element-->]]>

<!-- bug number/symbol override or initialization -->
<!-- to any related information -->
<!-- Units: unit of measure in which coordinates in this
		area are expressed; inherits from AreaSet and AreaSpec -->
<!-- OtherUnits: User-defined units -->


<!ENTITY % area.attlist "INCLUDE">
<![%area.attlist;[
<!ATTLIST area
		%label.attrib;
		%linkends.attrib;
		units		(calspair
				|linecolumn
				|linerange
				|linecolumnpair
				|other)		#IMPLIED
		otherunits	NMTOKEN		#IMPLIED
		coords		CDATA		#REQUIRED
		%idreq.common.attrib;
		%area.role.attrib;
		%local.area.attrib;
>
<!--end of area.attlist-->]]>
<!--end of area.module-->]]>

<!ENTITY % areaset.module "INCLUDE">
<![%areaset.module;[
<!ENTITY % local.areaset.attrib "">
<!ENTITY % areaset.role.attrib "%role.attrib;">

<!ENTITY % areaset.element "INCLUDE">
<![%areaset.element;[
<!--doc:A set of related areas in a graphic or code example.
An AreaSet contains one or more Areas. These areas are bound in a set in order to associate them with a single Callout description. See Area for a more complete description of the areas. For a complete description of callouts, see Callout.-->
<!ELEMENT areaset %ho; (area+)>
<!--end of areaset.element-->]]>

<!-- bug number/symbol override or initialization -->
<!-- Units: unit of measure in which coordinates in this
		area are expressed; inherits from AreaSpec -->


<!ENTITY % areaset.attlist "INCLUDE">
<![%areaset.attlist;[
<!ATTLIST areaset
		%label.attrib;
		units		(calspair
				|linecolumn
				|linerange
				|linecolumnpair
				|other)		#IMPLIED
		otherunits	NMTOKEN		#IMPLIED
		coords		CDATA		#REQUIRED
		%idreq.common.attrib;
		%areaset.role.attrib;
		%local.areaset.attrib;
>
<!--end of areaset.attlist-->]]>
<!--end of areaset.module-->]]>
<!--end of areaspec.content.module-->]]>

<!ENTITY % programlisting.module "INCLUDE">
<![%programlisting.module;[
<!ENTITY % local.programlisting.attrib "">
<!ENTITY % programlisting.role.attrib "%role.attrib;">

<!ENTITY % programlisting.element "INCLUDE">
<![%programlisting.element;[
<!--doc:A literal listing of all or part of a program.
A ProgramListing is a verbatim environment for program source or source fragment listings. ProgramListings are often placed in Examples or Figures so that they can be cross-referenced from the text.
Category: verbatim-->
<!ELEMENT programlisting %ho; (%para.char.mix;|co|coref|lineannotation|textobject)*>
<!--end of programlisting.element-->]]>

<!ENTITY % programlisting.attlist "INCLUDE">
<![%programlisting.attlist;[
<!ATTLIST programlisting
		%width.attrib;
		%linespecific.attrib;
		%common.attrib;
		%programlisting.role.attrib;
		%local.programlisting.attrib;
>
<!--end of programlisting.attlist-->]]>
<!--end of programlisting.module-->]]>

<!ENTITY % literallayout.module "INCLUDE">
<![%literallayout.module;[
<!ENTITY % local.literallayout.attrib "">
<!ENTITY % literallayout.role.attrib "%role.attrib;">

<!ENTITY % literallayout.element "INCLUDE">
<![%literallayout.element;[
<!--doc:A block of text in which line breaks and white space are to be reproduced faithfully.
LiteralLayout is a verbatim environment. Unlike the other verbatim environments, it does not have strong semantic overtones and may not imply a font change.
Category: verbatim-->
<!ELEMENT literallayout %ho; (%para.char.mix;|co|coref|textobject|lineannotation)*>
<!--end of literallayout.element-->]]>

<!ENTITY % literallayout.attlist "INCLUDE">
<![%literallayout.attlist;[
<!ATTLIST literallayout
		%width.attrib;
		%linespecific.attrib;
		class	(monospaced|normal)	"normal"
		%common.attrib;
		%literallayout.role.attrib;
		%local.literallayout.attrib;
>
<!--end of literallayout.attlist-->]]>
<!-- LineAnnotation (defined in the Inlines section, below)-->
<!--end of literallayout.module-->]]>

<!ENTITY % screenco.module "INCLUDE">
<![%screenco.module;[
<!ENTITY % local.screenco.attrib "">
<!ENTITY % screenco.role.attrib "%role.attrib;">

<!ENTITY % screenco.element "INCLUDE">
<![%screenco.element;[
<!--doc:A screen with associated areas used in callouts.
Callouts, such as numbered bullets, are an annotation mechanism. In an online system, these bullets are frequently hot, and clicking on them navigates to the corresponding annotation. A ScreenCO is a wrapper around an AreaSpec and a Screen. An AreaSpec identifies the locations (coordinates) in the Screen where the callouts occur. The ScreenCO may also contain the list of annotations in a CalloutList, although the CalloutList may also occur outside of the wrapper, elsewhere in the document. It is also possible to embed CO elements directly in the verbatim text, in order to avoid the overhead of calculating the correct coordinates. If you decide to follow this route, use a Screen and a CalloutList without the ScreenCO wrapper. A ScreenCO must specify at least one coordinate. For a complete description of callouts, see Callout.
Category: Callouts-->
<!ELEMENT screenco %ho; (areaspec, screen, calloutlist*)>
<!--end of screenco.element-->]]>

<!ENTITY % screenco.attlist "INCLUDE">
<![%screenco.attlist;[
<!ATTLIST screenco
		%common.attrib;
		%screenco.role.attrib;
		%local.screenco.attrib;
>
<!--end of screenco.attlist-->]]>
<!-- AreaSpec (defined above)-->
<!-- CalloutList (defined above in Lists)-->
<!--end of screenco.module-->]]>

<!ENTITY % screen.module "INCLUDE">
<![%screen.module;[
<!ENTITY % local.screen.attrib "">
<!ENTITY % screen.role.attrib "%role.attrib;">

<!ENTITY % screen.element "INCLUDE">
<![%screen.element;[
<!--doc:Text that a user sees or might see on a computer screen.
A Screen is a verbatim environment for displaying text that the user might see on a computer terminal. It is often used to display the results of a command. Having less specific semantic overtones, Screen is often used wherever a verbatim presentation is desired, but the semantic of ProgramListing is inappropriate.
Category: verbatim-->
<!ELEMENT screen %ho; (%para.char.mix;|co|coref|textobject|lineannotation)*>
<!--end of screen.element-->]]>

<!ENTITY % screen.attlist "INCLUDE">
<![%screen.attlist;[
<!ATTLIST screen
		%width.attrib;
		%linespecific.attrib;
		%common.attrib;
		%screen.role.attrib;
		%local.screen.attrib;
>
<!--end of screen.attlist-->]]>
<!--end of screen.module-->]]>

<!ENTITY % screenshot.content.module "INCLUDE">
<![%screenshot.content.module;[
<!ENTITY % screenshot.module "INCLUDE">
<![%screenshot.module;[
<!ENTITY % local.screenshot.attrib "">
<!ENTITY % screenshot.role.attrib "%role.attrib;">

<!ENTITY % screenshot.element "INCLUDE">
<![%screenshot.element;[
<!--doc:A representation of what the user sees or might see on a computer screen.
A ScreenShot is a graphical environment for displaying an image of what the user might see on a computer screen. It is often used to display application screen shots, dialog boxes, and other components of a graphical user interface.
Category: verbatim-->
<!ELEMENT screenshot %ho; (screeninfo?,
                      (graphic|graphicco
                      |mediaobject|mediaobjectco))>
<!--end of screenshot.element-->]]>

<!ENTITY % screenshot.attlist "INCLUDE">
<![%screenshot.attlist;[
<!ATTLIST screenshot
		%common.attrib;
		%screenshot.role.attrib;
		%local.screenshot.attrib;
>
<!--end of screenshot.attlist-->]]>
<!--end of screenshot.module-->]]>

<!ENTITY % screeninfo.module "INCLUDE">
<![%screeninfo.module;[
<!ENTITY % local.screeninfo.attrib "">
<!ENTITY % screeninfo.role.attrib "%role.attrib;">

<!ENTITY % screeninfo.element "INCLUDE">
<![%screeninfo.element;[
<!--doc:Information about how a screen shot was produced.
ScreenInfo contains meta-information about how a ScreenShot was produced. Note that the content model of ScreenShot is radically different from the other info elements, to which it bears little or no resemblance. ScreenInfo is a good place to store information about how and at what resolution a screen shot was produced, when it was produced, and by whom.
Category: Meta-wrappers-->
<!ELEMENT screeninfo %ho; (%para.char.mix;)*
		%ubiq.exclusion;>
<!--end of screeninfo.element-->]]>

<!ENTITY % screeninfo.attlist "INCLUDE">
<![%screeninfo.attlist;[
<!ATTLIST screeninfo
		%common.attrib;
		%screeninfo.role.attrib;
		%local.screeninfo.attrib;
>
<!--end of screeninfo.attlist-->]]>
<!--end of screeninfo.module-->]]>
<!--end of screenshot.content.module-->]]>

<!-- Figures etc. ..................... -->

<!ENTITY % figure.module "INCLUDE">
<![%figure.module;[
<!ENTITY % local.figure.attrib "">
<!ENTITY % figure.role.attrib "%role.attrib;">

<!ENTITY % figure.element "INCLUDE">
<![%figure.element;[
<!--doc:A formal figure, generally an illustration, with a title.
Figure is a formal example with a title. Figures often contain Graphics, or other large, display elements. Frequently they are given IDs and referenced from the text with XRef or Link.
Category: figures, examples, etc.-->
<!ELEMENT figure %ho; (blockinfo?, (%formalobject.title.content;),
                       (%figure.mix; | %link.char.class;)+)>
<!--end of figure.element-->]]>

<!-- Float: Whether the Figure is supposed to be rendered
		where convenient (yes (1) value) or at the place it occurs
		in the text (no (0) value, the default) -->


<!ENTITY % figure.attlist "INCLUDE">
<![%figure.attlist;[
<!ATTLIST figure
		float		%yesorno.attvals;	'0'
		floatstyle	CDATA			#IMPLIED
		pgwide      	%yesorno.attvals;       #IMPLIED
		%label.attrib;
		%common.attrib;
		%figure.role.attrib;
		%local.figure.attrib;
>
<!--end of figure.attlist-->]]>
<!--end of figure.module-->]]>

<!ENTITY % informalfigure.module "INCLUDE">
<![ %informalfigure.module; [
<!ENTITY % local.informalfigure.attrib "">
<!ENTITY % informalfigure.role.attrib "%role.attrib;">

<!ENTITY % informalfigure.element "INCLUDE">
<![ %informalfigure.element; [
<!--doc:A untitled figure.
An InformalFigure is a figure without a title. Figures often contain Graphics, or other large display elements.
Category: figures, examples, etc.-->
<!ELEMENT informalfigure %ho; (blockinfo?, (%figure.mix; | %link.char.class;)+)>
<!--end of informalfigure.element-->]]>

<!ENTITY % informalfigure.attlist "INCLUDE">
<![ %informalfigure.attlist; [
<!--
Float: Whether the Figure is supposed to be rendered
where convenient (yes (1) value) or at the place it occurs
in the text (no (0) value, the default)
-->
<!ATTLIST informalfigure
		float		%yesorno.attvals;	"0"
		floatstyle	CDATA			#IMPLIED
		pgwide      	%yesorno.attvals;       #IMPLIED
		%label.attrib;
		%common.attrib;
		%informalfigure.role.attrib;
		%local.informalfigure.attrib;
>
<!--end of informalfigure.attlist-->]]>
<!--end of informalfigure.module-->]]>

<!ENTITY % graphicco.module "INCLUDE">
<![%graphicco.module;[
<!ENTITY % local.graphicco.attrib "">
<!ENTITY % graphicco.role.attrib "%role.attrib;">

<!ENTITY % graphicco.element "INCLUDE">
<![%graphicco.element;[
<!--doc:A graphic that contains callout areas.
Callouts, such as numbered bullets, are an annotation mechanism. In an online system, these bullets are frequently hot, and clicking on them sends you to the corresponding annotation. A GraphicCO is a wrapper around an AreaSpec and a Graphic. An AreaSpec identifies the locations (coordinates) on the Graphic in which the callouts occur. The GraphicCO may also contain the list of annotations in a CalloutList, although the CalloutList may also occur outside of the wrapper, elsewhere in the document.
Category: Callouts-->
<!ELEMENT graphicco %ho; (areaspec, graphic, calloutlist*)>
<!--end of graphicco.element-->]]>

<!ENTITY % graphicco.attlist "INCLUDE">
<![%graphicco.attlist;[
<!ATTLIST graphicco
		%common.attrib;
		%graphicco.role.attrib;
		%local.graphicco.attrib;
>
<!--end of graphicco.attlist-->]]>
<!-- AreaSpec (defined above in Examples)-->
<!-- CalloutList (defined above in Lists)-->
<!--end of graphicco.module-->]]>

<!-- Graphical data can be the content of Graphic, or you can reference
     an external file either as an entity (Entitref) or a filename
     (Fileref). -->

<!ENTITY % graphic.module "INCLUDE">
<![%graphic.module;[
<!ENTITY % local.graphic.attrib "">
<!ENTITY % graphic.role.attrib "%role.attrib;">

<!ENTITY % graphic.element "INCLUDE">
<![%graphic.element;[
<!--doc:A displayed graphical object (not an inline).
This element contains graphical data, or a pointer to an external entity containing graphical data. One of the deficiencies of the DocBook Graphic element is that there is no way to specify an alternate text description of the graphic. This has been rectified by the introduction of MediaObject.
Category: Graphics-->
<!ELEMENT graphic %ho; EMPTY>
<!--end of graphic.element-->]]>

<!ENTITY % graphic.attlist "INCLUDE">
<![%graphic.attlist;[
<!ATTLIST graphic
		%graphics.attrib;
		%common.attrib;
		%graphic.role.attrib;
		%local.graphic.attrib;
>
<!--end of graphic.attlist-->]]>
<!--end of graphic.module-->]]>

<!ENTITY % inlinegraphic.module "INCLUDE">
<![%inlinegraphic.module;[
<!ENTITY % local.inlinegraphic.attrib "">
<!ENTITY % inlinegraphic.role.attrib "%role.attrib;">

<!ENTITY % inlinegraphic.element "INCLUDE">
<![%inlinegraphic.element;[
<!--doc:An object containing or pointing to graphical data that will be rendered inline.
This element contains graphical data, or a pointer to an external entity containing graphical data. One of the deficiencies of the DocBook Graphic element is that there is no way to specify an alternate text description of the graphic. This has been rectified by the introduction of InlineMediaObject.
Category: Graphics-->
<!ELEMENT inlinegraphic %ho; EMPTY>
<!--end of inlinegraphic.element-->]]>

<!ENTITY % inlinegraphic.attlist "INCLUDE">
<![%inlinegraphic.attlist;[
<!ATTLIST inlinegraphic
		%graphics.attrib;
		%common.attrib;
		%inlinegraphic.role.attrib;
		%local.inlinegraphic.attrib;
>
<!--end of inlinegraphic.attlist-->]]>
<!--end of inlinegraphic.module-->]]>

<!ENTITY % mediaobject.content.module "INCLUDE">
<![ %mediaobject.content.module; [

<!ENTITY % mediaobject.module "INCLUDE">
<![ %mediaobject.module; [
<!ENTITY % local.mediaobject.attrib "">
<!ENTITY % mediaobject.role.attrib "%role.attrib;">

<!ENTITY % mediaobject.element "INCLUDE">
<![ %mediaobject.element; [
<!--doc:A displayed media object (video, audio, image, etc.).
This element contains a set of alternative “media objects.” In DocBook V3.1, three types of external objects are defined: VideoObjects,AudioObjects, and ImageObjects. Additional textual descriptions may be provided with TextObjects.
Category: Graphics-->
<!ELEMENT mediaobject %ho; (objectinfo?,
                           (%mediaobject.mix;)+,
			   caption?)>
<!--end of mediaobject.element-->]]>

<!ENTITY % mediaobject.attlist "INCLUDE">
<![ %mediaobject.attlist; [
<!ATTLIST mediaobject
		%common.attrib;
		%mediaobject.role.attrib;
		%local.mediaobject.attrib;
>
<!--end of mediaobject.attlist-->]]>
<!--end of mediaobject.module-->]]>

<!ENTITY % inlinemediaobject.module "INCLUDE">
<![ %inlinemediaobject.module; [
<!ENTITY % local.inlinemediaobject.attrib "">
<!ENTITY % inlinemediaobject.role.attrib "%role.attrib;">

<!ENTITY % inlinemediaobject.element "INCLUDE">
<![ %inlinemediaobject.element; [
<!--doc:An inline media object (video, audio, image, and so on).
InlineMediaObject contains a set of alternative “graphical objects.” In DocBook V3.1, three types of external graphical objects are defined: VideoObjects,AudioObjects, and ImageObjects. Additional textual descriptions may be provided with TextObjects.
Category: Graphics-->
<!ELEMENT inlinemediaobject %ho; (objectinfo?,
                	         (%mediaobject.mix;)+)>
<!--end of inlinemediaobject.element-->]]>

<!ENTITY % inlinemediaobject.attlist "INCLUDE">
<![ %inlinemediaobject.attlist; [
<!ATTLIST inlinemediaobject
		%common.attrib;
		%inlinemediaobject.role.attrib;
		%local.inlinemediaobject.attrib;
>
<!--end of inlinemediaobject.attlist-->]]>
<!--end of inlinemediaobject.module-->]]>

<!ENTITY % videoobject.module "INCLUDE">
<![ %videoobject.module; [
<!ENTITY % local.videoobject.attrib "">
<!ENTITY % videoobject.role.attrib "%role.attrib;">

<!ENTITY % videoobject.element "INCLUDE">
<![ %videoobject.element; [
<!--doc:A wrapper for video data and its associated meta-information.
A VideoObject is a wrapper containing VideoData and its associated meta-information.
Category: Graphics-->
<!ELEMENT videoobject %ho; (objectinfo?, videodata)>
<!--end of videoobject.element-->]]>

<!ENTITY % videoobject.attlist "INCLUDE">
<![ %videoobject.attlist; [
<!ATTLIST videoobject
		%common.attrib;
		%videoobject.role.attrib;
		%local.videoobject.attrib;
>
<!--end of videoobject.attlist-->]]>
<!--end of videoobject.module-->]]>

<!ENTITY % audioobject.module "INCLUDE">
<![ %audioobject.module; [
<!ENTITY % local.audioobject.attrib "">
<!ENTITY % audioobject.role.attrib "%role.attrib;">

<!ENTITY % audioobject.element "INCLUDE">
<![ %audioobject.element; [
<!--doc:A wrapper for audio data and its associated meta-information.
AudioObject is a wrapper for AudioData.
Category: Graphics-->
<!ELEMENT audioobject %ho; (objectinfo?, audiodata)>
<!--end of audioobject.element-->]]>

<!ENTITY % audioobject.attlist "INCLUDE">
<![ %audioobject.attlist; [
<!ATTLIST audioobject
		%common.attrib;
		%audioobject.role.attrib;
		%local.audioobject.attrib;
>
<!--end of audioobject.attlist-->]]>
<!--end of audioobject.module-->]]>

<!ENTITY % imageobject.module "INCLUDE">
<![ %imageobject.module; [
<!ENTITY % local.imageobject.attrib "">
<!ENTITY % imageobject.role.attrib "%role.attrib;">

<!ENTITY % imageobject.element "INCLUDE">
<![ %imageobject.element; [
<!--doc:A wrapper for image data and its associated meta-information.
An ImageObject is a wrapper containing ImageData and its associated meta-information.If the SVG Module is used, ImageObject can also contain the svg:svg element.
Category: Graphics-->
<!ELEMENT imageobject %ho; (objectinfo?, imagedata)>
<!--end of imageobject.element-->]]>

<!ENTITY % imageobject.attlist "INCLUDE">
<![ %imageobject.attlist; [
<!ATTLIST imageobject
		%common.attrib;
		%imageobject.role.attrib;
		%local.imageobject.attrib;
>
<!--end of imageobject.attlist-->]]>
<!--end of imageobject.module-->]]>

<!ENTITY % textobject.module "INCLUDE">
<![ %textobject.module; [
<!ENTITY % local.textobject.attrib "">
<!ENTITY % textobject.role.attrib "%role.attrib;">

<!ENTITY % textobject.element "INCLUDE">
<![ %textobject.element; [
<!--doc:A wrapper for a text description of an object and its associated meta-information.
A TextObject is a wrapper containing a textual description of a media object, and its associated meta-information.TextObjects are only allowed in MediaObjects as a fall-back option, they cannot be the primary content. There are two different forms of TextObject, and it is not unreasonable for a media object to contain both of them. In the first form, the content of a TextObject is simply a Phrase. This form is a mechanism for providing a simple alt text for a media object. The phrase might be used, for example, as the value of the ALT attribute on an HTML IMG, with the primary content of the image coming from one of the other objects in the media object. In the second form, the content of TextObject is a longer, prose description. This form could be used when rendering to devices that are incapable of displaying any of the other alternatives.
Category: Graphics-->
<!ELEMENT textobject %ho; (objectinfo?, (phrase|textdata|(%textobject.mix;)+))>
<!--end of textobject.element-->]]>

<!ENTITY % textobject.attlist "INCLUDE">
<![ %textobject.attlist; [
<!ATTLIST textobject
		%common.attrib;
		%textobject.role.attrib;
		%local.textobject.attrib;
>
<!--end of textobject.attlist-->]]>
<!--end of textobject.module-->]]>

<!ENTITY % objectinfo.module "INCLUDE">
<![ %objectinfo.module; [
<!ENTITY % local.objectinfo.attrib "">
<!ENTITY % objectinfo.role.attrib "%role.attrib;">

<!ENTITY % objectinfo.element "INCLUDE">
<![ %objectinfo.element; [
<!--doc:Meta-information for an object.
The ObjectInfo element is a wrapper for the meta-information about a video, audio, image, or text object.
Category: Meta-wrappers-->
<!ELEMENT objectinfo %ho; ((%info.class;)+)
	%beginpage.exclusion;>
<!--end of objectinfo.element-->]]>

<!ENTITY % objectinfo.attlist "INCLUDE">
<![ %objectinfo.attlist; [
<!ATTLIST objectinfo
		%common.attrib;
		%objectinfo.role.attrib;
		%local.objectinfo.attrib;
>
<!--end of objectinfo.attlist-->]]>
<!--end of objectinfo.module-->]]>

<!--EntityRef: Name of an external entity containing the content
	of the object data-->
<!--FileRef: Filename, qualified by a pathname if desired,
	designating the file containing the content of the object data-->
<!--Format: Notation of the element content, if any-->
<!--SrcCredit: Information about the source of the image-->
<!ENTITY % local.objectdata.attrib "">
<!ENTITY % objectdata.attrib
	"
	entityref	ENTITY		#IMPLIED
	fileref 	CDATA		#IMPLIED
	format		(%notation.class;)
					#IMPLIED
	srccredit	CDATA		#IMPLIED
	%local.objectdata.attrib;"
>

<!ENTITY % videodata.module "INCLUDE">
<![ %videodata.module; [
<!ENTITY % local.videodata.attrib "">
<!ENTITY % videodata.role.attrib "%role.attrib;">

<!ENTITY % videodata.element "INCLUDE">
<![ %videodata.element; [
<!--doc:Pointer to external video data.
This element points to an external entity containing video data.-->
<!ELEMENT videodata %ho; EMPTY>
<!--end of videodata.element-->]]>

<!ENTITY % videodata.attlist "INCLUDE">
<![ %videodata.attlist; [

<!--Width: Same as CALS reprowid (desired width)-->
<!--Depth: Same as CALS reprodep (desired depth)-->
<!--Align: Same as CALS hplace with 'none' removed; #IMPLIED means
	application-specific-->
<!--Scale: Conflation of CALS hscale and vscale-->
<!--Scalefit: Same as CALS scalefit-->
<!ATTLIST videodata
	width		CDATA		#IMPLIED
	contentwidth	CDATA		#IMPLIED
	depth		CDATA		#IMPLIED
	contentdepth	CDATA		#IMPLIED
	align		(left
			|right
			|center)	#IMPLIED
	valign		(top
			|middle
			|bottom)	#IMPLIED
	scale		CDATA		#IMPLIED
	scalefit	%yesorno.attvals;
					#IMPLIED
		%objectdata.attrib;
		%common.attrib;
		%videodata.role.attrib;
		%local.videodata.attrib;
>
<!--end of videodata.attlist-->]]>
<!--end of videodata.module-->]]>

<!ENTITY % audiodata.module "INCLUDE">
<![ %audiodata.module; [
<!ENTITY % local.audiodata.attrib "">
<!ENTITY % audiodata.role.attrib "%role.attrib;">

<!ENTITY % audiodata.element "INCLUDE">
<![ %audiodata.element; [
<!--doc:Pointer to external audio data.
This empty element points to external audio data.-->
<!ELEMENT audiodata %ho; EMPTY>
<!--end of audiodata.element-->]]>

<!ENTITY % audiodata.attlist "INCLUDE">
<![ %audiodata.attlist; [
<!ATTLIST audiodata
		%objectdata.attrib;
		%common.attrib;
		%audiodata.role.attrib;
		%local.audiodata.attrib;
>
<!--end of audiodata.attlist-->]]>
<!--end of audiodata.module-->]]>

<!ENTITY % imagedata.module "INCLUDE">
<![ %imagedata.module; [
<!ENTITY % local.imagedata.attrib "">
<!ENTITY % imagedata.role.attrib "%role.attrib;">

<!ENTITY % imagedata.element "INCLUDE">
<![ %imagedata.element; [
<!--doc:Pointer to external image data.
This element points to an external entity containing graphical image data.-->
<!ELEMENT imagedata %ho; EMPTY>
<!--end of imagedata.element-->]]>

<!ENTITY % imagedata.attlist "INCLUDE">
<![ %imagedata.attlist; [

<!--Width: Same as CALS reprowid (desired width)-->
<!--Depth: Same as CALS reprodep (desired depth)-->
<!--Align: Same as CALS hplace with 'none' removed; #IMPLIED means
	application-specific-->
<!--Scale: Conflation of CALS hscale and vscale-->
<!--Scalefit: Same as CALS scalefit-->
<!ATTLIST imagedata
	width		CDATA		#IMPLIED
	contentwidth	CDATA		#IMPLIED
	depth		CDATA		#IMPLIED
	contentdepth	CDATA		#IMPLIED
	align		(left
			|right
			|center)	#IMPLIED
	valign		(top
			|middle
			|bottom)	#IMPLIED
	scale		CDATA		#IMPLIED
	scalefit	%yesorno.attvals;
					#IMPLIED
		%objectdata.attrib;
		%common.attrib;
		%imagedata.role.attrib;
		%local.imagedata.attrib;
>
<!--end of imagedata.attlist-->]]>
<!--end of imagedata.module-->]]>

<!ENTITY % textdata.module "INCLUDE">
<![ %textdata.module; [
<!ENTITY % local.textdata.attrib "">
<!ENTITY % textdata.role.attrib "%role.attrib;">

<!ENTITY % textdata.element "INCLUDE">
<![ %textdata.element; [
<!--doc:Pointer to external text data.
This element points to an external entity containing text to be inserted.-->
<!ELEMENT textdata %ho; EMPTY>
<!--end of textdata.element-->]]>

<!ENTITY % textdata.attlist "INCLUDE">
<![ %textdata.attlist; [
<!ATTLIST textdata
		encoding	CDATA	#IMPLIED
		%objectdata.attrib;
		%common.attrib;
		%textdata.role.attrib;
		%local.textdata.attrib;
>
<!--end of textdata.attlist-->]]>
<!--end of textdata.module-->]]>

<!ENTITY % mediaobjectco.module "INCLUDE">
<![ %mediaobjectco.module; [
<!ENTITY % local.mediaobjectco.attrib "">
<!ENTITY % mediaobjectco.role.attrib "%role.attrib;">

<!ENTITY % mediaobjectco.element "INCLUDE">
<![ %mediaobjectco.element; [
<!--doc:A media object that contains callouts.
A MediaObjectCO is a wrapper around a set of alternative, annotated media objects.
Category: Callouts-->
<!ELEMENT mediaobjectco %ho; (objectinfo?, imageobjectco,
			   (imageobjectco|textobject)*)>
<!--end of mediaobjectco.element-->]]>

<!ENTITY % mediaobjectco.attlist "INCLUDE">
<![ %mediaobjectco.attlist; [
<!ATTLIST mediaobjectco
		%common.attrib;
		%mediaobjectco.role.attrib;
		%local.mediaobjectco.attrib;
>
<!--end of mediaobjectco.attlist-->]]>
<!--end of mediaobjectco.module-->]]>

<!ENTITY % imageobjectco.module "INCLUDE">
<![ %imageobjectco.module; [
<!ENTITY % local.imageobjectco.attrib "">
<!ENTITY % imageobjectco.role.attrib "%role.attrib;">

<!ENTITY % imageobjectco.element "INCLUDE">
<![ %imageobjectco.element; [
<!--doc:A wrapper for an image object with callouts.
Callouts, such as numbered bullets, are an annotation mechanism. In an online system, these bullets are frequently hot, and clicking on them navigates to the corresponding annotation. A ImageObjectCO is a wrapper around an AreaSpec and an ImageObject. An AreaSpec identifies the locations (coordinates) on the image where the Callouts occur. The ImageObjectCO may also contain the list of annotations in a CalloutList, although the CalloutList may also occur outside of the wrapper, elsewhere in the document.
Category: Callouts-->
<!ELEMENT imageobjectco %ho; (areaspec, imageobject, calloutlist*)>
<!--end of imageobjectco.element-->]]>

<!ENTITY % imageobjectco.attlist "INCLUDE">
<![ %imageobjectco.attlist; [
<!ATTLIST imageobjectco
		%common.attrib;
		%imageobjectco.role.attrib;
		%local.imageobjectco.attrib;
>
<!--end of imageobjectco.attlist-->]]>
<!--end of imageobjectco.module-->]]>
<!--end of mediaobject.content.module-->]]>

<!-- Equations ........................ -->

<!-- This PE provides a mechanism for replacing equation content, -->
<!-- perhaps adding a new or different model (e.g., MathML) -->
<!ENTITY % equation.content "(alt?, (graphic+|mediaobject+))">
<!ENTITY % inlineequation.content "(alt?, (graphic+|inlinemediaobject+))">

<!ENTITY % equation.module "INCLUDE">
<![%equation.module;[
<!ENTITY % local.equation.attrib "">
<!ENTITY % equation.role.attrib "%role.attrib;">

<!ENTITY % equation.element "INCLUDE">
<![%equation.element;[
<!--doc:A displayed mathematical equation.
An Equation is a formal mathematical equation (with an optional rather than required title).If the MathML Module is used, Equation can also contain the mml:math element.
Category: Mathematics-->
<!ELEMENT equation %ho; (blockinfo?, (%formalobject.title.content;)?,
                         (informalequation | %equation.content;))>
<!--end of equation.element-->]]>

<!ENTITY % equation.attlist "INCLUDE">
<![%equation.attlist;[
<!ATTLIST equation
		floatstyle	CDATA			#IMPLIED
		%label.attrib;
	 	%common.attrib;
		%equation.role.attrib;
		%local.equation.attrib;
>
<!--end of equation.attlist-->]]>
<!--end of equation.module-->]]>

<!ENTITY % informalequation.module "INCLUDE">
<![%informalequation.module;[
<!ENTITY % local.informalequation.attrib "">
<!ENTITY % informalequation.role.attrib "%role.attrib;">

<!ENTITY % informalequation.element "INCLUDE">
<![%informalequation.element;[
<!--doc:A displayed mathematical equation without a title.
An InformalEquation is usually a mathematical equation or a group of related mathematical equations.
Category: Mathematics-->
<!ELEMENT informalequation %ho; (blockinfo?, %equation.content;) >
<!--end of informalequation.element-->]]>

<!ENTITY % informalequation.attlist "INCLUDE">
<![%informalequation.attlist;[
<!ATTLIST informalequation
		floatstyle	CDATA			#IMPLIED
		%common.attrib;
		%informalequation.role.attrib;
		%local.informalequation.attrib;
>
<!--end of informalequation.attlist-->]]>
<!--end of informalequation.module-->]]>

<!ENTITY % inlineequation.module "INCLUDE">
<![%inlineequation.module;[
<!ENTITY % local.inlineequation.attrib "">
<!ENTITY % inlineequation.role.attrib "%role.attrib;">

<!ENTITY % inlineequation.element "INCLUDE">
<![%inlineequation.element;[
<!--doc:A mathematical equation or expression occurring inline.
InlineEquations are expressions (usually mathematical) that occur in the text flow.
Category: Mathematics-->
<!ELEMENT inlineequation %ho; (%inlineequation.content;)>
<!--end of inlineequation.element-->]]>

<!ENTITY % inlineequation.attlist "INCLUDE">
<![%inlineequation.attlist;[
<!ATTLIST inlineequation
		%common.attrib;
		%inlineequation.role.attrib;
		%local.inlineequation.attrib;
>
<!--end of inlineequation.attlist-->]]>
<!--end of inlineequation.module-->]]>

<!ENTITY % alt.module "INCLUDE">
<![%alt.module;[
<!ENTITY % local.alt.attrib "">
<!ENTITY % alt.role.attrib "%role.attrib;">

<!ENTITY % alt.element "INCLUDE">
<![%alt.element;[
<!--doc:Text representation for a graphical element.
A text (or other nonvisual) description of a graphical element. This is intended to be an alternative to the graphical presentation.
Category: Graphics-->
<!ELEMENT alt %ho; (#PCDATA)>
<!--end of alt.element-->]]>

<!ENTITY % alt.attlist "INCLUDE">
<![%alt.attlist;[
<!ATTLIST alt
		%common.attrib;
		%alt.role.attrib;
		%local.alt.attrib;
>
<!--end of alt.attlist-->]]>
<!--end of alt.module-->]]>

<!-- Tables ........................... -->

<!ENTITY % table.module "INCLUDE">
<![%table.module;[

<!-- Choose a table model. CALS or OASIS XML Exchange -->

<!ENTITY % cals.table.module "INCLUDE">
<![%cals.table.module;[
<!ENTITY % exchange.table.module "IGNORE">
]]>
<!ENTITY % exchange.table.module "INCLUDE">

<!-- Do we allow the HTML table model as well? -->
<!ENTITY % allow.html.tables "INCLUDE">
<![%allow.html.tables;[
  <!-- ====================================================== -->
  <!--  xhtmltbl.mod defines HTML tables and sets parameter
        entities so that, when the CALS table module is read,
        we end up allowing any table to be CALS or HTML.
        i.e. This include must come first!                    -->
  <!-- ====================================================== -->

<!ENTITY % htmltbl
  PUBLIC "-//OASIS//ELEMENTS DocBook XML HTML Tables V4.3//EN"
  "htmltblx.mod">
%htmltbl;
<!--end of allow.html.tables-->]]>

<!ENTITY % tables.role.attrib "%role.attrib;">

<![%cals.table.module;[
<!-- Add label and role attributes to table and informaltable -->
<!ENTITY % bodyatt "
		floatstyle	CDATA			#IMPLIED
                %label.attrib;"
>

<!-- Add common attributes to Table, TGroup, TBody, THead, TFoot, Row,
     EntryTbl, and Entry (and InformalTable element). -->
<!ENTITY % secur
	"%common.attrib;
	%tables.role.attrib;">

<!ENTITY % common.table.attribs
	"%bodyatt;
	%secur;">

<!-- Content model for Table. -->
<!ENTITY % tbl.table.mdl
	"(blockinfo?, (%formalobject.title.content;), (%ndxterm.class;)*,
	  textobject*,
          (graphic+|mediaobject+|tgroup+))">

<!-- Allow either objects or inlines; beware of REs between elements. -->
<!ENTITY % tbl.entry.mdl "%para.char.mix; | %tabentry.mix;">

<!-- Reference CALS Table Model -->
<!ENTITY % tablemodel
  PUBLIC "-//OASIS//DTD DocBook CALS Table Model V4.3//EN"
  "calstblx.dtd">
]]>

<![%exchange.table.module;[
<!-- Add common attributes and the Label attribute to Table and -->
<!-- InformalTable.                                             -->
<!ENTITY % bodyatt
	"%common.attrib;
	%label.attrib;
	%tables.role.attrib;">

<!ENTITY % common.table.attribs
	"%bodyatt;">

<!-- Add common attributes to TGroup, ColSpec, TBody, THead, Row, Entry -->

<!ENTITY % tbl.tgroup.att       "%common.attrib;">
<!ENTITY % tbl.colspec.att      "%common.attrib;">
<!ENTITY % tbl.tbody.att        "%common.attrib;">
<!ENTITY % tbl.thead.att        "%common.attrib;">
<!ENTITY % tbl.row.att          "%common.attrib;">
<!ENTITY % tbl.entry.att        "%common.attrib;">

<!-- Content model for Table. -->
<!ENTITY % tbl.table.mdl
	"(blockinfo?, (%formalobject.title.content;), (%ndxterm.class;)*,
	  textobject*,
          (graphic+|mediaobject+|tgroup+))">

<!-- Allow either objects or inlines; beware of REs between elements. -->
<!ENTITY % tbl.entry.mdl "(%para.char.mix; | %tabentry.mix;)*">

<!-- Reference OASIS Exchange Table Model -->
<!ENTITY % tablemodel
  PUBLIC "-//OASIS//DTD XML Exchange Table Model 19990315//EN"
  "soextblx.dtd">
]]>

%tablemodel;

<!--end of table.module-->]]>

<!ENTITY % informaltable.module "INCLUDE">
<![%informaltable.module;[

<!-- Note that InformalTable is dependent on some of the entity
     declarations that customize Table. -->

<!ENTITY % local.informaltable.attrib "">

<!-- the following entity may have been declared by the XHTML table module -->
<!ENTITY % informal.tbl.table.mdl "textobject*, (graphic+|mediaobject+|tgroup+)">

<!ENTITY % informaltable.element "INCLUDE">
<![%informaltable.element;[
<!--doc:A table without a title.
An InformalTable element identifies an informal table (one without a Title). DocBook uses the CALS table model, which describes tables geometrically using rows, columns, and cells. Tables may include column headers and footers, but there is no provision for row headers.
Category: Tables-->
<!ELEMENT informaltable %ho; (blockinfo?, (%informal.tbl.table.mdl;))>
<!--end of informaltable.element-->]]>

<!-- Frame, Colsep, and Rowsep must be repeated because
		they are not in entities in the table module. -->
<!-- includes TabStyle, ToCentry, ShortEntry,
				Orient, PgWide -->
<!-- includes Label -->
<!-- includes common attributes -->

<!ENTITY % informaltable.attlist "INCLUDE">
<![%informaltable.attlist;[
<!ATTLIST informaltable
		frame		(%tbl.frame.attval;)	#IMPLIED
		colsep		%yesorno.attvals;	#IMPLIED
		rowsep		%yesorno.attvals;	#IMPLIED
		%common.table.attribs;
		%tbl.table.att;
		%local.informaltable.attrib;
>
<!--end of informaltable.attlist-->]]>
<!--end of informaltable.module-->]]>

<!ENTITY % caption.module "INCLUDE">
<![ %caption.module; [
<!ENTITY % local.caption.attrib "">
<!ENTITY % caption.role.attrib "%role.attrib;">

<!ENTITY % caption.element "INCLUDE">
<![ %caption.element; [
<!--doc:A Caption is an extended description of a MediaObject. Unlike a TextObject, which is an alternative to the other elements in the MediaObject, the Caption augments the object.
Category: Graphics-->
<!ELEMENT caption %ho; (#PCDATA | %textobject.mix;)*>
<!--end of caption.element-->]]>

<!ENTITY % caption.attlist "INCLUDE">
<![ %caption.attlist; [
<!-- attrs comes from HTML tables ... -->

<![ %allow.html.tables; [
<!-- common.attrib, but without ID because ID is in attrs -->
<!ENTITY % caption.attlist.content "
	%lang.attrib;
	%remap.attrib;
	%xreflabel.attrib;
	%revisionflag.attrib;
	%effectivity.attrib;
	%dir.attrib;
	%xml-base.attrib;
	%local.common.attrib;
		%caption.role.attrib;
		%attrs;
		align	(top|bottom|left|right)	#IMPLIED
		%local.caption.attrib;
">
]]>
<!ENTITY % caption.attlist.content "
		%common.attrib;
		%caption.role.attrib;
		%local.caption.attrib;
">

<!ATTLIST caption %caption.attlist.content;>

<!--end of caption.attlist-->]]>
<!--end of caption.module-->]]>

<!-- ...................................................................... -->
<!-- Synopses ............................................................. -->

<!-- Synopsis ......................... -->

<!ENTITY % synopsis.module "INCLUDE">
<![%synopsis.module;[
<!ENTITY % local.synopsis.attrib "">
<!ENTITY % synopsis.role.attrib "%role.attrib;">

<!ENTITY % synopsis.element "INCLUDE">
<![%synopsis.element;[
<!--doc:A general-purpose element for representing the syntax of commands or functions.
A Synopsis is a verbatim environment for displaying command, function, and other syntax summaries. Unlike CmdSynopsis and FuncSynopsis which have a complex interior structure,Synopsis is simply a verbatim environment.
Category: synopsis-->
<!ELEMENT synopsis %ho; (%para.char.mix;|graphic|mediaobject|co|coref|textobject|lineannotation)*>
<!--end of synopsis.element-->]]>

<!ENTITY % synopsis.attlist "INCLUDE">
<![%synopsis.attlist;[
<!ATTLIST synopsis
		%label.attrib;
		%linespecific.attrib;
		%common.attrib;
		%synopsis.role.attrib;
		%local.synopsis.attrib;
>
<!--end of synopsis.attlist-->]]>

<!-- LineAnnotation (defined in the Inlines section, below)-->
<!--end of synopsis.module-->]]>

<!-- CmdSynopsis ...................... -->

<!ENTITY % cmdsynopsis.content.module "INCLUDE">
<![%cmdsynopsis.content.module;[
<!ENTITY % cmdsynopsis.module "INCLUDE">
<![%cmdsynopsis.module;[
<!ENTITY % local.cmdsynopsis.attrib "">
<!ENTITY % cmdsynopsis.role.attrib "%role.attrib;">

<!ENTITY % cmdsynopsis.element "INCLUDE">
<![%cmdsynopsis.element;[
<!--doc:A syntax summary for a software command.
A CmdSynopsis summarizes the options and parameters of a command started from a text prompt. This is usually a program started from the DOS, Windows, or UNIX shell prompt. CmdSynopsis operates under the following general model: commands have arguments, that may be grouped; arguments and groups may be required or optional and may be repeated.
Category: cmdsynopsis-->
<!ELEMENT cmdsynopsis %ho; ((command | arg | group | sbr)+, synopfragment*)>
<!--end of cmdsynopsis.element-->]]>

<!-- Sepchar: Character that should separate command and all
		top-level arguments; alternate value might be e.g., &Delta; -->


<!ENTITY % cmdsynopsis.attlist "INCLUDE">
<![%cmdsynopsis.attlist;[
<!ATTLIST cmdsynopsis
		%label.attrib;
		sepchar		CDATA		" "
		cmdlength	CDATA		#IMPLIED
		%common.attrib;
		%cmdsynopsis.role.attrib;
		%local.cmdsynopsis.attrib;
>
<!--end of cmdsynopsis.attlist-->]]>
<!--end of cmdsynopsis.module-->]]>

<!ENTITY % arg.module "INCLUDE">
<![%arg.module;[
<!ENTITY % local.arg.attrib "">
<!ENTITY % arg.role.attrib "%role.attrib;">

<!ENTITY % arg.element "INCLUDE">
<![%arg.element;[
<!--doc:An argument in a CmdSynopsis.
See CmdSynopsis for more information.
Category: cmdsynopsis-->
<!ELEMENT arg %ho; (#PCDATA
		| arg
		| group
		| option
		| synopfragmentref
		| replaceable
		| sbr)*>
<!--end of arg.element-->]]>

<!-- Choice: Whether Arg must be supplied: Opt (optional to
		supply, e.g. [arg]; the default), Req (required to supply,
		e.g. {arg}), or Plain (required to supply, e.g. arg) -->
<!-- Rep: whether Arg is repeatable: Norepeat (e.g. arg without
		ellipsis; the default), or Repeat (e.g. arg...) -->


<!ENTITY % arg.attlist "INCLUDE">
<![%arg.attlist;[
<!ATTLIST arg
		choice		(opt
				|req
				|plain)		'opt'
		rep		(norepeat
				|repeat)	'norepeat'
		%common.attrib;
		%arg.role.attrib;
		%local.arg.attrib;
>
<!--end of arg.attlist-->]]>
<!--end of arg.module-->]]>

<!ENTITY % group.module "INCLUDE">
<![%group.module;[

<!ENTITY % local.group.attrib "">
<!ENTITY % group.role.attrib "%role.attrib;">

<!ENTITY % group.element "INCLUDE">
<![%group.element;[
<!--doc:A group of elements in a CmdSynopsis.
A Group surrounds several related items. Usually, they are grouped because they are mutually exclusive. The user is expected to select one of the items.
Category: cmdsynopsis-->
<!ELEMENT group %ho; ((arg | group | option | synopfragmentref
		| replaceable | sbr)+)>
<!--end of group.element-->]]>

<!-- Choice: Whether Group must be supplied: Opt (optional to
		supply, e.g.  [g1|g2|g3]; the default), Req (required to
		supply, e.g.  {g1|g2|g3}), Plain (required to supply,
		e.g.  g1|g2|g3), OptMult (can supply zero or more, e.g.
		[[g1|g2|g3]]), or ReqMult (must supply one or more, e.g.
		{{g1|g2|g3}}) -->
<!-- Rep: whether Group is repeatable: Norepeat (e.g. group
		without ellipsis; the default), or Repeat (e.g. group...) -->


<!ENTITY % group.attlist "INCLUDE">
<![%group.attlist;[
<!ATTLIST group
		choice		(opt
				|req
				|plain)         'opt'
		rep		(norepeat
				|repeat)	'norepeat'
		%common.attrib;
		%group.role.attrib;
		%local.group.attrib;
>
<!--end of group.attlist-->]]>
<!--end of group.module-->]]>

<!ENTITY % sbr.module "INCLUDE">
<![%sbr.module;[
<!ENTITY % local.sbr.attrib "">
<!-- Synopsis break -->
<!ENTITY % sbr.role.attrib "%role.attrib;">

<!ENTITY % sbr.element "INCLUDE">
<![%sbr.element;[
<!--doc:An explicit line break in a command synopsis.
For the most part, DocBook attempts to describe document structure rather than presentation. However, in some complex environments, it is possible to demonstrate that there is no reasonable set of processing expectations that can guarantee correct formatting. CmdSynopsis is one of those environments. Within a long synopsis, it may be necessary to specify the location of a line break explicitly. The SBR element indicates the position of such a line break in a CmdSynopsis. It is purely presentational.
Category: cmdsynopsis-->
<!ELEMENT sbr %ho; EMPTY>
<!--end of sbr.element-->]]>

<!ENTITY % sbr.attlist "INCLUDE">
<![%sbr.attlist;[
<!ATTLIST sbr
		%common.attrib;
		%sbr.role.attrib;
		%local.sbr.attrib;
>
<!--end of sbr.attlist-->]]>
<!--end of sbr.module-->]]>

<!ENTITY % synopfragmentref.module "INCLUDE">
<![%synopfragmentref.module;[
<!ENTITY % local.synopfragmentref.attrib "">
<!ENTITY % synopfragmentref.role.attrib "%role.attrib;">

<!ENTITY % synopfragmentref.element "INCLUDE">
<![%synopfragmentref.element;[
<!--doc:A reference to a fragment of a command synopsis.
A complex CmdSynopsis can be made more manageable with SynopFragments. Rather than attempting to present the entire synopsis in one large piece, parts of the synopsis can be extracted out and presented elsewhere. At the point where each piece was extracted, insert a SynopFragmentRef that points to the fragment. The content of the SynopFragmentRef will be presented inline. The extracted pieces are placed in SynopFragments at the end of the CmdSynopsis.
Category: cmdsynopsis-->
<!ELEMENT synopfragmentref %ho; (#PCDATA)>
<!--end of synopfragmentref.element-->]]>

<!-- to SynopFragment of complex synopsis
			material for separate referencing -->


<!ENTITY % synopfragmentref.attlist "INCLUDE">
<![%synopfragmentref.attlist;[
<!ATTLIST synopfragmentref
		%linkendreq.attrib;		%common.attrib;
		%synopfragmentref.role.attrib;
		%local.synopfragmentref.attrib;
>
<!--end of synopfragmentref.attlist-->]]>
<!--end of synopfragmentref.module-->]]>

<!ENTITY % synopfragment.module "INCLUDE">
<![%synopfragment.module;[
<!ENTITY % local.synopfragment.attrib "">
<!ENTITY % synopfragment.role.attrib "%role.attrib;">

<!ENTITY % synopfragment.element "INCLUDE">
<![%synopfragment.element;[
<!--doc:A portion of a CmdSynopsis broken out from the main body of the synopsis.
A complex CmdSynopsis can be made more manageable with SynopFragments. Rather than attempting to present the entire synopsis in one large piece, parts of the synopsis can be extracted out and presented elsewhere. These extracted pieces are placed in SynopFragments at the end of the CmdSynopsis. At the point in which each piece was extracted, insert a SynopFragmentRef that points to the fragment. The content of the reference element will be presented inline.
Category: cmdsynopsis-->
<!ELEMENT synopfragment %ho; ((arg | group)+)>
<!--end of synopfragment.element-->]]>

<!ENTITY % synopfragment.attlist "INCLUDE">
<![%synopfragment.attlist;[
<!ATTLIST synopfragment
		%idreq.common.attrib;
		%synopfragment.role.attrib;
		%local.synopfragment.attrib;
>
<!--end of synopfragment.attlist-->]]>
<!--end of synopfragment.module-->]]>

<!-- Command (defined in the Inlines section, below)-->
<!-- Option (defined in the Inlines section, below)-->
<!-- Replaceable (defined in the Inlines section, below)-->
<!--end of cmdsynopsis.content.module-->]]>

<!-- FuncSynopsis ..................... -->

<!ENTITY % funcsynopsis.content.module "INCLUDE">
<![%funcsynopsis.content.module;[
<!ENTITY % funcsynopsis.module "INCLUDE">
<![%funcsynopsis.module;[

<!ENTITY % local.funcsynopsis.attrib "">
<!ENTITY % funcsynopsis.role.attrib "%role.attrib;">

<!ENTITY % funcsynopsis.element "INCLUDE">
<![%funcsynopsis.element;[
<!--doc:The syntax summary for a function definition.
A FuncSynopsis contains the syntax summary of a function prototype or a set of function prototypes. The content model of this element was designed specifically to capture the semantics of most C-language function prototypes (for use in UNIX reference pages). This is one of the few places where DocBook attempts to model as well as describe. Using FuncSynopsis for languages that are unrelated to C may prove difficult.
Category: synopsis-->
<!ELEMENT funcsynopsis %ho; ((funcsynopsisinfo | funcprototype)+)>
<!--end of funcsynopsis.element-->]]>

<!ENTITY % funcsynopsis.attlist "INCLUDE">
<![%funcsynopsis.attlist;[
<!ATTLIST funcsynopsis
		%label.attrib;
		%common.attrib;
		%funcsynopsis.role.attrib;
		%local.funcsynopsis.attrib;
>
<!--end of funcsynopsis.attlist-->]]>
<!--end of funcsynopsis.module-->]]>

<!ENTITY % funcsynopsisinfo.module "INCLUDE">
<![%funcsynopsisinfo.module;[
<!ENTITY % local.funcsynopsisinfo.attrib "">
<!ENTITY % funcsynopsisinfo.role.attrib "%role.attrib;">

<!ENTITY % funcsynopsisinfo.element "INCLUDE">
<![%funcsynopsisinfo.element;[
<!--doc:Information supplementing the FuncDefs of a FuncSynopsis.
Supplementary information in a FuncSynopsis. See FuncSynopsis. Unlike the other info elements, FuncSynopsisInfo is not a container for meta-information. Instead FuncSynopsisInfo is a verbatim environment for adding additional information to a function synopsis.
Category: funcsynopsis-->
<!ELEMENT funcsynopsisinfo %ho; (%cptr.char.mix;|textobject|lineannotation)*>
<!--end of funcsynopsisinfo.element-->]]>

<!ENTITY % funcsynopsisinfo.attlist "INCLUDE">
<![%funcsynopsisinfo.attlist;[
<!ATTLIST funcsynopsisinfo
		%linespecific.attrib;
		%common.attrib;
		%funcsynopsisinfo.role.attrib;
		%local.funcsynopsisinfo.attrib;
>
<!--end of funcsynopsisinfo.attlist-->]]>
<!--end of funcsynopsisinfo.module-->]]>

<!ENTITY % funcprototype.module "INCLUDE">
<![%funcprototype.module;[
<!ENTITY % local.funcprototype.attrib "">
<!ENTITY % funcprototype.role.attrib "%role.attrib;">

<!ENTITY % funcprototype.element "INCLUDE">
<![%funcprototype.element;[
<!--doc:The prototype of a function.
A wrapper for a function prototype in a FuncSynopsis.
Category: funcsynopsis-->
<!ELEMENT funcprototype %ho; (modifier*,
                              funcdef,
                              (void|varargs|(paramdef+, varargs?)),
                              modifier*)>

<!--end of funcprototype.element-->]]>

<!ENTITY % funcprototype.attlist "INCLUDE">
<![%funcprototype.attlist;[
<!ATTLIST funcprototype
		%common.attrib;
		%funcprototype.role.attrib;
		%local.funcprototype.attrib;
>
<!--end of funcprototype.attlist-->]]>
<!--end of funcprototype.module-->]]>

<!ENTITY % funcdef.module "INCLUDE">
<![%funcdef.module;[
<!ENTITY % local.funcdef.attrib "">
<!ENTITY % funcdef.role.attrib "%role.attrib;">

<!ENTITY % funcdef.element "INCLUDE">
<![%funcdef.element;[
<!--doc:A function (subroutine) name and its return type.
A FuncDef contains the name of a programming language function, and its return type. Within the FuncDef, the function name is identified with Function, and the rest of the content is assumed to be the return type. In the following definition, max is the name of the function and int is the return type: <funcdef>int <function>max</function></funcdef>
Category: funcsynopsis-->
<!ELEMENT funcdef %ho; (#PCDATA
		| type
		| replaceable
		| function)*>
<!--end of funcdef.element-->]]>

<!ENTITY % funcdef.attlist "INCLUDE">
<![%funcdef.attlist;[
<!ATTLIST funcdef
		%common.attrib;
		%funcdef.role.attrib;
		%local.funcdef.attrib;
>
<!--end of funcdef.attlist-->]]>
<!--end of funcdef.module-->]]>

<!ENTITY % void.module "INCLUDE">
<![%void.module;[
<!ENTITY % local.void.attrib "">
<!ENTITY % void.role.attrib "%role.attrib;">

<!ENTITY % void.element "INCLUDE">
<![%void.element;[
<!--doc:An empty element in a function synopsis indicating that the function in question takes no arguments.
The Void element indicates explicitly that a Function has no arguments.
Category: funcsynopsis-->
<!ELEMENT void %ho; EMPTY>
<!--end of void.element-->]]>

<!ENTITY % void.attlist "INCLUDE">
<![%void.attlist;[
<!ATTLIST void
		%common.attrib;
		%void.role.attrib;
		%local.void.attrib;
>
<!--end of void.attlist-->]]>
<!--end of void.module-->]]>

<!ENTITY % varargs.module "INCLUDE">
<![%varargs.module;[
<!ENTITY % local.varargs.attrib "">
<!ENTITY % varargs.role.attrib "%role.attrib;">

<!ENTITY % varargs.element "INCLUDE">
<![%varargs.element;[
<!--doc:An empty element in a function synopsis indicating a variable number of arguments.
VarArgs indicates that a function takes a variable number of arguments.
Category: funcsynopsis-->
<!ELEMENT varargs %ho; EMPTY>
<!--end of varargs.element-->]]>

<!ENTITY % varargs.attlist "INCLUDE">
<![%varargs.attlist;[
<!ATTLIST varargs
		%common.attrib;
		%varargs.role.attrib;
		%local.varargs.attrib;
>
<!--end of varargs.attlist-->]]>
<!--end of varargs.module-->]]>

<!-- Processing assumes that only one Parameter will appear in a
     ParamDef, and that FuncParams will be used at most once, for
     providing information on the "inner parameters" for parameters that
     are pointers to functions. -->

<!ENTITY % paramdef.module "INCLUDE">
<![%paramdef.module;[
<!ENTITY % local.paramdef.attrib "">
<!ENTITY % paramdef.role.attrib "%role.attrib;">

<!ENTITY % paramdef.element "INCLUDE">
<![%paramdef.element;[
<!--doc:Information about a function parameter in a programming language.
In the syntax summary for a function in a programming language,ParamDef provides the description of a parameter to the function. Typically, this includes the data type of the parameter and its name. For parameters that are pointers to functions, it also includes a summary of the nested parameters. Within the ParamDef, the parameter name is identified with Parameter, and the rest of the content is assumed to be the data type. In the following definition, str is the name of the parameter and char * is its type: <paramdef>char *<parameter>str</parameter></paramdef> Sometimes a data type requires punctuation on both sides of the parameter. For example, the a parameter in this definition is an array of char *: <paramdef>char *<parameter>a</parameter>[]</paramdef>
Category: funcsynopsis-->
<!ELEMENT paramdef %ho; (#PCDATA
                | initializer
		| type
		| replaceable
		| parameter
		| funcparams)*>
<!--end of paramdef.element-->]]>

<!ENTITY % paramdef.attlist "INCLUDE">
<![%paramdef.attlist;[
<!ATTLIST paramdef
		choice		(opt
				|req)	#IMPLIED
		%common.attrib;
		%paramdef.role.attrib;
		%local.paramdef.attrib;
>
<!--end of paramdef.attlist-->]]>
<!--end of paramdef.module-->]]>

<!ENTITY % funcparams.module "INCLUDE">
<![%funcparams.module;[
<!ENTITY % local.funcparams.attrib "">
<!ENTITY % funcparams.role.attrib "%role.attrib;">

<!ENTITY % funcparams.element "INCLUDE">
<![%funcparams.element;[
<!--doc:Parameters for a function referenced through a function pointer in a synopsis.
In some programming languages (like C), it is possible for a function to have a pointer to another function as one of its parameters. In the syntax summary for such a function, the FuncParams element provides a wrapper for the function pointer. For example, the following prototype describes the functionsort, which takes two parameters. The first parameter, arr, is an array of integers. The second parameter is a pointer to a function, comp that returns an int. The comp function takes two parameters, both of type int *: <funcprototype> <funcdef>void <function>sort</function></funcdef> <paramdef>int *<parameter>arr</parameter>[]</paramdef> <paramdef>int <parameter>(* comp)</parameter> <funcparams>int *, int *</funcparams></paramdef> </funcprototype>
Category: funcsynopsis-->
<!ELEMENT funcparams %ho; (%cptr.char.mix;)*>
<!--end of funcparams.element-->]]>

<!ENTITY % funcparams.attlist "INCLUDE">
<![%funcparams.attlist;[
<!ATTLIST funcparams
		%common.attrib;
		%funcparams.role.attrib;
		%local.funcparams.attrib;
>
<!--end of funcparams.attlist-->]]>
<!--end of funcparams.module-->]]>

<!-- LineAnnotation (defined in the Inlines section, below)-->
<!-- Replaceable (defined in the Inlines section, below)-->
<!-- Function (defined in the Inlines section, below)-->
<!-- Parameter (defined in the Inlines section, below)-->
<!--end of funcsynopsis.content.module-->]]>

<!-- ClassSynopsis ..................... -->

<!ENTITY % classsynopsis.content.module "INCLUDE">
<![%classsynopsis.content.module;[

<!ENTITY % classsynopsis.module "INCLUDE">
<![%classsynopsis.module;[
<!ENTITY % local.classsynopsis.attrib "">
<!ENTITY % classsynopsis.role.attrib "%role.attrib;">

<!ENTITY % classsynopsis.element "INCLUDE">
<![%classsynopsis.element;[
<!--doc:The syntax summary for a class definition.
A ClassSynopsis contains the syntax summary of a class (generally speaking, a class in the object-oriented programming language sense). This is one of the few places where DocBook attempts to model as well as describe. Unlike FuncSynopsis which was designed with C language function prototypes in mind, the content model of ClassSynopsis was designed to capture a wide range of object-oriented language semantics.-->
<!ELEMENT classsynopsis %ho; ((ooclass|oointerface|ooexception)+,
                         (classsynopsisinfo
                          |fieldsynopsis|%method.synop.class;)*)>
<!--end of classsynopsis.element-->]]>

<!ENTITY % classsynopsis.attlist "INCLUDE">
<![%classsynopsis.attlist;[
<!ATTLIST classsynopsis
	language	CDATA	#IMPLIED
	class	(class|interface)	"class"
	%common.attrib;
	%classsynopsis.role.attrib;
	%local.classsynopsis.attrib;
>
<!--end of classsynopsis.attlist-->]]>
<!--end of classsynopsis.module-->]]>

<!ENTITY % classsynopsisinfo.module "INCLUDE">
<![ %classsynopsisinfo.module; [
<!ENTITY % local.classsynopsisinfo.attrib "">
<!ENTITY % classsynopsisinfo.role.attrib "%role.attrib;">

<!ENTITY % classsynopsisinfo.element "INCLUDE">
<![ %classsynopsisinfo.element; [
<!--doc:Information supplementing the contents of a ClassSynopsis.
Supplementary information in a ClassSynopsis. See ClassSynopsis. Unlike the other info elements, ClassSynopsisInfo is not a container for meta-information. Instead, ClassSynopsisInfo is a verbatim environment for adding additional information to a class synopsis.-->
<!ELEMENT classsynopsisinfo %ho; (%cptr.char.mix;|textobject|lineannotation)*>
<!--end of classsynopsisinfo.element-->]]>

<!ENTITY % classsynopsisinfo.attlist "INCLUDE">
<![ %classsynopsisinfo.attlist; [
<!ATTLIST classsynopsisinfo
		%linespecific.attrib;
		%common.attrib;
		%classsynopsisinfo.role.attrib;
		%local.classsynopsisinfo.attrib;
>
<!--end of classsynopsisinfo.attlist-->]]>
<!--end of classsynopsisinfo.module-->]]>

<!ENTITY % ooclass.module "INCLUDE">
<![%ooclass.module;[
<!ENTITY % local.ooclass.attrib "">
<!ENTITY % ooclass.role.attrib "%role.attrib;">

<!ENTITY % ooclass.element "INCLUDE">
<![%ooclass.element;[
<!--doc:A class in an object-oriented programming language.
The OOClass element identifies programming language classes, generally from object-oriented programming languages. The OOClass is a wrapper for the ClassName plus some Modifiers.-->
<!ELEMENT ooclass %ho; (modifier*, classname)>
<!--end of ooclass.element-->]]>

<!ENTITY % ooclass.attlist "INCLUDE">
<![%ooclass.attlist;[
<!ATTLIST ooclass
	%common.attrib;
	%ooclass.role.attrib;
	%local.ooclass.attrib;
>
<!--end of ooclass.attlist-->]]>
<!--end of ooclass.module-->]]>

<!ENTITY % oointerface.module "INCLUDE">
<![%oointerface.module;[
<!ENTITY % local.oointerface.attrib "">
<!ENTITY % oointerface.role.attrib "%role.attrib;">

<!ENTITY % oointerface.element "INCLUDE">
<![%oointerface.element;[
<!--doc:An interface in an object-oriented programming language.
The OOInterface element identifies programming language interfaces, generally from object-oriented programming languages. The OOInterface is a wrapper for the InterfaceName plus some Modifiers.-->
<!ELEMENT oointerface %ho; (modifier*, interfacename)>
<!--end of oointerface.element-->]]>

<!ENTITY % oointerface.attlist "INCLUDE">
<![%oointerface.attlist;[
<!ATTLIST oointerface
	%common.attrib;
	%oointerface.role.attrib;
	%local.oointerface.attrib;
>
<!--end of oointerface.attlist-->]]>
<!--end of oointerface.module-->]]>

<!ENTITY % ooexception.module "INCLUDE">
<![%ooexception.module;[
<!ENTITY % local.ooexception.attrib "">
<!ENTITY % ooexception.role.attrib "%role.attrib;">

<!ENTITY % ooexception.element "INCLUDE">
<![%ooexception.element;[
<!--doc:An exception in an object-oriented programming language.
The OOException element identifies programming language exceptions, generally from object-oriented programming languages. The OOException is a wrapper for the ExceptionName plus some Modifiers.-->
<!ELEMENT ooexception %ho; (modifier*, exceptionname)>
<!--end of ooexception.element-->]]>

<!ENTITY % ooexception.attlist "INCLUDE">
<![%ooexception.attlist;[
<!ATTLIST ooexception
	%common.attrib;
	%ooexception.role.attrib;
	%local.ooexception.attrib;
>
<!--end of ooexception.attlist-->]]>
<!--end of ooexception.module-->]]>

<!ENTITY % modifier.module "INCLUDE">
<![%modifier.module;[
<!ENTITY % local.modifier.attrib "">
<!ENTITY % modifier.role.attrib "%role.attrib;">

<!ENTITY % modifier.element "INCLUDE">
<![%modifier.element;[
<!--doc:Modifiers in a synopsis.
A Modifier identifies additional information about some identifier. For example, the public or private nature of a OOClass name, or information about a static or synchronized nature of a MethodSynopsis.-->
<!ELEMENT modifier %ho; (%smallcptr.char.mix;)*>
<!--end of modifier.element-->]]>

<!ENTITY % modifier.attlist "INCLUDE">
<![%modifier.attlist;[
<!ATTLIST modifier
	%common.attrib;
	%modifier.role.attrib;
	%local.modifier.attrib;
>
<!--end of modifier.attlist-->]]>
<!--end of modifier.module-->]]>

<!ENTITY % interfacename.module "INCLUDE">
<![%interfacename.module;[
<!ENTITY % local.interfacename.attrib "">
<!ENTITY % interfacename.role.attrib "%role.attrib;">

<!ENTITY % interfacename.element "INCLUDE">
<![%interfacename.element;[
<!--doc:The InterfaceName element is used to identify the name of an interface. This is likely to occur only in documentation about object-oriented programming systems, languages, and architectures.-->
<!ELEMENT interfacename %ho; (%cptr.char.mix;)*>
<!--end of interfacename.element-->]]>

<!ENTITY % interfacename.attlist "INCLUDE">
<![%interfacename.attlist;[
<!ATTLIST interfacename
	%common.attrib;
	%interfacename.role.attrib;
	%local.interfacename.attrib;
>
<!--end of interfacename.attlist-->]]>
<!--end of interfacename.module-->]]>

<!ENTITY % exceptionname.module "INCLUDE">
<![%exceptionname.module;[
<!ENTITY % local.exceptionname.attrib "">
<!ENTITY % exceptionname.role.attrib "%role.attrib;">

<!ENTITY % exceptionname.element "INCLUDE">
<![%exceptionname.element;[
<!--doc:The name of an exception.
The ExceptionName element is used to identify the name of an interface. This is likely to occur only in documentation about object-oriented programming systems, languages, and architectures.-->
<!ELEMENT exceptionname %ho; (%smallcptr.char.mix;)*>
<!--end of exceptionname.element-->]]>

<!ENTITY % exceptionname.attlist "INCLUDE">
<![%exceptionname.attlist;[
<!ATTLIST exceptionname
	%common.attrib;
	%exceptionname.role.attrib;
	%local.exceptionname.attrib;
>
<!--end of exceptionname.attlist-->]]>
<!--end of exceptionname.module-->]]>

<!ENTITY % fieldsynopsis.module "INCLUDE">
<![%fieldsynopsis.module;[
<!ENTITY % local.fieldsynopsis.attrib "">
<!ENTITY % fieldsynopsis.role.attrib "%role.attrib;">

<!ENTITY % fieldsynopsis.element "INCLUDE">
<![%fieldsynopsis.element;[
<!--doc:The name of a field in a class definition.
A FieldSynopsis contains the syntax summary of a field (generally speaking, fields in the object-oriented programming language sense).-->
<!ELEMENT fieldsynopsis %ho; (modifier*, type?, varname, initializer?)>
<!--end of fieldsynopsis.element-->]]>

<!ENTITY % fieldsynopsis.attlist "INCLUDE">
<![%fieldsynopsis.attlist;[
<!ATTLIST fieldsynopsis
	language	CDATA	#IMPLIED
	%common.attrib;
	%fieldsynopsis.role.attrib;
	%local.fieldsynopsis.attrib;
>
<!--end of fieldsynopsis.attlist-->]]>
<!--end of fieldsynopsis.module-->]]>

<!ENTITY % initializer.module "INCLUDE">
<![%initializer.module;[
<!ENTITY % local.initializer.attrib "">
<!ENTITY % initializer.role.attrib "%role.attrib;">

<!ENTITY % initializer.element "INCLUDE">
<![%initializer.element;[
<!--doc:The initializer for a FieldSynopsis.
An Initializer identifies the initial or default value for a field (FieldSynopsis) or method parameter (MethodParam).-->
<!ELEMENT initializer %ho; (%smallcptr.char.mix;)*>
<!--end of initializer.element-->]]>

<!ENTITY % initializer.attlist "INCLUDE">
<![%initializer.attlist;[
<!ATTLIST initializer
	%common.attrib;
	%initializer.role.attrib;
	%local.initializer.attrib;
>
<!--end of initializer.attlist-->]]>
<!--end of initializer.module-->]]>

<!ENTITY % constructorsynopsis.module "INCLUDE">
<![%constructorsynopsis.module;[
<!ENTITY % local.constructorsynopsis.attrib "">
<!ENTITY % constructorsynopsis.role.attrib "%role.attrib;">

<!ENTITY % constructorsynopsis.element "INCLUDE">
<![%constructorsynopsis.element;[
<!--doc:A syntax summary for a constructor.
A ConstructorSynopsis contains the syntax summary of a constructor in an object-oriented programming language. Unlike a MethodSynopsis, which it closely resembles, it may not identify a return type and the MethodName is optional (in some languages, constructor names can be generated automatically).-->
<!ELEMENT constructorsynopsis %ho; (modifier*,
                               methodname?,
                               (methodparam+|void?),
                               exceptionname*)>
<!--end of constructorsynopsis.element-->]]>

<!ENTITY % constructorsynopsis.attlist "INCLUDE">
<![%constructorsynopsis.attlist;[
<!ATTLIST constructorsynopsis
	language	CDATA	#IMPLIED
	%common.attrib;
	%constructorsynopsis.role.attrib;
	%local.constructorsynopsis.attrib;
>
<!--end of constructorsynopsis.attlist-->]]>
<!--end of constructorsynopsis.module-->]]>

<!ENTITY % destructorsynopsis.module "INCLUDE">
<![%destructorsynopsis.module;[
<!ENTITY % local.destructorsynopsis.attrib "">
<!ENTITY % destructorsynopsis.role.attrib "%role.attrib;">

<!ENTITY % destructorsynopsis.element "INCLUDE">
<![%destructorsynopsis.element;[
<!--doc:A syntax summary for a destructor.
A DestructorSynopsis contains the syntax summary of a destructor in an object-oriented programming language. Unlike a MethodSynopsis, which it closely resembles, it may not identify a return type and the MethodName is optional (in some languages, destructors have an immutable name which may be generated automatically).-->
<!ELEMENT destructorsynopsis %ho; (modifier*,
                              methodname?,
                              (methodparam+|void?),
                              exceptionname*)>
<!--end of destructorsynopsis.element-->]]>

<!ENTITY % destructorsynopsis.attlist "INCLUDE">
<![%destructorsynopsis.attlist;[
<!ATTLIST destructorsynopsis
	language	CDATA	#IMPLIED
	%common.attrib;
	%destructorsynopsis.role.attrib;
	%local.destructorsynopsis.attrib;
>
<!--end of destructorsynopsis.attlist-->]]>
<!--end of destructorsynopsis.module-->]]>

<!ENTITY % methodsynopsis.module "INCLUDE">
<![%methodsynopsis.module;[
<!ENTITY % local.methodsynopsis.attrib "">
<!ENTITY % methodsynopsis.role.attrib "%role.attrib;">

<!ENTITY % methodsynopsis.element "INCLUDE">
<![%methodsynopsis.element;[
<!--doc:A syntax summary for a method.
A MethodSynopsis contains the syntax summary of a method (generally speaking, methods in the object-oriented programming language sense). This is one of the few places where DocBook attempts to model as well as describe. Unlike FuncSynopsis which was designed with C language function prototypes in mind, the content model of MethodSynopsis was designed to capture a wide range of semantics.-->
<!ELEMENT methodsynopsis %ho; (modifier*,
                          (type|void)?,
                          methodname,
                          (methodparam+|void?),
                          exceptionname*,
                          modifier*)>
<!--end of methodsynopsis.element-->]]>

<!ENTITY % methodsynopsis.attlist "INCLUDE">
<![%methodsynopsis.attlist;[
<!ATTLIST methodsynopsis
	language	CDATA	#IMPLIED
	%common.attrib;
	%methodsynopsis.role.attrib;
	%local.methodsynopsis.attrib;
>
<!--end of methodsynopsis.attlist-->]]>
<!--end of methodsynopsis.module-->]]>

<!ENTITY % methodname.module "INCLUDE">
<![%methodname.module;[
<!ENTITY % local.methodname.attrib "">
<!ENTITY % methodname.role.attrib "%role.attrib;">

<!ENTITY % methodname.element "INCLUDE">
<![%methodname.element;[
<!--doc:The MethodName element is used to identify the name of a method. This is likely to occur only in documentation about object-oriented programming systems, languages, and architectures.-->
<!ELEMENT methodname %ho; (%smallcptr.char.mix;)*>
<!--end of methodname.element-->]]>

<!ENTITY % methodname.attlist "INCLUDE">
<![%methodname.attlist;[
<!ATTLIST methodname
	%common.attrib;
	%methodname.role.attrib;
	%local.methodname.attrib;
>
<!--end of methodname.attlist-->]]>
<!--end of methodname.module-->]]>

<!ENTITY % methodparam.module "INCLUDE">
<![%methodparam.module;[
<!ENTITY % local.methodparam.attrib "">
<!ENTITY % methodparam.role.attrib "%role.attrib;">

<!ENTITY % methodparam.element "INCLUDE">
<![%methodparam.element;[
<!--doc:Parameters to a method.
In the syntax summary of a ConstructorSynopsis,DestructorSynopsis, orMethodSynopsis,MethodParam provides the description of a parameter to the method. Typically, this includes the data type of the parameter and its name, but may also include an initial value and other modifiers.-->
<!ELEMENT methodparam %ho; (modifier*,
                       type?,
                       ((parameter,initializer?)|funcparams),
                       modifier*)>
<!--end of methodparam.element-->]]>

<!ENTITY % methodparam.attlist "INCLUDE">
<![%methodparam.attlist;[
<!ATTLIST methodparam
	choice		(opt
			|req
			|plain)		"req"
	rep		(norepeat
			|repeat)	"norepeat"
	%common.attrib;
	%methodparam.role.attrib;
	%local.methodparam.attrib;
>
<!--end of methodparam.attlist-->]]>
<!--end of methodparam.module-->]]>
<!--end of classsynopsis.content.module-->]]>

<!-- ...................................................................... -->
<!-- Document information entities and elements ........................... -->

<!-- The document information elements include some elements that are
     currently used only in the document hierarchy module. They are
     defined here so that they will be available for use in customized
     document hierarchies. -->

<!-- .................................. -->

<!ENTITY % docinfo.content.module "INCLUDE">
<![%docinfo.content.module;[

<!-- Ackno ............................ -->

<!ENTITY % ackno.module "INCLUDE">
<![%ackno.module;[
<!ENTITY % local.ackno.attrib "">
<!ENTITY % ackno.role.attrib "%role.attrib;">

<!ENTITY % ackno.element "INCLUDE">
<![%ackno.element;[
<!--doc:Acknowledgements in an Article.-->
<!ELEMENT ackno %ho; (%docinfo.char.mix;)*>
<!--end of ackno.element-->]]>

<!ENTITY % ackno.attlist "INCLUDE">
<![%ackno.attlist;[
<!ATTLIST ackno
		%common.attrib;
		%ackno.role.attrib;
		%local.ackno.attrib;
>
<!--end of ackno.attlist-->]]>
<!--end of ackno.module-->]]>

<!-- Address .......................... -->

<!ENTITY % address.content.module "INCLUDE">
<![%address.content.module;[
<!ENTITY % address.module "INCLUDE">
<![%address.module;[
<!ENTITY % local.address.attrib "">
<!ENTITY % address.role.attrib "%role.attrib;">

<!ENTITY % address.element "INCLUDE">
<![%address.element;[
<!--doc:A real-world address, generally a postal address.
An address is generally a postal address, although it does contain elements for FAX and Email addresses as well as the catch-all OtherAddr. The linespecific notation on the Format attribute makes line breaks and other spaces significant in an Address.
Category: Addresses-->
<!ELEMENT address %ho; (#PCDATA|personname|%person.ident.mix;
		|street|pob|postcode|city|state|country|phone
		|fax|email|otheraddr)*>
<!--end of address.element-->]]>

<!ENTITY % address.attlist "INCLUDE">
<![%address.attlist;[
<!ATTLIST address
		%linespecific.attrib;
		%common.attrib;
		%address.role.attrib;
		%local.address.attrib;
>
<!--end of address.attlist-->]]>
<!--end of address.module-->]]>

  <!ENTITY % street.module "INCLUDE">
  <![%street.module;[
 <!ENTITY % local.street.attrib "">
  <!ENTITY % street.role.attrib "%role.attrib;">

<!ENTITY % street.element "INCLUDE">
<![%street.element;[
<!--doc:A street address in an address.
In postal addresses, the Street element contains the street address portion of the Address. If an address contains more than one line of street address information, each line should appear in its own Street.
Category: Addresses-->
<!ELEMENT street %ho; (%docinfo.char.mix;)*>
<!--end of street.element-->]]>

<!ENTITY % street.attlist "INCLUDE">
<![%street.attlist;[
<!ATTLIST street
		%common.attrib;
		%street.role.attrib;
		%local.street.attrib;
>
<!--end of street.attlist-->]]>
  <!--end of street.module-->]]>

  <!ENTITY % pob.module "INCLUDE">
  <![%pob.module;[
  <!ENTITY % local.pob.attrib "">
  <!ENTITY % pob.role.attrib "%role.attrib;">

<!ENTITY % pob.element "INCLUDE">
<![%pob.element;[
<!--doc:A post office box in an address.
POB is a post office box number in an Address.
Category: Addresses-->
<!ELEMENT pob %ho; (%docinfo.char.mix;)*>
<!--end of pob.element-->]]>

<!ENTITY % pob.attlist "INCLUDE">
<![%pob.attlist;[
<!ATTLIST pob
		%common.attrib;
		%pob.role.attrib;
		%local.pob.attrib;
>
<!--end of pob.attlist-->]]>
  <!--end of pob.module-->]]>

  <!ENTITY % postcode.module "INCLUDE">
  <![%postcode.module;[
  <!ENTITY % local.postcode.attrib "">
  <!ENTITY % postcode.role.attrib "%role.attrib;">

<!ENTITY % postcode.element "INCLUDE">
<![%postcode.element;[
<!--doc:A postal code in an address.
PostCode is a postal code (in the United States, a ZIP code) in an Address.
Category: Addresses-->
<!ELEMENT postcode %ho; (%docinfo.char.mix;)*>
<!--end of postcode.element-->]]>

<!ENTITY % postcode.attlist "INCLUDE">
<![%postcode.attlist;[
<!ATTLIST postcode
		%common.attrib;
		%postcode.role.attrib;
		%local.postcode.attrib;
>
<!--end of postcode.attlist-->]]>
  <!--end of postcode.module-->]]>

  <!ENTITY % city.module "INCLUDE">
  <![%city.module;[
  <!ENTITY % local.city.attrib "">
  <!ENTITY % city.role.attrib "%role.attrib;">

<!ENTITY % city.element "INCLUDE">
<![%city.element;[
<!--doc:The name of a city in an Address.
Category: Addresses-->
<!ELEMENT city %ho; (%docinfo.char.mix;)*>
<!--end of city.element-->]]>

<!ENTITY % city.attlist "INCLUDE">
<![%city.attlist;[
<!ATTLIST city
		%common.attrib;
		%city.role.attrib;
		%local.city.attrib;
>
<!--end of city.attlist-->]]>
  <!--end of city.module-->]]>

  <!ENTITY % state.module "INCLUDE">
  <![%state.module;[
  <!ENTITY % local.state.attrib "">
  <!ENTITY % state.role.attrib "%role.attrib;">

<!ENTITY % state.element "INCLUDE">
<![%state.element;[
<!--doc:A state or province in an address.
A State is the name or postal abbreviation for a state (or province) in an Address.
Category: Addresses-->
<!ELEMENT state %ho; (%docinfo.char.mix;)*>
<!--end of state.element-->]]>

<!ENTITY % state.attlist "INCLUDE">
<![%state.attlist;[
<!ATTLIST state
		%common.attrib;
		%state.role.attrib;
		%local.state.attrib;
>
<!--end of state.attlist-->]]>
  <!--end of state.module-->]]>

  <!ENTITY % country.module "INCLUDE">
  <![%country.module;[
  <!ENTITY % local.country.attrib "">
  <!ENTITY % country.role.attrib "%role.attrib;">

<!ENTITY % country.element "INCLUDE">
<![%country.element;[
<!--doc:The name of a country, typically in an address.
Category: Addresses-->
<!ELEMENT country %ho; (%docinfo.char.mix;)*>
<!--end of country.element-->]]>

<!ENTITY % country.attlist "INCLUDE">
<![%country.attlist;[
<!ATTLIST country
		%common.attrib;
		%country.role.attrib;
		%local.country.attrib;
>
<!--end of country.attlist-->]]>
  <!--end of country.module-->]]>

  <!ENTITY % phone.module "INCLUDE">
  <![%phone.module;[
  <!ENTITY % local.phone.attrib "">
  <!ENTITY % phone.role.attrib "%role.attrib;">

<!ENTITY % phone.element "INCLUDE">
<![%phone.element;[
<!--doc:Phone identifies a telephone number in an Address.
Category: Addresses-->
<!ELEMENT phone %ho; (%docinfo.char.mix;)*>
<!--end of phone.element-->]]>

<!ENTITY % phone.attlist "INCLUDE">
<![%phone.attlist;[
<!ATTLIST phone
		%common.attrib;
		%phone.role.attrib;
		%local.phone.attrib;
>
<!--end of phone.attlist-->]]>
  <!--end of phone.module-->]]>

  <!ENTITY % fax.module "INCLUDE">
  <![%fax.module;[
  <!ENTITY % local.fax.attrib "">
  <!ENTITY % fax.role.attrib "%role.attrib;">

<!ENTITY % fax.element "INCLUDE">
<![%fax.element;[
<!--doc:Fax is a fax number in an address.
Category: Addresses-->
<!ELEMENT fax %ho; (%docinfo.char.mix;)*>
<!--end of fax.element-->]]>

<!ENTITY % fax.attlist "INCLUDE">
<![%fax.attlist;[
<!ATTLIST fax
		%common.attrib;
		%fax.role.attrib;
		%local.fax.attrib;
>
<!--end of fax.attlist-->]]>
  <!--end of fax.module-->]]>

  <!-- Email (defined in the Inlines section, below)-->

  <!ENTITY % otheraddr.module "INCLUDE">
  <![%otheraddr.module;[
  <!ENTITY % local.otheraddr.attrib "">
  <!ENTITY % otheraddr.role.attrib "%role.attrib;">

<!ENTITY % otheraddr.element "INCLUDE">
<![%otheraddr.element;[
<!--doc:Uncategorized information in address.
Within an Address, OtherAddr is a wrapper for parts of an address other than Street, POB,Postcode, City, State,Country, Phone, Fax, and Email, all of which have elements specific to their content. In early versions of DocBook, Address was not allowed to contain character data (it was a database-like collection of fields). In that context, a wrapper was necessary for any random pieces of information that might be required for an address. With the introduction of character data directly in the Address element, OtherAddr may have lost most of its raison d'être.
Category: Addresses-->
<!ELEMENT otheraddr %ho; (%docinfo.char.mix;)*>
<!--end of otheraddr.element-->]]>

<!ENTITY % otheraddr.attlist "INCLUDE">
<![%otheraddr.attlist;[
<!ATTLIST otheraddr
		%common.attrib;
		%otheraddr.role.attrib;
		%local.otheraddr.attrib;
>
<!--end of otheraddr.attlist-->]]>
  <!--end of otheraddr.module-->]]>
<!--end of address.content.module-->]]>

<!-- Affiliation ...................... -->

<!ENTITY % affiliation.content.module "INCLUDE">
<![%affiliation.content.module;[
<!ENTITY % affiliation.module "INCLUDE">
<![%affiliation.module;[
<!ENTITY % local.affiliation.attrib "">
<!ENTITY % affiliation.role.attrib "%role.attrib;">

<!ENTITY % affiliation.element "INCLUDE">
<![%affiliation.element;[
<!--doc:The institutional affiliation of an individual.
The institutional affiliation of an author, contributor, or other individual.
Category: person-meta-->
<!ELEMENT affiliation %ho; (shortaffil?, jobtitle*, orgname?, orgdiv*,
		address*)>
<!--end of affiliation.element-->]]>

<!ENTITY % affiliation.attlist "INCLUDE">
<![%affiliation.attlist;[
<!ATTLIST affiliation
		%common.attrib;
		%affiliation.role.attrib;
		%local.affiliation.attrib;
>
<!--end of affiliation.attlist-->]]>
<!--end of affiliation.module-->]]>

  <!ENTITY % shortaffil.module "INCLUDE">
  <![%shortaffil.module;[
  <!ENTITY % local.shortaffil.attrib "">
  <!ENTITY % shortaffil.role.attrib "%role.attrib;">

<!ENTITY % shortaffil.element "INCLUDE">
<![%shortaffil.element;[
<!--doc:A brief description of an affiliation.
ShortAffil contains an abbreviated or brief description of an individual’s Affiliation.
Category: affiliations-->
<!ELEMENT shortaffil %ho; (%docinfo.char.mix;)*>
<!--end of shortaffil.element-->]]>

<!ENTITY % shortaffil.attlist "INCLUDE">
<![%shortaffil.attlist;[
<!ATTLIST shortaffil
		%common.attrib;
		%shortaffil.role.attrib;
		%local.shortaffil.attrib;
>
<!--end of shortaffil.attlist-->]]>
  <!--end of shortaffil.module-->]]>

  <!ENTITY % jobtitle.module "INCLUDE">
  <![%jobtitle.module;[
  <!ENTITY % local.jobtitle.attrib "">
  <!ENTITY % jobtitle.role.attrib "%role.attrib;">

<!ENTITY % jobtitle.element "INCLUDE">
<![%jobtitle.element;[
<!--doc:The title of an individual in an organization.
A JobTitle describes the position of an individual within an organization. This tag is generally reserved for the name of the title for which an individual is paid.
Category: affiliations-->
<!ELEMENT jobtitle %ho; (%docinfo.char.mix;)*>
<!--end of jobtitle.element-->]]>

<!ENTITY % jobtitle.attlist "INCLUDE">
<![%jobtitle.attlist;[
<!ATTLIST jobtitle
		%common.attrib;
		%jobtitle.role.attrib;
		%local.jobtitle.attrib;
>
<!--end of jobtitle.attlist-->]]>
  <!--end of jobtitle.module-->]]>

  <!-- OrgName (defined elsewhere in this section)-->

  <!ENTITY % orgdiv.module "INCLUDE">
  <![%orgdiv.module;[
  <!ENTITY % local.orgdiv.attrib "">
  <!ENTITY % orgdiv.role.attrib "%role.attrib;">

<!ENTITY % orgdiv.element "INCLUDE">
<![%orgdiv.element;[
<!--doc:A division of an organization.
OrgDiv identifies a division in an organization, such as Chrysler in General Motors.
Category: affiliations-->
<!ELEMENT orgdiv %ho; (%docinfo.char.mix;)*>
<!--end of orgdiv.element-->]]>

<!ENTITY % orgdiv.attlist "INCLUDE">
<![%orgdiv.attlist;[
<!ATTLIST orgdiv
		%common.attrib;
		%orgdiv.role.attrib;
		%local.orgdiv.attrib;
>
<!--end of orgdiv.attlist-->]]>
  <!--end of orgdiv.module-->]]>

  <!-- Address (defined elsewhere in this section)-->
<!--end of affiliation.content.module-->]]>

<!-- ArtPageNums ...................... -->

<!ENTITY % artpagenums.module "INCLUDE">
<![%artpagenums.module;[
<!ENTITY % local.artpagenums.attrib "">
<!ENTITY % artpagenums.role.attrib "%role.attrib;">

<!ENTITY % artpagenums.element "INCLUDE">
<![%artpagenums.element;[
<!--doc:This element holds the page numbers of an article as published. Its content is not intended to influence the page numbers used by a presentation system formatting the parent Article.-->
<!ELEMENT artpagenums %ho; (%docinfo.char.mix;)*>
<!--end of artpagenums.element-->]]>

<!ENTITY % artpagenums.attlist "INCLUDE">
<![%artpagenums.attlist;[
<!ATTLIST artpagenums
		%common.attrib;
		%artpagenums.role.attrib;
		%local.artpagenums.attrib;
>
<!--end of artpagenums.attlist-->]]>
<!--end of artpagenums.module-->]]>

<!-- PersonName -->

<!ENTITY % personname.module "INCLUDE">
<![%personname.module;[
<!ENTITY % local.personname.attrib "">
<!ENTITY % personname.role.attrib "%role.attrib;">

<!ENTITY % personname.element "INCLUDE">
<![%personname.element;[
<!--doc:The personname identifies the personal name of an individual.
Category: author-->
<!ELEMENT personname %ho; ((honorific|firstname|surname|lineage|othername)+)>
<!--end of personname.element-->]]>

<!ENTITY % personname.attlist "INCLUDE">
<![%personname.attlist;[
<!ATTLIST personname
		%common.attrib;
		%personname.role.attrib;
		%local.personname.attrib;
>
<!--end of personname.attlist-->]]>
<!--end of personname.module-->]]>

<!-- Author ........................... -->

<!ENTITY % author.module "INCLUDE">
<![%author.module;[
<!ENTITY % local.author.attrib "">
<!ENTITY % author.role.attrib "%role.attrib;">

<!ENTITY % author.element "INCLUDE">
<![%author.element;[
<!--doc:The name of an individual author.
The Author element holds information about the author of the document in which it occurs; it is meta-information about the current document or document section, not a reference to the author of an external document.
Category: author-->
<!ELEMENT author %ho; ((personname|(%person.ident.mix;)+),(personblurb|email|address)*)>
<!--end of author.element-->]]>

<!ENTITY % author.attlist "INCLUDE">
<![%author.attlist;[
<!ATTLIST author
		%common.attrib;
		%author.role.attrib;
		%local.author.attrib;
>
<!--end of author.attlist-->]]>
<!--(see "Personal identity elements" for %person.ident.mix;)-->
<!--end of author.module-->]]>

<!-- AuthorGroup ...................... -->

<!ENTITY % authorgroup.content.module "INCLUDE">
<![%authorgroup.content.module;[
<!ENTITY % authorgroup.module "INCLUDE">
<![%authorgroup.module;[
<!ENTITY % local.authorgroup.attrib "">
<!ENTITY % authorgroup.role.attrib "%role.attrib;">

<!ENTITY % authorgroup.element "INCLUDE">
<![%authorgroup.element;[
<!--doc:Wrapper for author information when a document has multiple authors or collaborators.
The AuthorGroup element is a wrapper around multiple authors or other collaborators.
Category: author-->
<!ELEMENT authorgroup %ho; ((author|editor|collab|corpauthor|corpcredit|othercredit)+)>
<!--end of authorgroup.element-->]]>

<!ENTITY % authorgroup.attlist "INCLUDE">
<![%authorgroup.attlist;[
<!ATTLIST authorgroup
		%common.attrib;
		%authorgroup.role.attrib;
		%local.authorgroup.attrib;
>
<!--end of authorgroup.attlist-->]]>
<!--end of authorgroup.module-->]]>

  <!-- Author (defined elsewhere in this section)-->
  <!-- Editor (defined elsewhere in this section)-->

  <!ENTITY % collab.content.module "INCLUDE">
  <![%collab.content.module;[
  <!ENTITY % collab.module "INCLUDE">
  <![%collab.module;[
  <!ENTITY % local.collab.attrib "">
  <!ENTITY % collab.role.attrib "%role.attrib;">

<!ENTITY % collab.element "INCLUDE">
<![%collab.element;[
<!--doc:Identifies a collaborator.
This element identifies a collaborative partner in a document. It associates the name of a collaborator with his or her Affiliation.
Category: author-->
<!ELEMENT collab %ho; (collabname, affiliation*)>
<!--end of collab.element-->]]>

<!ENTITY % collab.attlist "INCLUDE">
<![%collab.attlist;[
<!ATTLIST collab
		%common.attrib;
		%collab.role.attrib;
		%local.collab.attrib;
>
<!--end of collab.attlist-->]]>
  <!--end of collab.module-->]]>

    <!ENTITY % collabname.module "INCLUDE">
  <![%collabname.module;[
  <!ENTITY % local.collabname.attrib "">
  <!ENTITY % collabname.role.attrib "%role.attrib;">

<!ENTITY % collabname.element "INCLUDE">
<![%collabname.element;[
<!--doc:The name of a collaborator.
Category: author-->
<!ELEMENT collabname %ho; (%docinfo.char.mix;)*>
<!--end of collabname.element-->]]>

<!ENTITY % collabname.attlist "INCLUDE">
<![%collabname.attlist;[
<!ATTLIST collabname
		%common.attrib;
		%collabname.role.attrib;
		%local.collabname.attrib;
>
<!--end of collabname.attlist-->]]>
    <!--end of collabname.module-->]]>

    <!-- Affiliation (defined elsewhere in this section)-->
  <!--end of collab.content.module-->]]>

  <!-- CorpAuthor (defined elsewhere in this section)-->
  <!-- OtherCredit (defined elsewhere in this section)-->

<!--end of authorgroup.content.module-->]]>

<!-- AuthorInitials ................... -->

<!ENTITY % authorinitials.module "INCLUDE">
<![%authorinitials.module;[
<!ENTITY % local.authorinitials.attrib "">
<!ENTITY % authorinitials.role.attrib "%role.attrib;">

<!ENTITY % authorinitials.element "INCLUDE">
<![%authorinitials.element;[
<!--doc:The initials or other short identifier for an author.
Author initials occur most frequently in a Revision or Comment.-->
<!ELEMENT authorinitials %ho; (%docinfo.char.mix;)*>
<!--end of authorinitials.element-->]]>

<!ENTITY % authorinitials.attlist "INCLUDE">
<![%authorinitials.attlist;[
<!ATTLIST authorinitials
		%common.attrib;
		%authorinitials.role.attrib;
		%local.authorinitials.attrib;
>
<!--end of authorinitials.attlist-->]]>
<!--end of authorinitials.module-->]]>

<!-- ConfGroup ........................ -->

<!ENTITY % confgroup.content.module "INCLUDE">
<![%confgroup.content.module;[
<!ENTITY % confgroup.module "INCLUDE">
<![%confgroup.module;[
<!ENTITY % local.confgroup.attrib "">
<!ENTITY % confgroup.role.attrib "%role.attrib;">

<!ENTITY % confgroup.element "INCLUDE">
<![%confgroup.element;[
<!--doc:A wrapper for document meta-information about a conference.
If a document, for example an Article, is written in connection with a conference, the elements in this wrapper are used to hold information about the conference: titles, sponsors, addresses, dates, etc.
Category: conference meta-->
<!ELEMENT confgroup %ho; ((confdates|conftitle|confnum|address|confsponsor)*)>
<!--end of confgroup.element-->]]>

<!ENTITY % confgroup.attlist "INCLUDE">
<![%confgroup.attlist;[
<!ATTLIST confgroup
		%common.attrib;
		%confgroup.role.attrib;
		%local.confgroup.attrib;
>
<!--end of confgroup.attlist-->]]>
<!--end of confgroup.module-->]]>

  <!ENTITY % confdates.module "INCLUDE">
  <![%confdates.module;[
  <!ENTITY % local.confdates.attrib "">
  <!ENTITY % confdates.role.attrib "%role.attrib;">

<!ENTITY % confdates.element "INCLUDE">
<![%confdates.element;[
<!--doc:ConfDates holds the dates of a conference for which a document was written or at which it was presented.
Category: conference meta-->
<!ELEMENT confdates %ho; (%docinfo.char.mix;)*>
<!--end of confdates.element-->]]>

<!ENTITY % confdates.attlist "INCLUDE">
<![%confdates.attlist;[
<!ATTLIST confdates
		%common.attrib;
		%confdates.role.attrib;
		%local.confdates.attrib;
>
<!--end of confdates.attlist-->]]>
  <!--end of confdates.module-->]]>

  <!ENTITY % conftitle.module "INCLUDE">
  <![%conftitle.module;[
  <!ENTITY % local.conftitle.attrib "">
  <!ENTITY % conftitle.role.attrib "%role.attrib;">

<!ENTITY % conftitle.element "INCLUDE">
<![%conftitle.element;[
<!--doc:The title of a conference for which a document was written.
See ConfGroup.
Category: conference meta-->
<!ELEMENT conftitle %ho; (%docinfo.char.mix;)*>
<!--end of conftitle.element-->]]>

<!ENTITY % conftitle.attlist "INCLUDE">
<![%conftitle.attlist;[
<!ATTLIST conftitle
		%common.attrib;
		%conftitle.role.attrib;
		%local.conftitle.attrib;
>
<!--end of conftitle.attlist-->]]>
  <!--end of conftitle.module-->]]>

  <!ENTITY % confnum.module "INCLUDE">
  <![%confnum.module;[
  <!ENTITY % local.confnum.attrib "">
  <!ENTITY % confnum.role.attrib "%role.attrib;">

<!ENTITY % confnum.element "INCLUDE">
<![%confnum.element;[
<!--doc:An identifier, frequently numerical, associated with a conference for which a document was written.
See ConfGroup.
Category: conference meta-->
<!ELEMENT confnum %ho; (%docinfo.char.mix;)*>
<!--end of confnum.element-->]]>

<!ENTITY % confnum.attlist "INCLUDE">
<![%confnum.attlist;[
<!ATTLIST confnum
		%common.attrib;
		%confnum.role.attrib;
		%local.confnum.attrib;
>
<!--end of confnum.attlist-->]]>
  <!--end of confnum.module-->]]>

  <!-- Address (defined elsewhere in this section)-->

  <!ENTITY % confsponsor.module "INCLUDE">
  <![%confsponsor.module;[
  <!ENTITY % local.confsponsor.attrib "">
  <!ENTITY % confsponsor.role.attrib "%role.attrib;">

<!ENTITY % confsponsor.element "INCLUDE">
<![%confsponsor.element;[
<!--doc:The sponsor of a conference for which a document was written.
See ConfGroup.
Category: conference meta-->
<!ELEMENT confsponsor %ho; (%docinfo.char.mix;)*>
<!--end of confsponsor.element-->]]>

<!ENTITY % confsponsor.attlist "INCLUDE">
<![%confsponsor.attlist;[
<!ATTLIST confsponsor
		%common.attrib;
		%confsponsor.role.attrib;
		%local.confsponsor.attrib;
>
<!--end of confsponsor.attlist-->]]>
  <!--end of confsponsor.module-->]]>
<!--end of confgroup.content.module-->]]>

<!-- ContractNum ...................... -->

<!ENTITY % contractnum.module "INCLUDE">
<![%contractnum.module;[
<!ENTITY % local.contractnum.attrib "">
<!ENTITY % contractnum.role.attrib "%role.attrib;">

<!ENTITY % contractnum.element "INCLUDE">
<![%contractnum.element;[
<!--doc:The contract number of a document.
The ContractNum element that occurs in bibliographic metadata contains information about the contract number of a contract under which a document was written.
Category: conference meta-->
<!ELEMENT contractnum %ho; (%docinfo.char.mix;)*>
<!--end of contractnum.element-->]]>

<!ENTITY % contractnum.attlist "INCLUDE">
<![%contractnum.attlist;[
<!ATTLIST contractnum
		%common.attrib;
		%contractnum.role.attrib;
		%local.contractnum.attrib;
>
<!--end of contractnum.attlist-->]]>
<!--end of contractnum.module-->]]>

<!-- ContractSponsor .................. -->

<!ENTITY % contractsponsor.module "INCLUDE">
<![%contractsponsor.module;[
<!ENTITY % local.contractsponsor.attrib "">
<!ENTITY % contractsponsor.role.attrib "%role.attrib;">

<!ENTITY % contractsponsor.element "INCLUDE">
<![%contractsponsor.element;[
<!--doc:The ContractSponsor element that occurs in bibliographic metadata contains information about the sponsor of a contract under which a document was written.
Category: conference meta-->
<!ELEMENT contractsponsor %ho; (%docinfo.char.mix;)*>
<!--end of contractsponsor.element-->]]>

<!ENTITY % contractsponsor.attlist "INCLUDE">
<![%contractsponsor.attlist;[
<!ATTLIST contractsponsor
		%common.attrib;
		%contractsponsor.role.attrib;
		%local.contractsponsor.attrib;
>
<!--end of contractsponsor.attlist-->]]>
<!--end of contractsponsor.module-->]]>

<!-- Copyright ........................ -->

<!ENTITY % copyright.content.module "INCLUDE">
<![%copyright.content.module;[
<!ENTITY % copyright.module "INCLUDE">
<![%copyright.module;[
<!ENTITY % local.copyright.attrib "">
<!ENTITY % copyright.role.attrib "%role.attrib;">

<!ENTITY % copyright.element "INCLUDE">
<![%copyright.element;[
<!--doc:Copyright information about a document.
The Copyright element holds information about the date(s) and holder(s) of a document copyright. If an extended block of text describing the copyright or other legal status is required, use LegalNotice. The Copyright element is confined to meta-information. For copyright statements in running text, see Trademark.
Category: product names-->
<!ELEMENT copyright %ho; (year+, holder*)>
<!--end of copyright.element-->]]>

<!ENTITY % copyright.attlist "INCLUDE">
<![%copyright.attlist;[
<!ATTLIST copyright
		%common.attrib;
		%copyright.role.attrib;
		%local.copyright.attrib;
>
<!--end of copyright.attlist-->]]>
<!--end of copyright.module-->]]>

  <!ENTITY % year.module "INCLUDE">
  <![%year.module;[
  <!ENTITY % local.year.attrib "">
  <!ENTITY % year.role.attrib "%role.attrib;">

<!ENTITY % year.element "INCLUDE">
<![%year.element;[
<!--doc:The year of publication of a document.
Year identifies a year. In DocBook V3.0, this is only used in Copyright, to identify the year or years in which copyright is asserted.-->
<!ELEMENT year %ho; (%docinfo.char.mix;)*>
<!--end of year.element-->]]>

<!ENTITY % year.attlist "INCLUDE">
<![%year.attlist;[
<!ATTLIST year
		%common.attrib;
		%year.role.attrib;
		%local.year.attrib;
>
<!--end of year.attlist-->]]>
  <!--end of year.module-->]]>

  <!ENTITY % holder.module "INCLUDE">
  <![%holder.module;[
  <!ENTITY % local.holder.attrib "">
  <!ENTITY % holder.role.attrib "%role.attrib;">

<!ENTITY % holder.element "INCLUDE">
<![%holder.element;[
<!--doc:The name of the individual or organization that holds a copyright.
Holder in Copyright identifies an individual or organization that asserts a copyright on the document.-->
<!ELEMENT holder %ho; (%docinfo.char.mix;)*>
<!--end of holder.element-->]]>

<!ENTITY % holder.attlist "INCLUDE">
<![%holder.attlist;[
<!ATTLIST holder
		%common.attrib;
		%holder.role.attrib;
		%local.holder.attrib;
>
<!--end of holder.attlist-->]]>
  <!--end of holder.module-->]]>
<!--end of copyright.content.module-->]]>

<!-- CorpAuthor ....................... -->

<!ENTITY % corpauthor.module "INCLUDE">
<![%corpauthor.module;[
<!ENTITY % local.corpauthor.attrib "">
<!ENTITY % corpauthor.role.attrib "%role.attrib;">

<!ENTITY % corpauthor.element "INCLUDE">
<![%corpauthor.element;[
<!--doc:A corporate author, as opposed to an individual.
In documents that have no specific authors, but are credited as authored by a corporation, the CorpAuthor tag can be used in place of the Author tag to indicate authorship. This element is used in bibliographic metadata.
Category: author-->
<!ELEMENT corpauthor %ho; (%docinfo.char.mix;)*>
<!--end of corpauthor.element-->]]>

<!ENTITY % corpauthor.attlist "INCLUDE">
<![%corpauthor.attlist;[
<!ATTLIST corpauthor
		%common.attrib;
		%corpauthor.role.attrib;
		%local.corpauthor.attrib;
>
<!--end of corpauthor.attlist-->]]>
<!--end of corpauthor.module-->]]>

<!-- CorpCredit ...................... -->

<!ENTITY % corpcredit.module "INCLUDE">
<![%corpcredit.module;[
<!ENTITY % local.corpcredit.attrib "">
<!ENTITY % corpcredit.role.attrib "%role.attrib;">

<!ENTITY % corpcredit.element "INCLUDE">
<![%corpcredit.element;[
<!--doc:A corporation or organization credited in a document.
The corpcredit element identifies corporations for credit analogous to the way that author and editor, and other credit identify individuals.-->
<!ELEMENT corpcredit %ho; (%docinfo.char.mix;)*>
<!--end of corpcredit.element-->]]>

<!ENTITY % corpcredit.attlist "INCLUDE">
<![%corpcredit.attlist;[
<!ATTLIST corpcredit
		class	(graphicdesigner
			|productioneditor
			|copyeditor
			|technicaleditor
			|translator
			|other)			#IMPLIED
		%common.attrib;
		%corpcredit.role.attrib;
		%local.corpcredit.attrib;
>
<!--end of corpcredit.attlist-->]]>
<!--end of corpcredit.module-->]]>

<!-- CorpName ......................... -->

<!ENTITY % corpname.module "INCLUDE">
<![%corpname.module;[
<!ENTITY % local.corpname.attrib "">

<!ENTITY % corpname.element "INCLUDE">
<![%corpname.element;[
<!--doc:The name of a corporation.
Category: affiliations-->
<!ELEMENT corpname %ho; (%docinfo.char.mix;)*>
<!--end of corpname.element-->]]>
<!ENTITY % corpname.role.attrib "%role.attrib;">

<!ENTITY % corpname.attlist "INCLUDE">
<![%corpname.attlist;[
<!ATTLIST corpname
		%common.attrib;
		%corpname.role.attrib;
		%local.corpname.attrib;
>
<!--end of corpname.attlist-->]]>
<!--end of corpname.module-->]]>

<!-- Date ............................. -->

<!ENTITY % date.module "INCLUDE">
<![%date.module;[
<!ENTITY % local.date.attrib "">
<!ENTITY % date.role.attrib "%role.attrib;">

<!ENTITY % date.element "INCLUDE">
<![%date.element;[
<!--doc:The date of publication or revision of a document.
The Date element identifies a date.
Category: date meta-->
<!ELEMENT date %ho; (%docinfo.char.mix;)*>
<!--end of date.element-->]]>

<!ENTITY % date.attlist "INCLUDE">
<![%date.attlist;[
<!ATTLIST date
		%common.attrib;
		%date.role.attrib;
		%local.date.attrib;
>
<!--end of date.attlist-->]]>
<!--end of date.module-->]]>

<!-- Edition .......................... -->

<!ENTITY % edition.module "INCLUDE">
<![%edition.module;[
<!ENTITY % local.edition.attrib "">
<!ENTITY % edition.role.attrib "%role.attrib;">

<!ENTITY % edition.element "INCLUDE">
<![%edition.element;[
<!--doc:The name or number of an edition of a document.
The Edition contains the name or number of the edition of the document.
Category: date meta-->
<!ELEMENT edition %ho; (%docinfo.char.mix;)*>
<!--end of edition.element-->]]>

<!ENTITY % edition.attlist "INCLUDE">
<![%edition.attlist;[
<!ATTLIST edition
		%common.attrib;
		%edition.role.attrib;
		%local.edition.attrib;
>
<!--end of edition.attlist-->]]>
<!--end of edition.module-->]]>

<!-- Editor ........................... -->

<!ENTITY % editor.module "INCLUDE">
<![%editor.module;[
<!ENTITY % local.editor.attrib "">
<!ENTITY % editor.role.attrib "%role.attrib;">

<!ENTITY % editor.element "INCLUDE">
<![%editor.element;[
<!--doc:The name of the editor of a document.
Category: author-->
<!ELEMENT editor %ho; ((personname|(%person.ident.mix;)+),(personblurb|email|address)*)>
<!--end of editor.element-->]]>

<!ENTITY % editor.attlist "INCLUDE">
<![%editor.attlist;[
<!ATTLIST editor
		%common.attrib;
		%editor.role.attrib;
		%local.editor.attrib;
>
<!--end of editor.attlist-->]]>
  <!--(see "Personal identity elements" for %person.ident.mix;)-->
<!--end of editor.module-->]]>

<!-- ISBN ............................. -->

<!ENTITY % isbn.module "INCLUDE">
<![%isbn.module;[
<!ENTITY % local.isbn.attrib "">
<!ENTITY % isbn.role.attrib "%role.attrib;">

<!ENTITY % isbn.element "INCLUDE">
<![%isbn.element;[
<!--doc:An ISBN is the International Standard Book Number of a document.
Category: numbers-->
<!ELEMENT isbn %ho; (%docinfo.char.mix;)*>
<!--end of isbn.element-->]]>

<!ENTITY % isbn.attlist "INCLUDE">
<![%isbn.attlist;[
<!ATTLIST isbn
		%common.attrib;
		%isbn.role.attrib;
		%local.isbn.attrib;
>
<!--end of isbn.attlist-->]]>
<!--end of isbn.module-->]]>

<!-- ISSN ............................. -->

<!ENTITY % issn.module "INCLUDE">
<![%issn.module;[
<!ENTITY % local.issn.attrib "">
<!ENTITY % issn.role.attrib "%role.attrib;">

<!ENTITY % issn.element "INCLUDE">
<![%issn.element;[
<!--doc:An ISSN is the International Standard Serial Number of a periodical.
Category: numbers-->
<!ELEMENT issn %ho; (%docinfo.char.mix;)*>
<!--end of issn.element-->]]>

<!ENTITY % issn.attlist "INCLUDE">
<![%issn.attlist;[
<!ATTLIST issn
		%common.attrib;
		%issn.role.attrib;
		%local.issn.attrib;
>
<!--end of issn.attlist-->]]>
<!--end of issn.module-->]]>

<!-- BiblioId ................. -->
<!ENTITY % biblio.class.attrib
		"class	(uri
                         |doi
                         |isbn
                         |issn
                         |libraryofcongress
                         |pubnumber
                         |other)	#IMPLIED
		otherclass	CDATA	#IMPLIED"
>

<!ENTITY % biblioid.module "INCLUDE">
<![%biblioid.module;[
<!ENTITY % local.biblioid.attrib "">
<!ENTITY % biblioid.role.attrib "%role.attrib;">

<!ENTITY % biblioid.element "INCLUDE">
<![%biblioid.element;[
<!--doc:An identifier for a document.
A bibliographic identifier, such as an ISBN number, Library of Congress identifier, or URI.This element supersedes the isbn,issn, and pubsnumber elements.
Category: numbers-->
<!ELEMENT biblioid %ho; (%docinfo.char.mix;)*>
<!--end of biblioid.element-->]]>

<!ENTITY % biblioid.attlist "INCLUDE">
<![%biblioid.attlist;[
<!ATTLIST biblioid
		%biblio.class.attrib;
		%common.attrib;
		%biblioid.role.attrib;
		%local.biblioid.attrib;
>
<!--end of biblioid.attlist-->]]>
<!--end of biblioid.module-->]]>

<!-- CiteBiblioId ................. -->

<!ENTITY % citebiblioid.module "INCLUDE">
<![%citebiblioid.module;[
<!ENTITY % local.citebiblioid.attrib "">
<!ENTITY % citebiblioid.role.attrib "%role.attrib;">

<!ENTITY % citebiblioid.element "INCLUDE">
<![%citebiblioid.element;[
<!--doc:A citation of a bibliographic identifier.
A citebiblioid identifies a citation to another work by bibliographic identifier.
Category: Cross References 1-->
<!ELEMENT citebiblioid %ho; (%docinfo.char.mix;)*>
<!--end of citebiblioid.element-->]]>

<!ENTITY % citebiblioid.attlist "INCLUDE">
<![%citebiblioid.attlist;[
<!ATTLIST citebiblioid
		%biblio.class.attrib;
		%common.attrib;
		%citebiblioid.role.attrib;
		%local.citebiblioid.attrib;
>
<!--end of citebiblioid.attlist-->]]>
<!--end of citebiblioid.module-->]]>

<!-- BiblioSource ................. -->

<!ENTITY % bibliosource.module "INCLUDE">
<![%bibliosource.module;[
<!ENTITY % local.bibliosource.attrib "">
<!ENTITY % bibliosource.role.attrib "%role.attrib;">

<!ENTITY % bibliosource.element "INCLUDE">
<![%bibliosource.element;[
<!--doc:The source of a document.
The bibliosource element satisfies the source element of the Dublin Core Metadata Initiative.The Dublin Core defines source as a reference to a resource from which the present resource is derived. It goes on to note that the present resource may be derived from the source resource in whole or in part. Recommended best practice is to reference the resource by means of a string or number conforming to a formal identification system. DocBook 4.2 added bibliocoverage,bibliorelation, and bibliosource to make the DocBook meta-information wrappers a complete superset of the Dublin Core.-->
<!ELEMENT bibliosource %ho; (%docinfo.char.mix;)*>
<!--end of bibliosource.element-->]]>

<!ENTITY % bibliosource.attlist "INCLUDE">
<![%bibliosource.attlist;[
<!ATTLIST bibliosource
		%biblio.class.attrib;
		%common.attrib;
		%bibliosource.role.attrib;
		%local.bibliosource.attrib;
>
<!--end of bibliosource.attlist-->]]>
<!--end of bibliosource.module-->]]>

<!-- BiblioRelation ................. -->

<!ENTITY % bibliorelation.module "INCLUDE">
<![%bibliorelation.module;[
<!ENTITY % local.bibliorelation.attrib "">
<!ENTITY % local.bibliorelation.types "">

<!ENTITY % bibliorelation.type.attrib
                "type    (isversionof
                         |hasversion
                         |isreplacedby
                         |replaces
                         |isrequiredby
                         |requires
                         |ispartof
                         |haspart
                         |isreferencedby
                         |references
                         |isformatof
                         |hasformat
                         |othertype
                         %local.bibliorelation.types;)       #IMPLIED
		othertype	CDATA	#IMPLIED
">

<!ENTITY % bibliorelation.role.attrib "%role.attrib;">

<!ENTITY % bibliorelation.element "INCLUDE">
<![%bibliorelation.element;[
<!--doc:The relationship of a document to another.
The bibliorelation element satisfies the relation element of the Dublin Core Metadata Initiative.The Dublin Core defines relation as a reference to a related resource. It goes on to note that recommended best practice is to reference the resource by means of a string or number conforming to a formal identification system. DocBook 4.2 added bibliocoverage,bibliorelation, and bibliosource to make the DocBook meta-information wrappers a complete superset of the Dublin Core.-->
<!ELEMENT bibliorelation %ho; (%docinfo.char.mix;)*>
<!--end of bibliorelation.element-->]]>

<!ENTITY % bibliorelation.attlist "INCLUDE">
<![%bibliorelation.attlist;[
<!ATTLIST bibliorelation
		%biblio.class.attrib;
		%bibliorelation.type.attrib;
		%common.attrib;
		%bibliorelation.role.attrib;
		%local.bibliorelation.attrib;
>
<!--end of bibliorelation.attlist-->]]>
<!--end of bibliorelation.module-->]]>

<!-- BiblioCoverage ................. -->

<!ENTITY % bibliocoverage.module "INCLUDE">
<![%bibliocoverage.module;[
<!ENTITY % local.bibliocoverage.attrib "">
<!ENTITY % bibliocoverage.role.attrib "%role.attrib;">

<!ENTITY % bibliocoverage.element "INCLUDE">
<![%bibliocoverage.element;[
<!--doc:The spatial or temporal coverage of a document.
The bibliocoverage element satisfies coverage element of the Dublin Core Metadata Initiative.The Dublin Core defines coverage as the extent or scope of the content of the resource. It goes on to say:DocBook 4.2 added bibliocoverage,bibliorelation, and bibliosource to make the DocBook meta-information wrappers a complete superset of the Dublin Core.-->
<!ELEMENT bibliocoverage %ho; (%docinfo.char.mix;)*>
<!--end of bibliocoverage.element-->]]>

<!ENTITY % bibliocoverage.attlist "INCLUDE">
<![%bibliocoverage.attlist;[
<!ATTLIST bibliocoverage
		spatial	(dcmipoint|iso3166|dcmibox|tgn|otherspatial)	#IMPLIED
		otherspatial	CDATA	#IMPLIED
		temporal (dcmiperiod|w3c-dtf|othertemporal) #IMPLIED
		othertemporal	CDATA	#IMPLIED
		%common.attrib;
		%bibliocoverage.role.attrib;
		%local.bibliocoverage.attrib;
>
<!--end of bibliocoverage.attlist-->]]>
<!--end of bibliocoverage.module-->]]>

<!-- InvPartNumber .................... -->

<!ENTITY % invpartnumber.module "INCLUDE">
<![%invpartnumber.module;[
<!ENTITY % local.invpartnumber.attrib "">
<!ENTITY % invpartnumber.role.attrib "%role.attrib;">

<!ENTITY % invpartnumber.element "INCLUDE">
<![%invpartnumber.element;[
<!--doc:An InvPartNumber identifies a number (an inventory part number) in some organization-specific numbering scheme.
Category: numbers-->
<!ELEMENT invpartnumber %ho; (%docinfo.char.mix;)*>
<!--end of invpartnumber.element-->]]>

<!ENTITY % invpartnumber.attlist "INCLUDE">
<![%invpartnumber.attlist;[
<!ATTLIST invpartnumber
		%common.attrib;
		%invpartnumber.role.attrib;
		%local.invpartnumber.attrib;
>
<!--end of invpartnumber.attlist-->]]>
<!--end of invpartnumber.module-->]]>

<!-- IssueNum ......................... -->

<!ENTITY % issuenum.module "INCLUDE">
<![%issuenum.module;[
<!ENTITY % local.issuenum.attrib "">
<!ENTITY % issuenum.role.attrib "%role.attrib;">

<!ENTITY % issuenum.element "INCLUDE">
<![%issuenum.element;[
<!--doc:The number of an issue of a journal.
The IssueNum contains the issue number of a periodical.
Category: numbers-->
<!ELEMENT issuenum %ho; (%docinfo.char.mix;)*>
<!--end of issuenum.element-->]]>

<!ENTITY % issuenum.attlist "INCLUDE">
<![%issuenum.attlist;[
<!ATTLIST issuenum
		%common.attrib;
		%issuenum.role.attrib;
		%local.issuenum.attrib;
>
<!--end of issuenum.attlist-->]]>
<!--end of issuenum.module-->]]>

<!-- LegalNotice ...................... -->

<!ENTITY % legalnotice.module "INCLUDE">
<![%legalnotice.module;[
<!ENTITY % local.legalnotice.attrib "">
<!ENTITY % legalnotice.role.attrib "%role.attrib;">

<!ENTITY % legalnotice.element "INCLUDE">
<![%legalnotice.element;[
<!--doc:A statement of legal obligations or requirements.
LegalNotice identifies a statement of legal obligation, requirement, or warranty. It occurs in the meta-information for a document in which it frequently explains copyright, trademark, and other legal formalities of a document.
Category: copyright-->
<!ELEMENT legalnotice %ho; (blockinfo?, title?, (%legalnotice.mix;)+)
		%formal.exclusion;>
<!--end of legalnotice.element-->]]>

<!ENTITY % legalnotice.attlist "INCLUDE">
<![%legalnotice.attlist;[
<!ATTLIST legalnotice
		%common.attrib;
		%legalnotice.role.attrib;
		%local.legalnotice.attrib;
>
<!--end of legalnotice.attlist-->]]>
<!--end of legalnotice.module-->]]>

<!-- ModeSpec ......................... -->

<!ENTITY % modespec.module "INCLUDE">
<![%modespec.module;[
<!ENTITY % local.modespec.attrib "">
<!ENTITY % modespec.role.attrib "%role.attrib;">

<!ENTITY % modespec.element "INCLUDE">
<![%modespec.element;[
<!--doc:Application-specific information necessary for the completion of an OLink.
ModeSpec contains application-specific instructions required to process an OLink. See OLink.-->
<!ELEMENT modespec %ho; (%docinfo.char.mix;)*
		%ubiq.exclusion;>
<!--end of modespec.element-->]]>

<!-- Application: Type of action required for completion
		of the links to which the ModeSpec is relevant (e.g.,
		retrieval query) -->


<!ENTITY % modespec.attlist "INCLUDE">
<![%modespec.attlist;[
<!ATTLIST modespec
		application	NOTATION
				(%notation.class;)	#IMPLIED
		%common.attrib;
		%modespec.role.attrib;
		%local.modespec.attrib;
>
<!--end of modespec.attlist-->]]>
<!--end of modespec.module-->]]>

<!-- OrgName .......................... -->

<!ENTITY % orgname.module "INCLUDE">
<![%orgname.module;[
<!ENTITY % local.orgname.attrib "">
<!ENTITY % orgname.role.attrib "%role.attrib;">

<!ENTITY % orgname.element "INCLUDE">
<![%orgname.element;[
<!--doc:The name of an organization other than a corporation.
An OrgName identifies the name of an organization or corporation. Outside of an Affiliation, CorpName is a more appropriate element for the name of a corporation.
Category: affiliations-->
<!ELEMENT orgname %ho; (%docinfo.char.mix;)*>
<!--end of orgname.element-->]]>

<!ENTITY % orgname.attlist "INCLUDE">
<![%orgname.attlist;[
<!ATTLIST orgname
		%common.attrib;
		class	(corporation|nonprofit|consortium|informal|other)	#IMPLIED
		otherclass	CDATA			#IMPLIED
		%orgname.role.attrib;
		%local.orgname.attrib;
>
<!--end of orgname.attlist-->]]>
<!--end of orgname.module-->]]>

<!-- OtherCredit ...................... -->

<!ENTITY % othercredit.module "INCLUDE">
<![%othercredit.module;[
<!ENTITY % local.othercredit.attrib "">
<!ENTITY % othercredit.role.attrib "%role.attrib;">

<!ENTITY % othercredit.element "INCLUDE">
<![%othercredit.element;[
<!--doc:A person or entity, other than an author or editor, credited in a document.
DocBook allows you to directly identify Authors and Editors. OtherCredit provides a mechanism for identifying other individuals, for example, contributors or production editors, in a similar context.
Category: author-->
<!ELEMENT othercredit %ho; ((personname|(%person.ident.mix;)+),
                            (personblurb|email|address)*)>
<!--end of othercredit.element-->]]>

<!ENTITY % othercredit.attlist "INCLUDE">
<![%othercredit.attlist;[
<!ATTLIST othercredit
		class	(graphicdesigner
			|productioneditor
			|copyeditor
			|technicaleditor
			|translator
			|other)			#IMPLIED
		%common.attrib;
		%othercredit.role.attrib;
		%local.othercredit.attrib;
>
<!--end of othercredit.attlist-->]]>
  <!--(see "Personal identity elements" for %person.ident.mix;)-->
<!--end of othercredit.module-->]]>

<!-- PageNums ......................... -->

<!ENTITY % pagenums.module "INCLUDE">
<![%pagenums.module;[
<!ENTITY % local.pagenums.attrib "">
<!ENTITY % pagenums.role.attrib "%role.attrib;">

<!ENTITY % pagenums.element "INCLUDE">
<![%pagenums.element;[
<!--doc:The numbers of the pages in a book, for use in a bibliographic entry.
PageNums identifies a page or range of pages. This may be useful in the bibliography of a book, to indicate the number of pages, or in a citation to a journal article.-->
<!ELEMENT pagenums %ho; (%docinfo.char.mix;)*>
<!--end of pagenums.element-->]]>

<!ENTITY % pagenums.attlist "INCLUDE">
<![%pagenums.attlist;[
<!ATTLIST pagenums
		%common.attrib;
		%pagenums.role.attrib;
		%local.pagenums.attrib;
>
<!--end of pagenums.attlist-->]]>
<!--end of pagenums.module-->]]>

<!-- Personal identity elements ....... -->

<!-- These elements are used only within Author, Editor, and
OtherCredit. -->

<!ENTITY % person.ident.module "INCLUDE">
<![%person.ident.module;[
  <!ENTITY % contrib.module "INCLUDE">
  <![%contrib.module;[
  <!ENTITY % local.contrib.attrib "">
  <!ENTITY % contrib.role.attrib "%role.attrib;">

<!ENTITY % contrib.element "INCLUDE">
<![%contrib.element;[
<!--doc:A summary of the contributions made to a document by a credited source.
The Contrib element contains a summary or description of the contributions made by an author, editor, or other credited source.
Category: author-->
<!ELEMENT contrib %ho; (%docinfo.char.mix;)*>
<!--end of contrib.element-->]]>

<!ENTITY % contrib.attlist "INCLUDE">
<![%contrib.attlist;[
<!ATTLIST contrib
		%common.attrib;
		%contrib.role.attrib;
		%local.contrib.attrib;
>
<!--end of contrib.attlist-->]]>
  <!--end of contrib.module-->]]>

  <!ENTITY % firstname.module "INCLUDE">
  <![%firstname.module;[
  <!ENTITY % local.firstname.attrib "">
  <!ENTITY % firstname.role.attrib "%role.attrib;">

<!ENTITY % firstname.element "INCLUDE">
<![%firstname.element;[
<!--doc:The first name of a person.
The Western-style first name of an author, editor, or other individual.
Category: person-meta-->
<!ELEMENT firstname %ho; (%docinfo.char.mix;)*>
<!--end of firstname.element-->]]>

<!ENTITY % firstname.attlist "INCLUDE">
<![%firstname.attlist;[
<!ATTLIST firstname
		%common.attrib;
		%firstname.role.attrib;
		%local.firstname.attrib;
>
<!--end of firstname.attlist-->]]>
  <!--end of firstname.module-->]]>

  <!ENTITY % honorific.module "INCLUDE">
  <![%honorific.module;[
  <!ENTITY % local.honorific.attrib "">
  <!ENTITY % honorific.role.attrib "%role.attrib;">

<!ENTITY % honorific.element "INCLUDE">
<![%honorific.element;[
<!--doc:The title of a person.
An Honorific occurs in the name of an individual. It is the honorific title of the individual, such as Dr., Mr., or Ms.
Category: person-meta-->
<!ELEMENT honorific %ho; (%docinfo.char.mix;)*>
<!--end of honorific.element-->]]>

<!ENTITY % honorific.attlist "INCLUDE">
<![%honorific.attlist;[
<!ATTLIST honorific
		%common.attrib;
		%honorific.role.attrib;
		%local.honorific.attrib;
>
<!--end of honorific.attlist-->]]>
  <!--end of honorific.module-->]]>

  <!ENTITY % lineage.module "INCLUDE">
  <![%lineage.module;[
  <!ENTITY % local.lineage.attrib "">
  <!ENTITY % lineage.role.attrib "%role.attrib;">

<!ENTITY % lineage.element "INCLUDE">
<![%lineage.element;[
<!--doc:The portion of a person's name indicating a relationship to ancestors.
Lineage is a portion of a person's name, typically “Jr.” or “Sr.”
Category: person-meta-->
<!ELEMENT lineage %ho; (%docinfo.char.mix;)*>
<!--end of lineage.element-->]]>

<!ENTITY % lineage.attlist "INCLUDE">
<![%lineage.attlist;[
<!ATTLIST lineage
		%common.attrib;
		%lineage.role.attrib;
		%local.lineage.attrib;
>
<!--end of lineage.attlist-->]]>
  <!--end of lineage.module-->]]>

  <!ENTITY % othername.module "INCLUDE">
  <![%othername.module;[
  <!ENTITY % local.othername.attrib "">
  <!ENTITY % othername.role.attrib "%role.attrib;">

<!ENTITY % othername.element "INCLUDE">
<![%othername.element;[
<!--doc:A component of a persons name that is not a first name, surname, or lineage.
OtherName is a generic wrapper for parts of an individual’s name other than Honorific,FirstName,Surname and Lineage. One common use is to identify an individual’s middle name or initial. Use Role to classify the type of other name.
Category: person-meta-->
<!ELEMENT othername %ho; (%docinfo.char.mix;)*>
<!--end of othername.element-->]]>

<!ENTITY % othername.attlist "INCLUDE">
<![%othername.attlist;[
<!ATTLIST othername
		%common.attrib;
		%othername.role.attrib;
		%local.othername.attrib;
>
<!--end of othername.attlist-->]]>
  <!--end of othername.module-->]]>

  <!ENTITY % surname.module "INCLUDE">
  <![%surname.module;[
  <!ENTITY % local.surname.attrib "">
  <!ENTITY % surname.role.attrib "%role.attrib;">

<!ENTITY % surname.element "INCLUDE">
<![%surname.element;[
<!--doc:A family name; in western cultures the last name.
A Surname is a family name; in Western cultures, the last name.
Category: person-meta-->
<!ELEMENT surname %ho; (%docinfo.char.mix;)*>
<!--end of surname.element-->]]>

<!ENTITY % surname.attlist "INCLUDE">
<![%surname.attlist;[
<!ATTLIST surname
		%common.attrib;
		%surname.role.attrib;
		%local.surname.attrib;
>
<!--end of surname.attlist-->]]>
  <!--end of surname.module-->]]>
<!--end of person.ident.module-->]]>

<!-- PrintHistory ..................... -->

<!ENTITY % printhistory.module "INCLUDE">
<![%printhistory.module;[
<!ENTITY % local.printhistory.attrib "">
<!ENTITY % printhistory.role.attrib "%role.attrib;">

<!ENTITY % printhistory.element "INCLUDE">
<![%printhistory.element;[
<!--doc:The printing history of a document.
The PrintHistory of a document identifies when various editions and revisions were printed.
Category: date meta-->
<!ELEMENT printhistory %ho; ((%para.class;)+)>
<!--end of printhistory.element-->]]>

<!ENTITY % printhistory.attlist "INCLUDE">
<![%printhistory.attlist;[
<!ATTLIST printhistory
		%common.attrib;
		%printhistory.role.attrib;
		%local.printhistory.attrib;
>
<!--end of printhistory.attlist-->]]>
<!--end of printhistory.module-->]]>

<!-- ProductName ...................... -->

<!ENTITY % productname.module "INCLUDE">
<![%productname.module;[
<!ENTITY % local.productname.attrib "">
<!ENTITY % productname.role.attrib "%role.attrib;">

<!ENTITY % productname.element "INCLUDE">
<![%productname.element;[
<!--doc:The formal name of a product.
A ProductName is the formal name of any product. Identifying a product this way may be useful if you need to provide explicit disclaimers about product names or information. For example, the copyright statement on this book includes the following general notice: Some of the designations used by manufacturers and sellers to distinguish their products are claimed as trademarks. Where those designations appear in this book, and O'Reilly & Associates, Inc., was aware of the trademark claim, the designations have been printed in caps or initial caps. or words to that effect. If every product name in this book had been diligently coded as a ProductName, we could have automatically generated a complete list of all the product names and mentioned them explicitly in the notice. In running prose, the distinction between an Application and a ProductName may be very subjective.
Category: things-->
<!ELEMENT productname %ho; (%para.char.mix;)*>
<!--end of productname.element-->]]>

<!-- Class: More precisely identifies the item the element names -->


<!ENTITY % productname.attlist "INCLUDE">
<![%productname.attlist;[
<!ATTLIST productname
		class		(service
				|trade
				|registered
				|copyright)	'trade'
		%common.attrib;
		%productname.role.attrib;
		%local.productname.attrib;
>
<!--end of productname.attlist-->]]>
<!--end of productname.module-->]]>

<!-- ProductNumber .................... -->

<!ENTITY % productnumber.module "INCLUDE">
<![%productnumber.module;[
<!ENTITY % local.productnumber.attrib "">
<!ENTITY % productnumber.role.attrib "%role.attrib;">

<!ENTITY % productnumber.element "INCLUDE">
<![%productnumber.element;[
<!--doc:A number assigned to a product.
An ProductNumber identifies a product number in some unspecified numbering scheme. It's possible that product numbers for different products might not even come from the same scheme.
Category: numbers-->
<!ELEMENT productnumber %ho; (%docinfo.char.mix;)*>
<!--end of productnumber.element-->]]>

<!ENTITY % productnumber.attlist "INCLUDE">
<![%productnumber.attlist;[
<!ATTLIST productnumber
		%common.attrib;
		%productnumber.role.attrib;
		%local.productnumber.attrib;
>
<!--end of productnumber.attlist-->]]>
<!--end of productnumber.module-->]]>

<!-- PubDate .......................... -->

<!ENTITY % pubdate.module "INCLUDE">
<![%pubdate.module;[
<!ENTITY % local.pubdate.attrib "">
<!ENTITY % pubdate.role.attrib "%role.attrib;">

<!ENTITY % pubdate.element "INCLUDE">
<![%pubdate.element;[
<!--doc:The PubDate is the date of publication of a document.
Category: date meta-->
<!ELEMENT pubdate %ho; (%docinfo.char.mix;)*>
<!--end of pubdate.element-->]]>

<!ENTITY % pubdate.attlist "INCLUDE">
<![%pubdate.attlist;[
<!ATTLIST pubdate
		%common.attrib;
		%pubdate.role.attrib;
		%local.pubdate.attrib;
>
<!--end of pubdate.attlist-->]]>
<!--end of pubdate.module-->]]>

<!-- Publisher ........................ -->

<!ENTITY % publisher.content.module "INCLUDE">
<![%publisher.content.module;[
<!ENTITY % publisher.module "INCLUDE">
<![%publisher.module;[
<!ENTITY % local.publisher.attrib "">
<!ENTITY % publisher.role.attrib "%role.attrib;">

<!ENTITY % publisher.element "INCLUDE">
<![%publisher.element;[
<!--doc:The publisher of a document.
Publisher associates a PublisherName and an Address. Many publishers have offices in more than one city. Publisher can be used to list or distinguish between the multiple offices.-->
<!ELEMENT publisher %ho; (publishername, address*)>
<!--end of publisher.element-->]]>

<!ENTITY % publisher.attlist "INCLUDE">
<![%publisher.attlist;[
<!ATTLIST publisher
		%common.attrib;
		%publisher.role.attrib;
		%local.publisher.attrib;
>
<!--end of publisher.attlist-->]]>
<!--end of publisher.module-->]]>

  <!ENTITY % publishername.module "INCLUDE">
  <![%publishername.module;[
  <!ENTITY % local.publishername.attrib "">
  <!ENTITY % publishername.role.attrib "%role.attrib;">

<!ENTITY % publishername.element "INCLUDE">
<![%publishername.element;[
<!--doc:The name of the publisher of a document.
A PublisherName is the name of a publisher. Historically, this has been used in bibliographic meta-information to identify the publisher of a book or other document. It is also reasonable to identify the publisher of an electronic publication in this way.
Category: orgnames-->
<!ELEMENT publishername %ho; (%docinfo.char.mix;)*>
<!--end of publishername.element-->]]>

<!ENTITY % publishername.attlist "INCLUDE">
<![%publishername.attlist;[
<!ATTLIST publishername
		%common.attrib;
		%publishername.role.attrib;
		%local.publishername.attrib;
>
<!--end of publishername.attlist-->]]>
  <!--end of publishername.module-->]]>

  <!-- Address (defined elsewhere in this section)-->
<!--end of publisher.content.module-->]]>

<!-- PubsNumber ....................... -->

<!ENTITY % pubsnumber.module "INCLUDE">
<![%pubsnumber.module;[
<!ENTITY % local.pubsnumber.attrib "">
<!ENTITY % pubsnumber.role.attrib "%role.attrib;">

<!ENTITY % pubsnumber.element "INCLUDE">
<![%pubsnumber.element;[
<!--doc:A number assigned to a publication other than an ISBN or ISSN or inventory part number.
A PubsNumber identifies a document in some unspecified numbering scheme. This number may exist instead of, or in addition to, an ISBN or ISSN number.
Category: numbers-->
<!ELEMENT pubsnumber %ho; (%docinfo.char.mix;)*>
<!--end of pubsnumber.element-->]]>

<!ENTITY % pubsnumber.attlist "INCLUDE">
<![%pubsnumber.attlist;[
<!ATTLIST pubsnumber
		%common.attrib;
		%pubsnumber.role.attrib;
		%local.pubsnumber.attrib;
>
<!--end of pubsnumber.attlist-->]]>
<!--end of pubsnumber.module-->]]>

<!-- ReleaseInfo ...................... -->

<!ENTITY % releaseinfo.module "INCLUDE">
<![%releaseinfo.module;[
<!ENTITY % local.releaseinfo.attrib "">
<!ENTITY % releaseinfo.role.attrib "%role.attrib;">

<!ENTITY % releaseinfo.element "INCLUDE">
<![%releaseinfo.element;[
<!--doc:Information about a particular release of a document.
ReleaseInfo contains a brief description of the release or published version of a document or part of a document. For example, the release information may state that the document is in beta, or that the software it describes is a beta version. It may also contain more specific information, such as the version number from a revision control system.
Category: date meta-->
<!ELEMENT releaseinfo %ho; (%docinfo.char.mix;)*>
<!--end of releaseinfo.element-->]]>

<!ENTITY % releaseinfo.attlist "INCLUDE">
<![%releaseinfo.attlist;[
<!ATTLIST releaseinfo
		%common.attrib;
		%releaseinfo.role.attrib;
		%local.releaseinfo.attrib;
>
<!--end of releaseinfo.attlist-->]]>
<!--end of releaseinfo.module-->]]>

<!-- RevHistory ....................... -->

<!ENTITY % revhistory.content.module "INCLUDE">
<![%revhistory.content.module;[
<!ENTITY % revhistory.module "INCLUDE">
<![%revhistory.module;[
<!ENTITY % local.revhistory.attrib "">
<!ENTITY % revhistory.role.attrib "%role.attrib;">

<!ENTITY % revhistory.element "INCLUDE">
<![%revhistory.element;[
<!--doc:A history of the revisions to a document.
RevHistory is a structure for documenting a history of changes, specifically, a history of changes to the document or section in which it occurs.DocBook does not mandate an order for revisions: ascending order by date, descending order by date, and orders based on some other criteria are all equally acceptable.
Category: date meta-->
<!ELEMENT revhistory %ho; (revision+)>
<!--end of revhistory.element-->]]>

<!ENTITY % revhistory.attlist "INCLUDE">
<![%revhistory.attlist;[
<!ATTLIST revhistory
		%common.attrib;
		%revhistory.role.attrib;
		%local.revhistory.attrib;
>
<!--end of revhistory.attlist-->]]>
<!--end of revhistory.module-->]]>

<!ENTITY % revision.module "INCLUDE">
<![%revision.module;[
<!ENTITY % local.revision.attrib "">
<!ENTITY % revision.role.attrib "%role.attrib;">

<!ENTITY % revision.element "INCLUDE">
<![%revision.element;[
<!--doc:An entry describing a single revision in the history of the revisions to a document.
Revision contains information about a single revision to a document. Revisions are identified by a number and a date. They may also include the initials of the author, and additional remarks.-->
<!ELEMENT revision %ho; (revnumber, date, (author|authorinitials)*,
                    (revremark|revdescription)?)>
<!--end of revision.element-->]]>

<!ENTITY % revision.attlist "INCLUDE">
<![%revision.attlist;[
<!ATTLIST revision
		%common.attrib;
		%revision.role.attrib;
		%local.revision.attrib;
>
<!--end of revision.attlist-->]]>
<!--end of revision.module-->]]>

<!ENTITY % revnumber.module "INCLUDE">
<![%revnumber.module;[
<!ENTITY % local.revnumber.attrib "">
<!ENTITY % revnumber.role.attrib "%role.attrib;">

<!ENTITY % revnumber.element "INCLUDE">
<![%revnumber.element;[
<!--doc:A document revision number.
A RevNumber identifies the revision number of a document. The revision number should uniquely identify a particular revision of a document.-->
<!ELEMENT revnumber %ho; (%docinfo.char.mix;)*>
<!--end of revnumber.element-->]]>

<!ENTITY % revnumber.attlist "INCLUDE">
<![%revnumber.attlist;[
<!ATTLIST revnumber
		%common.attrib;
		%revnumber.role.attrib;
		%local.revnumber.attrib;
>
<!--end of revnumber.attlist-->]]>
<!--end of revnumber.module-->]]>

<!-- Date (defined elsewhere in this section)-->
<!-- AuthorInitials (defined elsewhere in this section)-->

<!ENTITY % revremark.module "INCLUDE">
<![%revremark.module;[
<!ENTITY % local.revremark.attrib "">
<!ENTITY % revremark.role.attrib "%role.attrib;">

<!ENTITY % revremark.element "INCLUDE">
<![%revremark.element;[
<!--doc:A description of a revision to a document.
The RevRemark associated with a revision is a short summary of the changes made in that revision. If a longer, more complete summary is desired, see RevDescription.-->
<!ELEMENT revremark %ho; (%docinfo.char.mix;)*>
<!--end of revremark.element-->]]>

<!ENTITY % revremark.attlist "INCLUDE">
<![%revremark.attlist;[
<!ATTLIST revremark
		%common.attrib;
		%revremark.role.attrib;
		%local.revremark.attrib;
>
<!--end of revremark.attlist-->]]>
<!--end of revremark.module-->]]>

<!ENTITY % revdescription.module "INCLUDE">
<![ %revdescription.module; [
<!ENTITY % local.revdescription.attrib "">
<!ENTITY % revdescription.role.attrib "%role.attrib;">

<!ENTITY % revdescription.element "INCLUDE">
<![ %revdescription.element; [
<!--doc:A extended description of a revision to a document.
The RevDescription associated with a revision is a summary of the changes made in that revision. RevDescription is intended for long, complete summaries. For a simple text-only summary, see RevRemark.-->
<!ELEMENT revdescription %ho; ((%revdescription.mix;)+)>
<!--end of revdescription.element-->]]>

<!ENTITY % revdescription.attlist "INCLUDE">
<![ %revdescription.attlist; [
<!ATTLIST revdescription
		%common.attrib;
		%revdescription.role.attrib;
		%local.revdescription.attrib;
>
<!--end of revdescription.attlist-->]]>
<!--end of revdescription.module-->]]>
<!--end of revhistory.content.module-->]]>

<!-- SeriesVolNums .................... -->

<!ENTITY % seriesvolnums.module "INCLUDE">
<![%seriesvolnums.module;[
<!ENTITY % local.seriesvolnums.attrib "">
<!ENTITY % seriesvolnums.role.attrib "%role.attrib;">

<!ENTITY % seriesvolnums.element "INCLUDE">
<![%seriesvolnums.element;[
<!--doc:Numbers of the volumes in a series of books.
SeriesVolNums contains the numbers of the volumes of the books in a series. It is a wrapper for bibliographic information.
Category: numbers-->
<!ELEMENT seriesvolnums %ho; (%docinfo.char.mix;)*>
<!--end of seriesvolnums.element-->]]>

<!ENTITY % seriesvolnums.attlist "INCLUDE">
<![%seriesvolnums.attlist;[
<!ATTLIST seriesvolnums
		%common.attrib;
		%seriesvolnums.role.attrib;
		%local.seriesvolnums.attrib;
>
<!--end of seriesvolnums.attlist-->]]>
<!--end of seriesvolnums.module-->]]>

<!-- VolumeNum ........................ -->

<!ENTITY % volumenum.module "INCLUDE">
<![%volumenum.module;[
<!ENTITY % local.volumenum.attrib "">
<!ENTITY % volumenum.role.attrib "%role.attrib;">

<!ENTITY % volumenum.element "INCLUDE">
<![%volumenum.element;[
<!--doc:The volume number of a document in a set (as of books in a set or articles in a journal).
VolumeNum identifies the volume number of a Book in a Set, or a periodical. It is a wrapper for bibliographic information.
Category: numbers-->
<!ELEMENT volumenum %ho; (%docinfo.char.mix;)*>
<!--end of volumenum.element-->]]>

<!ENTITY % volumenum.attlist "INCLUDE">
<![%volumenum.attlist;[
<!ATTLIST volumenum
		%common.attrib;
		%volumenum.role.attrib;
		%local.volumenum.attrib;
>
<!--end of volumenum.attlist-->]]>
<!--end of volumenum.module-->]]>

<!-- .................................. -->

<!--end of docinfo.content.module-->]]>

<!-- ...................................................................... -->
<!-- Inline, link, and ubiquitous elements ................................ -->

<!-- Technical and computer terms ......................................... -->

<!ENTITY % accel.module "INCLUDE">
<![%accel.module;[
<!ENTITY % local.accel.attrib "">
<!ENTITY % accel.role.attrib "%role.attrib;">

<!ENTITY % accel.element "INCLUDE">
<![%accel.element;[
<!--doc:A graphical user interface (GUI) keyboard shortcut.
An accelerator is usually a letter used with a meta key (such as control or alt) to activate some element of a GUI without using the mouse to point and click at it.
Category: User Interface-->
<!ELEMENT accel %ho; (%smallcptr.char.mix;)*>
<!--end of accel.element-->]]>

<!ENTITY % accel.attlist "INCLUDE">
<![%accel.attlist;[
<!ATTLIST accel
		%common.attrib;
		%accel.role.attrib;
		%local.accel.attrib;
>
<!--end of accel.attlist-->]]>
<!--end of accel.module-->]]>

<!ENTITY % action.module "INCLUDE">
<![%action.module;[
<!ENTITY % local.action.attrib "">
<!ENTITY % action.role.attrib "%role.attrib;">

<!ENTITY % action.element "INCLUDE">
<![%action.element;[
<!--doc:A response to a user event.
Actions are usually associated with GUIs. An event might be movement or clicking of the mouse, a change in focus, or any number of other occurrences.-->
<!ELEMENT action %ho; (%cptr.char.mix;)*>
<!--end of action.element-->]]>

<!ENTITY % action.attlist "INCLUDE">
<![%action.attlist;[
<!ATTLIST action
		%moreinfo.attrib;
		%common.attrib;
		%action.role.attrib;
		%local.action.attrib;
>
<!--end of action.attlist-->]]>
<!--end of action.module-->]]>

<!ENTITY % application.module "INCLUDE">
<![%application.module;[
<!ENTITY % local.application.attrib "">
<!ENTITY % application.role.attrib "%role.attrib;">

<!ENTITY % application.element "INCLUDE">
<![%application.element;[
<!--doc:The name of a software program.
The appellation “application” is usually reserved for larger software packages—WordPerfect, for example, but not grep. In some domains, Application may also apply to a piece of hardware.
Category: things-->
<!ELEMENT application %ho; (%para.char.mix;)*>
<!--end of application.element-->]]>

<!ENTITY % application.attlist "INCLUDE">
<![%application.attlist;[
<!ATTLIST application
		class 		(hardware
				|software)	#IMPLIED
		%moreinfo.attrib;
		%common.attrib;
		%application.role.attrib;
		%local.application.attrib;
>
<!--end of application.attlist-->]]>
<!--end of application.module-->]]>

<!ENTITY % classname.module "INCLUDE">
<![%classname.module;[
<!ENTITY % local.classname.attrib "">
<!ENTITY % classname.role.attrib "%role.attrib;">

<!ENTITY % classname.element "INCLUDE">
<![%classname.element;[
<!--doc:The name of a class, in the object-oriented programming sense.
The ClassName tag is used to identify the name of a class. This is likely to occur only in documentation about object-oriented programming systems, languages, and architectures. DocBook does not contain a complete set of inlines appropriate for describing object-oriented programming environments. (While it has ClassName, for example, it has nothing suitable for methods.) This will be addressed in a future version of DocBook.
Category: source code-->
<!ELEMENT classname %ho; (%smallcptr.char.mix;)*>
<!--end of classname.element-->]]>

<!ENTITY % classname.attlist "INCLUDE">
<![%classname.attlist;[
<!ATTLIST classname
		%common.attrib;
		%classname.role.attrib;
		%local.classname.attrib;
>
<!--end of classname.attlist-->]]>
<!--end of classname.module-->]]>

<!ENTITY % co.module "INCLUDE">
<![%co.module;[
<!ENTITY % local.co.attrib "">
<!-- CO is a callout area of the LineColumn unit type (a single character
     position); the position is directly indicated by the location of CO. -->
<!ENTITY % co.role.attrib "%role.attrib;">

<!ENTITY % co.element "INCLUDE">
<![%co.element;[
<!--doc:The location of a callout embedded in text.
A CO identifies (by its location) a point of reference for a callout. See Callout.
Category: Callouts-->
<!ELEMENT co %ho; EMPTY>
<!--end of co.element-->]]>

<!-- bug number/symbol override or initialization -->
<!-- to any related information -->


<!ENTITY % co.attlist "INCLUDE">
<![%co.attlist;[
<!ATTLIST co
		%label.attrib;
		%linkends.attrib;
		%idreq.common.attrib;
		%co.role.attrib;
		%local.co.attrib;
>
<!--end of co.attlist-->]]>
<!--end of co.module-->]]>

<!ENTITY % coref.module "INCLUDE">
<![%coref.module;[
<!ENTITY % local.coref.attrib "">
<!-- COREF is a reference to a CO -->
<!ENTITY % coref.role.attrib "%role.attrib;">

<!ENTITY % coref.element "INCLUDE">
<![%coref.element;[
<!--doc:A cross reference to a co.
The coref plays a role for callouts that is analogous to the role of footnoteref for footnotes.Use one co and one or morecoref elements when you want to indicate that the same callout should appear in several places.
Category: Callouts-->
<!ELEMENT coref %ho; EMPTY>
<!--end of coref.element-->]]>

<!-- bug number/symbol override or initialization -->
<!-- to any related information -->

<!ENTITY % coref.attlist "INCLUDE">
<![%coref.attlist;[
<!ATTLIST coref
		%label.attrib;
		%linkendreq.attrib;
		%common.attrib;
		%coref.role.attrib;
		%local.coref.attrib;
>
<!--end of coref.attlist-->]]>
<!--end of coref.module-->]]>

<!ENTITY % command.module "INCLUDE">
<![%command.module;[
<!ENTITY % local.command.attrib "">
<!ENTITY % command.role.attrib "%role.attrib;">

<!ENTITY % command.element "INCLUDE">
<![%command.element;[
<!--doc:The name of an executable program or other software command.
This element holds the name of an executable program or the text of a command that a user enters to execute a program. Command is an integral part of the CmdSynopsis environment as well as being a common inline.
Category: literals-->
<!ELEMENT command %ho; (%cptr.char.mix;)*>
<!--end of command.element-->]]>

<!ENTITY % command.attlist "INCLUDE">
<![%command.attlist;[
<!ATTLIST command
		%moreinfo.attrib;
		%common.attrib;
		%command.role.attrib;
		%local.command.attrib;
>
<!--end of command.attlist-->]]>
<!--end of command.module-->]]>

<!ENTITY % computeroutput.module "INCLUDE">
<![%computeroutput.module;[
<!ENTITY % local.computeroutput.attrib "">
<!ENTITY % computeroutput.role.attrib "%role.attrib;">

<!ENTITY % computeroutput.element "INCLUDE">
<![%computeroutput.element;[
<!--doc:Data, generally text, displayed or presented by a computer.
ComputerOutput identifies lines of text generated by a computer program (messages, results, or other output).Note that ComputerOutput is not a verbatim environment, but an inline.
Category: technical markup-->
<!ELEMENT computeroutput %ho; (%cptr.char.mix;|co)*>
<!--end of computeroutput.element-->]]>

<!ENTITY % computeroutput.attlist "INCLUDE">
<![%computeroutput.attlist;[
<!ATTLIST computeroutput
		%moreinfo.attrib;
		%common.attrib;
		%computeroutput.role.attrib;
		%local.computeroutput.attrib;
>
<!--end of computeroutput.attlist-->]]>
<!--end of computeroutput.module-->]]>

<!ENTITY % database.module "INCLUDE">
<![%database.module;[
<!ENTITY % local.database.attrib "">
<!ENTITY % database.role.attrib "%role.attrib;">

<!ENTITY % database.element "INCLUDE">
<![%database.element;[
<!--doc:The name of a database, or part of a database.
Category: things-->
<!ELEMENT database %ho; (%cptr.char.mix;)*>
<!--end of database.element-->]]>

<!-- Class: Type of database the element names; no default -->


<!ENTITY % database.attlist "INCLUDE">
<![%database.attlist;[
<!ATTLIST database
		class 		(name
				|table
				|field
				|key1
				|key2
				|record
                                |index
                                |view
                                |primarykey
                                |secondarykey
                                |foreignkey
                                |altkey
                                |procedure
                                |datatype
                                |constraint
                                |rule
                                |user
                                |group)	#IMPLIED
		%moreinfo.attrib;
		%common.attrib;
		%database.role.attrib;
		%local.database.attrib;
>
<!--end of database.attlist-->]]>
<!--end of database.module-->]]>

<!ENTITY % email.module "INCLUDE">
<![%email.module;[
<!ENTITY % local.email.attrib "">
<!ENTITY % email.role.attrib "%role.attrib;">

<!ENTITY % email.element "INCLUDE">
<![%email.element;[
<!--doc:Inline markup identifying an email address.
Category: Addresses-->
<!ELEMENT email %ho; (%docinfo.char.mix;)*>
<!--end of email.element-->]]>

<!ENTITY % email.attlist "INCLUDE">
<![%email.attlist;[
<!ATTLIST email
		%common.attrib;
		%email.role.attrib;
		%local.email.attrib;
>
<!--end of email.attlist-->]]>
<!--end of email.module-->]]>

<!ENTITY % envar.module "INCLUDE">
<![%envar.module;[
<!ENTITY % local.envar.attrib "">
<!ENTITY % envar.role.attrib "%role.attrib;">

<!ENTITY % envar.element "INCLUDE">
<![%envar.element;[
<!--doc:A software environment variable.
EnVar is an environment variable used most often for the UNIX,DOS, or Windows environments.
Category: Operating System-->
<!ELEMENT envar %ho; (%smallcptr.char.mix;)*>
<!--end of envar.element-->]]>

<!ENTITY % envar.attlist "INCLUDE">
<![%envar.attlist;[
<!ATTLIST envar
		%common.attrib;
		%envar.role.attrib;
		%local.envar.attrib;
>
<!--end of envar.attlist-->]]>
<!--end of envar.module-->]]>


<!ENTITY % errorcode.module "INCLUDE">
<![%errorcode.module;[
<!ENTITY % local.errorcode.attrib "">
<!ENTITY % errorcode.role.attrib "%role.attrib;">

<!ENTITY % errorcode.element "INCLUDE">
<![%errorcode.element;[
<!--doc:An error code. Error codes are often numeric, but in some environments they may be symbolic constants.DocBook provides four elements for identifying the parts of an error message:ErrorCode, for the alphanumeric error code (e.g., -2);ErrorName, for the symbolic name of the error (e.g., ENOENT);ErrorText, for the text of the error message (e.g., file not found); andErrorType, for the error type (e.g., recoverable).
Category: Error Messages-->
<!ELEMENT errorcode %ho; (%smallcptr.char.mix;)*>
<!--end of errorcode.element-->]]>

<!ENTITY % errorcode.attlist "INCLUDE">
<![%errorcode.attlist;[
<!ATTLIST errorcode
		%moreinfo.attrib;
		%common.attrib;
		%errorcode.role.attrib;
		%local.errorcode.attrib;
>
<!--end of errorcode.attlist-->]]>
<!--end of errorcode.module-->]]>

<!ENTITY % errorname.module "INCLUDE">
<![%errorname.module;[
<!ENTITY % local.errorname.attrib "">
<!ENTITY % errorname.role.attrib "%role.attrib;">

<!ENTITY % errorname.element "INCLUDE">
<![%errorname.element;[
<!--doc:An error name.
ErrorName holds the symbolic name of an error.DocBook provides four elements for identifying the parts of an error message:ErrorCode, for the alphanumeric error code (e.g., -2);ErrorName, for the symbolic name of the error (e.g., ENOENT);ErrorText, for the text of the error message (e.g., file not found); and ErrorType, for the error type (e.g., recoverable).Prior to DocBook V4.2, the ErrorName element was the recommended element for error messages. However, this left no element for symbolic names, so the ErrorText element was added and the semantics of the error elements adjusted slightly.
Category: Error Messages-->
<!ELEMENT errorname %ho; (%smallcptr.char.mix;)*>
<!--end of errorname.element-->]]>

<!ENTITY % errorname.attlist "INCLUDE">
<![%errorname.attlist;[
<!ATTLIST errorname
		%common.attrib;
		%errorname.role.attrib;
		%local.errorname.attrib;
>
<!--end of errorname.attlist-->]]>
<!--end of errorname.module-->]]>

<!ENTITY % errortext.module "INCLUDE">
<![%errortext.module;[
<!ENTITY % local.errortext.attrib "">
<!ENTITY % errortext.role.attrib "%role.attrib;">

<!ENTITY % errortext.element "INCLUDE">
<![%errortext.element;[
<!--doc:ErrorText holds the text of an error message.DocBook provides four elements for identifying the parts of an error message:ErrorCode, for the alphanumeric error code (e.g., -2);ErrorName, for the symbolic name of the error (e.g., ENOENT);ErrorText, for the text of the error message (e.g., file not found); and ErrorType, for the error type (e.g., recoverable).Prior to DocBook V4.2, the ErrorName element was the recommended element for error messages. However, this left no element for symbolic names, so the ErrorText element was added and the semantics of the error elements adjusted slightly.
Category: Error Messages-->
<!ELEMENT errortext %ho; (%smallcptr.char.mix;)*>
<!--end of errortext.element-->]]>

<!ENTITY % errortext.attlist "INCLUDE">
<![%errortext.attlist;[
<!ATTLIST errortext
		%common.attrib;
		%errortext.role.attrib;
		%local.errortext.attrib;
>
<!--end of errortext.attlist-->]]>
<!--end of errortext.module-->]]>

<!ENTITY % errortype.module "INCLUDE">
<![%errortype.module;[
<!ENTITY % local.errortype.attrib "">
<!ENTITY % errortype.role.attrib "%role.attrib;">

<!ENTITY % errortype.element "INCLUDE">
<![%errortype.element;[
<!--doc:The classification of an error message.
The ErrorType element identifies a class of error. The exact classifications are naturally going to vary by system, but recoverable and fatal are two possibilities.DocBook provides four elements for identifying the parts of an error message:ErrorCode, for the alphanumeric error code (e.g., -2);ErrorName, for the symbolic name of the error (e.g., ENOENT);ErrorText, for the text of the error message (e.g., file not found); and ErrorType, for the error type (e.g., recoverable).
Category: Error Messages-->
<!ELEMENT errortype %ho; (%smallcptr.char.mix;)*>
<!--end of errortype.element-->]]>

<!ENTITY % errortype.attlist "INCLUDE">
<![%errortype.attlist;[
<!ATTLIST errortype
		%common.attrib;
		%errortype.role.attrib;
		%local.errortype.attrib;
>
<!--end of errortype.attlist-->]]>
<!--end of errortype.module-->]]>

<!ENTITY % filename.module "INCLUDE">
<![%filename.module;[
<!ENTITY % local.filename.attrib "">
<!ENTITY % filename.role.attrib "%role.attrib;">

<!ENTITY % filename.element "INCLUDE">
<![%filename.element;[
<!--doc:A Filename is the name of a file on a local or network disk. It may be a simple name or may include a path or other elements specific to the operating system.
Category: Operating System-->
<!ELEMENT filename %ho; (%cptr.char.mix;)*>
<!--end of filename.element-->]]>

<!-- Class: Type of filename the element names; no default -->
<!-- Path: Search path (possibly system-specific) in which
		file can be found -->


<!ENTITY % filename.attlist "INCLUDE">
<![%filename.attlist;[
<!ATTLIST filename
		class		(headerfile
                                |partition
                                |devicefile
                                |libraryfile
                                |directory
                                |extension
				|symlink)       #IMPLIED
		path		CDATA		#IMPLIED
		%moreinfo.attrib;
		%common.attrib;
		%filename.role.attrib;
		%local.filename.attrib;
>
<!--end of filename.attlist-->]]>
<!--end of filename.module-->]]>

<!ENTITY % function.module "INCLUDE">
<![%function.module;[
<!ENTITY % local.function.attrib "">
<!ENTITY % function.role.attrib "%role.attrib;">

<!ENTITY % function.element "INCLUDE">
<![%function.element;[
<!--doc:The name of a function or subroutine, as in a programming language.
This element marks up the name of a function. To markup the parts of a function definition, see FuncSynopsis.
Category: funcsynopsis-->
<!ELEMENT function %ho; (%cptr.char.mix;)*>
<!--end of function.element-->]]>

<!ENTITY % function.attlist "INCLUDE">
<![%function.attlist;[
<!ATTLIST function
		%moreinfo.attrib;
		%common.attrib;
		%function.role.attrib;
		%local.function.attrib;
>
<!--end of function.attlist-->]]>
<!--end of function.module-->]]>

<!ENTITY % guibutton.module "INCLUDE">
<![%guibutton.module;[
<!ENTITY % local.guibutton.attrib "">
<!ENTITY % guibutton.role.attrib "%role.attrib;">

<!ENTITY % guibutton.element "INCLUDE">
<![%guibutton.element;[
<!--doc:The text on a button in a GUI.
GUIButton identifies the text that appears on a button in a graphical user interface.
Category: User Interface-->
<!ELEMENT guibutton %ho; (%smallcptr.char.mix;|accel)*>
<!--end of guibutton.element-->]]>

<!ENTITY % guibutton.attlist "INCLUDE">
<![%guibutton.attlist;[
<!ATTLIST guibutton
		%moreinfo.attrib;
		%common.attrib;
		%guibutton.role.attrib;
		%local.guibutton.attrib;
>
<!--end of guibutton.attlist-->]]>
<!--end of guibutton.module-->]]>

<!ENTITY % guiicon.module "INCLUDE">
<![%guiicon.module;[
<!ENTITY % local.guiicon.attrib "">
<!ENTITY % guiicon.role.attrib "%role.attrib;">

<!ENTITY % guiicon.element "INCLUDE">
<![%guiicon.element;[
<!--doc:Graphic and/or text appearing as a icon in a GUI.
GUIIcon identifies a graphic or text icon that appears in a graphical user interface.
Category: User Interface-->
<!ELEMENT guiicon %ho; (%smallcptr.char.mix;|accel)*>
<!--end of guiicon.element-->]]>

<!ENTITY % guiicon.attlist "INCLUDE">
<![%guiicon.attlist;[
<!ATTLIST guiicon
		%moreinfo.attrib;
		%common.attrib;
		%guiicon.role.attrib;
		%local.guiicon.attrib;
>
<!--end of guiicon.attlist-->]]>
<!--end of guiicon.module-->]]>

<!ENTITY % guilabel.module "INCLUDE">
<![%guilabel.module;[
<!ENTITY % local.guilabel.attrib "">
<!ENTITY % guilabel.role.attrib "%role.attrib;">

<!ENTITY % guilabel.element "INCLUDE">
<![%guilabel.element;[
<!--doc:The text of a label in a GUI.
GUILabel identifies text that appears as a label in a graphical user interface. What constitutes a label may vary from application to application. In general, any text that appears in a GUI may be considered a label, for example a message in a dialog box or a window title.
Category: User Interface-->
<!ELEMENT guilabel %ho; (%smallcptr.char.mix;|accel)*>
<!--end of guilabel.element-->]]>

<!ENTITY % guilabel.attlist "INCLUDE">
<![%guilabel.attlist;[
<!ATTLIST guilabel
		%moreinfo.attrib;
		%common.attrib;
		%guilabel.role.attrib;
		%local.guilabel.attrib;
>
<!--end of guilabel.attlist-->]]>
<!--end of guilabel.module-->]]>

<!ENTITY % guimenu.module "INCLUDE">
<![%guimenu.module;[
<!ENTITY % local.guimenu.attrib "">
<!ENTITY % guimenu.role.attrib "%role.attrib;">

<!ENTITY % guimenu.element "INCLUDE">
<![%guimenu.element;[
<!--doc:The name of a menu in a GUI.
GUIMenu identifies a menu name in a graphical user interface. In particular, this is distinct from a menu item (GUIMenuItem), which is terminal, and a submenu (GUISubmenu), which occurs as a selection from a menu.
Category: User Interface-->
<!ELEMENT guimenu %ho; (%smallcptr.char.mix;|accel)*>
<!--end of guimenu.element-->]]>

<!ENTITY % guimenu.attlist "INCLUDE">
<![%guimenu.attlist;[
<!ATTLIST guimenu
		%moreinfo.attrib;
		%common.attrib;
		%guimenu.role.attrib;
		%local.guimenu.attrib;
>
<!--end of guimenu.attlist-->]]>
<!--end of guimenu.module-->]]>

<!ENTITY % guimenuitem.module "INCLUDE">
<![%guimenuitem.module;[
<!ENTITY % local.guimenuitem.attrib "">
<!ENTITY % guimenuitem.role.attrib "%role.attrib;">

<!ENTITY % guimenuitem.element "INCLUDE">
<![%guimenuitem.element;[
<!--doc:The name of a terminal menu item in a GUI.
GUIMenuItem identifies a terminal selection from a menu in a graphical user interface. In particular, this is distinct from a menu (GUIMenu) and a submenu (GUISubmenu). The distinction between a GUIMenuItem and a GUISubmenu is simply whether or not the selection is terminal or leads to an additional submenu.
Category: User Interface-->
<!ELEMENT guimenuitem %ho; (%smallcptr.char.mix;|accel)*>
<!--end of guimenuitem.element-->]]>

<!ENTITY % guimenuitem.attlist "INCLUDE">
<![%guimenuitem.attlist;[
<!ATTLIST guimenuitem
		%moreinfo.attrib;
		%common.attrib;
		%guimenuitem.role.attrib;
		%local.guimenuitem.attrib;
>
<!--end of guimenuitem.attlist-->]]>
<!--end of guimenuitem.module-->]]>

<!ENTITY % guisubmenu.module "INCLUDE">
<![%guisubmenu.module;[
<!ENTITY % local.guisubmenu.attrib "">
<!ENTITY % guisubmenu.role.attrib "%role.attrib;">

<!ENTITY % guisubmenu.element "INCLUDE">
<![%guisubmenu.element;[
<!--doc:The name of a submenu in a GUI.
The name of a submenu in a graphical user interface is identified by the GUISubmenu element. A submenu is a menu invoked from another menu that leads either to terminal items (GUIMenuItems) or additional submenus.
Category: User Interface-->
<!ELEMENT guisubmenu %ho; (%smallcptr.char.mix;|accel)*>
<!--end of guisubmenu.element-->]]>

<!ENTITY % guisubmenu.attlist "INCLUDE">
<![%guisubmenu.attlist;[
<!ATTLIST guisubmenu
		%moreinfo.attrib;
		%common.attrib;
		%guisubmenu.role.attrib;
		%local.guisubmenu.attrib;
>
<!--end of guisubmenu.attlist-->]]>
<!--end of guisubmenu.module-->]]>

<!ENTITY % hardware.module "INCLUDE">
<![%hardware.module;[
<!ENTITY % local.hardware.attrib "">
<!ENTITY % hardware.role.attrib "%role.attrib;">

<!ENTITY % hardware.element "INCLUDE">
<![%hardware.element;[
<!--doc:A physical part of a computer system.
Hardware identifies some physical component of a computer system. Even though DocBook provides a broad range of inlines for describing the various software components of a system, it provides relatively few for describing hardware. If you need to identify a number of different hardware components, you may wish to consider extending DocBook, or at least using the Role attribute to further classify Hardware.
Category: things-->
<!ELEMENT hardware %ho; (%cptr.char.mix;)*>
<!--end of hardware.element-->]]>

<!ENTITY % hardware.attlist "INCLUDE">
<![%hardware.attlist;[
<!ATTLIST hardware
		%moreinfo.attrib;
		%common.attrib;
		%hardware.role.attrib;
		%local.hardware.attrib;
>
<!--end of hardware.attlist-->]]>
<!--end of hardware.module-->]]>

<!ENTITY % interface.module "INCLUDE">
<![%interface.module;[
<!ENTITY % local.interface.attrib "">
<!ENTITY % interface.role.attrib "%role.attrib;">

<!ENTITY % interface.element "INCLUDE">
<![%interface.element;[
<!--doc:An element of a GUI.
An Interface identifies some part of a graphical user interface. This element became obsolete in DocBook V3.0 with the introduction ofGUIButton,GUIIcon,GUILabel,GUIMenu,GUIMenuItem, andGUISubMenu.
Category: source code-->
<!ELEMENT interface %ho; (%smallcptr.char.mix;|accel)*>
<!--end of interface.element-->]]>

<!-- Class: Type of the Interface item; no default -->


<!ENTITY % interface.attlist "INCLUDE">
<![%interface.attlist;[
<!ATTLIST interface
		%moreinfo.attrib;
		%common.attrib;
		%interface.role.attrib;
		%local.interface.attrib;
>
<!--end of interface.attlist-->]]>
<!--end of interface.module-->]]>

<!ENTITY % keycap.module "INCLUDE">
<![%keycap.module;[
<!ENTITY % local.keycap.attrib "">
<!ENTITY % keycap.role.attrib "%role.attrib;">

<!ENTITY % keycap.element "INCLUDE">
<![%keycap.element;[
<!--doc:The text printed on a key on a keyboard.
The KeyCap identifies the text printed on a physical key on a computer keyboard. This is distinct from any scan code that it may generate (KeyCode), or any symbolic name (KeySym) that might exist for the key.
Category: User Interface-->
<!ELEMENT keycap %ho; (%cptr.char.mix;)*>
<!--end of keycap.element-->]]>

<!ENTITY % keycap.attlist "INCLUDE">
<![%keycap.attlist;[
<!ATTLIST keycap
		function	(alt
				|control
				|shift
				|meta
				|escape
				|enter
				|tab
				|backspace
				|command
				|option
				|space
				|delete
				|insert
				|up
				|down
				|left
				|right
				|home
				|end
				|pageup
				|pagedown
				|other)		#IMPLIED
		otherfunction	CDATA		#IMPLIED
		%moreinfo.attrib;
		%common.attrib;
		%keycap.role.attrib;
		%local.keycap.attrib;
>
<!--end of keycap.attlist-->]]>
<!--end of keycap.module-->]]>

<!ENTITY % keycode.module "INCLUDE">
<![%keycode.module;[
<!ENTITY % local.keycode.attrib "">
<!ENTITY % keycode.role.attrib "%role.attrib;">

<!ENTITY % keycode.element "INCLUDE">
<![%keycode.element;[
<!--doc:The internal, frequently numeric, identifier for a key on a keyboard.
The KeyCode identifies the numeric value associated with a key on a computer keyboard. This is distinct from any scan code that it may generate (KeyCode), or any symbolic name (KeySym) that might exist for the key.
Category: User Interface-->
<!ELEMENT keycode %ho; (%smallcptr.char.mix;)*>
<!--end of keycode.element-->]]>

<!ENTITY % keycode.attlist "INCLUDE">
<![%keycode.attlist;[
<!ATTLIST keycode
		%common.attrib;
		%keycode.role.attrib;
		%local.keycode.attrib;
>
<!--end of keycode.attlist-->]]>
<!--end of keycode.module-->]]>

<!ENTITY % keycombo.module "INCLUDE">
<![%keycombo.module;[
<!ENTITY % local.keycombo.attrib "">
<!ENTITY % keycombo.role.attrib "%role.attrib;">

<!ENTITY % keycombo.element "INCLUDE">
<![%keycombo.element;[
<!--doc:A combination of input actions.
For actions that require multiple keystrokes, mouse actions, or other physical input selections, the KeyCombo element provides a wrapper for the entire set of events.
Category: User Interface-->
<!ELEMENT keycombo %ho; ((keycap|keycombo|keysym|mousebutton)+)>
<!--end of keycombo.element-->]]>

<!ENTITY % keycombo.attlist "INCLUDE">
<![%keycombo.attlist;[
<!ATTLIST keycombo
		%keyaction.attrib;
		%moreinfo.attrib;
		%common.attrib;
		%keycombo.role.attrib;
		%local.keycombo.attrib;
>
<!--end of keycombo.attlist-->]]>
<!--end of keycombo.module-->]]>

<!ENTITY % keysym.module "INCLUDE">
<![%keysym.module;[
<!ENTITY % local.keysym.attrib "">
<!ENTITY % keysysm.role.attrib "%role.attrib;">

<!ENTITY % keysym.element "INCLUDE">
<![%keysym.element;[
<!--doc:The symbolic name of a key on a keyboard.
The KeySym identifies the symbolic name of a key on a computer keyboard. This is distinct from any scan code that it may generate (KeyCode), or any symbolic name (KeySym) that might exist for the key.
Category: User Interface-->
<!ELEMENT keysym %ho; (%smallcptr.char.mix;)*>
<!--end of keysym.element-->]]>

<!ENTITY % keysym.attlist "INCLUDE">
<![%keysym.attlist;[
<!ATTLIST keysym
		%common.attrib;
		%keysysm.role.attrib;
		%local.keysym.attrib;
>
<!--end of keysym.attlist-->]]>
<!--end of keysym.module-->]]>

<!ENTITY % lineannotation.module "INCLUDE">
<![%lineannotation.module;[
<!ENTITY % local.lineannotation.attrib "">
<!ENTITY % lineannotation.role.attrib "%role.attrib;">

<!ENTITY % lineannotation.element "INCLUDE">
<![%lineannotation.element;[
<!--doc:A comment on a line in a verbatim listing.
A LineAnnotation is an author or editor's comment on a line in one of the verbatim environments. These are annotations added by the documenter, not part of the original listing.
Category: verbatim-->
<!ELEMENT lineannotation %ho; (%para.char.mix;)*>
<!--end of lineannotation.element-->]]>

<!ENTITY % lineannotation.attlist "INCLUDE">
<![%lineannotation.attlist;[
<!ATTLIST lineannotation
		%common.attrib;
		%lineannotation.role.attrib;
		%local.lineannotation.attrib;
>
<!--end of lineannotation.attlist-->]]>
<!--end of lineannotation.module-->]]>

<!ENTITY % literal.module "INCLUDE">
<![%literal.module;[
<!ENTITY % local.literal.attrib "">
<!ENTITY % literal.role.attrib "%role.attrib;">

<!ENTITY % literal.element "INCLUDE">
<![%literal.element;[
<!--doc:Inline text that is some literal value.
A Literal is some specific piece of data, taken literally, from a computer system. It is similar in some ways to UserInput andComputerOutput, but is somewhat more of a general classification. The sorts of things that constitute literals varies by domain.
Category: technical markup-->
<!ELEMENT literal %ho; (%cptr.char.mix;)*>
<!--end of literal.element-->]]>

<!ENTITY % literal.attlist "INCLUDE">
<![%literal.attlist;[
<!ATTLIST literal
		%moreinfo.attrib;
		%common.attrib;
		%literal.role.attrib;
		%local.literal.attrib;
>
<!--end of literal.attlist-->]]>
<!--end of literal.module-->]]>

<!ENTITY % code.module "INCLUDE">
<![%code.module;[
<!ENTITY % local.code.attrib "">
<!ENTITY % code.role.attrib "%role.attrib;">

<!ENTITY % code.element "INCLUDE">
<![%code.element;[
<!--doc:An inline code fragment.
The code element is an inline element for identifying small fragments of programming language code.-->
<!ELEMENT code %ho; (%cptr.char.mix;)*>
<!--end of code.element-->]]>

<!ENTITY % code.attlist "INCLUDE">
<![%code.attlist;[
<!ATTLIST code
		language	CDATA	#IMPLIED
		%common.attrib;
		%code.role.attrib;
		%local.code.attrib;
>
<!--end of code.attlist-->]]>
<!--end of code.module-->]]>

<!ENTITY % constant.module "INCLUDE">
<![ %constant.module; [
<!ENTITY % local.constant.attrib "">
<!ENTITY % constant.role.attrib "%role.attrib;">

<!ENTITY % constant.element "INCLUDE">
<![ %constant.element; [
<!--doc:A programming or system constant.
A Constant identifies a value as immutable. It is most often used to identify system limitations or other defined constants.
Category: technical markup-->
<!ELEMENT constant %ho; (%smallcptr.char.mix;)*>
<!--end of constant.element-->]]>

<!ENTITY % constant.attlist "INCLUDE">
<![ %constant.attlist; [
<!ATTLIST constant
		class	(limit)		#IMPLIED
		%common.attrib;
		%constant.role.attrib;
		%local.constant.attrib;
>
<!--end of constant.attlist-->]]>
<!--end of constant.module-->]]>

<!ENTITY % varname.module "INCLUDE">
<![ %varname.module; [
<!ENTITY % local.varname.attrib "">
<!ENTITY % varname.role.attrib "%role.attrib;">

<!ENTITY % varname.element "INCLUDE">
<![ %varname.element; [
<!--doc:The name of a variable.
A VarName identifies a variable name in a programming or expression language. Variables most often get their values from Literals, Replaceable values, Constants, or Symbols.
Category: technical markup-->
<!ELEMENT varname %ho; (%smallcptr.char.mix;)*>
<!--end of varname.element-->]]>

<!ENTITY % varname.attlist "INCLUDE">
<![ %varname.attlist; [
<!ATTLIST varname
		%common.attrib;
		%varname.role.attrib;
		%local.varname.attrib;
>
<!--end of varname.attlist-->]]>
<!--end of varname.module-->]]>

<!ENTITY % markup.module "INCLUDE">
<![%markup.module;[
<!ENTITY % local.markup.attrib "">
<!ENTITY % markup.role.attrib "%role.attrib;">

<!ENTITY % markup.element "INCLUDE">
<![%markup.element;[
<!--doc:A string of formatting markup in text that is to be represented literally.
Markup contains a string of formatting markup that is to be represented literally in the text. The utility of this element is almost wholly constrained to books about document formatting tools.
Category: technical markup-->
<!ELEMENT markup %ho; (%smallcptr.char.mix;)*>
<!--end of markup.element-->]]>

<!ENTITY % markup.attlist "INCLUDE">
<![%markup.attlist;[
<!ATTLIST markup
		%common.attrib;
		%markup.role.attrib;
		%local.markup.attrib;
>
<!--end of markup.attlist-->]]>
<!--end of markup.module-->]]>

<!ENTITY % medialabel.module "INCLUDE">
<![%medialabel.module;[
<!ENTITY % local.medialabel.attrib "">
<!ENTITY % medialabel.role.attrib "%role.attrib;">

<!ENTITY % medialabel.element "INCLUDE">
<![%medialabel.element;[
<!--doc:A name that identifies the physical medium on which some information resides.
The MediaLabel element identifies the name of a specific piece of physical media, such as a tape or disk label. Usually, a media label is something external, written by hand on the media itself, for example, but it may also refer to digital labels.
Category: things-->
<!ELEMENT medialabel %ho; (%smallcptr.char.mix;)*>
<!--end of medialabel.element-->]]>

<!-- Class: Type of medium named by the element; no default -->


<!ENTITY % medialabel.attlist "INCLUDE">
<![%medialabel.attlist;[
<!ATTLIST medialabel
		class 		(cartridge
				|cdrom
				|disk
				|tape)		#IMPLIED
		%common.attrib;
		%medialabel.role.attrib;
		%local.medialabel.attrib;
>
<!--end of medialabel.attlist-->]]>
<!--end of medialabel.module-->]]>

<!ENTITY % menuchoice.content.module "INCLUDE">
<![%menuchoice.content.module;[
<!ENTITY % menuchoice.module "INCLUDE">
<![%menuchoice.module;[
<!ENTITY % local.menuchoice.attrib "">
<!ENTITY % menuchoice.role.attrib "%role.attrib;">

<!ENTITY % menuchoice.element "INCLUDE">
<![%menuchoice.element;[
<!--doc:A selection or series of selections from a menu.
In applications that present graphical user interfaces, it is often necessary to select an item, or a series of items, from a menu in order to accomplish some action. The MenuChoice element provides a wrapper to contain the complete combination of selections.
Category: User Interface-->
<!ELEMENT menuchoice %ho; (shortcut?, (guibutton|guiicon|guilabel
		|guimenu|guimenuitem|guisubmenu|interface)+)>
<!--end of menuchoice.element-->]]>

<!ENTITY % menuchoice.attlist "INCLUDE">
<![%menuchoice.attlist;[
<!ATTLIST menuchoice
		%moreinfo.attrib;
		%common.attrib;
		%menuchoice.role.attrib;
		%local.menuchoice.attrib;
>
<!--end of menuchoice.attlist-->]]>
<!--end of menuchoice.module-->]]>

<!ENTITY % shortcut.module "INCLUDE">
<![%shortcut.module;[
<!-- See also KeyCombo -->
<!ENTITY % local.shortcut.attrib "">
<!ENTITY % shortcut.role.attrib "%role.attrib;">

<!ENTITY % shortcut.element "INCLUDE">
<![%shortcut.element;[
<!--doc:A key combination for an action that is also accessible through a menu.
A Shortcut contains the key combination that is a shortcut for a MenuChoice. Users that are familiar with the shortcuts can access the functionality of the corresponding menu choice, without navigating through the menu structure to find the right menu item.
Category: User Interface-->
<!ELEMENT shortcut %ho; ((keycap|keycombo|keysym|mousebutton)+)>
<!--end of shortcut.element-->]]>

<!ENTITY % shortcut.attlist "INCLUDE">
<![%shortcut.attlist;[
<!ATTLIST shortcut
		%keyaction.attrib;
		%moreinfo.attrib;
		%common.attrib;
		%shortcut.role.attrib;
		%local.shortcut.attrib;
>
<!--end of shortcut.attlist-->]]>
<!--end of shortcut.module-->]]>
<!--end of menuchoice.content.module-->]]>

<!ENTITY % mousebutton.module "INCLUDE">
<![%mousebutton.module;[
<!ENTITY % local.mousebutton.attrib "">
<!ENTITY % mousebutton.role.attrib "%role.attrib;">

<!ENTITY % mousebutton.element "INCLUDE">
<![%mousebutton.element;[
<!--doc:The MouseButton element identifies the conventional name of a mouse button. Because mouse buttons are not physically labelled, the name is just that, a convention. Adding explicit markup for the naming of mouse buttons allow easier translation from one convention to another and might allow an online system to adapt to right- or left-handed usage.
Category: User Interface-->
<!ELEMENT mousebutton %ho; (%smallcptr.char.mix;)*>
<!--end of mousebutton.element-->]]>

<!ENTITY % mousebutton.attlist "INCLUDE">
<![%mousebutton.attlist;[
<!ATTLIST mousebutton
		%moreinfo.attrib;
		%common.attrib;
		%mousebutton.role.attrib;
		%local.mousebutton.attrib;
>
<!--end of mousebutton.attlist-->]]>
<!--end of mousebutton.module-->]]>

<!ENTITY % msgtext.module "INCLUDE">
<![%msgtext.module;[
<!ENTITY % local.msgtext.attrib "">
<!ENTITY % msgtext.role.attrib "%role.attrib;">

<!ENTITY % msgtext.element "INCLUDE">
<![%msgtext.element;[
<!--doc:The actual text of a message component in a message set.
The MsgText is the actual content of the message in a MsgMain, MsgSub, or MsgRel.-->
<!ELEMENT msgtext %ho; ((%component.mix;)+)>
<!--end of msgtext.element-->]]>

<!ENTITY % msgtext.attlist "INCLUDE">
<![%msgtext.attlist;[
<!ATTLIST msgtext
		%common.attrib;
		%msgtext.role.attrib;
		%local.msgtext.attrib;
>
<!--end of msgtext.attlist-->]]>
<!--end of msgtext.module-->]]>

<!ENTITY % option.module "INCLUDE">
<![%option.module;[
<!ENTITY % local.option.attrib "">
<!ENTITY % option.role.attrib "%role.attrib;">

<!ENTITY % option.element "INCLUDE">
<![%option.element;[
<!--doc:An option for a software command.
Option identifies an argument to a software command or instruction. Options may or may not be required. The optional element can be used to explicitly identify options that are not required.
Category: technical markup-->
<!ELEMENT option %ho; (%cptr.char.mix;)*>
<!--end of option.element-->]]>

<!ENTITY % option.attlist "INCLUDE">
<![%option.attlist;[
<!ATTLIST option
		%common.attrib;
		%option.role.attrib;
		%local.option.attrib;
>
<!--end of option.attlist-->]]>
<!--end of option.module-->]]>

<!ENTITY % optional.module "INCLUDE">
<![%optional.module;[
<!ENTITY % local.optional.attrib "">
<!ENTITY % optional.role.attrib "%role.attrib;">

<!ENTITY % optional.element "INCLUDE">
<![%optional.element;[
<!--doc:Optional information.
The Optional element indicates that a specified argument, option, or other text is optional. The precise meaning of optional varies according to the application or process begin documented.
Category: technical markup-->
<!ELEMENT optional %ho; (%cptr.char.mix;)*>
<!--end of optional.element-->]]>

<!ENTITY % optional.attlist "INCLUDE">
<![%optional.attlist;[
<!ATTLIST optional
		%common.attrib;
		%optional.role.attrib;
		%local.optional.attrib;
>
<!--end of optional.attlist-->]]>
<!--end of optional.module-->]]>

<!ENTITY % parameter.module "INCLUDE">
<![%parameter.module;[
<!ENTITY % local.parameter.attrib "">
<!ENTITY % parameter.role.attrib "%role.attrib;">

<!ENTITY % parameter.element "INCLUDE">
<![%parameter.element;[
<!--doc:A value or a symbolic reference to a value.
A Parameter identifies something passed from one part of a computer system to another. In this regard Parameter is fairly generic, but it may have a more constrained semantic in some contexts (for example in a ParamDef). In a document that describes more than one kind of parameter, for example, parameters to functions and commands, the Class attribute can be used to distinguish between them, if necessary.
Category: technical markup-->
<!ELEMENT parameter %ho; (%cptr.char.mix;)*>
<!--end of parameter.element-->]]>

<!-- Class: Type of the Parameter; no default -->


<!ENTITY % parameter.attlist "INCLUDE">
<![%parameter.attlist;[
<!ATTLIST parameter
		class 		(command
				|function
				|option)	#IMPLIED
		%moreinfo.attrib;
		%common.attrib;
		%parameter.role.attrib;
		%local.parameter.attrib;
>
<!--end of parameter.attlist-->]]>
<!--end of parameter.module-->]]>

<!ENTITY % prompt.module "INCLUDE">
<![%prompt.module;[
<!ENTITY % local.prompt.attrib "">
<!ENTITY % prompt.role.attrib "%role.attrib;">

<!ENTITY % prompt.element "INCLUDE">
<![%prompt.element;[
<!--doc:A character or string indicating the start of an input field in a computer display.
A Prompt is a character or character string marking the beginning of an input field. Prompts are generally associated with command-line interfaces and not graphical user interfaces (GUIs). In GUIs, GUILabel is usually more appropriate.
Category: technical markup-->
<!ELEMENT prompt %ho; (%smallcptr.char.mix;|co)*>
<!--end of prompt.element-->]]>

<!ENTITY % prompt.attlist "INCLUDE">
<![%prompt.attlist;[
<!ATTLIST prompt
		%moreinfo.attrib;
		%common.attrib;
		%prompt.role.attrib;
		%local.prompt.attrib;
>
<!--end of prompt.attlist-->]]>
<!--end of prompt.module-->]]>

<!ENTITY % property.module "INCLUDE">
<![%property.module;[
<!ENTITY % local.property.attrib "">
<!ENTITY % property.role.attrib "%role.attrib;">

<!ENTITY % property.element "INCLUDE">
<![%property.element;[
<!--doc:A unit of data associated with some part of a computer system.
The notion of a Property is very domain-dependent in computer documentation. Some object-oriented systems speak of properties; the components from which GUIs are constructed have properties; and one can speak of properties in very general terms; the properties of a relational database. You might use Property for any of these in your documentation.
Category: source code-->
<!ELEMENT property %ho; (%cptr.char.mix;)*>
<!--end of property.element-->]]>

<!ENTITY % property.attlist "INCLUDE">
<![%property.attlist;[
<!ATTLIST property
		%moreinfo.attrib;
		%common.attrib;
		%property.role.attrib;
		%local.property.attrib;
>
<!--end of property.attlist-->]]>
<!--end of property.module-->]]>

<!ENTITY % replaceable.module "INCLUDE">
<![%replaceable.module;[
<!ENTITY % local.replaceable.attrib "">
<!ENTITY % replaceable.role.attrib "%role.attrib;">

<!ENTITY % replaceable.element "INCLUDE">
<![%replaceable.element;[
<!--doc:Content that may or must be replaced by the user.
Replaceable is used to mark text that describes what a user is supposed to enter, but not the actual text that they are supposed to enter. It is used to identify a class of object in the document, in which the user is expected to replace the text that identifies the class with some specific instance of that class. A canonical example is <replaceable>filename</replaceable> in which the user is expected to provide the name of some specific file to replace the text filename.
Category: technical markup-->
<!ELEMENT replaceable %ho; (#PCDATA
		| %link.char.class;
		| optional
		| %base.char.class;
		| %other.char.class;
		| inlinegraphic
                | inlinemediaobject
		| co)*>
<!--end of replaceable.element-->]]>

<!-- Class: Type of information the element represents; no
		default -->


<!ENTITY % replaceable.attlist "INCLUDE">
<![%replaceable.attlist;[
<!ATTLIST replaceable
		class		(command
				|function
				|option
				|parameter)	#IMPLIED
		%common.attrib;
		%replaceable.role.attrib;
		%local.replaceable.attrib;
>
<!--end of replaceable.attlist-->]]>
<!--end of replaceable.module-->]]>

<!ENTITY % returnvalue.module "INCLUDE">
<![%returnvalue.module;[
<!ENTITY % local.returnvalue.attrib "">
<!ENTITY % returnvalue.role.attrib "%role.attrib;">

<!ENTITY % returnvalue.element "INCLUDE">
<![%returnvalue.element;[
<!--doc:ReturnValue identifies the value returned by a function or command.
Category: funcsynopsis-->
<!ELEMENT returnvalue %ho; (%smallcptr.char.mix;)*>
<!--end of returnvalue.element-->]]>

<!ENTITY % returnvalue.attlist "INCLUDE">
<![%returnvalue.attlist;[
<!ATTLIST returnvalue
		%common.attrib;
		%returnvalue.role.attrib;
		%local.returnvalue.attrib;
>
<!--end of returnvalue.attlist-->]]>
<!--end of returnvalue.module-->]]>

<!ENTITY % sgmltag.module "INCLUDE">
<![%sgmltag.module;[
<!ENTITY % local.sgmltag.attrib "">
<!ENTITY % sgmltag.role.attrib "%role.attrib;">

<!ENTITY % sgmltag.element "INCLUDE">
<![%sgmltag.element;[
<!--doc:A component of SGML markup.
An SGML Tag identifies an SGML markup construct. The utility of this element is almost wholly constrained to books about SGML. SGML Tag is sufficient for most XML constructs, which are identical to the corresponding SGML constructs, it but does not have any provisions for handling the special features of XML markup. A future version of DocBook will address this issue, probably by adding new Class values. In the meantime, you may get by assigning a Role attribute for XML.
Category: technical markup-->
<!ELEMENT sgmltag %ho; (%smallcptr.char.mix;)*>
<!--end of sgmltag.element-->]]>

<!-- Class: Type of SGML construct the element names; no default -->


<!ENTITY % sgmltag.attlist "INCLUDE">
<![%sgmltag.attlist;[
<!ATTLIST sgmltag
		class 		(attribute
				|attvalue
				|element
				|endtag
                                |emptytag
				|genentity
				|numcharref
				|paramentity
				|pi
                                |xmlpi
				|starttag
				|sgmlcomment
                                |prefix
                                |namespace
                                |localname)	#IMPLIED
		namespace	CDATA		#IMPLIED
		%common.attrib;
		%sgmltag.role.attrib;
		%local.sgmltag.attrib;
>
<!--end of sgmltag.attlist-->]]>
<!--end of sgmltag.module-->]]>

<!ENTITY % structfield.module "INCLUDE">
<![%structfield.module;[
<!ENTITY % local.structfield.attrib "">
<!ENTITY % structfield.role.attrib "%role.attrib;">

<!ENTITY % structfield.element "INCLUDE">
<![%structfield.element;[
<!--doc:A field in a structure (in the programming language sense).
A StructField is a wrapper for the name of a field in a struct (a syntactic element of the C programming language) or a field in an equivalent construct in another programming language.
Category: source code-->
<!ELEMENT structfield %ho; (%smallcptr.char.mix;)*>
<!--end of structfield.element-->]]>

<!ENTITY % structfield.attlist "INCLUDE">
<![%structfield.attlist;[
<!ATTLIST structfield
		%common.attrib;
		%structfield.role.attrib;
		%local.structfield.attrib;
>
<!--end of structfield.attlist-->]]>
<!--end of structfield.module-->]]>

<!ENTITY % structname.module "INCLUDE">
<![%structname.module;[
<!ENTITY % local.structname.attrib "">
<!ENTITY % structname.role.attrib "%role.attrib;">

<!ENTITY % structname.element "INCLUDE">
<![%structname.element;[
<!--doc:The name of a structure (in the programming language sense).
StructName is an inline wrapper for the name of a struct (a syntactic element of the C programming language) or an equivalent construct in another programming language.
Category: source code-->
<!ELEMENT structname %ho; (%smallcptr.char.mix;)*>
<!--end of structname.element-->]]>

<!ENTITY % structname.attlist "INCLUDE">
<![%structname.attlist;[
<!ATTLIST structname
		%common.attrib;
		%structname.role.attrib;
		%local.structname.attrib;
>
<!--end of structname.attlist-->]]>
<!--end of structname.module-->]]>

<!ENTITY % symbol.module "INCLUDE">
<![%symbol.module;[
<!ENTITY % local.symbol.attrib "">
<!ENTITY % symbol.role.attrib "%role.attrib;">

<!ENTITY % symbol.element "INCLUDE">
<![%symbol.element;[
<!--doc:A name that is replaced by a value before processing.
A Symbol is a name that represents a value. It should be used in contexts in which the name will actually be replaced by a value before processing. The canonical example is a #defined symbol in a C program where the C preprocessor replaces every occurrence of the symbol with its value before compilation begins. The Limit value of the Class attribute identifies those symbols that represent system limitations (for example, the number of characters allowed in a path name or the largest possible positive integer). DocBook V3.1 introduced the Constant element, which may be more suitable for some of these symbols.
Category: source code-->
<!ELEMENT symbol %ho; (%smallcptr.char.mix;)*>
<!--end of symbol.element-->]]>

<!-- Class: Type of symbol; no default -->


<!ENTITY % symbol.attlist "INCLUDE">
<![%symbol.attlist;[
<!ATTLIST symbol
		class		(limit)		#IMPLIED
		%common.attrib;
		%symbol.role.attrib;
		%local.symbol.attrib;
>
<!--end of symbol.attlist-->]]>
<!--end of symbol.module-->]]>

<!ENTITY % systemitem.module "INCLUDE">
<![%systemitem.module;[
<!ENTITY % local.systemitem.attrib "">
<!ENTITY % systemitem.role.attrib "%role.attrib;">

<!ENTITY % systemitem.element "INCLUDE">
<![%systemitem.element;[
<!--doc:A system-related item or term.
A SystemItem identifies any system-related item or term. The Class attribute defines a number of common system-related terms. Many inline elements in DocBook are, in fact, system-related. Some of the objects identified by the Class attribute on SystemItem may eventually migrate out to be inline elements of their own accord…and vice versa.
Category: Operating System-->
<!ELEMENT systemitem %ho; (%cptr.char.mix; | acronym | co)*>
<!--end of systemitem.element-->]]>

<!-- Class: Type of system item the element names; no default -->

<!ENTITY % systemitem.attlist "INCLUDE">
<![%systemitem.attlist;[
<!ATTLIST systemitem
		class	(constant
			|event
			|eventhandler
			|domainname
			|fqdomainname
			|ipaddress
			|netmask
			|etheraddress
			|groupname
			|library
			|macro
			|osname
			|filesystem
			|resource
			|systemname
			|username
			|newsgroup
                        |process
                        |service
                        |server
                        |daemon)	#IMPLIED
		%moreinfo.attrib;
		%common.attrib;
		%systemitem.role.attrib;
		%local.systemitem.attrib;
>
<!--end of systemitem.attlist-->]]>
<!--end of systemitem.module-->]]>

<!ENTITY % uri.module "INCLUDE">
<![%uri.module;[
<!ENTITY % local.uri.attrib "">
<!ENTITY % uri.role.attrib "%role.attrib;">

<!ENTITY % uri.element "INCLUDE">
<![%uri.element;[
<!--doc:The URI element identifies a Uniform Resource Identifier (URI) in content.-->
<!ELEMENT uri %ho; (%smallcptr.char.mix;)*>
<!--end of uri.element-->]]>

<!-- Type: Type of URI; no default -->

<!ENTITY % uri.attlist "INCLUDE">
<![%uri.attlist;[
<!ATTLIST uri
		type	CDATA	#IMPLIED
		%common.attrib;
		%uri.role.attrib;
		%local.uri.attrib;
>
<!--end of uri.attlist-->]]>
<!--end of uri.module-->]]>

<!ENTITY % token.module "INCLUDE">
<![%token.module;[
<!ENTITY % local.token.attrib "">
<!ENTITY % token.role.attrib "%role.attrib;">

<!ENTITY % token.element "INCLUDE">
<![%token.element;[
<!--doc:A Token identifies a unit of information. Usually,tokens are the result of some processing pass that has performed lexical analysis and divided a data set into the smallest units of information used for subsequent processing. Exactly what constitutes a token varies by context.
Category: source code-->
<!ELEMENT token %ho; (%smallcptr.char.mix;)*>
<!--end of token.element-->]]>

<!ENTITY % token.attlist "INCLUDE">
<![%token.attlist;[
<!ATTLIST token
		%common.attrib;
		%token.role.attrib;
		%local.token.attrib;
>
<!--end of token.attlist-->]]>
<!--end of token.module-->]]>

<!ENTITY % type.module "INCLUDE">
<![%type.module;[
<!ENTITY % local.type.attrib "">
<!ENTITY % type.role.attrib "%role.attrib;">

<!ENTITY % type.element "INCLUDE">
<![%type.element;[
<!--doc:The classification of a value.
In general usage, Type identifies one member of a class of values. In documenting computer programs, it identifies specifically a type, as might be declared with typedef in the C programming language.
Category: source code-->
<!ELEMENT type %ho; (%smallcptr.char.mix;)*>
<!--end of type.element-->]]>

<!ENTITY % type.attlist "INCLUDE">
<![%type.attlist;[
<!ATTLIST type
		%common.attrib;
		%type.role.attrib;
		%local.type.attrib;
>
<!--end of type.attlist-->]]>
<!--end of type.module-->]]>

<!ENTITY % userinput.module "INCLUDE">
<![%userinput.module;[
<!ENTITY % local.userinput.attrib "">
<!ENTITY % userinput.role.attrib "%role.attrib;">

<!ENTITY % userinput.element "INCLUDE">
<![%userinput.element;[
<!--doc:Data entered by the user.
The UserInput element identifies words or phrases that the user is expected to provide as input to a computer program.Note that UserInput is not a verbatim environment, but an inline.
Category: technical markup-->
<!ELEMENT userinput %ho; (%cptr.char.mix;|co)*>
<!--end of userinput.element-->]]>

<!ENTITY % userinput.attlist "INCLUDE">
<![%userinput.attlist;[
<!ATTLIST userinput
		%moreinfo.attrib;
		%common.attrib;
		%userinput.role.attrib;
		%local.userinput.attrib;
>
<!--end of userinput.attlist-->]]>
<!--end of userinput.module-->]]>

<!-- General words and phrases ............................................ -->

<!ENTITY % abbrev.module "INCLUDE">
<![%abbrev.module;[
<!ENTITY % local.abbrev.attrib "">
<!ENTITY % abbrev.role.attrib "%role.attrib;">

<!ENTITY % abbrev.element "INCLUDE">
<![%abbrev.element;[
<!--doc:An abbreviation, especially one followed by a period.
Category: Traditional Publishing Inlines-->
<!ELEMENT abbrev %ho; (%word.char.mix;)*>
<!--end of abbrev.element-->]]>

<!ENTITY % abbrev.attlist "INCLUDE">
<![%abbrev.attlist;[
<!ATTLIST abbrev
		%common.attrib;
		%abbrev.role.attrib;
		%local.abbrev.attrib;
>
<!--end of abbrev.attlist-->]]>
<!--end of abbrev.module-->]]>

<!ENTITY % acronym.module "INCLUDE">
<![%acronym.module;[
<!ENTITY % local.acronym.attrib "">
<!ENTITY % acronym.role.attrib "%role.attrib;">

<!ENTITY % acronym.element "INCLUDE">
<![%acronym.element;[
<!--doc:An often pronounceable word made from the initial (or selected) letters of a name or phrase.
A pronounceable contraction of initials. An acronym is often printed in all capitals or small capitals, although this is sometimes incorrect (consider dpi or bps).
Category: Traditional Publishing Inlines-->
<!ELEMENT acronym %ho; (%word.char.mix;)*
		%acronym.exclusion;>
<!--end of acronym.element-->]]>

<!ENTITY % acronym.attlist "INCLUDE">
<![%acronym.attlist;[
<!ATTLIST acronym
		%common.attrib;
		%acronym.role.attrib;
		%local.acronym.attrib;
>
<!--end of acronym.attlist-->]]>
<!--end of acronym.module-->]]>

<!ENTITY % citation.module "INCLUDE">
<![%citation.module;[
<!ENTITY % local.citation.attrib "">
<!ENTITY % citation.role.attrib "%role.attrib;">

<!ENTITY % citation.element "INCLUDE">
<![%citation.element;[
<!--doc:An inline bibliographic reference to another published work.
The content of a Citation is assumed to be a reference string, perhaps identical to an abbreviation in an entry in a Bibliography.
Category: Cross References 1-->
<!ELEMENT citation %ho; (%para.char.mix;)*>
<!--end of citation.element-->]]>

<!ENTITY % citation.attlist "INCLUDE">
<![%citation.attlist;[
<!ATTLIST citation
		%common.attrib;
		%citation.role.attrib;
		%local.citation.attrib;
>
<!--end of citation.attlist-->]]>
<!--end of citation.module-->]]>

<!ENTITY % citerefentry.module "INCLUDE">
<![%citerefentry.module;[
<!ENTITY % local.citerefentry.attrib "">
<!ENTITY % citerefentry.role.attrib "%role.attrib;">

<!ENTITY % citerefentry.element "INCLUDE">
<![%citerefentry.element;[
<!--doc:A citation to a reference page.
This element is a citation to a RefEntry. It must include a RefEntryTitle that should exactly match the title of aRefEntry.
Category: Cross References 1-->
<!ELEMENT citerefentry %ho; (refentrytitle, manvolnum?)>
<!--end of citerefentry.element-->]]>

<!ENTITY % citerefentry.attlist "INCLUDE">
<![%citerefentry.attlist;[
<!ATTLIST citerefentry
		%common.attrib;
		%citerefentry.role.attrib;
		%local.citerefentry.attrib;
>
<!--end of citerefentry.attlist-->]]>
<!--end of citerefentry.module-->]]>

<!ENTITY % refentrytitle.module "INCLUDE">
<![%refentrytitle.module;[
<!ENTITY % local.refentrytitle.attrib "">
<!ENTITY % refentrytitle.role.attrib "%role.attrib;">

<!ENTITY % refentrytitle.element "INCLUDE">
<![%refentrytitle.element;[
<!--doc:A RefEntryTitle is the title of a reference page. It is frequently the same as the firstRefName or the RefDescriptor, although it may also be a longer, more general title.
Category: refentry names-->
<!ELEMENT refentrytitle %ho; (%para.char.mix;)*>
<!--end of refentrytitle.element-->]]>

<!ENTITY % refentrytitle.attlist "INCLUDE">
<![%refentrytitle.attlist;[
<!ATTLIST refentrytitle
		%common.attrib;
		%refentrytitle.role.attrib;
		%local.refentrytitle.attrib;
>
<!--end of refentrytitle.attlist-->]]>
<!--end of refentrytitle.module-->]]>

<!ENTITY % manvolnum.module "INCLUDE">
<![%manvolnum.module;[
<!ENTITY % local.manvolnum.attrib "">
<!ENTITY % namvolnum.role.attrib "%role.attrib;">

<!ENTITY % manvolnum.element "INCLUDE">
<![%manvolnum.element;[
<!--doc:A reference volume number.
In a DocBook reference page, the ManVolNum holds the number of the volume in which the RefEntry belongs. The notion of a volume number is historical. UNIX manual pages (man pages), for which RefEntry was devised, were typically stored in three ring binders. Each bound manual was a volume in a set and contained information about a particular class of things. For example, volume 1 was for user commands, and volume 8 was for administrator commands. Volume numbers need not be strictly numerical; volume frequently held manual pages for local additions to the system, and the X Window System manual pages had an x in the volume number: for example, 1x.-->
<!ELEMENT manvolnum %ho; (%word.char.mix;)*>
<!--end of manvolnum.element-->]]>

<!ENTITY % manvolnum.attlist "INCLUDE">
<![%manvolnum.attlist;[
<!ATTLIST manvolnum
		%common.attrib;
		%namvolnum.role.attrib;
		%local.manvolnum.attrib;
>
<!--end of manvolnum.attlist-->]]>
<!--end of manvolnum.module-->]]>

<!ENTITY % citetitle.module "INCLUDE">
<![%citetitle.module;[
<!ENTITY % local.citetitle.attrib "">
<!ENTITY % citetitle.role.attrib "%role.attrib;">

<!ENTITY % citetitle.element "INCLUDE">
<![%citetitle.element;[
<!--doc:CiteTitle provides inline markup for the title of a cited work.
Category: Cross References 1-->
<!ELEMENT citetitle %ho; (%para.char.mix;)*>
<!--end of citetitle.element-->]]>

<!-- Pubwork: Genre of published work cited; no default -->


<!ENTITY % citetitle.attlist "INCLUDE">
<![%citetitle.attlist;[
<!ATTLIST citetitle
		pubwork		(article
				|book
				|chapter
				|part
				|refentry
				|section
				|journal
				|series
				|set
				|manuscript
				|cdrom
				|dvd
				|wiki
				|gopher
				|bbs
                                |emailmessage
                                |webpage
                                |newsposting)	#IMPLIED
		%common.attrib;
		%citetitle.role.attrib;
		%local.citetitle.attrib;
>
<!--end of citetitle.attlist-->]]>
<!--end of citetitle.module-->]]>

<!ENTITY % emphasis.module "INCLUDE">
<![%emphasis.module;[
<!ENTITY % local.emphasis.attrib "">
<!ENTITY % emphasis.role.attrib "%role.attrib;">

<!ENTITY % emphasis.element "INCLUDE">
<![%emphasis.element;[
<!--doc:Emphasized text.
Emphasis provides a method for indicating that certain text should be stressed in some way.
Category: Traditional Publishing Inlines-->
<!ELEMENT emphasis %ho; (%para.char.mix;)*>
<!--end of emphasis.element-->]]>

<!ENTITY % emphasis.attlist "INCLUDE">
<![%emphasis.attlist;[
<!ATTLIST emphasis
		%common.attrib;
		%emphasis.role.attrib;
		%local.emphasis.attrib;
>
<!--end of emphasis.attlist-->]]>
<!--end of emphasis.module-->]]>

<!ENTITY % foreignphrase.module "INCLUDE">
<![%foreignphrase.module;[
<!ENTITY % local.foreignphrase.attrib "">
<!ENTITY % foreignphrase.role.attrib "%role.attrib;">

<!ENTITY % foreignphrase.element "INCLUDE">
<![%foreignphrase.element;[
<!--doc:A word or phrase in a language other than the primary language of the document.
The ForeignPhrase element can be used to markup the text of a foreign word or phrase.Foreign in this context means that it is a language other than the primary language of the document and is not intended to be pejorative in any way.
Category: Traditional Publishing Inlines-->
<!ELEMENT foreignphrase %ho; (%para.char.mix;)*>
<!--end of foreignphrase.element-->]]>

<!ENTITY % foreignphrase.attlist "INCLUDE">
<![%foreignphrase.attlist;[
<!ATTLIST foreignphrase
		%common.attrib;
		%foreignphrase.role.attrib;
		%local.foreignphrase.attrib;
>
<!--end of foreignphrase.attlist-->]]>
<!--end of foreignphrase.module-->]]>

<!ENTITY % glossterm.module "INCLUDE">
<![%glossterm.module;[
<!ENTITY % local.glossterm.attrib "">
<!ENTITY % glossterm.role.attrib "%role.attrib;">

<!ENTITY % glossterm.element "INCLUDE">
<![%glossterm.element;[
<!--doc:A glossary term.
GlossTerm identifies a term that appears in a Glossary or GlossList. This element occurs in two very different places: it is both an inline, and a structure element of a GlossEntry. As an inline, it identifies a term defined in a glossary, and may point to it. Within a GlossEntry, it identifies the term defined by that particular entry.
Category: Cross References 3-->
<!ELEMENT glossterm %ho; (%para.char.mix;)*
		%glossterm.exclusion;>
<!--end of glossterm.element-->]]>

<!-- to GlossEntry if Glossterm used in text -->
<!-- BaseForm: Provides the form of GlossTerm to be used
		for indexing -->

<!ENTITY % glossterm.attlist "INCLUDE">
<![%glossterm.attlist;[
<!ATTLIST glossterm
		baseform	CDATA		#IMPLIED
		%linkend.attrib;
		%common.attrib;
		%glossterm.role.attrib;
		%local.glossterm.attrib;
>
<!--end of glossterm.attlist-->]]>
<!--end of glossterm.module-->]]>

<!ENTITY % firstterm.module "INCLUDE">
<![%firstterm.module;[
<!ENTITY % local.firstterm.attrib "">
<!ENTITY % firstterm.role.attrib "%role.attrib;">

<!ENTITY % firstterm.element "INCLUDE">
<![%firstterm.element;[
<!--doc:The first occurrence of a term.
This element marks the first occurrence of a word or term in a given context.
Category: Cross References 3-->
<!ELEMENT firstterm %ho; (%para.char.mix;)*
		%glossterm.exclusion;>
<!--end of firstterm.element-->]]>

<!-- to GlossEntry or other explanation -->


<!ENTITY % firstterm.attlist "INCLUDE">
<![%firstterm.attlist;[
<!ATTLIST firstterm
		baseform	CDATA		#IMPLIED
		%linkend.attrib;
		%common.attrib;
		%firstterm.role.attrib;
		%local.firstterm.attrib;
>
<!--end of firstterm.attlist-->]]>
<!--end of firstterm.module-->]]>

<!ENTITY % phrase.module "INCLUDE">
<![%phrase.module;[
<!ENTITY % local.phrase.attrib "">
<!ENTITY % phrase.role.attrib "%role.attrib;">

<!ENTITY % phrase.element "INCLUDE">
<![%phrase.element;[
<!--doc:A span of text.
The Phrase element in DocBook has no specific semantic. It is provided as a wrapper around a selection of words smaller than a paragraph so that it is possible to provide an ID or other attributes for them. For example, if you are making note of changes to a document using one of the effectivity attributes, you might use Phrase to mark up specific sentences with revisions.
Category: Traditional Publishing Inlines-->
<!ELEMENT phrase %ho; (%para.char.mix;)*>
<!--end of phrase.element-->]]>

<!ENTITY % phrase.attlist "INCLUDE">
<![%phrase.attlist;[
<!ATTLIST phrase
		%common.attrib;
		%phrase.role.attrib;
		%local.phrase.attrib;
>
<!--end of phrase.attlist-->]]>
<!--end of phrase.module-->]]>

<!ENTITY % quote.module "INCLUDE">
<![%quote.module;[
<!ENTITY % local.quote.attrib "">
<!ENTITY % quote.role.attrib "%role.attrib;">

<!ENTITY % quote.element "INCLUDE">
<![%quote.element;[
<!--doc:Quote surrounds an inline quotation. Using an element for quotations is frequently more convenient than entering the character entities for the quotation marks by hand, and makes it possible for a presentation system to alter the format of the quotation marks. Block quotations are properly identified as BlockQuotes.
Category: Traditional Publishing Inlines-->
<!ELEMENT quote %ho; (%para.char.mix;)*>
<!--end of quote.element-->]]>

<!ENTITY % quote.attlist "INCLUDE">
<![%quote.attlist;[
<!ATTLIST quote
		%common.attrib;
		%quote.role.attrib;
		%local.quote.attrib;
>
<!--end of quote.attlist-->]]>
<!--end of quote.module-->]]>

<!ENTITY % ssscript.module "INCLUDE">
<![%ssscript.module;[
<!ENTITY % local.ssscript.attrib "">
<!ENTITY % ssscript.role.attrib "%role.attrib;">

<!ENTITY % subscript.element "INCLUDE">
<![%subscript.element;[
<!--doc:A subscript (as in H2O, the molecular formula for water).
Subscript identifies text that is to be displayed as a subscript when rendered.
Category: Mathematics-->
<!ELEMENT subscript %ho; (#PCDATA
		| %link.char.class;
		| emphasis
		| replaceable
		| symbol
		| inlinegraphic
                | inlinemediaobject
		| %base.char.class;
		| %other.char.class;)*
		%ubiq.exclusion;>
<!--end of subscript.element-->]]>

<!ENTITY % subscript.attlist "INCLUDE">
<![%subscript.attlist;[
<!ATTLIST subscript
		%common.attrib;
		%ssscript.role.attrib;
		%local.ssscript.attrib;
>
<!--end of subscript.attlist-->]]>

<!ENTITY % superscript.element "INCLUDE">
<![%superscript.element;[
<!--doc:A superscript (as in x2, the mathematical notation for x multiplied by itself).
Superscript identifies text that is to be displayed as a superscript when rendered.
Category: Mathematics-->
<!ELEMENT superscript %ho; (#PCDATA
		| %link.char.class;
		| emphasis
		| replaceable
		| symbol
		| inlinegraphic
                | inlinemediaobject
		| %base.char.class;
		| %other.char.class;)*
		%ubiq.exclusion;>
<!--end of superscript.element-->]]>

<!ENTITY % superscript.attlist "INCLUDE">
<![%superscript.attlist;[
<!ATTLIST superscript
		%common.attrib;
		%ssscript.role.attrib;
		%local.ssscript.attrib;
>
<!--end of superscript.attlist-->]]>
<!--end of ssscript.module-->]]>

<!ENTITY % trademark.module "INCLUDE">
<![%trademark.module;[
<!ENTITY % local.trademark.attrib "">
<!ENTITY % trademark.role.attrib "%role.attrib;">

<!ENTITY % trademark.element "INCLUDE">
<![%trademark.element;[
<!--doc:A trademark.
Trademark identifies a legal trademark. One of the values of the Class attribute on Trademark is Copyright. DocBook also has a Copyright element, but it is confined to meta-information. A copyright in running text is best represented as trademark class=copyright.
Category: product names-->
<!ELEMENT trademark %ho; (#PCDATA
		| %link.char.class;
		| %tech.char.class;
		| %base.char.class;
		| %other.char.class;
		| inlinegraphic
                | inlinemediaobject
		| emphasis)*>
<!--end of trademark.element-->]]>

<!-- Class: More precisely identifies the item the element names -->


<!ENTITY % trademark.attlist "INCLUDE">
<![%trademark.attlist;[
<!ATTLIST trademark
		class		(service
				|trade
				|registered
				|copyright)	'trade'
		%common.attrib;
		%trademark.role.attrib;
		%local.trademark.attrib;
>
<!--end of trademark.attlist-->]]>
<!--end of trademark.module-->]]>

<!ENTITY % wordasword.module "INCLUDE">
<![%wordasword.module;[
<!ENTITY % local.wordasword.attrib "">
<!ENTITY % wordasword.role.attrib "%role.attrib;">

<!ENTITY % wordasword.element "INCLUDE">
<![%wordasword.element;[
<!--doc:A word meant specifically as a word and not representing anything else.
A lot of technical documentation contains words that have overloaded meanings. Sometimes it is useful to be able to use a word without invoking its technical meaning. The WordAsWord element identifies a word or phrase that might otherwise be interpreted in some specific way, and asserts that it should be interpreted simply as a word. It is unlikely that the presentation of this element will be able to help readers understand the variation in meaning; good writing will have to achieve that goal. The real value of WordAsWord lies in the fact that full-text searching and indexing tools can use it to avoid false-positives.
Category: Traditional Publishing Inlines-->
<!ELEMENT wordasword %ho; (%word.char.mix;)*>
<!--end of wordasword.element-->]]>

<!ENTITY % wordasword.attlist "INCLUDE">
<![%wordasword.attlist;[
<!ATTLIST wordasword
		%common.attrib;
		%wordasword.role.attrib;
		%local.wordasword.attrib;
>
<!--end of wordasword.attlist-->]]>
<!--end of wordasword.module-->]]>

<!-- Links and cross-references ........................................... -->

<!ENTITY % link.module "INCLUDE">
<![%link.module;[
<!ENTITY % local.link.attrib "">
<!ENTITY % link.role.attrib "%role.attrib;">

<!ENTITY % link.element "INCLUDE">
<![%link.element;[
<!--doc:A hypertext link.
Link is a general purpose hypertext element. Usually, Link surrounds the text that should be made “hot,” (unlike XRef which must generate the text) but the EndTerm attribute can be used to copy text from another element.
Category: Cross References 2-->
<!ELEMENT link %ho; (%para.char.mix;)*
		%links.exclusion;>
<!--end of link.element-->]]>

<!-- Endterm: ID of element containing text that is to be
		fetched from elsewhere in the document to appear as
		the content of this element -->
<!-- to linked-to object -->
<!-- Type: Freely assignable parameter -->


<!ENTITY % link.attlist "INCLUDE">
<![%link.attlist;[
<!ATTLIST link
		endterm		IDREF		#IMPLIED
		xrefstyle	CDATA		#IMPLIED
		type		CDATA		#IMPLIED
		%linkendreq.attrib;
		%common.attrib;
		%link.role.attrib;
		%local.link.attrib;
>
<!--end of link.attlist-->]]>
<!--end of link.module-->]]>

<!ENTITY % olink.module "INCLUDE">
<![%olink.module;[
<!ENTITY % local.olink.attrib "">
<!ENTITY % olink.role.attrib "%role.attrib;">

<!ENTITY % olink.element "INCLUDE">
<![%olink.element;[
<!--doc:A link that addresses its target indirectly, through an entity.
Unlike Link and ULink, the semantics ofOLink are application-specific. OLink provides a mechanism for establishing links across documents, where ID/IDREF linking is not possible and ULink is inappropriate. In general terms, the strategy employed by OLink is to point to the target document via an external general entity, and point into that document in some application-specific way.
Category: Cross References 2-->
<!ELEMENT olink %ho; (%para.char.mix;)*
		%links.exclusion;>
<!--end of olink.element-->]]>

<!-- TargetDocEnt: Name of an entity to be the target of the link -->
<!-- LinkMode: ID of a ModeSpec containing instructions for
		operating on the entity named by TargetDocEnt -->
<!-- LocalInfo: Information that may be passed to ModeSpec -->
<!-- Type: Freely assignable parameter -->


<!ENTITY % olink.attlist "INCLUDE">
<![%olink.attlist;[
<!ATTLIST olink
		targetdocent	ENTITY 		#IMPLIED
		linkmode	IDREF		#IMPLIED
		localinfo 	CDATA		#IMPLIED
		type		CDATA		#IMPLIED
		targetdoc	CDATA		#IMPLIED
		targetptr	CDATA		#IMPLIED
		xrefstyle	CDATA		#IMPLIED
		%common.attrib;
		%olink.role.attrib;
		%local.olink.attrib;
>
<!--end of olink.attlist-->]]>
<!--end of olink.module-->]]>

<!ENTITY % ulink.module "INCLUDE">
<![%ulink.module;[
<!ENTITY % local.ulink.attrib "">
<!ENTITY % ulink.role.attrib "%role.attrib;">

<!ENTITY % ulink.element "INCLUDE">
<![%ulink.element;[
<!--doc:A link that addresses its target by means of a URL (Uniform Resource Locator).
The ULink element forms the equivalent of an HTML anchor (A HREF="...") for cross reference by a Uniform Resource Locator (URL).
Category: Cross References 2-->
<!ELEMENT ulink %ho; (%para.char.mix;)*
		%links.exclusion;>
<!--end of ulink.element-->]]>

<!-- URL: uniform resource locator; the target of the ULink -->
<!-- Type: Freely assignable parameter -->


<!ENTITY % ulink.attlist "INCLUDE">
<![%ulink.attlist;[
<!ATTLIST ulink
		url		CDATA		#REQUIRED
		type		CDATA		#IMPLIED
		xrefstyle	CDATA		#IMPLIED
		%common.attrib;
		%ulink.role.attrib;
		%local.ulink.attrib;
>
<!--end of ulink.attlist-->]]>
<!--end of ulink.module-->]]>

<!ENTITY % footnoteref.module "INCLUDE">
<![%footnoteref.module;[
<!ENTITY % local.footnoteref.attrib "">
<!ENTITY % footnoteref.role.attrib "%role.attrib;">

<!ENTITY % footnoteref.element "INCLUDE">
<![%footnoteref.element;[
<!--doc:A cross reference to a footnote (a footnote mark).
This element forms an IDREF link to a Footnote. It generates the same mark or link as the Footnote to which it points. In technical documentation, FootnoteRef occurs most frequently in tables.-->
<!ELEMENT footnoteref %ho; EMPTY>
<!--end of footnoteref.element-->]]>

<!-- to footnote content supplied elsewhere -->


<!ENTITY % footnoteref.attlist "INCLUDE">
<![%footnoteref.attlist;[
<!ATTLIST footnoteref
		%linkendreq.attrib;		%label.attrib;
		%common.attrib;
		%footnoteref.role.attrib;
		%local.footnoteref.attrib;
>
<!--end of footnoteref.attlist-->]]>
<!--end of footnoteref.module-->]]>

<!ENTITY % xref.module "INCLUDE">
<![%xref.module;[
<!ENTITY % local.xref.attrib "">
<!ENTITY % xref.role.attrib "%role.attrib;">

<!ENTITY % xref.element "INCLUDE">
<![%xref.element;[
<!--doc:A cross reference to another part of the document.
The XRef element forms a cross-reference from the location of the XRef to the element to which it points. Unlike Link and the other cross-referencing elements, XRef is empty. The processing system has to generate appropriate cross-reference text for the reader.
Category: Cross References 2-->
<!ELEMENT xref %ho; EMPTY>
<!--end of xref.element-->]]>

<!-- Endterm: ID of element containing text that is to be
		fetched from elsewhere in the document to appear as
		the content of this element -->
<!-- to linked-to object -->


<!ENTITY % xref.attlist "INCLUDE">
<![%xref.attlist;[
<!ATTLIST xref
		endterm		IDREF		#IMPLIED
		xrefstyle	CDATA		#IMPLIED
		%common.attrib;
		%linkendreq.attrib;
		%xref.role.attrib;
		%local.xref.attrib;
>
<!--end of xref.attlist-->]]>
<!--end of xref.module-->]]>

<!-- Ubiquitous elements .................................................. -->

<!ENTITY % anchor.module "INCLUDE">
<![%anchor.module;[
<!ENTITY % local.anchor.attrib "">
<!ENTITY % anchor.role.attrib "%role.attrib;">

<!ENTITY % anchor.element "INCLUDE">
<![%anchor.element;[
<!--doc:A spot in the document.
An anchor identifies a single spot in the content. This may serve as the target for a cross reference, for example, from a Link. The Anchor element may occur almost anywhere. Anchor has the Role attribute and all of the common attributes except Lang.
Category: Cross References 2-->
<!ELEMENT anchor %ho; EMPTY>
<!--end of anchor.element-->]]>

<!-- required -->
<!-- replaces Lang -->


<!ENTITY % anchor.attlist "INCLUDE">
<![%anchor.attlist;[
<!ATTLIST anchor
		%idreq.attrib;		%pagenum.attrib;		%remap.attrib;
		%xreflabel.attrib;
		%revisionflag.attrib;
		%effectivity.attrib;
		%anchor.role.attrib;
		%local.anchor.attrib;
>
<!--end of anchor.attlist-->]]>
<!--end of anchor.module-->]]>

<!ENTITY % beginpage.module "INCLUDE">
<![%beginpage.module;[
<!ENTITY % local.beginpage.attrib "">
<!ENTITY % beginpage.role.attrib "%role.attrib;">

<!ENTITY % beginpage.element "INCLUDE">
<![%beginpage.element;[
<!--doc:The location of a page break in a print version of the document.
The BeginPage element marks the location of an actual page break in a print version of the document, as opposed to where a page break might appear in a further rendition of the document. This information may be used, for example, to allow support staff using an online system to coordinate with a user referring to a page number in a printed manual.-->
<!ELEMENT beginpage %ho; EMPTY>
<!--end of beginpage.element-->]]>

<!-- PageNum: Number of page that begins at this point -->


<!ENTITY % beginpage.attlist "INCLUDE">
<![%beginpage.attlist;[
<!ATTLIST beginpage
		%pagenum.attrib;
		%common.attrib;
		%beginpage.role.attrib;
		%local.beginpage.attrib;
>
<!--end of beginpage.attlist-->]]>
<!--end of beginpage.module-->]]>

<!-- IndexTerms appear in the text flow for generating or linking an
     index. -->

<!ENTITY % indexterm.content.module "INCLUDE">
<![%indexterm.content.module;[
<!ENTITY % indexterm.module "INCLUDE">
<![%indexterm.module;[
<!ENTITY % local.indexterm.attrib "">
<!ENTITY % indexterm.role.attrib "%role.attrib;">

<!ENTITY % indexterm.element "INCLUDE">
<![%indexterm.element;[
<!--doc:A wrapper for terms to be indexed.
IndexTerms identify text that is to be placed in the index. In the simplest case, the placement of the IndexTerm in the document identifies the location of the term in the text. In other words, the IndexTerm is placed in the flow of the document at the point where the IndexEntry in the Index should point. In other cases, attributes on IndexTerm are used to identify the location of the term in the text. IndexTerms mark either a single point in the document or a range. A single point is marked with an IndexTerm placed in the text at the point of reference. There are two ways to identify a range of text: Place an IndexTerm at the beginning of the range with Class set to StartOfRange and give this term an ID. Place another IndexTerm at the end of the range with StartRef pointing to the ID of the starting IndexTerm. This second IndexTerm must be empty. The advantage of this method is that the range can span unbalanced element boundaries. Place the IndexTerm anywhere you like and point to the element that contains the range of text you wish to index with the Zone attribute on the IndexTerm. Note that Zone is defined asIDREFS so a single IndexTerm can point to multiple ranges. The advantage of this method is that IndexTerms can be collected together or even stored totally outside the flow of the document (in the meta for example).
Category: index terms-->
<!ELEMENT indexterm %ho; (primary?, ((secondary, ((tertiary, (see|seealso+)?)
		| see | seealso+)?) | see | seealso+)?)
			%ubiq.exclusion;>
<!--end of indexterm.element-->]]>

<!-- Scope: Indicates which generated indices the IndexTerm
		should appear in: Global (whole document set), Local (this
		document only), or All (both) -->
<!-- Significance: Whether this IndexTerm is the most pertinent
		of its series (Preferred) or not (Normal, the default) -->
<!-- Class: Indicates type of IndexTerm; default is Singular,
		or EndOfRange if StartRef is supplied; StartOfRange value
		must be supplied explicitly on starts of ranges -->
<!-- StartRef: ID of the IndexTerm that starts the indexing
		range ended by this IndexTerm -->
<!-- Zone: IDs of the elements to which the IndexTerm applies,
		and indicates that the IndexTerm applies to those entire
		elements rather than the point at which the IndexTerm
		occurs -->


<!ENTITY % indexterm.attlist "INCLUDE">
<![%indexterm.attlist;[
<!ATTLIST indexterm
		%pagenum.attrib;
		scope		(all
				|global
				|local)		#IMPLIED
		significance	(preferred
				|normal)	"normal"
		class		(singular
				|startofrange
				|endofrange)	#IMPLIED
		startref	IDREF		#IMPLIED
		zone		IDREFS		#IMPLIED
		type		CDATA		#IMPLIED
		%common.attrib;
		%indexterm.role.attrib;
		%local.indexterm.attrib;
>
<!--end of indexterm.attlist-->]]>
<!--end of indexterm.module-->]]>

<!ENTITY % primsecter.module "INCLUDE">
<![%primsecter.module;[
<!ENTITY % local.primsecter.attrib "">
<!ENTITY % primsecter.role.attrib "%role.attrib;">


<!ENTITY % primary.element "INCLUDE">
<![%primary.element;[
<!--doc:The primary word or phrase under which an index term should be sorted.
In an IndexTerm, Primary identifies the most significant word or words in the entry. All IndexTerms must have a Primary.
Category: index terms-->
<!ELEMENT primary %ho;   (%ndxterm.char.mix;)*>
<!--end of primary.element-->]]>
<!-- SortAs: Alternate sort string for index sorting, e.g.,
		"fourteen" for an element containing "14" -->

<!ENTITY % primary.attlist "INCLUDE">
<![%primary.attlist;[
<!ATTLIST primary
		sortas		CDATA		#IMPLIED
		%common.attrib;
		%primsecter.role.attrib;
		%local.primsecter.attrib;
>
<!--end of primary.attlist-->]]>


<!ENTITY % secondary.element "INCLUDE">
<![%secondary.element;[
<!--doc:A secondary word or phrase in an index term.
Secondary contains a secondary word or phrase in an IndexTerm. The text of a Secondary term is less significant than the Primary term, but more significant than the Tertiary term for sorting and display purposes. In IndexTerms, you can only have one primary, secondary, and tertiary term. If you want to index multiple secondary terms for the same primary, you must repeat the primary in another IndexTerm. You cannot place several Secondarys in the same primary.
Category: index terms-->
<!ELEMENT secondary %ho; (%ndxterm.char.mix;)*>
<!--end of secondary.element-->]]>
<!-- SortAs: Alternate sort string for index sorting, e.g.,
		"fourteen" for an element containing "14" -->

<!ENTITY % secondary.attlist "INCLUDE">
<![%secondary.attlist;[
<!ATTLIST secondary
		sortas		CDATA		#IMPLIED
		%common.attrib;
		%primsecter.role.attrib;
		%local.primsecter.attrib;
>
<!--end of secondary.attlist-->]]>


<!ENTITY % tertiary.element "INCLUDE">
<![%tertiary.element;[
<!--doc:A tertiary word or phrase in an index term.
Tertiary contains a third-level word or phrase in an IndexTerm. The text of a Tertiary term is less significant than the Primary and Secondary terms for sorting and display purposes. DocBook does not define any additional levels. You cannot use IndexTerms to construct indexes with more than three levels without extending the DTD. In IndexTerms, you can only have one primary, secondary, and tertiary term. If you want to index multiple tertiary terms for the same primary and secondary, you must repeat the primary and secondary in another IndexTerm. You cannot place several Tertiarys in the same primary.
Category: index terms-->
<!ELEMENT tertiary %ho;  (%ndxterm.char.mix;)*>
<!--end of tertiary.element-->]]>
<!-- SortAs: Alternate sort string for index sorting, e.g.,
		"fourteen" for an element containing "14" -->

<!ENTITY % tertiary.attlist "INCLUDE">
<![%tertiary.attlist;[
<!ATTLIST tertiary
		sortas		CDATA		#IMPLIED
		%common.attrib;
		%primsecter.role.attrib;
		%local.primsecter.attrib;
>
<!--end of tertiary.attlist-->]]>

<!--end of primsecter.module-->]]>

<!ENTITY % seeseealso.module "INCLUDE">
<![%seeseealso.module;[
<!ENTITY % local.seeseealso.attrib "">
<!ENTITY % seeseealso.role.attrib "%role.attrib;">

<!ENTITY % see.element "INCLUDE">
<![%see.element;[
<!--doc:Part of an index term directing the reader instead to another entry in the index.
The use of See in an IndexTerm indicates that the reader should be directed elsewhere in the index if they attempt to look up this term. The content of See identifies another term in the index which the reader should consult instead of the current term.
Category: index terms-->
<!ELEMENT see %ho; (%ndxterm.char.mix;)*>
<!--end of see.element-->]]>

<!ENTITY % see.attlist "INCLUDE">
<![%see.attlist;[
<!ATTLIST see
		%common.attrib;
		%seeseealso.role.attrib;
		%local.seeseealso.attrib;
>
<!--end of see.attlist-->]]>

<!ENTITY % seealso.element "INCLUDE">
<![%seealso.element;[
<!--doc:Part of an index term directing the reader also to another entry in the index.
The use of SeeAlso in an IndexTerm indicates that the reader should be directed elsewhere in the index for additional information. The content of SeeAlso identifies another term in the index that the reader should consult in addition to the current term.
Category: index terms-->
<!ELEMENT seealso %ho; (%ndxterm.char.mix;)*>
<!--end of seealso.element-->]]>

<!ENTITY % seealso.attlist "INCLUDE">
<![%seealso.attlist;[
<!ATTLIST seealso
		%common.attrib;
		%seeseealso.role.attrib;
		%local.seeseealso.attrib;
>
<!--end of seealso.attlist-->]]>
<!--end of seeseealso.module-->]]>
<!--end of indexterm.content.module-->]]>

<!-- End of DocBook XML information pool module V4.3 ...................... -->
<!-- ...................................................................... -->
