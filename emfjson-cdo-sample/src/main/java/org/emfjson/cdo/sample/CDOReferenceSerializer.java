package org.emfjson.cdo.sample;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.ecore.EObject;
import org.emfjson.jackson.databind.ser.references.ReferenceSerializer;

import java.io.IOException;

public class CDOReferenceSerializer implements ReferenceSerializer {

	@Override
	public void serialize(EObject source, EObject value, JsonGenerator jg, SerializerProvider provider) throws IOException {
		final CDOObject cdoObject = CDOUtil.getCDOObject(value);
		final CDOID cdoid = CDOIDUtil.getCDOID(cdoObject);

		if (cdoid != null) {
			jg.writeNumber(CDOIDUtil.getLong(cdoid));
		} else {
			jg.writeNull();
		}
	}

}
