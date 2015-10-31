package org.emfjson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emfjson.jackson.resource.JsonResourceFactory;

/**
 * This example demonstrates the use of EMFJson with the EMF Resource API. 
 * It shows how to configure a ResourceSet to read and write JSON files.
 */
public class Example1 {

	public static void main(String[] args) throws IOException {
		// setting up the resourceSet with resourceFactory for JSON
		//
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new JsonResourceFactory());

		// register ecore package for standalone (outside eclipse) execution
		//
		resourceSet.getPackageRegistry().put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);

		// options
		// We indicate that we want to indent the output to 
		// be more readable.
		Map<String, Object> options = new HashMap<>();
		options.put(EMFJs.OPTION_INDENT_OUTPUT, true);

		// create a model
		//
		EPackage samplePackage = EcoreFactory.eINSTANCE.createEPackage();
		samplePackage.setName("sample");
		samplePackage.setNsPrefix("sample");
		samplePackage.setNsURI("http://sample.org/sample");
		
		EClass userClass = EcoreFactory.eINSTANCE.createEClass();
		userClass.setName("User");
		
		EAttribute userName = EcoreFactory.eINSTANCE.createEAttribute();
		userName.setName("name");
		userName.setEType(EcorePackage.Literals.ESTRING);
		
		userClass.getEStructuralFeatures().add(userName);
		samplePackage.getEClassifiers().add(userClass);

		// register the model package
		//
		resourceSet.getPackageRegistry().put(samplePackage.getNsURI(), samplePackage);
		
		// create instances
		//
		EObject u1 = EcoreUtil.create(userClass);
		u1.eSet(userName, "u1");

		// add package and instance in a resource
		//
		Resource res = resourceSet.createResource(URI.createURI("http://sample.org/sample"));
		res.getContents().add(samplePackage);
		res.getContents().add(u1);

		// save it as JSON
		//
		res.save(System.out, options);
	}

}
