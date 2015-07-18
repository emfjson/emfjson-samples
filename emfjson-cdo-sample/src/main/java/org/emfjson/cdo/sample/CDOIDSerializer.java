package org.emfjson.cdo.sample;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.ecore.EObject;
import org.emfjson.jackson.JacksonOptions;
import org.emfjson.jackson.databind.ser.IdSerializer;

import java.io.IOException;

public class CDOIDSerializer implements IdSerializer {

	@Override
	public void serialize(EObject object, JsonGenerator jg, SerializerProvider provider) throws IOException {
		final JacksonOptions options = (JacksonOptions) provider.getAttribute("options");
		final CDOObject cdoObject = CDOUtil.getCDOObject(object);
		final CDOID cdoid = CDOIDUtil.getCDOID(cdoObject);

		jg.writeNumberField(options.idField, CDOIDUtil.getLong(cdoid));
	}

}
