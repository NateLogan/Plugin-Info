<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" omit-xml-declaration="yes" indent="yes"/>
	<xsl:variable name="plugin_def" select="document('plugins_def.xml')"/>
	<xsl:template match="/">
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			<title/>
		</head>
		<body>
			<div id="server">
				<h2>Server</h2>
				Name: <xsl:value-of select="plugin-info/server/name"/><br/>
				Port: <xsl:value-of select="plugin-info/server/port"/><br/>
				Version: <strong><xsl:value-of select="plugin-info/server/minecraft-server/version"/></strong><br/>
				Bukkit build: <strong><xsl:value-of select="plugin-info/server/bukkit/build"/></strong>
			</div>
			<div id="plugins">
				<h2>Plugins</h2>
				<ul>
					<xsl:for-each select="plugin-info/plugins/plugin">
						<xsl:variable name="plugin_def_val" select="$plugin_def//plugin[name=current()/name]"/>
						<li>
							<xsl:choose>
								<xsl:when test="$plugin_def_val/bukkit-forum">
									<xsl:element name="a">
										<xsl:attribute name="href">http://forums.bukkit.org/threads/<xsl:value-of select="$plugin_def_val/bukkit-forum"/></xsl:attribute>
										<xsl:value-of select="name"/>
									</xsl:element>
								</xsl:when>
								<xsl:when test="$plugin_def_val/website[@title='Homepage']">
									<xsl:element name="a">
										<xsl:attribute name="href"><xsl:value-of select="$plugin_def_val/website[@title='Homepage']"/></xsl:attribute>
										<xsl:value-of select="name"/>
									</xsl:element>
								</xsl:when>
								<xsl:when test="$plugin_def_val/website">
									<xsl:element name="a">
										<xsl:attribute name="href"><xsl:value-of select="$plugin_def_val/website"/></xsl:attribute>
										<xsl:value-of select="name"/>
									</xsl:element>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="name"/>
								</xsl:otherwise>
							</xsl:choose>
							
							<xsl:if test="$plugin_def_val/type">
								[<xsl:for-each select="$plugin_def_val/type">
									<xsl:value-of select="current()"/>
									<xsl:if test="not(position() = last())">/</xsl:if>
								</xsl:for-each>] 
							</xsl:if>						
				
							<xsl:if test="$plugin_def_val/website">			
								(<xsl:for-each select="$plugin_def_val/website">
									<xsl:element name="a">
										<xsl:attribute name="href"><xsl:value-of select="current()"/></xsl:attribute>
										<xsl:value-of select="@title"/>
									</xsl:element>
									<xsl:if test="not(position() = last())">, </xsl:if>
								</xsl:for-each>)
							</xsl:if>
			
							<xsl:text> - </xsl:text>
							<strong><xsl:value-of select="version"/></strong>
								
							<xsl:if test="description">
								<xsl:text> - </xsl:text>
								<xsl:value-of select="description"/>
							</xsl:if>
						</li>
					</xsl:for-each>
				</ul>
			</div>
			<div id="generated">
				<xsl:value-of select="plugin-info/generated/date"/>, 
				<xsl:value-of select="plugin-info/generated/time"/>
			</div>
		</body>
	</xsl:template>
</xsl:stylesheet>
