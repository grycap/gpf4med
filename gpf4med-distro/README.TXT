This folder contains the installation files for the Graph processing framework 
for medical information (Gpf4Med), including base graph plug-ins and report 
templates.

The DICOM graph study service is the file with extension ".tar.gz" and 
"gpf4med-distro" in the name. Graph plug-ins are those with extension ".jar".

To install the DICOM graph study service, create the directory "/opt/gpf4med/" 
and extract the latest version of the framework:

$ mkdir /opt/gpf4med/ ; cd /opt/gpf4med/ ; tar xvzf gpf4med-distro-{version}.tar.gz

A script is available for automatic installation. For example, as part of a 
virtual machine deployment:

$ curl -O -J -L http://sourceforge.net/projects/gpf4med/files/gpf4med/1.0.0/install-gpf4med.sh/download ; bash install-gpf4med.sh

This will install the DICOM graph study service in the directory "/tmp/gpf4med",
and will start the container server in the network port 8080. Be sure that Java
is installed in the machine or the script will fail to start the service.

Documentation is available in the "htdocs/" directory.

Release notes for Gpf4Med version 1.0.0:

* Initial release of the DICOM graph study service.

* Initial release of the Graph Base Connector.

* Breast cancer report templates on clinical examination, medical imaging 
(mammography, ultrasound, magnetic resonance imaging -- MRI) and biopsy.
