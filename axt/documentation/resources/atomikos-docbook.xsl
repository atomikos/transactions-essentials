<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format">

  <!-- Import standard DocBook FO stylesheets -->
  <xsl:import href="docbook/xsl/fo/docbook.xsl"/>

  <!-- Import custom titlepages -->
  <xsl:import href="fo-titlepage.xsl"/>

  <!-- Enable autolabelling of sections -->
  <xsl:param name="section.autolabel" select="1"/>
  <xsl:param name="section.label.includes.component.label" select="1"/>

  <!-- If you need bookmarks, uncomment one of the following lines, depending on your FO processor -->
  <!-- <xsl:param name="xep.extensions" select="1"/> -->
  <!-- <xsl:param name="fop.extensions" select="1"/> -->
  <!-- <xsl:param name="fop1.extensions" select="1"/> -->

  <xsl:attribute-set name="root.properties">
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-family">lucinda</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="admonition.properties">
    <xsl:attribute name="border">0.5pt solid blue</xsl:attribute>
    <xsl:attribute name="background-color">#DDDDFF</xsl:attribute>
    <xsl:attribute name="padding">0.1in</xsl:attribute>
  </xsl:attribute-set>
  
  <xsl:attribute-set name="section.title.properties">
    <xsl:attribute name="font-weight">normal</xsl:attribute>
    <xsl:attribute name="color">blue</xsl:attribute>
  </xsl:attribute-set>

  <xsl:param name="body.start.indent">1.0in</xsl:param>
  <xsl:param name="title.margin.left">0pc</xsl:param>
  <xsl:param name="admon.textlabel" select="0"/>

</xsl:stylesheet>
