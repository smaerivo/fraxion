<?xml version='1.0' encoding='ISO-8859-1' ?>
<!DOCTYPE helpset   
PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 2.0//EN"
         "http://java.sun.com/products/javahelp/helpset_2_0.dtd">

<helpset version="2.0">

	<!-- title -->
		<title>Fraxion Help</title>

	<!-- maps -->
	<maps>
		<homeID>general_information_overview</homeID>
		<mapref location="nl_BE/fraxion.jhm" />
	</maps>

  <!-- views -->
  <view mergetype="javax.help.UniteAppendMerge">
    <name>TOC</name>
    <label>Table of Contents</label>
    <type>javax.help.TOCView</type>
    <data>nl_BE/fraxion_toc.xml</data>
  </view>

	<!-- presentation windows -->
	<presentation default=true>
		<name>Fraxion Help</name>
		<size width="1000" height="600" />
		<location x="200" y="200" />
		<title>Fraxion Help</title>
		<toolbar>
			<helpaction>javax.help.BackAction</helpaction>
			<helpaction>javax.help.ForwardAction</helpaction>
			<helpaction>javax.help.HomeAction</helpaction>
			<helpaction>javax.help.PrintAction</helpaction>
		</toolbar>
	</presentation>
</helpset>
