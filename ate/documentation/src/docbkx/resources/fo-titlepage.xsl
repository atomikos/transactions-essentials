<?xml version="1.0" encoding="utf-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exsl="http://exslt.org/common" version="1.0" exclude-result-prefixes="exsl">

<!-- This stylesheet was created by template/titlepage.xsl; do not edit it by hand. -->

<xsl:template name="article.titlepage.recto">
  <xsl:choose>
    <xsl:when test="articleinfo/title">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/title"/>
    </xsl:when>
    <xsl:when test="artheader/title">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/title"/>
    </xsl:when>
    <xsl:when test="info/title">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/title"/>
    </xsl:when>
    <xsl:when test="title">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  <xsl:choose>
    <xsl:when test="articleinfo/subtitle">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/subtitle"/>
    </xsl:when>
    <xsl:when test="artheader/subtitle">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/subtitle"/>
    </xsl:when>
    <xsl:when test="info/subtitle">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/subtitle"/>
    </xsl:when>
    <xsl:when test="subtitle">
      <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/mediaobject"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/mediaobject"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/mediaobject"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/corpauthor"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/corpauthor"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/corpauthor"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/authorgroup"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/authorgroup"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/authorgroup"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/author"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/author"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/author"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/othercredit"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/othercredit"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/othercredit"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/releaseinfo"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/releaseinfo"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/releaseinfo"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/copyright"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/copyright"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/copyright"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/legalnotice"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/legalnotice"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/legalnotice"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/pubdate"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/pubdate"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/pubdate"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/revision"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/revision"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/revision"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/revhistory"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/revhistory"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/revhistory"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="articleinfo/abstract"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="artheader/abstract"/>
  <xsl:apply-templates mode="article.titlepage.recto.auto.mode" select="info/abstract"/>
</xsl:template>

<xsl:template name="article.titlepage.verso">
</xsl:template>

<xsl:template name="article.titlepage.separator">
</xsl:template>

<xsl:template name="article.titlepage.before.recto">
</xsl:template>

<xsl:template name="article.titlepage.before.verso">
</xsl:template>

<xsl:template name="article.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" font-family="{$title.fontset}">
    <xsl:variable name="recto.content">
      <xsl:call-template name="article.titlepage.before.recto"/>
      <xsl:call-template name="article.titlepage.recto"/>
    </xsl:variable>
    <xsl:variable name="recto.elements.count">
      <xsl:choose>
        <xsl:when test="function-available('exsl:node-set')"><xsl:value-of select="count(exsl:node-set($recto.content)/*)"/></xsl:when>
        <xsl:when test="contains(system-property('xsl:vendor'), 'Apache Software Foundation')">
          <!--Xalan quirk--><xsl:value-of select="count(exsl:node-set($recto.content)/*)"/></xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="(normalize-space($recto.content) != '') or ($recto.elements.count &gt; 0)">
      <fo:block start-indent="0pt" text-align="center"><xsl:copy-of select="$recto.content"/></fo:block>
    </xsl:if>
    <xsl:variable name="verso.content">
      <xsl:call-template name="article.titlepage.before.verso"/>
      <xsl:call-template name="article.titlepage.verso"/>
    </xsl:variable>
    <xsl:variable name="verso.elements.count">
      <xsl:choose>
        <xsl:when test="function-available('exsl:node-set')"><xsl:value-of select="count(exsl:node-set($verso.content)/*)"/></xsl:when>
        <xsl:when test="contains(system-property('xsl:vendor'), 'Apache Software Foundation')">
          <!--Xalan quirk--><xsl:value-of select="count(exsl:node-set($verso.content)/*)"/></xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="(normalize-space($verso.content) != '') or ($verso.elements.count &gt; 0)">
      <fo:block><xsl:copy-of select="$verso.content"/></fo:block>
    </xsl:if>
    <xsl:call-template name="article.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template match="*" mode="article.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template match="*" mode="article.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template match="title" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" keep-with-next.within-column="always" font-size="24.8832pt" font-weight="bold">
<xsl:call-template name="component.title">
<xsl:with-param name="node" select="ancestor-or-self::article[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template match="subtitle" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="mediaobject" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="1em" space-after="1em" text-align="center">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="corpauthor" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em" font-size="14.4pt">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="authorgroup" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em" font-size="14.4pt">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="author" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em" font-size="14.4pt">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="othercredit" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="releaseinfo" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="copyright" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="legalnotice" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" text-align="start" margin-left="0.5in" margin-right="0.5in" font-family="{$body.fontset}">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="pubdate" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="revision" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="revhistory" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="abstract" mode="article.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="article.titlepage.recto.style" space-before="0.5em" text-align="start" margin-left="0.5in" margin-right="0.5in" font-family="{$body.fontset}">
<xsl:apply-templates select="." mode="article.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template name="book.titlepage.recto">
  <xsl:choose>
    <xsl:when test="bookinfo/title">
      <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/title"/>
    </xsl:when>
    <xsl:when test="info/title">
      <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="info/title"/>
    </xsl:when>
    <xsl:when test="title">
      <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  <xsl:choose>
    <xsl:when test="bookinfo/subtitle">
      <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/subtitle"/>
    </xsl:when>
    <xsl:when test="info/subtitle">
      <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="info/subtitle"/>
    </xsl:when>
    <xsl:when test="subtitle">
      <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="subtitle"/>
    </xsl:when>
  </xsl:choose>

  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/mediaobject"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="info/mediaobject"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/corpauthor"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="info/corpauthor"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/authorgroup"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="info/authorgroup"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="bookinfo/author"/>
  <xsl:apply-templates mode="book.titlepage.recto.auto.mode" select="info/author"/>
