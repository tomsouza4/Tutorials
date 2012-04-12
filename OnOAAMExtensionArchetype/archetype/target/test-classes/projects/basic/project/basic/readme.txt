Instructions for assembling basic.war
=====================================================
1) Following steps treat the folder where this is unzipped this war as <temp-folder>
2) Copy all your custom jars to <temp-folder>/WEB-INF/lib folder
3) Copy all your custom properties to <temp-folder>/WEB-INF/classes folder
4) If you are redeploying the basic.war then make sure the following attributes in <temp-folder>/META-INF/MANIFEST.MF are not changed.
   However if you are want to deploy a different version of basic.war without undeploying the original one, then you can increment the version numbers.
	Weblogic-Application-Version: 11.1.1.3.0
	Implementation-Version: 11.1.1.3.0
	Specification-Version: 11.1.1.3.0
5) Do not overwrite any other values in MANIFEST.MF file
6) Re-jar basic.war using the following command:
jar -cvfm basic.war <temp-folder>/META-INF/MANIFEST.MF -C <temp-folder> .
