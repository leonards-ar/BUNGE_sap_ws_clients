<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="2.0">

  <xsl:output method="xml"/>

  <xsl:template match="/child::*[1]">
    <root>
      <responseTested>
        <xsl:value-of select="response"/>
      </responseTested>
      <testResult>
        <xsl:text>tested OK!!</xsl:text>
      </testResult>
    </root>
  </xsl:template>
  
</xsl:stylesheet>