</xsl:template>

<xsl:template name="book.titlepage.verso">
  <xsl:choose>
    <xsl:when test="bookinfo/title">
      <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/title"/>
    </xsl:when>
    <xsl:when test="info/title">
      <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/title"/>
    </xsl:when>
    <xsl:when test="title">
      <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="title"/>
    </xsl:when>
  </xsl:choose>

  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/corpauthor"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/corpauthor"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/authorgroup"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/authorgroup"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/author"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/author"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/othercredit"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/othercredit"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/releaseinfo"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/releaseinfo"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/pubdate"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/pubdate"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/copyright"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/copyright"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/abstract"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/abstract"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="bookinfo/legalnotice"/>
  <xsl:apply-templates mode="book.titlepage.verso.auto.mode" select="info/legalnotice"/>
</xsl:template>

<xsl:template name="book.titlepage.separator"><fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" break-after="page"/>
</xsl:template>

<xsl:template name="book.titlepage.before.recto">
</xsl:template>

<xsl:template name="book.titlepage.before.verso"><fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" break-after="page"/>
</xsl:template>

<xsl:template name="book.titlepage">
  <fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:variable name="recto.content">
      <xsl:call-template name="book.titlepage.before.recto"/>
      <xsl:call-template name="book.titlepage.recto"/>
    </xsl:variable>
    <xsl:variable name="recto.elements.count">
      <xsl:choose>
        <xsl:when test="function-available('exsl:node-set')"><xsl:value-of select="count(exsl:node-set($recto.content)/*)"/></xsl:when>
        <xsl:when test="contains(system-property('xsl:vendor'), 'Apache Software Foundation')">
          <!--Xalan quirk--><xsl:value-of select="count(exsl:node-set($recto.content)/*)"/></xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="(normalize-space($recto.content) != '') or ($recto.elements.count &gt; 0)">
      <fo:block><xsl:copy-of select="$recto.content"/></fo:block>
    </xsl:if>
    <xsl:variable name="verso.content">
      <xsl:call-template name="book.titlepage.before.verso"/>
      <xsl:call-template name="book.titlepage.verso"/>
    </xsl:variable>
    <xsl:variable name="verso.elements.count">
      <xsl:choose>
        <xsl:when test="function-available('exsl:node-set')"><xsl:value-of select="count(exsl:node-set($verso.content)/*)"/></xsl:when>
        <xsl:when test="contains(system-property('xsl:vendor'), 'Apache Software Foundation')">
          <!--Xalan quirk--><xsl:value-of select="count(exsl:node-set($verso.content)/*)"/></xsl:when>
        <xsl:otherwise>1</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:if test="(normalize-space($verso.content) != '') or ($verso.elements.count &gt; 0)">
      <fo:block><xsl:copy-of select="$verso.content"/></fo:block>
    </xsl:if>
    <xsl:call-template name="book.titlepage.separator"/>
  </fo:block>
</xsl:template>

<xsl:template match="*" mode="book.titlepage.recto.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template match="*" mode="book.titlepage.verso.mode">
  <!-- if an element isn't found in this mode, -->
  <!-- try the generic titlepage.mode -->
  <xsl:apply-templates select="." mode="titlepage.mode"/>
</xsl:template>

<xsl:template match="title" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="24.8832pt" space-before="18.6624pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template name="division.title">
<xsl:with-param name="node" select="ancestor-or-self::book[1]"/>
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template match="subtitle" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" text-align="center" font-size="20.736pt" space-before="15.552pt" font-family="{$title.fontset}">
<xsl:apply-templates select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="mediaobject" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" space-before="6em" space-after="1em" text-align="center">
<xsl:apply-templates select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="corpauthor" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" font-size="17.28pt" keep-with-next.within-column="always" space-before="2in">
<xsl:apply-templates select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="authorgroup" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" space-before="2in">
<xsl:apply-templates select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="author" mode="book.titlepage.recto.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.recto.style" font-size="17.28pt" space-before="10.8pt" keep-with-next.within-column="always">
<xsl:apply-templates select="." mode="book.titlepage.recto.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="title" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" font-size="14.4pt" font-weight="bold" font-family="{$title.fontset}">
<xsl:call-template name="book.verso.title">
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template match="corpauthor" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="authorgroup" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:call-template name="verso.authorgroup">
</xsl:call-template>
</fo:block>
</xsl:template>

<xsl:template match="author" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="othercredit" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="releaseinfo" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" space-before="0.5em">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="pubdate" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" space-before="1em">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="copyright" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="abstract" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

<xsl:template match="legalnotice" mode="book.titlepage.verso.auto.mode">
<fo:block xmlns:fo="http://www.w3.org/1999/XSL/Format" xsl:use-attribute-sets="book.titlepage.verso.style" font-size="8pt">
<xsl:apply-templates select="." mode="book.titlepage.verso.mode"/>
</fo:block>
</xsl:template>

</xsl:stylesheet>
